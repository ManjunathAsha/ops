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
#include "OPSTypeDefs.h"
#include "Domain.h"
#include "NoSuchTopicException.h"
#include "BoostIOServiceImpl.h"

namespace ops
{

Domain::Domain() : 
	timeToLive(1), 
	localInterface("0.0.0.0"),
	inSocketBufferSize(16000000),
	outSocketBufferSize(16000000),
	metaDataMcPort(9494)		// Default port 
{
	appendType(std::string("Domain"));

	//For backward compability.
	//appendType(std::string("MulticastDomain"));
}
std::string Domain::getDomainAddress()
{
	return domainAddress;
}
std::vector<Topic* > Domain::getTopics()
{
	for(unsigned int i = 0 ; i < topics.size(); i++)
	{
		if(topics[i]->getDomainAddress() == "")
		{
			topics[i]->setDomainAddress(domainAddress);
		}
	}
	return topics;
}
Topic Domain::getTopic(std::string name)
{
	for(unsigned int i = 0 ; i < topics.size(); i++)
	{
		if(topics[i]->getDomainAddress() == "")
		{
			topics[i]->setDomainAddress(domainAddress);
		}
		if(topics[i]->getName() == name)
		{
			return *topics[i];
		}
	}
	throw NoSuchTopicException("Topic " + name + " does not exist in ops config file.");

}
bool Domain::existsTopic(std::string name)
{
	for(unsigned int i = 0 ; i < topics.size(); i++)
	{
		if(topics[i]->getName() == name)
		{
			return true;
		}
	}
	return false;
}
std::string Domain::getDomainID()
{
	return domainID;
}


void Domain::serialize(ArchiverInOut* archiver)
{
	OPSObject::serialize(archiver);
	archiver->inout(std::string("domainID"), domainID);
	archiver->inout<Topic>(std::string("topics"), topics);
	archiver->inout(std::string("domainAddress"), domainAddress);
	archiver->inout(std::string("localInterface"), localInterface);
	archiver->inout(std::string("timeToLive"), timeToLive);
	archiver->inout(std::string("inSocketBufferSize"), inSocketBufferSize);
	archiver->inout(std::string("outSocketBufferSize"), outSocketBufferSize);
	archiver->inout(std::string("metaDataMcPort"), metaDataMcPort);
}
int Domain::getTimeToLive()
{
	return timeToLive;
}

std::string Domain::getLocalInterface()
{
	return localInterface;
}

int Domain::getInSocketBufferSize()
{
	return inSocketBufferSize;
}
int Domain::getOutSocketBufferSize()
{
	return outSocketBufferSize;
}
int Domain::getMetaDataMcPort()
{
	return metaDataMcPort;
}

Domain::~Domain()
{
}

// If argument contains a "/" we assume it is on the form:  subnet-address/subnet-mask
// In that case we loop over all interfaces and take the first one that matches
// i.e. the one whos interface address is on the subnet
std::string Domain::doSubnetTranslation(std::string addr, IOService* ioServ)
{
	using boost::asio::ip::udp;

	std::basic_string <char>::size_type index;

	index = addr.find("/");
	if (index == std::string::npos) return addr;

	std::string subnet = addr.substr(0, index);
	std::string mask = addr.substr(index+1);

	unsigned long subnetIp = boost::asio::ip::address_v4::from_string(subnet).to_ulong();
	unsigned long subnetMask = boost::asio::ip::address_v4::from_string(mask).to_ulong();

	boost::asio::io_service* ioService = ((BoostIOServiceImpl*) ioServ)->boostIOService;

	udp::resolver resolver(*ioService);
    udp::resolver::query query(boost::asio::ip::host_name(), "");
    udp::resolver::iterator it = resolver.resolve(query);
    udp::resolver::iterator end;
    while (it != end) {
		boost::asio::ip::address ipaddr = it->endpoint().address();
	    if (ipaddr.is_v4())	{
			unsigned long Ip = ipaddr.to_v4().to_ulong();
			if ((Ip & subnetMask) == subnetIp) 
				return ipaddr.to_string();
        }
        it++;
    }

	return subnet;
}

}

