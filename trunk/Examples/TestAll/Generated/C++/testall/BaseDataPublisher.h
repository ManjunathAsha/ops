#ifndef testall_BaseDataPublisher_h
#define testall_BaseDataPublisher_h

#include "Publisher.h"
#include "Topic.h"
#include "OPSObject.h"
#include "BaseData.h"
#include <string>

namespace testall {



class BaseDataPublisher : public ops::Publisher 
{
    
public:
    BaseDataPublisher(ops::Topic<> t)
        : ops::Publisher(t)
    {


    }
    
    ~BaseDataPublisher(void)
    {
    }
    
    void write(BaseData* data)
    {
        ops::Publisher::write(data);

    }
    /*
    ops::AckData writeReliable(BaseData* data, std::string destinationIdentity)
    {
        return ops::Publisher::writeReliable(data, destinationIdentity);

    }
    */
    

};


}


#endif