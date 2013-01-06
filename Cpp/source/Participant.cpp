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
#include <strstream>
#include "OPSTypeDefs.h"
#include "Participant.h"
#include "SingleThreadPool.h"
//#include "MultiThreadPool.h"
#include "ReceiveDataHandler.h"
#include "ReceiveDataHandlerFactory.h"
#include "SendDataHandlerFactory.h"
#include "OPSObjectFactoryImpl.h"
#include "CommException.h"


namespace ops
{
	//static
	std::map<std::string, Participant*> Participant::instances;
	Lockable Participant::creationMutex;


	Participant* Participant::getInstance(std::string domainID_)
	{
		return getInstance(domainID_, "DEFAULT_PARTICIPANT");
	}

	Participant* Participant::getInstance(std::string domainID_, std::string participantID)
	{
		return getInstance(domainID_, participantID, "");
	}

	Participant* Participant::getInstance(std::string domainID_, std::string participantID, std::string configFile)
	{
		SafeLock lock(&creationMutex);
		if (instances.find(participantID) == instances.end()) {
			try
			{
				Participant* newInst = new Participant(domainID_, participantID, configFile);
				Domain* tDomain = newInst->getDomain();

				if (tDomain != NULL) {
					instances[participantID] = newInst;
				} else {
					delete newInst;
					return NULL;
				}
			}
			catch(...)
			{
				return NULL;
			}
		}
		return instances[participantID];
	}

	///Remove this instance from the static instance map
	void Participant::RemoveInstance()
	{
		SafeLock lock(&creationMutex);
		instances.erase(participantID);		
	}


	Participant::Participant(std::string domainID_, std::string participantID_, std::string configFile_):
		domainID(domainID_), 
		participantID(participantID_),
		keepRunning(true),
		aliveTimeout(1000),
		config(NULL),
		ioService(NULL),
		domain(NULL), 
		objectFactory(NULL),
		errorService(NULL), 
		partInfoPub(NULL),
		partInfoSub(NULL),
		receiveDataHandlerFactory(NULL),
		sendDataHandlerFactory(NULL),
		aliveDeadlineTimer(NULL),
		threadPool(NULL)
	{
		
		ioService = IOService::create();
		
		if(!ioService)
		{
			//Error, should never happen, throw?
            exceptions::CommException ex("No config on rundirectory");
			throw ex;
		}

		//Should trow?
		if (configFile_ == "") {
			// This gets a reference to a singleton instance and should NOT be deleted.
			// It may be shared between several Participants.
			config = OPSConfig::getConfig();
		} else {
			// This gets a reference to a unique instance and should eventually be deleted.
			// Note however that the getDomain() call below returns a reference to an
			// object internally in config.
			config = OPSConfig::getConfig(configFile_);
		}
		if(!config)
		{
			exceptions::CommException ex("No config on rundirectory");
			throw ex;
		}

		//Get the domain from config. Note should not be deleted, owned by config.
		domain = config->getDomain(domainID);
///TODO borde väl kolla att domain fanns i config???

		//Create a factory instance for each participant
		objectFactory = new OPSObjectFactoryImpl();
		
		// Initialize static data in partInfoData (ReceiveDataHandlerFactory() will set some more fields)
		std::string Name;
#ifdef _WIN32
		char hname[1024];
		hname[0] ='\0';
		gethostname(hname, sizeof(hname));
		std::ostrstream myStream;
		myStream << hname << " (" << _getpid() << ")" << std::ends;
		Name = myStream.str();
#endif
///TODO Linux
		partInfoData.name = Name;
        partInfoData.languageImplementation = "c++";
        partInfoData.id = participantID;
        partInfoData.domain = domainID;

		//-----------Create delegate helper classes---
		errorService = new ErrorService();
		receiveDataHandlerFactory = new ReceiveDataHandlerFactory(this);
		sendDataHandlerFactory = new SendDataHandlerFactory();
		//--------------------------------------------

///		//------------Will be created when needed------
///		partInfoPub = NULL;
///		//-------------------------------------------

		//------------Create timer for periodic events-
		aliveDeadlineTimer = DeadlineTimer::create(ioService);
		aliveDeadlineTimer->addListener(this);
		//--------------------------------------------

		//------------Create thread pool--------------
		threadPool = new SingleThreadPool();
		//threadPool = new MultiThreadPool();
		threadPool->addRunnable(this);
		threadPool->start();
		//--------------------------------------------
	}

	Participant::~Participant()
	{
		// We assume that the user has deleted all publishers and subscribers connected to this Participant.
		// We also assume that the user has cancelled eventual deadlinetimers etc. connected to the ioService.
		// We also assume that the user has unreserved() all messages that he has reserved().

		// Remove this instance from the static instance map
		RemoveInstance();

		{
			SafeLock lock(&serviceMutex);
		
			// Indicate that shutdown is in progress
			keepRunning = false;

			// We have indicated shutdown in progress. Delete the partInfoData Publisher.
			// Note that this uses our sendDataHandlerFactory.
			if (partInfoPub) delete partInfoPub;
			partInfoPub = NULL;
		}

		// Stop and delete the subscriber for partInfoData. This requires ioService to be running.
		// Note that this uses our receiveDataHandlerFactory.
		if (partInfoSub) delete partInfoSub;
		partInfoSub = NULL;

		// Now delete our send factory (TODO it does not cleanup correctly yet)
		if (sendDataHandlerFactory) delete sendDataHandlerFactory;
		sendDataHandlerFactory = NULL;

		// Our timer is required for ReceiveDataHandlers to be cleaned up so it shouldn't be stopped 
		// before receiveDataHandlerFactory is finished.
		// Wait until receiveDataHandlerFactory has no more cleanup to do
		while (!receiveDataHandlerFactory->cleanUpDone()) {
			Sleep(1);
		}

		// Now stop and delete our timer (NOTE requires ioService to be running).
		// If the timer is in the callback, the delete will wait for it to finish and then the object is deleted.
		if (aliveDeadlineTimer) delete aliveDeadlineTimer; 
		aliveDeadlineTimer = NULL;

		// Now time to delete our rceive factory
		if (receiveDataHandlerFactory) delete receiveDataHandlerFactory;
		receiveDataHandlerFactory = NULL;

		// There should now not be anything left requiring the ioService to be running.

		// Then we request the IO Service to stop the processing (it's running on the threadpool).
		// The stop() call will not block, it just signals that we want it to finish as soon as possible.
		if (ioService) ioService->stop();

		// Now we delete the threadpool, which will wait for the thread(s) to finish 
		if (threadPool) delete threadPool;
		threadPool = NULL;

		// Now when the threads are gone, it's safe to delete the rest of our objects 
		if (objectFactory) delete objectFactory;
		if (errorService) delete errorService;
		// All objects connected to this ioservice should now be deleted, so it should be safe to delete it
		if (ioService) delete ioService;
	}

	ops::Topic Participant::createParticipantInfoTopic()
	{
///		ops::Topic infoTopic("ops.bit.ParticipantInfoTopic", 9494, "ops.ParticipantInfoData", domain->getDomainAddress());
		ops::Topic infoTopic("ops.bit.ParticipantInfoTopic", domain->getMetaDataMcPort(), "ops.ParticipantInfoData", domain->getDomainAddress());
		infoTopic.setDomainID(domainID);
		infoTopic.setParticipantID(participantID);
		infoTopic.setTransport(Topic::TRANSPORT_MC);
		
		return infoTopic;
	}

	//Report an error via the participants ErrorService
	void Participant::reportError(Error* err)
	{
		errorService->report(err);
	}

	//Report an error via all participants ErrorServices
	void Participant::reportStaticError(Error* err)
	{
		std::map<std::string, Participant*>::iterator it = instances.begin();
		while(it !=instances.end())
		{
			it->second->getErrorService()->report(err);
			it++;
		}
	}

	// This will be called by our threadpool (started in the constructor())
	void Participant::run()
	{
		// Start our timer. Calls onNewEvent(Notifier<int>* sender, int message) on timeout
		aliveDeadlineTimer->start(aliveTimeout);
		ioService->run();	
	}

	// Called on aliveDeadlineTimer timeouts
	void Participant::onNewEvent(Notifier<int>* sender, int message)
	{
		SafeLock lock(&serviceMutex);
		receiveDataHandlerFactory->cleanUpReceiveDataHandlers();

		if (keepRunning) {
			// Create the meta data publisher if user hasn't disabled it for the domain.
			// The meta data publisher is only necessary if we have topics using transport UDP.
			if ( (partInfoPub == NULL) && (domain->getMetaDataMcPort() > 0) )
			{
				partInfoPub = new Publisher(createParticipantInfoTopic());
			}
			if (partInfoPub) {
				SafeLock lock(&partInfoDataMutex);
				partInfoPub->writeOPSObject(&partInfoData);
			}
		}

		// Start a new timeout
		aliveDeadlineTimer->start(aliveTimeout);
	}

	void Participant::setUdpTransportInfo(std::string ip, int port)
	{
		SafeLock lock(&partInfoDataMutex);
		partInfoData.ip = ip;
		partInfoData.mc_udp_port = port;
	}

	void Participant::addTypeSupport(ops::SerializableFactory* typeSupport)
	{
		objectFactory->add(typeSupport);
	}

	Topic Participant::createTopic(std::string name)
	{
		Topic topic = domain->getTopic(name);
		topic.setParticipantID(participantID);
		topic.setDomainID(domainID);
		topic.participant = this;		
		
		return topic;
	}

	///Deprecated, use getErrorService()->addListener instead. Add a listener for OPS core reported Errors
	void Participant::addListener(Listener<Error*>* listener)
	{
		errorService->addListener(listener);
	}

	///Deprecated, use getErrorService()->removeListener instead. Remove a listener for OPS core reported Errors
	void Participant::removeListener(Listener<Error*>* listener)
	{
		errorService->removeListener(listener);
	}

	bool Participant::hasPublisherOn(std::string topicName)
	{
		///TODO
		return true;
	}

	ReceiveDataHandler* Participant::getReceiveDataHandler(Topic top)
	{
		ReceiveDataHandler* result = receiveDataHandlerFactory->getReceiveDataHandler(top, this);
		if (result) {
			SafeLock lock(&partInfoDataMutex);
			//Need to add topic to partInfoData.subscribeTopics (TODO ref count if same topic??)
            partInfoData.subscribeTopics.push_back(TopicInfoData(top));
		}
		return result;
		
	}

	void Participant::releaseReceiveDataHandler(Topic top)
	{
		receiveDataHandlerFactory->releaseReceiveDataHandler(top, this);

		SafeLock lock(&partInfoDataMutex);
		// Remove topic from partInfoData.subscribeTopics (TODO the same topic, ref count?)
		std::vector<TopicInfoData>::iterator it;
		for (it = partInfoData.subscribeTopics.begin(); it != partInfoData.subscribeTopics.end(); it++) {
			if (it->name == top.getName()) {
				partInfoData.subscribeTopics.erase(it);
				break;
			}
		}
	}

	//TODO: Delegate to factory class
	SendDataHandler* Participant::getSendDataHandler(Topic top)
	{
		return sendDataHandlerFactory->getSendDataHandler(top, this);
	}

	//TODO: Delegate to factory class
	void Participant::releaseSendDataHandler(Topic top)
	{
		sendDataHandlerFactory->releaseSendDataHandler(top, this);
	}

}
