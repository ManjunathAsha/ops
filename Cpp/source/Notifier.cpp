#ifdef NOT_USED_ANYMORE

#include "OPSTypeDefs.h"
#include "DataNotifier.h"

namespace ops
{
    
    DataNotifier::~DataNotifier()
    {
        listeners.clear();
    }
    
	void DataNotifier::notifyNewEvent()
    {
        for(unsigned int i = 0; i < listeners.size() ; i++)
        {
			listeners[i]->onNewEvent(this);
        }
    }
	void DataNotifier::addDataListener(DataListener* listener)
    {
        listeners.push_back(listener);
    }
    
}

#endif // NOT_USED_ANYMORE
