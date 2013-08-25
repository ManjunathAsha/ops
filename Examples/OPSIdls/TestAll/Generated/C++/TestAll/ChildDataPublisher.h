#ifndef TestAll_ChildDataPublisher_h
#define TestAll_ChildDataPublisher_h

#include "Publisher.h"
#include "Topic.h"
#include "OPSObject.h"
#include "ChildData.h"
#include <string>

namespace TestAll {



class ChildDataPublisher : public ops::Publisher 
{
    
public:
    ChildDataPublisher(ops::Topic t)
        : ops::Publisher(t)
    {


    }
    
    ~ChildDataPublisher(void)
    {
    }
    
    void write(ChildData* data)
    {
        ops::Publisher::write(data);

    }
    /*
    ops::AckData writeReliable(ChildData* data, std::string destinationIdentity)
    {
        return ops::Publisher::writeReliable(data, destinationIdentity);

    }
    */
    

};


}


#endif