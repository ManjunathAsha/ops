#include <strstream>

#include "OPSTypeDefs.h"
#include "ReceiveDataHandlerFactory.h"

#include "ReceiveDataHandler.h"
#include "UDPReceiver.h"
#include "BasicError.h"


namespace ops
{

    ReceiveDataHandlerFactory::ReceiveDataHandlerFactory(Participant* participant)
    {
        SafeLock lock(&garbageLock);
        //------------Setup udpReceiveDataHandler-----
        Topic topic("__", 0, "__", "__"); //TODO: this is just a dummy construction, we should inject receiver int ReceiveDataHandler instead.
        topic.setParticipantID(participant->participantID);
        topic.setTransport(Topic::TRANSPORT_UDP);
		topic.setInSocketBufferSize(participant->getDomain()->getInSocketBufferSize());	/// Since this isn't a topic from the config file (use the domains size)
        udpReceiveDataHandler = new ReceiveDataHandler(topic, participant);
        //--------------------------------------------

        participant->partInfoData.ip = ((UDPReceiver*) udpReceiveDataHandler->getReceiver())->getAddress();
        participant->partInfoData.mc_udp_port = ((UDPReceiver*) udpReceiveDataHandler->getReceiver())->getPort();

    }

    ReceiveDataHandler* ReceiveDataHandlerFactory::getReceiveDataHandler(Topic& top, Participant* participant)
    {
		// We need to store ReceiveDataHandlers with more than just the name as key.
		// Since topics can use the same port, we need to return the same ReceiveDataHandler.
		// Make a key with the transport info that uniquely defines the receiver.
		std::ostrstream myStream;
		myStream << top.getPort() << std::ends;
		std::string key = top.getTransport() + "::" + top.getDomainAddress() + "::" + myStream.str();

        SafeLock lock(&garbageLock);
        if (receiveDataHandlerInstances.find(key) != receiveDataHandlerInstances.end())
        {
            //If we already have a ReceiveDataHandler for this topic, return it.
            return receiveDataHandlerInstances[key];
        }
        else if (top.getTransport() == Topic::TRANSPORT_MC)
        {
            ReceiveDataHandler* newReceiveDataHandler = NULL;
///            //Check if there isnt already a multicast configured ReceiveDataHandler on tops port. If not create one.
///            if (multicastReceiveDataHandlerInstances.find(top.getPort()) == multicastReceiveDataHandlerInstances.end())
///            {
                newReceiveDataHandler = new ReceiveDataHandler(top, participant);
///                multicastReceiveDataHandlerInstances[top.getPort()] = newReceiveDataHandler;
///			} else {
///				newReceiveDataHandler = multicastReceiveDataHandlerInstances[top.getPort()];
///			}
///            participant->partInfoData.subscribeTopics.push_back(TopicInfoData(top));
            receiveDataHandlerInstances[key] = newReceiveDataHandler;
            return newReceiveDataHandler;
        }
        else if (top.getTransport() == Topic::TRANSPORT_TCP)
        {
            ReceiveDataHandler* newReceiveDataHandler = NULL;
///            //Check if there isnt already a tcp configured ReceiveDataHandler on tops port. If not create one.
///            if (tcpReceiveDataHandlerInstances.find(top.getPort()) == tcpReceiveDataHandlerInstances.end())
///            {
                newReceiveDataHandler = new ReceiveDataHandler(top, participant);
///                tcpReceiveDataHandlerInstances[top.getPort()] = newReceiveDataHandler;
///			} else {
///                newReceiveDataHandler = tcpReceiveDataHandlerInstances[top.getPort()];
///			}
///            participant->partInfoData.subscribeTopics.push_back(TopicInfoData(top));
            receiveDataHandlerInstances[key] = newReceiveDataHandler;
            return newReceiveDataHandler;
        }
        else if (top.getTransport() == Topic::TRANSPORT_UDP)
        {
///            participant->partInfoData.subscribeTopics.push_back(TopicInfoData(top));
			///LA We can't put the handler in the list, since it then can be deleted, and we don't want that (only created in the constructor)
            ///LA receiveDataHandlerInstances[key] = udpReceiveDataHandler;
            return udpReceiveDataHandler;
        }
        else //For now we can not handle more transports
        {
            //Signal an error by returning NULL.
            participant->reportError(&BasicError("ReceiveDataHandlerFactory", "getReceiveDataHandler", "Creation of ReceiveDataHandler failed. Topic = " + top.getName()));
            return NULL;
        }
    }

    void ReceiveDataHandlerFactory::releaseReceiveDataHandler(Topic& top, Participant* participant)
    {
		// Make a key with the transport info that uniquely defines the receiver.
		std::ostrstream myStream;
		myStream << top.getPort() << std::ends;
		std::string key = top.getTransport() + "::" + top.getDomainAddress() + "::" + myStream.str();

		SafeLock lock(&garbageLock);
        if (receiveDataHandlerInstances.find(key) != receiveDataHandlerInstances.end())
        {
            ReceiveDataHandler* topHandler = receiveDataHandlerInstances[key];
            if (topHandler->getNrOfListeners() == 0)
            {
                //Time to mark this receiveDataHandler as garbage.
                receiveDataHandlerInstances.erase(receiveDataHandlerInstances.find(key));

                topHandler->stop();

                garbageReceiveDataHandlers.push_back(topHandler);
                //if (top.getTransport() == Topic::TRANSPORT_MC)
                //{
                //    multicastReceiveDataHandlerInstances.erase(multicastReceiveDataHandlerInstances.find(top.getPort()));
                //}
                //else if (top.getTransport() == Topic::TRANSPORT_TCP)
                //{
                //    tcpReceiveDataHandlerInstances.erase(tcpReceiveDataHandlerInstances.find(top.getPort()));
                //}
            }
        }
    }

    void ReceiveDataHandlerFactory::cleanUpReceiveDataHandlers()
    {
        SafeLock lock(&garbageLock);
        
        for (int i = garbageReceiveDataHandlers.size() - 1; i >= 0; i--)
        {
            if (garbageReceiveDataHandlers[i]->numReservedMessages() == 0)
            {
                delete garbageReceiveDataHandlers[i];
                std::vector<ReceiveDataHandler*>::iterator iter = garbageReceiveDataHandlers.begin() + i;
                garbageReceiveDataHandlers.erase(iter);
            }
        }
    }

	bool ReceiveDataHandlerFactory::cleanUpDone()
	{
        SafeLock lock(&garbageLock);
		return garbageReceiveDataHandlers.size() == 0;
	}

    ReceiveDataHandlerFactory::~ReceiveDataHandlerFactory()
    {
		///TODO make sure udpReceiveDataHandler hasn't any reserved messages
		udpReceiveDataHandler->stop();
        delete udpReceiveDataHandler;
    }


}
