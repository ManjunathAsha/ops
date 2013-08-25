//Auto generated OPS-code. DO NOT MODIFY!

#ifndef DerivedIDLs_FooData_h
#define DerivedIDLs_FooData_h

#include "OPSObject.h"
#include "ArchiverInOut.h"
#include <string>
#include <vector>

#include "BaseIDLs/BaseData.h"


namespace DerivedIDLs {


class FooData :
	public BaseIDLs::BaseData
{
public:
   static std::string getTypeName(){return std::string("DerivedIDLs.FooData");}
	
	std::string fooString;

    ///Default constructor.
    FooData()
        : BaseIDLs::BaseData()
		
    {
        OPSObject::appendType(std::string("DerivedIDLs.FooData"));


    }
    ///Copy-constructor making full deep copy of a(n) FooData object.
    FooData(const FooData& __c)
       : BaseIDLs::BaseData()
		
    {
        OPSObject::appendType(std::string("DerivedIDLs.FooData"));

        __c.fillClone((FooData*)this);

    }
    ///Assignment operator making full deep copy of a(n) FooData object.
    FooData& operator = (const FooData& other)
    {
        other.fillClone(this);
        return *this;
    }

    ///This method acceptes an ops::ArchiverInOut visitor which will serialize or deserialize an
    ///instance of this class to a format dictated by the implementation of the ArchiverInout.
    void serialize(ops::ArchiverInOut* archive)
    {
		BaseIDLs::BaseData::serialize(archive);
		archive->inout(std::string("fooString"), fooString);

    }
    //Returns a deep copy of this object.
    virtual ops::OPSObject* clone()
    {
		FooData* ret = new FooData;
		this->fillClone(ret);
		return ret;

    }

    virtual void fillClone(ops::OPSObject* obj) const
    {
		FooData* narrRet = (FooData*)obj;
		BaseIDLs::BaseData::fillClone(narrRet);
		narrRet->fooString = fooString;

    }

    ///Destructor: Note that all aggregated data and vectors are completely deleted.
    virtual ~FooData(void)
    {

    }
    
};

}


#endif
