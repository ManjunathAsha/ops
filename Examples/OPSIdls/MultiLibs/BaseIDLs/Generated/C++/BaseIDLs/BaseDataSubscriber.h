#ifndef BaseIDLsBaseDataSubscriber_h
#define BaseIDLsBaseDataSubscriber_h

#include "Subscriber.h"
#include "Topic.h"
#include "OPSObject.h"
#include "BaseData.h"


namespace BaseIDLs {



class BaseDataSubscriber : public ops::Subscriber
{

public:
    BaseDataSubscriber(ops::Topic t)
        : ops::Subscriber(t)
    {

    }

    bool getData(BaseData* d)
    {
        if(!data) return false;
        aquireMessageLock();
		data->fillClone(d);
		releaseMessageLock();
        return true;
    }

    BaseData* getTypedDataReference()
    {
        return (BaseData*)getDataReference();
    }

    ~BaseDataSubscriber(void)
    {

    }
private:
    BaseData narrowedData;

};


}


#endif
