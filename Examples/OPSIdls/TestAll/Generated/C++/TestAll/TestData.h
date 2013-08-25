//Auto generated OPS-code. DO NOT MODIFY!

#ifndef TestAll_TestData_h
#define TestAll_TestData_h

#include "OPSObject.h"
#include "ArchiverInOut.h"
#include <string>
#include <vector>



namespace TestAll {


class TestData :
	public ops::OPSObject
{
public:
   static std::string getTypeName(){return std::string("TestAll.TestData");}
	
	std::string text;
	double value;

    ///Default constructor.
    TestData()
        : ops::OPSObject()
		, value(0)
    {
        OPSObject::appendType(std::string("TestAll.TestData"));


    }
    ///Copy-constructor making full deep copy of a(n) TestData object.
    TestData(const TestData& __c)
       : ops::OPSObject()
		, value(0)
    {
        OPSObject::appendType(std::string("TestAll.TestData"));

        __c.fillClone((TestData*)this);

    }
    ///Assignment operator making full deep copy of a(n) TestData object.
    TestData& operator = (const TestData& other)
    {
        other.fillClone(this);
        return *this;
    }

    ///This method acceptes an ops::ArchiverInOut visitor which will serialize or deserialize an
    ///instance of this class to a format dictated by the implementation of the ArchiverInout.
    void serialize(ops::ArchiverInOut* archive)
    {
		ops::OPSObject::serialize(archive);
		archive->inout(std::string("text"), text);
		archive->inout(std::string("value"), value);

    }
    //Returns a deep copy of this object.
    virtual ops::OPSObject* clone()
    {
		TestData* ret = new TestData;
		this->fillClone(ret);
		return ret;

    }

    virtual void fillClone(ops::OPSObject* obj) const
    {
		TestData* narrRet = (TestData*)obj;
		ops::OPSObject::fillClone(narrRet);
		narrRet->text = text;
		narrRet->value = value;

    }

    ///Destructor: Note that all aggregated data and vectors are completely deleted.
    virtual ~TestData(void)
    {

    }
    
};

}


#endif
