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

/* 
 * File:   Transport.h
 * Author: Anton Gravestam
 *
 * Created on den 22 oktober 2008, 20:01
 */

#ifndef ops_TCPServerH
#define	ops_TCPServerH

#include <string>
#include "Sender.h"
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
    ///Interface used to send data

	class TCPServer : public Sender
    {
    public:
		TCPServer(std::string serverIP, int serverPort, IOService* ioServ, __int64 outSocketBufferSize = 16000000) : 
			acceptor(NULL), sock(NULL), connected(false), canceled(false)
		{
			this->outSocketBufferSize = outSocketBufferSize;
			ioService = ((BoostIOServiceImpl*)ioServ)->boostIOService;//((BoostIOServiceImpl*)Participant::getIOService())->boostIOService;
			//boost::asio::ip::address ipAddr(boost::asio::ip::address_v4::from_string(serverIP));
			endpoint = new boost::asio::ip::tcp::endpoint(boost::asio::ip::tcp::v4(), serverPort);
			sock = new boost::asio::ip::tcp::socket(*ioService);
		}
		
		void open()
		{
			canceled = false;
			if (acceptor) delete acceptor;
			// This constructor opens, binds and listens to the given endpoint.
			acceptor = new boost::asio::ip::tcp::acceptor(*ioService, *endpoint);
			acceptor->async_accept(*sock, boost::bind(&TCPServer::handleAccept, this, boost::asio::placeholders::error));
		}

		///Sends buf to any Receiver connected to this Sender, ip and port are discarded and can be left blank.
        virtual bool sendTo(char* buf, int size, const std::string& ip, int port)
		{
			//Send to anyone connected. Loop backwards to avoid problems when removing broken sockets
			for(int i = connectedSockets.size() - 1; i >= 0 ; i--)
			{
				try
				{
					//First, prepare and send a package of fixed length 22 with information about the size of the data package
					char sizeInfo[100] = "opsp_tcp_size_info"; 
					memcpy(sizeInfo + 18, (void*)&size, 4);
					connectedSockets[i]->send(boost::asio::buffer(sizeInfo, 22));

					//Send the actual data
					connectedSockets[i]->send(boost::asio::buffer(buf, size));

				}
				catch(std::exception& e)
				{
					std::cout << "Socket closed, exception in TCPServer::sendTo():" << e.what() << std::endl;
					connectedSockets[i]->close();

					std::vector<boost::asio::ip::tcp::socket*>::iterator it;
					it = connectedSockets.begin();
					it += i;
					delete connectedSockets[i];
					connectedSockets.erase(it);
					
					connected = (connectedSockets.size() > 0);
				}
			}
			return true;
		}
		virtual int receiveReply(char* buf, int size)
		{
			return -9999999;
		}
        virtual int getPort()
		{
			return port;
		}
        virtual std::string getAddress()
		{
			return ipAddress;
		}

        virtual ~TCPServer()
        {
			close();
			if (acceptor) delete acceptor;
			delete sock;
			delete endpoint;
		}

		void close()
		{
			canceled = true;
			if (acceptor) acceptor->close();
			for(int i = connectedSockets.size() - 1; i >= 0 ; i--)
			{
				connectedSockets[i]->close();
				delete connectedSockets[i];
			}
			connectedSockets.clear();
			connected = false;
		}

    private:
		void handleAccept(const boost::system::error_code& error)
		{
			//This TCPServer is shuting down
			if(canceled) return;

			if(!error)
			{
				if (outSocketBufferSize > 0) {
					boost::asio::socket_base::send_buffer_size option(outSocketBufferSize);
					boost::system::error_code ec;
					ec = sock->set_option(option, ec);
					sock->get_option(option);
					if(ec != 0 || option.value() != outSocketBufferSize)
					{
						//std::cout << "Socket buffer size could not be set" << std::endl;
						Participant::reportStaticError(&ops::BasicError("TCPServer", "TCPServer", "Socket buffer size could not be set"));
					}
				}
//				std::cout << "accept ok" << std::endl;
				connectedSockets.push_back(sock);
				sock = new boost::asio::ip::tcp::socket(*ioService);
				acceptor->async_accept(*sock, boost::bind(&TCPServer::handleAccept, this, boost::asio::placeholders::error));
				connected = true;
			}
			else
			{
//				std::cout << "accept failed" << std::endl;
				acceptor->async_accept(*sock, boost::bind(&TCPServer::handleAccept, this, boost::asio::placeholders::error));
			}
		}

		int port;
		std::string ipAddress;
		__int64 outSocketBufferSize;
		boost::asio::ip::tcp::endpoint* endpoint;		//<-- The local port to bind to.
        boost::asio::ip::tcp::socket* sock;				//<-- The socket that handles next accept.
        
		boost::asio::ip::tcp::acceptor* acceptor;
		bool connected;
		std::vector<boost::asio::ip::tcp::socket*> connectedSockets; //<-- All connected sockets
		boost::asio::io_service* ioService;				//<-- Boost io_service ahndles the asynhronous operations on the sockets

		bool canceled;

    };
}



#endif	/* ops_TCPServerH */

