#ifndef DerivedIDLs_FooDataPublisher_h
#define DerivedIDLs_FooDataPublisher_h

#include "Publisher.h"
#include "Topic.h"
#include "OPSObject.h"
#include "FooData.h"
#include <string>

namespace DerivedIDLs {



class FooDataPublisher : public ops::Publisher 
{
    
public:
    FooDataPublisher(ops::Topic t)
        : ops::Publisher(t)
    {


    }
    
    ~FooDataPublisher(void)
    {
    }
    
    void write(FooData* data)
    {
        ops::Publisher::write(data);

    }
    /*
    ops::AckData writeReliable(FooData* data, std::string destinationIdentity)
    {
        return ops::Publisher::writeReliable(data, destinationIdentity);

    }
    */
    

};


}


#endif