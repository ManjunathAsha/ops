#ifndef DerivedIDLsFooDataSubscriber_h
#define DerivedIDLsFooDataSubscriber_h

#include "Subscriber.h"
#include "Topic.h"
#include "OPSObject.h"
#include "FooData.h"


namespace DerivedIDLs {



class FooDataSubscriber : public ops::Subscriber
{

public:
    FooDataSubscriber(ops::Topic t)
        : ops::Subscriber(t)
    {

    }

    bool getData(FooData* d)
    {
        if(!data) return false;
        aquireMessageLock();
		data->fillClone(d);
		releaseMessageLock();
        return true;
    }

    FooData* getTypedDataReference()
    {
        return (FooData*)getDataReference();
    }

    ~FooDataSubscriber(void)
    {

    }
private:
    FooData narrowedData;

};


}


#endif
