/**
* 
* Copyright (C) 2006-2009 Anton Gravestam.
*
* This file is part of OPS (Open Publish Subscribe).
*
* OPS (Open Publish Subscribe) is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* OPS (Open Publish Subscribe) is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with OPS (Open Publish Subscribe).  If not, see <http://www.gnu.org/licenses/>.
*/

#ifndef ops_MulticastReceiverH
#define ops_MulticastReceiverH

#include <string>
#include "Participant.h"
#include "Receiver.h"
#include <boost/asio.hpp>
#include "boost/bind.hpp"
#include "ByteBuffer.h"
#include "BoostIOServiceImpl.h"
#include <iostream>
#include "Participant.h"
#include "BasicError.h"

namespace ops
{
	using boost::asio::ip::udp;

	class MulticastReceiver : public Receiver
	{
	public:
		MulticastReceiver(std::string mcAddress, int bindPort, IOService* ioServ, std::string localInterface = "0.0.0.0", __int64 inSocketBufferSizent = 16000000): 
		  max_length(65535), m_receiveCounter(0),
		  cancelled(false)
		{
			ipaddress = mcAddress;
			this->localInterface = localInterface;
			this->inSocketBufferSizent = inSocketBufferSizent;

			boost::asio::io_service* ioService = ((BoostIOServiceImpl*)ioServ)->boostIOService;//((BoostIOServiceImpl*)Participant::getIOService())->boostIOService;
			//udp::resolver resolver(*ioService);
			//udp::resolver::query query(boost::asio::ip::host_name(),"");
			//udp::resolver::iterator it=resolver.resolve(query);
			//boost::asio::ip::address addr=(it++)->endpoint().address();

			// Linux needs INADDR_ANY here, for Windows it works with INADDR_ANY or localInterface
			boost::asio::ip::address ipAddr(boost::asio::ip::address_v4::from_string("0.0.0.0"));
//			boost::asio::ip::address ipAddr(boost::asio::ip::address_v4::from_string(localInterface));

			localEndpoint = new boost::asio::ip::udp::endpoint(ipAddr, bindPort);
			
			sock = new boost::asio::ip::udp::socket(*ioService);
		}

		///Override from Receiver
		void start()
		{
			sock->open(localEndpoint->protocol());
				
			if(inSocketBufferSizent > 0)
			{
				boost::asio::socket_base::receive_buffer_size option(inSocketBufferSizent);
				boost::system::error_code ec;
				ec = sock->set_option(option, ec);
				sock->get_option(option);
				if(ec != 0 || option.value() != inSocketBufferSizent)
				{
					//std::cout << "Socket buffer size could not be set" << std::endl;
					Participant::reportStaticError(&ops::BasicError("MulticastReceiver", "MulticastReceiver", "Socket buffer size could not be set"));
				}
			}
			
			sock->set_option(boost::asio::ip::udp::socket::reuse_address(true));
			sock->bind(*localEndpoint);

			// Join the multicast group.
			const boost::asio::ip::address_v4 multicastAddress = boost::asio::ip::address_v4::from_string(ipaddress);
			const boost::asio::ip::address_v4 networkInterface(boost::asio::ip::address_v4::from_string(localInterface));
			sock->set_option(boost::asio::ip::multicast::join_group(multicastAddress,networkInterface));

			port = sock->local_endpoint().port();
		}

		///Override from Receiver
		void stop()
		{
#if BOOST_VERSION == 103800
			boost::system::error_code error(BREAK_COMM_ERROR_CODE, boost::system::generic_category );
#else
			boost::system::error_code error(BREAK_COMM_ERROR_CODE, boost::system::generic_category() );
#endif
			cancelled = true;
			// sock->cancel(error);
			sock->close();
		}

		void asynchWait(char* bytes, int size)
		{
			data  = bytes;
			max_length = size;
			InterlockedIncrement(&m_receiveCounter);	// keep track of outstanding requests
			sock->async_receive(
				boost::asio::buffer(data, max_length), 
				boost::bind(&MulticastReceiver::handle_receive_from, this, boost::asio::placeholders::error, boost::asio::placeholders::bytes_transferred));
		}

		void handle_receive_from(const boost::system::error_code& error, size_t nrBytesReceived)
		{
			if (!cancelled) {
				if (!error && nrBytesReceived > 0)
				{
					//printf("Data receivedm in multicast receiver\n");
					handleReadOK(data, nrBytesReceived);
				}
				else
				{
					handleReadError(error);
				}
			}
			// We decrement the counter as the last thing in the callback, so we don't access the object any more 
			// in case the destructor is called and waiting for us to be finished.
			InterlockedDecrement(&m_receiveCounter);	// keep track of outstanding requests
		}
			
		void handleReadOK(char* bytes_, int size)
		{
			notifyNewEvent(BytesSizePair(data, size));
		}

		void handleReadError(const boost::system::error_code& error)
		{
			if(error.value() == BREAK_COMM_ERROR_CODE)
			{
				//Communcation has been canceled from stop, do not scedule new receive
				return;
			}

			//notifyNewEvent(data);
			//printf("___________handleReadError__________ %d\n", error.value());
			Participant::reportStaticError(&ops::BasicError("MulticastReceiver", "handleReadError", "Error"));
			
			//WSAEFAULT (10014) "Illegal buffer address" is fatal, happens e.g. if a too small buffer is given and
			// it probably wont go away by calling the same again, so just report error and then exit without 
			// starting a new async_receive().
			if (error.value() == WSAEFAULT) return;

			InterlockedIncrement(&m_receiveCounter);	// keep track of outstanding requests
			sock->async_receive(
				boost::asio::buffer(data, max_length), 
				boost::bind(&MulticastReceiver::handle_receive_from, this, boost::asio::placeholders::error, boost::asio::placeholders::bytes_transferred));
		}

		~MulticastReceiver()
		{
			// Make sure socket is closed
			stop();

			/// We must handle asynchronous callbacks that haven't finished yet.
			/// This approach works, but the recommended boost way is to use a shared pointer to the instance object
			/// between the "normal" code and the callbacks, so the callbacks can check if the object exists.
			while (m_receiveCounter) 
				Sleep(1);

			delete sock;
			delete localEndpoint;
		}

		int receive(char* buf, int size)
		{
			try
			{
				int nReceived = sock->receive_from(boost::asio::buffer(buf, size), lastEndpoint);
				return nReceived;
			}
			catch(...)
			{
				Participant::reportStaticError(&ops::BasicError("MulticastReceiver", "receive", "Exception in MulticastReceiver::receive()"));
				return -1;
			}
		}

		int available()
		{
			return sock->available();
		}

		bool sendReply(char* buf, int size)
		{
			try
			{
				sock->send_to(boost::asio::buffer(buf, size), lastEndpoint);
				return true;
			}
			catch (...)
			{
				return false;
			}
		}

		int getPort()
		{
			return port;
		}

		std::string getAddress()
		{
			return ipaddress;
		}

	private:
		int port;
		std::string ipaddress;
		std::string localInterface;
		__int64 inSocketBufferSizent;
		boost::asio::ip::udp::socket* sock;
		boost::asio::ip::udp::endpoint* localEndpoint;
		boost::asio::ip::udp::endpoint lastEndpoint;
		boost::asio::io_service* ioService;

		int max_length;
		char* data;

		static const int BREAK_COMM_ERROR_CODE = 345676;
		bool cancelled;

		// Counter to keep track of our outstanding requests, that will result in callbacks to us
		volatile LONG m_receiveCounter;
	};
}
#endif
