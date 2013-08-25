//Auto generated OPS-code. DO NOT MODIFY!

#ifndef TestAll_BaseData_h
#define TestAll_BaseData_h

#include "OPSObject.h"
#include "ArchiverInOut.h"
#include <string>
#include <vector>



namespace TestAll {


class BaseData :
	public ops::OPSObject
{
public:
   static std::string getTypeName(){return std::string("TestAll.BaseData");}
	
	std::string baseText;

    ///Default constructor.
    BaseData()
        : ops::OPSObject()
		
    {
        OPSObject::appendType(std::string("TestAll.BaseData"));


    }
    ///Copy-constructor making full deep copy of a(n) BaseData object.
    BaseData(const BaseData& __c)
       : ops::OPSObject()
		
    {
        OPSObject::appendType(std::string("TestAll.BaseData"));

        __c.fillClone((BaseData*)this);

    }
    ///Assignment operator making full deep copy of a(n) BaseData object.
    BaseData& operator = (const BaseData& other)
    {
        other.fillClone(this);
        return *this;
    }

    ///This method acceptes an ops::ArchiverInOut visitor which will serialize or deserialize an
    ///instance of this class to a format dictated by the implementation of the ArchiverInout.
    void serialize(ops::ArchiverInOut* archive)
    {
		ops::OPSObject::serialize(archive);
		archive->inout(std::string("baseText"), baseText);

    }
    //Returns a deep copy of this object.
    virtual ops::OPSObject* clone()
    {
		BaseData* ret = new BaseData;
		this->fillClone(ret);
		return ret;

    }

    virtual void fillClone(ops::OPSObject* obj) const
    {
		BaseData* narrRet = (BaseData*)obj;
		ops::OPSObject::fillClone(narrRet);
		narrRet->baseText = baseText;

    }

    ///Destructor: Note that all aggregated data and vectors are completely deleted.
    virtual ~BaseData(void)
    {

    }
    
};

}


#endif
