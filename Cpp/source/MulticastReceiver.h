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
		  max_length(65535),
		  cancelled(false)
		{
			boost::asio::io_service* ioService = ((BoostIOServiceImpl*)ioServ)->boostIOService;//((BoostIOServiceImpl*)Participant::getIOService())->boostIOService;
			//udp::resolver resolver(*ioService);
			//udp::resolver::query query(boost::asio::ip::host_name(),"");
			//udp::resolver::iterator it=resolver.resolve(query);
			//boost::asio::ip::address addr=(it++)->endpoint().address();

			// Linux needs INADDR_ANY here, for Windows it works with INADDR_ANY or localInterface
			boost::asio::ip::address ipAddr(boost::asio::ip::address_v4::from_string("0.0.0.0"));
//			boost::asio::ip::address ipAddr(boost::asio::ip::address_v4::from_string(localInterface));

			localEndpoint = new boost::asio::ip::udp::endpoint(ipAddr, bindPort);
			
			//localEndpoint = new udp::endpoint(addr, bindPort);

			sock = new boost::asio::ip::udp::socket(*ioService);


			
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
			//boost::asio::socket_base::receive_buffer_size option(inSocketBufferSizent);
			//sock->set_option(option);
			


			sock->set_option(boost::asio::ip::udp::socket::reuse_address(true));
			sock->bind(*localEndpoint);


			// Join the multicast group.
///LA
			////const boost::asio::ip::address multicastAddress = boost::asio::ip::address_v4::from_string(mcAddress);
			//////const boost::asio::ip::address multicastAddress = boost::asio::ip::address::from_string(mcAddress);
			////sock->set_option(boost::asio::ip::multicast::join_group(multicastAddress));
			const boost::asio::ip::address_v4 multicastAddress = boost::asio::ip::address_v4::from_string(mcAddress);
			const boost::asio::ip::address_v4 networkInterface(boost::asio::ip::address_v4::from_string(localInterface));
			sock->set_option(boost::asio::ip::multicast::join_group(multicastAddress,networkInterface));
///LA

			ipaddress = mcAddress;
			port = sock->local_endpoint().port();

			

		}

		void asynchWait(char* bytes, int size)
		{
			data  = bytes;
			max_length = size;
			sock->async_receive(
				boost::asio::buffer(data, max_length), 
				boost::bind(&MulticastReceiver::handle_receive_from, this, boost::asio::placeholders::error, boost::asio::placeholders::bytes_transferred));

		}

		void handle_receive_from(const boost::system::error_code& error,
			size_t nrBytesReceived)
		{
			if(cancelled)
			{
				return;
			}			
			if (!error && nrBytesReceived > 0)
			{
				//printf("Data receivedm in multicast receiver\n");
				handleReadOK(data, nrBytesReceived);
				//notifyNewEvent(data);
			
			}
			else
			{
				handleReadError(error);
			}
			
			
		}
		void handleReadOK(char* bytes_, int size)
		{
			notifyNewEvent(BytesSizePair(data, size));

			/*sock->async_receive(
				boost::asio::buffer(data, max_length), 
				boost::bind(&MulticastReceiver::handle_receive_from, this, boost::asio::placeholders::error, boost::asio::placeholders::bytes_transferred));*/

		}
		void handleReadError(const boost::system::error_code& error)
		{
			
			if(error.value() == BREAK_COMM_ERROR_CODE)
			{
				//Communcation has been canceled from stop, do not scedule new receive
				return;
			}
			//notifyNewEvent(data);
			//printf("___________handleReadError__________\n");
			Participant::reportStaticError(&ops::BasicError("MulticastReceiver", "handleReadError", "Error"));
			
			sock->async_receive(
				boost::asio::buffer(data, max_length), 
				boost::bind(&MulticastReceiver::handle_receive_from, this, boost::asio::placeholders::error, boost::asio::placeholders::bytes_transferred));

		}


		~MulticastReceiver()
		{
			delete sock;
			delete localEndpoint;
			//delete ioService;
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

		///Override from Receiver
		void stop()
		{
			boost::system::error_code error(BREAK_COMM_ERROR_CODE, boost::system::generic_category);
			cancelled = true;
			sock->cancel(error);
			
		}

		
	private:
		int port;
		std::string ipaddress;
		boost::asio::ip::udp::socket* sock;
		boost::asio::ip::udp::endpoint* localEndpoint;
		boost::asio::ip::udp::endpoint lastEndpoint;
		boost::asio::io_service* ioService;

		int max_length; //enum { max_length = 65535 };
		char* data;//[max_length];

		static const int BREAK_COMM_ERROR_CODE = 345676;
		bool cancelled;


	};
}
#endif
