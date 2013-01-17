// Example1.cpp : Defines the entry point for the console application.
//

//#include "stdafx.h"
#include <windows.h>	// for Sleep()

#include <ops.h>

// The following include files are created by the OPS IDL Builder
//  - From BaseData.idl
#include "TestAll/BaseData.h"
#include "TestAll/BaseDataPublisher.h"
#include "TestAll/BaseDataSubscriber.h"

//  - From ChildData.idl
#include "TestAll/ChildData.h"
#include "TestAll/ChildDataPublisher.h"
#include "TestAll/ChildDataSubscriber.h"

//  - Factory that is able to create our data objects
#include "TestAll/TestAllTypeFactory.h"

using namespace TestAll;

/// =======================================================================

void PublisherExample()
{
	// Create OPS participant to access a domain in the default configuration file "ops_config.xml" in current working directory
	// There are other calls to create a participant for a specific configuration file
	ops::Participant* participant = ops::Participant::getInstance("TestAllDomain");

	// Add our generated factory so OPS can create our data objects
	participant->addTypeSupport(new TestAll::TestAllTypeFactory());

	// Add an errorwriter instance to the participant to catch eventual internal OPS errors
	// We can easily write our own if we want to log data in another way.
	ops::ErrorWriter* errorWriter = new ops::ErrorWriter(std::cout);
	participant->addListener(errorWriter);


	// Create the topic to publish on, might throw ops::NoSuchTopicException
	// The topic must exist in the used ops configuration file
	ops::Topic topic = participant->createTopic("ChildTopic");

	std::cout << "Publishing on " << topic.getName() << 
		" [" << topic.getTransport() << 
		"::" << topic.getDomainAddress() <<
		"::" << topic.getPort() << 
		"] " << std::endl;


	// Create a publisher for ChildData
	ChildDataPublisher pub(topic);

	// It's a good practise to set a publisher name, but it's not necessary.
	pub.setName("TestAllPublisher");

	// If there are many publishers on the same topic, a key can be set to identify the specific publisher instance.
	// A subscriber can tell OPS to filter on this key.
	pub.setKey("InstanceOne");


	// Create some data to publish
	ChildData data;
	data.baseText = "Some text";
	data.l = 0;

	while (true) {
		// Change data
		data.l++;

		std::cout << "Writing ChildTopic " << data.l <<  std::endl;
		pub.write(&data);

		Sleep(1000);
	}

}

/// =======================================================================

class SubscriptionHandler : ops::DataListener, ops::DeadlineMissedListener
{
private:
	TestAll::ChildDataSubscriber* sub;
public:
	SubscriptionHandler()
	{
		// Create OPS participant to access a domain in the default configuration file "ops_config.xml" 
		// in current working directory.
		// There are other calls to create a participant for a specific configuration file
		ops::Participant* participant = ops::Participant::getInstance("TestAllDomain");

		// Add our generated factory so OPS can create our data objects
		participant->addTypeSupport(new TestAll::TestAllTypeFactory());

		// Add an errorwriter instance to the participant to catch eventual internal OPS errors
		// We can easily write our own if we want to log data in another way.
		ops::ErrorWriter* errorWriter = new ops::ErrorWriter(std::cout);
		participant->addListener(errorWriter);


		// Create the topic to subscribe to, might throw ops::NoSuchTopicException
		// The topic must exist in the used ops configuration file
		ops::Topic topic = participant->createTopic("ChildTopic");

		std::cout << "Subscribing to " << topic.getName() << 
			" [" << topic.getTransport() << 
			"::" << topic.getDomainAddress() <<
			"::" << topic.getPort() << 
			"] " << std::endl;


		// Create a subscriber listening on the topic
		sub = new ChildDataSubscriber(topic);

		// Add this class instance as listener for data, the method onNewData() will be called
		sub->addDataListener(this);

		// If you want, set a deadline with maximum allowed distance between two messages,
		// the method onDeadlineMissed() will be called if time exceeds maximum
		sub->deadlineMissedEvent.addDeadlineMissedListener(this);
		sub->setDeadlineQoS(5000);

		// If you want, add a keyfilter to just be notified with data objects with the specified key
		sub->addFilterQoSPolicy(new ops::KeyFilterQoSPolicy("InstanceOne"));

		// There are also some other filters that can be set, and we can implement our own
		//sub->setTimeBasedFilterQoS(1000);

		// Finally start the subscriber (tell it to start listening for data)
		sub->start();
	}

	// Override from ops::DataListener, called whenever new data arrives.
	void onNewData(ops::DataNotifier* subscriber)
	{
		// NOTE: It's important that we keep this callback fast, it will block
		// the receive for all topics belonging to the participant (currently a single 
		// thread for each participant instance, that does all receive handling).

		// If the class subscribes for more than one topic, all will come here
		// So check which it is
		if (subscriber == sub)
		{
			// The OPSMessage contains some metadata for the received message
			// eg. publisher name, publication id (message counter), ...
			// These may be of interrest
			ops::OPSMessage* newMess = sub->getMessage();
			
			// Get the actual data object published
			ChildData* data = (ChildData*)newMess->getData();

			// Use the data
			std::cout << "Received ChildTopic with " << data->l << std::endl;

			// NOTE that the OPSMessage instance and the data object, as default 
			// will be deleted when this callback returns. 
			// If you eg. want to buffer messages to keep the callback fast, you can
			// postpone the delete by calling "newMess->reserve()" here in the callback.
			// When you are finished with the message, call "newMess->unreserve()".
			// This will delete the message if the reserve count == 0 (ie. if the number 
			// of reserve() and unreserv() calls match.
			
		}
	}

	// Override from ops::DeadlineMissedListener, called if no new data has arrived within deadlineQoS.
	void onDeadlineMissed(ops::DeadlineMissedEvent* evt)
	{
		std::cout << "Deadline Missed!" << std::endl;
	}
};

void SubscriberExample()
{
	SubscriptionHandler handler;

	while(true) {
		Sleep(1000);
	}
}

void usage()
{
	std::cout << "" << std::endl;
	std::cout << "Usage: Example1 pub|sub" << std::endl;
	std::cout << "" << std::endl;
}

int main(int argc, char**argv)
{
	if (argc > 1) {
		std::string arg(argv[1]);
		if (arg == "pub") {
			PublisherExample();
		} else if (arg == "sub"){
			SubscriberExample();		
		} else {
			usage();
		}
	} else {
		usage();
	}
}


