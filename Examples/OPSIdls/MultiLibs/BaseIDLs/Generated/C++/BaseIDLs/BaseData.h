//Auto generated OPS-code. DO NOT MODIFY!

#ifndef BaseIDLs_BaseData_h
#define BaseIDLs_BaseData_h

#include "OPSObject.h"
#include "ArchiverInOut.h"
#include <string>
#include <vector>



namespace BaseIDLs {


class BaseData :
	public ops::OPSObject
{
public:
   static std::string getTypeName(){return std::string("BaseIDLs.BaseData");}
	
	/// Comment for Id, Line 1 
	/// Comment for Id, Line 2 
	/// Comment for Id, Line 3 
	int Id;
	/// Comment for Name, Line 1 
	/// Comment for Name, Line 2 
	/// Comment for Name, Line 3 
	std::string Name;

    ///Default constructor.
    BaseData()
        : ops::OPSObject()
		, Id(0)
    {
        OPSObject::appendType(std::string("BaseIDLs.BaseData"));


    }
    ///Copy-constructor making full deep copy of a(n) BaseData object.
    BaseData(const BaseData& __c)
       : ops::OPSObject()
		, Id(0)
    {
        OPSObject::appendType(std::string("BaseIDLs.BaseData"));

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
		archive->inout(std::string("Id"), Id);
		archive->inout(std::string("Name"), Name);

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
		narrRet->Id = Id;
		narrRet->Name = Name;

    }

    ///Destructor: Note that all aggregated data and vectors are completely deleted.
    virtual ~BaseData(void)
    {

    }
    
};

}


#endif
