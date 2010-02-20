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

//#include "boost\asio\basic_socket.hpp"

#include "OPSTypeDefs.h"
#include "boost/asio/basic_datagram_socket.hpp"

#include "UDPSender.h"
#include "TimeHelper.h"
#include <iostream>
#include "Participant.h"
#include "BasicError.h"

namespace ops
{
    using boost::asio::ip::udp;
	UDPSender::UDPSender(std::string localInterface, int ttl, __int64 outSocketBufferSize, bool multicastSocket)
           
    {
		boost::asio::ip::address ipAddr(boost::asio::ip::address_v4::from_string(localInterface));

		localEndpoint = new boost::asio::ip::udp::endpoint(ipAddr, 0);
        //localEndpoint = new boost::asio::ip::udp::endpoint(udp::v4(), 0);
        socket = new boost::asio::ip::udp::socket(io_service, localEndpoint->protocol());

		if(outSocketBufferSize > 0)
		{
			boost::asio::socket_base::send_buffer_size option(outSocketBufferSize);
			boost::system::error_code ec;
			ec = socket->set_option(option, ec);
			socket->get_option(option);
			if(ec != 0 || option.value() != outSocketBufferSize)
			{
				//std::cout << "Socket buffer size could not be set" << std::endl;
				Participant::reportStaticError(&ops::BasicError("UDPSender", "UDPSender", "Socket buffer size could not be set"));
			}
		}

		if(multicastSocket)
		{
			boost::asio::ip::multicast::hops ttlOption(ttl);
			socket->set_option(ttlOption);

	///LA
			boost::asio::ip::address_v4 local_interface = boost::asio::ip::address_v4::from_string(localInterface);
			boost::asio::ip::multicast::outbound_interface ifOption(local_interface);
			socket->set_option(ifOption);
	///LA
		}

		boost::asio::socket_base::non_blocking_io command(true);
		socket->io_control(command);
	
    }

    UDPSender::~UDPSender()
    {
        
        socket->close();
        delete socket;
        delete localEndpoint;
    }

    bool UDPSender::sendTo(char* buf, int size, const std::string& ip, int port)
    {
        try
        {
            boost::asio::ip::address ipaddress = boost::asio::ip::address::from_string(ip.c_str());
            boost::asio::ip::udp::endpoint endpoint(ipaddress, port);
            socket->send_to(boost::asio::buffer(buf, size), endpoint);
            return true;
        }
        catch (...)
        {
			Participant::reportStaticError(&ops::BasicError("UDPSender", "sendTo", "Error when sending udp message."));
            return false;
        }

    }
	int UDPSender::receiveReply(char* buf, int size)
	{
		int nReceived = socket->receive(boost::asio::buffer(buf, size));
		return nReceived;
	}
	bool UDPSender::waitForReply(int timeout)
	{
		/*__int64 startWait = TimeHelper::currentTimeMillis();
		while(TimeHelper::currentTimeMillis())*/
		return false;
	}

}

