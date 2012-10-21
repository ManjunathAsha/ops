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
#include "Publisher.h"
#include "Participant.h"
#include "Domain.h"

namespace ops
{

    Publisher::Publisher(Topic t) :
    topic(t),
    name(""),
    key(""),
    priority(0),
    currentPublicationID(0),
    memMap(t.getSampleMaxSize() / OPSConstants::PACKET_MAX_SIZE + 1, OPSConstants::PACKET_MAX_SIZE),
    sleepEverySendPacket(100000),
    sendSleepTime(1),
    sleepOnSendFailed(true)
    {
        participant = Participant::getInstance(topic.getDomainID(), topic.getParticipantID());
        //Participant* participant = topic.getParticipant();

        sendDataHandler = participant->getSendDataHandler(topic);

        //MulticastDomain* mcDomain = dynamic_cast<MulticastDomain*>(participant->getConfig()->getDomain(topic.getDomainID()));
        //if(mcDomain != NULL)
        //{
        //	if(topic.getTransport() == Topic::TRANSPORT_MC)
        //	{
        //		udpSender = Sender::create(mcDomain->getLocalInterface(), mcDomain->getTimeToLive(), topic.getOutSocketBufferSize());
        //	}
        //	else if(topic.getTransport() == Topic::TRANSPORT_TCP)
        //	{
        //		udpSender = Sender::createTCPServer(topic.getDomainAddress(), topic.getPort(), participant->getIOService());
        //	}
        //}
        //else
        //{
        //	//TODO: decide upon exceptions, application will crash...
        //}
        //bytes = new char[Participant::PACKET_MAX_SIZE];
        message.setPublisherName(name);
        message.setTopicName(topic.getName());
        message.setDataOwner(false);
		
		start();
    }

    Publisher::~Publisher()
    {
		stop();
		participant->releaseSendDataHandler(topic);
        //delete bytes;
    }

	void Publisher::start()
	{
		sendDataHandler->addPublisher(this);
	}

	void Publisher::stop()
	{
		sendDataHandler->removePublisher(this);
	}

    Topic Publisher::getTopic()
    {
        return this->topic;
    }

    void Publisher::setName(std::string name)
    {
        this->name = name;
    }

    void Publisher::setKey(std::string key)
    {
        this->key = key;
    }

    std::string Publisher::getKey()
    {
        return this->key;
    }

    std::string Publisher::getName()
    {
        return this->name;
    }

    void Publisher::writeOPSObject(OPSObject* obj)
    {
        write(obj);
    }

    void Publisher::write(OPSObject* data)
    {
        //ByteBuffer buf(bytes, Participant::MESSAGE_MAX_SIZE / Participant::PACKET_MAX_SIZE, Participant::PACKET_MAX_SIZE);

        if (key != "")
        {
            data->setKey(key);
        }


        ByteBuffer buf(&memMap);

        message.setData(data);

        message.setPublicationID(currentPublicationID);
        message.setPublisherName(name);

        buf.writeNewSegment();

        OPSArchiverOut archive(&buf);

        message = *((OPSMessage*) (archive.inout(std::string("message"), &message)));

        //If data has spare bytes, write them to the end of the buf
        if (message.getData()->spareBytes.size() > 0)
        {
            buf.WriteChars(&(message.getData()->spareBytes[0]), message.getData()->spareBytes.size());
        }

        buf.finish();

        for (int i = 0; i < buf.getNrOfSegments(); i++)
        {
            int segSize = buf.getSegmentSize(i);
            bool sendOK = sendDataHandler->sendData(buf.getSegment(i), segSize, topic);
            if (!sendOK)
            {
                TimeHelper::sleep(sendSleepTime);
                sendDataHandler->sendData(buf.getSegment(i), segSize, topic);
            }
			else if ((i > 0) && (i % sleepEverySendPacket == 0))
            {
                TimeHelper::sleep(sendSleepTime);
            }
        }

        //udpSender->sendTo(bytes, buf.GetSize(), topic.GetDomainAddress(), topic.GetPort());


        IncCurrentPublicationID();



    }

    void Publisher::setTopic(Topic topic)
    {
        this->topic = topic;
    }

    void Publisher::IncCurrentPublicationID()
    {
        currentPublicationID++;
    }



}
