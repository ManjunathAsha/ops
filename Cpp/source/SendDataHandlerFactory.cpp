
#include <strstream>

#include "OPSTypeDefs.h"
#include "SendDataHandlerFactory.h"
#include "SendDataHandler.h"
#include "ParticipantInfoDataListener.h"
#include "Participant.h"
#include "McSendDataHandler.h"
#include "McUdpSendDataHandler.h"
#include "TCPSendDataHandler.h"
#include "Domain.h"

namespace ops
{

	SendDataHandlerFactory::SendDataHandlerFactory()
	{	
		udpSendDataHandler = NULL;
	}

	
	SendDataHandler* SendDataHandlerFactory::getSendDataHandler(Topic& top, Participant* participant)
	{
		// We need to store SendDataHandlers with more than just the name as key.
		// Since topics can use the same port, we need to return the same SendDataHandler.
		// Make a key with the transport info that uniquely defines the receiver.
		std::ostrstream myStream;
		myStream << top.getPort() << std::ends;
		std::string key = top.getTransport() + "::" + top.getDomainAddress() + "::" + myStream.str();

		SafeLock lock(&mutex);

		if(top.getTransport() == Topic::TRANSPORT_MC)
		{
			return new McSendDataHandler(top, participant->getDomain()->getLocalInterface(), 1); //TODO: make ttl configurable.
		}
		else if(top.getTransport() == Topic::TRANSPORT_UDP)
		{
			if(udpSendDataHandler == NULL)
			{
				udpSendDataHandler = new McUdpSendDataHandler();
				partInfoListener = new ParticipantInfoDataListener(udpSendDataHandler, participant);

				participant->partInfoSub = new Subscriber(participant->createParticipantInfoTopic());
				participant->partInfoSub->addDataListener(partInfoListener);

				participant->partInfoSub->start();
			}
			return udpSendDataHandler;
		}
		else if(top.getTransport() == Topic::TRANSPORT_TCP)
		{
			if(tcpSendDataHandlers.find(key) == tcpSendDataHandlers.end() )
			{
				SendDataHandler* newSendDataHandler = new TCPSendDataHandler(top, participant->getIOService());
				tcpSendDataHandlers[key] = newSendDataHandler;
				return newSendDataHandler;
			}
			else
			{
				return tcpSendDataHandlers[key];
			}			

		}
		else
		{
			return NULL;
		}

	}
	void SendDataHandlerFactory::releaseSendDataHandler(Topic& top, Participant* participant)
	{
		SafeLock lock(&mutex);

	}


}
