#include <strstream>

#include "OPSTypeDefs.h"
#include "ReceiveDataHandlerFactory.h"

#include "ReceiveDataHandler.h"
#include "UDPReceiver.h"



namespace ops
{

    ReceiveDataHandlerFactory::ReceiveDataHandlerFactory(Participant* participant)
    {
        SafeLock lock(&garbageLock);
        //------------Setup udpReceiveDataHandler-----
        Topic topic("__", 0, "__", "__"); //TODO: this is just a dummy construction, we should inject receiver int ReceiveDataHandler instead.
        topic.setParticipantID(participant->participantID);
        topic.setTransport(Topic::TRANSPORT_UDP);
        udpReceiveDataHandler = new ReceiveDataHandler(topic, participant);
        //--------------------------------------------

        participant->partInfoData.languageImplementation = "c++";
        participant->partInfoData.id = participant->participantID;
        participant->partInfoData.domain = participant->domainID;
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
            //Check if there isnt already a multicast configured ReceiveDataHandler on tops port. If not create one.
            if (multicastReceiveDataHandlerInstances.find(top.getPort()) == multicastReceiveDataHandlerInstances.end())
            {
                newReceiveDataHandler = new ReceiveDataHandler(top, participant);
                multicastReceiveDataHandlerInstances[top.getPort()] = newReceiveDataHandler;
			} else {
				newReceiveDataHandler = multicastReceiveDataHandlerInstances[top.getPort()];
			}
            participant->partInfoData.subscribeTopics.push_back(TopicInfoData(top));
            receiveDataHandlerInstances[key] = newReceiveDataHandler;
            return multicastReceiveDataHandlerInstances[top.getPort()];
        }
        else if (top.getTransport() == Topic::TRANSPORT_TCP)
        {
            ReceiveDataHandler* newReceiveDataHandler = NULL;
            //Check if there isnt already a tcp configured ReceiveDataHandler on tops port. If not create one.
            if (tcpReceiveDataHandlerInstances.find(top.getPort()) == tcpReceiveDataHandlerInstances.end())
            {
                newReceiveDataHandler = new ReceiveDataHandler(top, participant);
                tcpReceiveDataHandlerInstances[top.getPort()] = newReceiveDataHandler;
			} else {
                newReceiveDataHandler = tcpReceiveDataHandlerInstances[top.getPort()];
			}
            participant->partInfoData.subscribeTopics.push_back(TopicInfoData(top));
            receiveDataHandlerInstances[key] = newReceiveDataHandler;
            return tcpReceiveDataHandlerInstances[top.getPort()];
        }
        else if (top.getTransport() == Topic::TRANSPORT_UDP)
        {
            participant->partInfoData.subscribeTopics.push_back(TopicInfoData(top));
            receiveDataHandlerInstances[key] = udpReceiveDataHandler;
            return udpReceiveDataHandler;
        }
        else //For now we can not handle more transports
        {
            //Signal an error by returning NULL.
            participant->getErrorService()->report(&BasicError("ReceiveDataHandlerFactory", "getReceiveDataHandler", "Creation of ReceiveDataHandler failed. Topic = " + top.getName()));
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
                ///LA
                topHandler->stop();
                ///LA
                garbageReceiveDataHandlers.push_back(topHandler);
                if (top.getTransport() == Topic::TRANSPORT_MC)
                {
                    multicastReceiveDataHandlerInstances.erase(multicastReceiveDataHandlerInstances.find(top.getPort()));
                }
                else if (top.getTransport() == Topic::TRANSPORT_TCP)
                {
                    tcpReceiveDataHandlerInstances.erase(tcpReceiveDataHandlerInstances.find(top.getPort()));
                }

            }
        }

    }//end releaseReceiveDataHandler

    void ReceiveDataHandlerFactory::cleanUpReceiveDataHandlers()
    {
        SafeLock lock(&garbageLock);
        ///LA
        for (int i = garbageReceiveDataHandlers.size() - 1; i >= 0; i--)
        {
            if (garbageReceiveDataHandlers[i]->numReservedMessages() == 0)
            {
                delete garbageReceiveDataHandlers[i];
                std::vector<ReceiveDataHandler*>::iterator iter = garbageReceiveDataHandlers.begin() + i;
                garbageReceiveDataHandlers.erase(iter);
            }
        }
        ////for(unsigned int i = 0; i < garbageReceiveDataHandlers.size(); i++)
        ////{
        ////	garbageReceiveDataHandlers[i]->stop();
        ////	delete garbageReceiveDataHandlers[i];
        ////}
        ////garbageReceiveDataHandlers.clear();
        ///LA
    }

    ReceiveDataHandlerFactory::~ReceiveDataHandlerFactory()
    {
        delete udpReceiveDataHandler;
///not used        delete udpRec;
    }


}
