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

Domain::~Domain(){}



}

