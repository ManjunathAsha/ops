// PizzaTest.cpp : Defines the entry point for the console application.
//

#ifdef _WIN32
#include "stdafx.h"
#include <windows.h>
#include <conio.h>
#endif

#include <stdio.h>
#include <stdlib.h>
#include <sstream>
#include <process.h>

#include <ops.h>
#include "pizza/PizzaData.h"
#include "pizza/PizzaDataSubscriber.h"
#include "pizza/PizzaDataPublisher.h"
#include "pizza/VessuvioData.h"
#include "pizza/VessuvioDataSubscriber.h"
#include "pizza/VessuvioDataPublisher.h"

#include "PizzaProject/PizzaProjectTypeFactory.h"

#ifdef _WIN32
#include "SdsSystemTime.h"
#endif

#undef USE_MESSAGE_HEADER

#ifndef _WIN32
#include <time.h>

__int64 getNow()
{
    struct timespec ts;
    memset(&ts, 0, sizeof(ts));
    clock_gettime(CLOCK_REALTIME, &ts);
    return ((1000 * ts.tv_sec) + (ts.tv_nsec / 1000000));
}

#include <stdio.h>
#include <sys/select.h>
#include <sys/ioctl.h> 
#include <termios.h>

int _kbhit() {
    static const int STDIN = 0;
    static bool initialized = false;

    if (! initialized) {
        // Use termios to turn off line buffering
        termios term;
        tcgetattr(STDIN, &term);
        term.c_lflag &= ~ICANON;
        tcsetattr(STDIN, TCSANOW, &term);
        setbuf(stdin, NULL);
        initialized = true;
    }

    int bytesWaiting;
    ioctl(STDIN, FIONREAD, &bytesWaiting);
    return bytesWaiting;
}
#else
__int64 getNow()
{
    return (__int64)timeGetTime();
}
#endif

template <class DataType>
class CHelperListener
{
public:
	virtual void onData(ops::Subscriber* sub, DataType* data) = 0;
};

class IHelper
{
public:
	virtual bool HasPublisher() = 0;
	virtual bool HasSubscriber() = 0;
	virtual void CreatePublisher(ops::Participant* part, std::string topicName) = 0;
	virtual void DeletePublisher(bool doLog = true) = 0;
	virtual void StartPublisher() = 0;
	virtual void StopPublisher() = 0;
	virtual void Write() = 0;
	virtual void CreateSubscriber(ops::Participant* part, std::string topicName) = 0;
	virtual void DeleteSubscriber(bool doLog = true) = 0;
	virtual void StartSubscriber() = 0;
	virtual void StopSubscriber() = 0;
	virtual void SetDeadlineQos(__int64 timeoutMs) = 0;
};

template <class DataType, class DataTypePublisher, class DataTypeSubscriber>
class CHelper : public IHelper, ops::DataListener, ops::DeadlineMissedListener
{
private:
	CHelperListener<DataType>* client;
	ops::Publisher* pub;
	ops::Subscriber* sub;
	__int64 expectedPubId;

public:
	DataType data;

	CHelper(CHelperListener<DataType>* client):
		pub(NULL), sub(NULL), expectedPubId(-1)
	{
		this->client = client;
	}

	~CHelper()
	{
		DeletePublisher(false);
		DeleteSubscriber(false);
	}

	bool HasPublisher() { return pub != NULL; }
	bool HasSubscriber() { return sub != NULL; }

	void CreatePublisher(ops::Participant* part, std::string topicName)
	{
		if (pub) {
			std::cout << "Publisher already exist for topic " << pub->getTopic().getName() << std::endl;
		} else {
			try {
				//Create topic, might throw ops::NoSuchTopicException
				ops::Topic topic = part->createTopic(topicName);

				std::cout << "Created topic " << topic.getName() << 
					" [" << topic.getTransport() << 
					"::" << topic.getDomainAddress() <<
					"::" << topic.getPort() << 
					"] " << std::endl;

				//Create a publisher on that topic
				pub = new DataTypePublisher(topic);

				std::ostringstream myStream;
#ifdef _WIN32
				myStream << " Win(" << _getpid() << ")" << std::ends;
#else
				myStream << " Linux(" << getpid() << ")" << std::ends;
#endif
				pub->setName("C++Test " + myStream.str());
			}
			catch (...) {
				std::cout << "Requested topic '" << topicName << "' does not exist!!" << std::endl;
			}
		}
	}

	void DeletePublisher(bool doLog = true)
	{
		if (pub) {
			std::cout << "Deleting publisher for topic " << pub->getTopic().getName() << std::endl;
			//pub->stop();
			delete pub; 
			pub = NULL;
		} else {
			if (doLog) std::cout << "Publisher must be created first!!" << std::endl;
		}
	}

	void StartPublisher()
	{
		if (pub) {
			pub->start();
		} else {
			std::cout << "Publisher must be created first!!" << std::endl;
		}
	}

	void StopPublisher()
	{
		if (pub) {
			pub->stop();
		} else {
			std::cout << "Publisher must be created first!!" << std::endl;
		}
	}

	void Write()
	{
		if (pub) {
			pub->writeOPSObject(&data);
		} else {
			std::cout << "Publisher must be created first!!" << std::endl;
		}
	}

	void CreateSubscriber(ops::Participant* part, std::string topicName)
	{
		if (sub) {
			std::cout << "Subscriber already exist for topic " << sub->getTopic().getName() << std::endl;
		} else {
			try {
				//Create topic, might throw ops::NoSuchTopicException
				ops::Topic topic = part->createTopic(topicName);

				std::cout << "Created topic " << topic.getName() << 
					" [" << topic.getTransport() << 
					"::" << topic.getDomainAddress() <<
					"::" << topic.getPort() << 
					"] " << std::endl;

				//Create a subscriber on that topic.
				sub = new DataTypeSubscriber(topic);
				sub->addDataListener(this);
				sub->deadlineMissedEvent.addDeadlineMissedListener(this);
				sub->start();
			}
			catch (...) {
				std::cout << "Requested topic '" << topicName << "' does not exist!!" << std::endl;
			}
		}
	}

	void DeleteSubscriber(bool doLog = true)
	{
		if (sub) {
			std::cout << "Deleting subscriber for topic " << sub->getTopic().getName() << std::endl;
			sub->stop();
			delete sub; 
			sub = NULL;
		} else {
			if (doLog) std::cout << "Subscriber must be created first!!" << std::endl;
		}
	}

	void StartSubscriber()
	{
		if (sub) {
			std::cout << "Starting subscriber for topic " << sub->getTopic().getName() << std::endl;
			sub->start();
		} else {
			std::cout << "Subscriber must be created first!!" << std::endl;
		}

	}

	void StopSubscriber()
	{
		if (sub) {
			std::cout << "Stoping subscriber for topic " << sub->getTopic().getName() << std::endl;
			sub->stop();
		} else {
			std::cout << "Subscriber must be created first!!" << std::endl;
		}
	}

	void SetDeadlineQos(__int64 timeoutMs)
	{
		if (sub) {
			std::cout << "Setting deadlineQos to " << timeoutMs << " [ms] for topic " << sub->getTopic().getName() << std::endl;
			sub->setDeadlineQoS(timeoutMs);		
		} else {
			std::cout << "Subscriber must be created first!!" << std::endl;
		}
	}

	///Override from ops::DataListener, called whenever new data arrives.
	void onNewData(ops::DataNotifier* subscriber)
	{
		if(subscriber == sub)
		{
			// Check if we have lost any messages. We use the publicationID and that works as long as
			// it is the same publisher sending us messages.
			ops::OPSMessage* newMess = sub->getMessage();
			if (expectedPubId >= 0) {
				if (expectedPubId != newMess->getPublicationID()) {
					std::cout << ">>>>> Lost message for topic " << sub->getTopic().getName() << 
						". Exp.pubid: " << expectedPubId << " got: " << newMess->getPublicationID() << std::endl;
				}
			}
			expectedPubId = newMess->getPublicationID() + 1;
			client->onData(sub, (DataType*)newMess->getData());
		}
	}

	///Override from ops::DeadlineMissedListener, called if no new data has arrived within deadlineQoS.
	void onDeadlineMissed(ops::DeadlineMissedEvent* evt)
	{
		std::cout << "Deadline Missed for topic " << sub->getTopic().getName() << std::endl;
	}

};

typedef CHelper<pizza::PizzaData, pizza::PizzaDataPublisher, pizza::PizzaDataSubscriber> TPizzaHelper;
typedef CHelper<pizza::VessuvioData, pizza::VessuvioDataPublisher, pizza::VessuvioDataSubscriber> TVessuvioHelper;

struct ItemInfo {
	std::string Domain;
	std::string TopicName;
	std::string TypeName;

	bool selected;
	IHelper* helper;
	ops::Participant* part;

	ItemInfo(std::string dom, std::string top, std::string typ) 
	{
		Domain = dom;
		TopicName = top;
		TypeName = typ;
		helper = NULL;
		part = NULL;
		selected = false;
	};
};
std::vector<ItemInfo*> ItemInfoList;
	

class MyListener : public CHelperListener<pizza::PizzaData>, public CHelperListener<pizza::VessuvioData>
{
public:
	void onData(ops::Subscriber* sub, pizza::PizzaData* data)
    {
		std::cout << 
			"[Topic: " << sub->getTopic().getName() << 
			"] Pizza:: Cheese: " << data->cheese << 
			",  Tomato sauce: " + data->tomatoSauce << std::endl;
    }

	void onData(ops::Subscriber* sub, pizza::VessuvioData* data)
    {
		std::cout << 
			"[Topic: " << sub->getTopic().getName() << 
			"] Vessuvio:: Cheese: " << data->cheese << 
			",  Tomato sauce: " << data->tomatoSauce << 
			", Ham length: " << data->ham.size() << std::endl;
    }
};

static int NumVessuvioBytes = 0;
static std::string FillerStr("");
__int64 sendPeriod = 1000;

void WriteToAllSelected()
{
	static __int64 Counter = 0;

	for (unsigned int i = 0; i < ItemInfoList.size(); i++) {
		ItemInfo* info = ItemInfoList[i];
		if (!info->selected) continue;
		std::stringstream str;
		str << Counter << std::ends;
		std::string CounterStr(str.str());
		Counter++;
		if (info->TypeName == pizza::PizzaData::getTypeName()) {
			TPizzaHelper* hlp = (TPizzaHelper*)info->helper;
			hlp->data.cheese = "Pizza from C++: " + CounterStr;
#ifdef USE_MESSAGE_HEADER
			hlp->data.systemTime = sds::sdsSystemTime();
#endif
		}
		if (info->TypeName == pizza::VessuvioData::getTypeName()) {
			TVessuvioHelper* hlp = (TVessuvioHelper*)info->helper;
			hlp->data.cheese = "Vessuvio from C++: " + CounterStr;
			hlp->data.ham = FillerStr;
#ifdef USE_MESSAGE_HEADER
			hlp->data.systemTime = sds::sdsSystemTime();
#endif
		}
		info->helper->Write();
	}
}

void menu()
{
	std::cout << "" << std::endl;
	for (unsigned int i = 0; i < ItemInfoList.size(); i++) {
            ItemInfo* ii = ItemInfoList[i];
		std::cout << "\t " << i << 
			" " << 
		(ii->helper->HasPublisher() ? "P" : " ") << 
		(ii->helper->HasSubscriber() ? "S" : " ") << 
		(ii->selected ? "*" : " ") << 
			" " <<
		ii->Domain << "::" << ii->TopicName << std::endl;
	}

	std::cout << "" << std::endl;
	std::cout << "\t PC    Create Publishers" << std::endl;
	std::cout << "\t PD    Delete Publishers" << std::endl;
	std::cout << "\t PS    Start Publishers" << std::endl;
	std::cout << "\t PT    Stop Publishers" << std::endl;
	std::cout << "\t SC    Create Subscriber" << std::endl;
	std::cout << "\t SD    Delete Subscriber" << std::endl;
	std::cout << "\t SS    Start Subscriber" << std::endl;
	std::cout << "\t ST    Stop Subscriber" << std::endl;
	std::cout << "\t L num Set num Vessuvio Bytes [" << NumVessuvioBytes << "]" << std::endl;
	std::cout << "\t T ms  Set deadline timeout [ms]" << std::endl;
	std::cout << "\t V ms  Set send period [ms] [" << sendPeriod << "]" << std::endl;
	std::cout << "\t A     Start/Stop periodical Write with set period" << std::endl;
	std::cout << "\t W     Write data" << std::endl;
	std::cout << "\t X     Exit program" << std::endl;
}

int main(int argc, char**argv)
{
#ifdef _WIN32
	// --------------------------------------------------------------------
	// Try to set timer resolution to 1 ms 
	#define TARGET_RESOLUTION 1         // 1-millisecond target resolution

	TIMECAPS tc;
	UINT     wTimerRes = TARGET_RESOLUTION;

	if (timeGetDevCaps(&tc, sizeof(TIMECAPS)) == TIMERR_NOERROR) {
		wTimerRes = min(max(tc.wPeriodMin, TARGET_RESOLUTION), tc.wPeriodMax);
	}
	timeBeginPeriod(wTimerRes); 

	sds::sdsSystemTimeInit();
#endif

	// --------------------------------------------------------------------
	MyListener myListener;

	// Setup the InfoItem list (TODO take from ops_config.xml)
	ItemInfoList.push_back(new ItemInfo("PizzaDomain", "PizzaTopic", "pizza.PizzaData"));
	ItemInfoList.push_back(new ItemInfo("PizzaDomain", "VessuvioTopic", "pizza.VessuvioData"));

	ItemInfoList.push_back(new ItemInfo("PizzaDomain", "PizzaTopic2", "pizza.PizzaData"));
	ItemInfoList.push_back(new ItemInfo("PizzaDomain", "VessuvioTopic2", "pizza.VessuvioData"));

	ItemInfoList.push_back(new ItemInfo("PizzaDomain", "TcpPizzaTopic", "pizza.PizzaData"));
	ItemInfoList.push_back(new ItemInfo("PizzaDomain", "TcpVessuvioTopic", "pizza.VessuvioData"));

	ItemInfoList.push_back(new ItemInfo("PizzaDomain", "TcpPizzaTopic2", "pizza.PizzaData"));
	ItemInfoList.push_back(new ItemInfo("PizzaDomain", "TcpVessuvioTopic2", "pizza.VessuvioData"));

	ItemInfoList.push_back(new ItemInfo("PizzaDomain", "UdpPizzaTopic", "pizza.PizzaData"));
	ItemInfoList.push_back(new ItemInfo("PizzaDomain", "UdpVessuvioTopic", "pizza.VessuvioData"));

	ItemInfoList.push_back(new ItemInfo("OtherPizzaDomain", "OtherPizzaTopic", "pizza.PizzaData"));
	ItemInfoList.push_back(new ItemInfo("OtherPizzaDomain", "OtherVessuvioTopic", "pizza.VessuvioData"));

	// Create participants
	// NOTE that the second parameter (participantID) must be different for the two participant instances
	ops::Participant* participant = ops::Participant::getInstance("PizzaDomain", "PizzaDomain");
        if (participant == NULL) {
            std::cout << "Failed to create Participant. Missing ops_config.xml ??" << std::endl;
            exit(-1);
        }
	participant->addTypeSupport(new PizzaProject::PizzaProjectTypeFactory());
	
	ops::Participant* otherParticipant = ops::Participant::getInstance("OtherPizzaDomain", "OtherPizzaDomain");
	otherParticipant->addTypeSupport(new PizzaProject::PizzaProjectTypeFactory());

	// Add error errorwriters to catch interna ops errors
	ops::ErrorWriter* errorWriter = new ops::ErrorWriter(std::cout);
	participant->getErrorService()->addListener(errorWriter);

	ops::ErrorWriter* errorWriter2 = new ops::ErrorWriter(std::cout);
	otherParticipant->getErrorService()->addListener(errorWriter2);

	// Finish up our ItemInfo's
	for (unsigned int idx = 0; idx < ItemInfoList.size(); idx++) {
		ItemInfo* info = ItemInfoList[idx];

		// Create helper
		if (info->TypeName == pizza::PizzaData::getTypeName()) {
			info->helper = new TPizzaHelper(&myListener);
		}
		if (info->TypeName == pizza::VessuvioData::getTypeName()) {
			info->helper = new TVessuvioHelper(&myListener);
		}

		// Set participant
		if (info->Domain == "PizzaDomain") {
			info->part = participant;
		}
		if (info->Domain == "OtherPizzaDomain") {
			info->part = otherParticipant;
		}
	}

	ItemInfo* ii = ItemInfoList[0]; 
        ii->selected = true;

	bool doExit = false;
	bool doPeriodicSend = false;

	menu();

	__int64 nextSendTime = (__int64)getNow() + sendPeriod;

	while (!doExit) {
		std::cout << std::endl << " (? = menu) > ";

		// Repeated sends
		if (doPeriodicSend) {
			while (!_kbhit()) {
				__int64 now = (__int64)getNow();
				if (now >= nextSendTime) {
					// write
					WriteToAllSelected();
					// Calc next time to send
					nextSendTime = now + sendPeriod;
				}
#ifdef _WIN32
				Sleep(1);			
#else
                                usleep(1000);
#endif
			}
		}

		typedef enum {NONE, PUB, SUB} TFunction;

		TFunction func = NONE;

		char buffer[1024];
		char* ptr = fgets(buffer, sizeof(buffer), stdin);
		std::string line(buffer);

		// trim start
		std::string::size_type idx = line.find_first_not_of(" \t");
		if (idx == std::string::npos) continue;
		if (idx > 0) line.erase(0, idx);
		if (line.size() == 0) continue;

		char ch = line[0];
		line.erase(0, 1);

		if ((ch == 'p') || (ch == 'P')) func = PUB;
		if ((ch == 's') || (ch == 'S')) func = SUB;

		if (func != NONE) {
			if (line.size() == 0) {
				std::cout << "ERROR: Expected character after '" << ch << "'" << std::endl;
				continue;
			}
			ch = line[0];			
			line.erase(0, 1);
		}

		int num = 0;
		if ((ch >= '0') && (ch <= '9')) {
			num = ch - '0';
			if (line.size() > 0) {
				ch = line[0];
				if ((ch >= '0') && (ch <= '9')) {
					num = (10 * num) + (ch - '0');
					line.erase(0, 1);
				}
			}
			ch = '0';
			if (num >= (int)ItemInfoList.size()) {
				std::cout << "ERROR: Index to large. Max = " << ItemInfoList.size() << std::endl;
				continue;
			}
		}

		// trim start
		idx = line.find_first_not_of(" \t");
		if (idx != std::string::npos) {
			if (idx > 0) line.erase(0, idx);
		}

                ItemInfo* ii = ItemInfoList[num];
                
		switch (ch) {
			case '?':
				menu();
				break;

			case '0':
				ii->selected = !ii->selected;
				break;

			case 'a':
			case 'A':
				doPeriodicSend = !doPeriodicSend;
				break;

			case 'c':
			case 'C':
				for (unsigned int i = 0; i < ItemInfoList.size(); i++) {
					ItemInfo* info = ItemInfoList[i];
					if (!info->selected) continue;
					if (func == PUB) {
						info->helper->CreatePublisher(info->part, info->TopicName);
					} else if (func == SUB) {
						info->helper->CreateSubscriber(info->part, info->TopicName);
					}
				}
				break;

			case 'd':
			case 'D':
				for (unsigned int i = 0; i < ItemInfoList.size(); i++) {
					ItemInfo* info = ItemInfoList[i];
					if (!info->selected) continue;
					if (func == PUB) {
						info->helper->DeletePublisher();
					} else if (func == SUB) {
						info->helper->DeleteSubscriber();
					}
				}
				break;

			case 's':
			case 'S':
				for (unsigned int i = 0; i < ItemInfoList.size(); i++) {
					ItemInfo* info = ItemInfoList[i];
					if (!info->selected) continue;
					if (func == PUB) {
						info->helper->StartPublisher();
					} else if (func == SUB) {
						info->helper->StartSubscriber();
					}
				}
				break;

			case 't':
			case 'T':
				for (unsigned int i = 0; i < ItemInfoList.size(); i++) {
					ItemInfo* info = ItemInfoList[i];
					if (!info->selected) continue;
					if (func == PUB) {
						info->helper->StopPublisher();
					} else if (func == SUB) {
						info->helper->StopSubscriber();
					} else {
						__int64 timeout = atoi(line.c_str());
						info->helper->SetDeadlineQos(timeout);
					}
				}
				break;

			case 'l':
			case 'L':
				{
					int num = atoi(line.c_str());
					if (num >= 0) NumVessuvioBytes = num;
					if (FillerStr.size() > (unsigned int)num) FillerStr.erase(num);
					while (FillerStr.size() < (unsigned int)num) FillerStr += " ";
					std::cout << "Length: " << FillerStr.size() << std::endl;
				}
				break;

			case 'v':
			case 'V':
				{
					int num = atoi(line.c_str());
					if (num >= 0) sendPeriod = num;
					std::cout << "sendPeriod: " << sendPeriod << std::endl;
				}
				break;

			case 'w':
			case 'W':
				WriteToAllSelected();
				break;

			case 'x':
			case 'X':
				doExit = true;	
				break;

			case 'y':
			case 'Y':
				break;
		}
	}

	// --------------------------------------------------------------------

	for (unsigned int idx = 0; idx < ItemInfoList.size(); idx++) {
                ItemInfo* ii = ItemInfoList[idx];
		if (ii->helper) delete ii->helper;
		ii->helper = NULL;
		ii->part = NULL;
		delete ItemInfoList[idx]; 
		ItemInfoList[idx] = NULL;
	}

	participant->getErrorService()->removeListener(errorWriter);
	otherParticipant->getErrorService()->removeListener(errorWriter2);

	delete errorWriter; errorWriter = NULL;
	delete errorWriter2; errorWriter2 = NULL;
	
///TODO this should be done by asking Participant to delete instances??
	delete participant;

#ifdef _WIN32
	// --------------------------------------------------------------------
	// We don't need the set resolution any more
	timeEndPeriod(wTimerRes); 
#endif
        
}


