#include "OPSTypeDefs.h"
#include "ParticipantInfoDataListener.h"
#include "McUdpSendDataHandler.h"
#include "Participant.h"
#include "BasicError.h"

namespace ops
{

    ParticipantInfoDataListener::ParticipantInfoDataListener(SendDataHandler* sndDataHandler, Participant* part)
    {
        this->sendDataHandler = sndDataHandler;
        this->participant = part;
    }

    void ParticipantInfoDataListener::onNewData(DataNotifier* notifier)
    {
        Subscriber* sub = dynamic_cast<Subscriber*> (notifier);
        if (sub)
        {
            ParticipantInfoData* partInfo = dynamic_cast<ParticipantInfoData*> (sub->getMessage()->getData());
            if (partInfo)
            {
				// Is it on our domain?
				if (partInfo->domain == participant->domainID) {
	                for (unsigned int i = 0; i < partInfo->subscribeTopics.size(); i++)
		            {
			            //Do an add sink here
				        if ( (partInfo->subscribeTopics[i].transport == Topic::TRANSPORT_UDP) && participant->hasPublisherOn(partInfo->subscribeTopics[i].name) )
					    {
						    ((McUdpSendDataHandler*) sendDataHandler)->addSink(partInfo->subscribeTopics[i].name, partInfo->ip, partInfo->mc_udp_port);
						}
					}
				}
            }
            else
            {
				BasicError err("ParticipantInfoDataListener", "onNewData", "Data could not be cast as expected.");
                participant->reportError(&err);
            }
        }
        else
        {
			BasicError err("ParticipantInfoDataListener", "onNewData", "Subscriber could not be cast as expected.");
            participant->reportError(&err);
        }
    }

    ParticipantInfoDataListener::~ParticipantInfoDataListener()
    {

    }





}
