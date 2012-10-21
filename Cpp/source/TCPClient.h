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

#ifndef ops_TCPCLientH
#define ops_TCPClientH

#include <string>
#include "Receiver.h"
#include "IOService.h" 
#include "BytesSizePair.h"

#include <iostream>
#include <boost/asio.hpp>
#include <boost/array.hpp>
#include "BoostIOServiceImpl.h"
#include "boost/bind.hpp"
#include "Participant.h"
#include "BasicError.h"

namespace ops
{

    class TCPClient : public Receiver
    {
    public:

        TCPClient(std::string serverIP, int serverPort, IOService* ioServ) : connected(false), tryToConnect(false), accumulatedSize(0),
			m_connectCounter(0), m_receiveCounter(0)
        {
            boost::asio::io_service* ioService = ((BoostIOServiceImpl*) ioServ)->boostIOService; //((BoostIOServiceImpl*)Participant::getIOService())->boostIOService;
            boost::asio::ip::address ipAddr(boost::asio::ip::address_v4::from_string(serverIP));
            endpoint = new boost::asio::ip::tcp::endpoint(ipAddr, serverPort);

            sock = new boost::asio::ip::tcp::socket(*ioService);
        }

		void start()
		{
			tryToConnect = true;
            connected = false;
			InterlockedIncrement(&m_connectCounter);	// keep track of outstanding requests
            sock->async_connect(*endpoint, boost::bind(&TCPClient::handleConnect, this, boost::asio::placeholders::error));
		}

        void stop()
        {
			//Close the socket 
			tryToConnect = false;
            connected = false;
            sock->close();
		}

        void handleConnect(const boost::system::error_code& error)
        {
			InterlockedDecrement(&m_connectCounter);	// keep track of outstanding requests

			if (!tryToConnect) return;

			if (error)
            {
                //connect again
                connected = false;
                //std::cout << "connection failed tcp asynch" << std::endl;
///LA too much output        Participant::reportStaticError(&ops::BasicError("TCPClient", "handleConnect", "connection failed tcp asynch"));
				InterlockedIncrement(&m_connectCounter);	// keep track of outstanding requests
                sock->async_connect(*endpoint, boost::bind(&TCPClient::handleConnect, this, boost::asio::placeholders::error));
            }
            else
            {
                connected = true;
                accumulatedSize = 0;

                boost::asio::socket_base::receive_buffer_size option(16000000);
                boost::system::error_code ec;
                ec = sock->set_option(option, ec);
                sock->get_option(option);
                if (ec != 0 || option.value() != 16000000)
                {
                    //std::cout << "Socket buffer size could not be set" << std::endl;
                    Participant::reportStaticError(&ops::BasicError("TCPClient", "TCPClient", "Socket buffer size could not be set"));
                }

                //Disable Nagle algorithm

                boost::asio::ip::tcp::no_delay option2(true);
                sock->set_option(option2);
                //if(sockOptErr != 0)
                //{
                //std::cout << "Failed to disable Nagle algorithm." << std::endl;
                //Participant::reportStaticError(&ops::BasicError("Failed to disable Nagle algorithm."));
                //}

//                std::cout << "connected tcp asynch" << std::endl;
                notifyNewEvent(BytesSizePair("", -5)); //Connection was down but has been reastablished.
            }
        }

        virtual ~TCPClient()
        {
			// Make sure socket is closed
			stop();

			/// We must handle asynchronous callbacks that haven't finished yet.
			/// This approach works, but the recommended boost way is to use a shared pointer to the instance object
			/// between the "normal" code and the callbacks, so the callbacks can check if the object exists.
			while (m_connectCounter || m_receiveCounter) 
				Sleep(1);

			delete sock;
            delete endpoint;
        }

        void asynchWait(char* bytes, int size)
        {
            data = bytes;
            max_length = size;

            if (connected) {
                //Always start by reading the packet size when using tcp
				InterlockedIncrement(&m_receiveCounter);	// keep track of outstanding requests
                sock->async_receive(
                        boost::asio::buffer(data, 22),
                        boost::bind(&TCPClient::handle_receive_sizeInfo, this, boost::asio::placeholders::error, boost::asio::placeholders::bytes_transferred));
            }

        }

        void handle_receive_sizeInfo(const boost::system::error_code& error, size_t nrBytesReceived)
        {
			InterlockedDecrement(&m_receiveCounter);	// keep track of outstanding requests

			if (!connected) {
                notifyNewEvent(BytesSizePair("", -2));
                return;
            }

			bool errorDetected = false;

            if (!error) {
                accumulatedSize += nrBytesReceived;
                if (accumulatedSize < 22)// we have not gotten the size pack yet
                {
					InterlockedIncrement(&m_receiveCounter);	// keep track of outstanding requests
                    sock->async_receive(
                            boost::asio::buffer(data + accumulatedSize, 22 - accumulatedSize),
                            boost::bind(&TCPClient::handle_receive_sizeInfo, this, boost::asio::placeholders::error, boost::asio::placeholders::bytes_transferred));
                }
                else // We are ready to receive the main data package
                {
                    accumulatedSize = 0;
					// Get size of data packet from the received size packet
					int sizeInfo = *((int*) (data + 18));
					if (sizeInfo > max_length) {
						// This is an error, we are not able to receive more than max_length bytes (the buffer size)
						errorDetected = true;
					} else {
		                max_length = sizeInfo;
						InterlockedIncrement(&m_receiveCounter);	// keep track of outstanding requests
				        sock->async_receive(
					            boost::asio::buffer(data, max_length),
						        boost::bind(&TCPClient::handle_receive_from, this, boost::asio::placeholders::error, boost::asio::placeholders::bytes_transferred));
					}
                }
            } else {
				errorDetected = true;
			}

			if (errorDetected) {
                Participant::reportStaticError(&ops::BasicError("TCPClient", "handle_receive_sizeInfo", "Error in receive."));
                notifyNewEvent(BytesSizePair("", -1));

                //Close the socket and try to connect again
                connected = false;
                sock->close();
				InterlockedIncrement(&m_connectCounter);	// keep track of outstanding requests
                sock->async_connect(*endpoint, boost::bind(&TCPClient::handleConnect, this, boost::asio::placeholders::error));
            }
        }

        void handle_receive_from(const boost::system::error_code& error, size_t nrBytesReceived)
        {
			InterlockedDecrement(&m_receiveCounter);	// keep track of outstanding requests

            if (!connected) {
                notifyNewEvent(BytesSizePair("", -2));
                return;
            }

            if (!error && nrBytesReceived > 0) {
                accumulatedSize += nrBytesReceived;
                if (accumulatedSize < max_length)// we have not gotten all bytes yet
                {
					InterlockedIncrement(&m_receiveCounter);	// keep track of outstanding requests
                    sock->async_receive(
                            boost::asio::buffer(data + accumulatedSize, max_length - accumulatedSize),
                            boost::bind(&TCPClient::handle_receive_from, this, boost::asio::placeholders::error, boost::asio::placeholders::bytes_transferred));
                }
                else
                {
                    notifyNewEvent(BytesSizePair(data, accumulatedSize));
                    accumulatedSize = 0;
                }
            } else {
                //handleReadError(error);
//                printf("Error \n");
                notifyNewEvent(BytesSizePair("", -1));

                //Close the socket and try to connect again
                connected = false;
                sock->close();
				InterlockedIncrement(&m_connectCounter);	// keep track of outstanding requests
                sock->async_connect(*endpoint, boost::bind(&TCPClient::handleConnect, this, boost::asio::placeholders::error));
            }
        }

    private:
        int port;
        std::string ipaddress;
        boost::asio::ip::tcp::socket* sock;
        boost::asio::ip::tcp::endpoint* endpoint;

        int max_length;
        char* data;
        bool connected;
		bool tryToConnect;

        int accumulatedSize;

		// Counters to keep track of our outstanding requests, that will result in callbacks to us
		volatile LONG m_connectCounter;
		volatile LONG m_receiveCounter;
    };
}
#endif
