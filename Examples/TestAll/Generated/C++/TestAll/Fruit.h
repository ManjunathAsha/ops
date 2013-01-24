//Auto generated OPS-code. DO NOT MODIFY!

#ifndef TestAll_Fruit_h
#define TestAll_Fruit_h

#include "OPSObject.h"
#include "ArchiverInOut.h"
#include <string>

namespace TestAll {


class Fruit :
	public ops::OPSObject
{
public:
   static std::string getTypeName(){return std::string("TestAll.Fruit");}

	const static int APPLE = 0;
	const static int BANANA = 1;
	const static int PEAR = 2;
	const static int UNDEFINED = 3;

    int value;

    ///Default constructor.
    Fruit()
        : ops::OPSObject()
    {
        OPSObject::appendType(getTypeName());
        value = 0;
    }

    ///This method acceptes an ops::ArchiverInOut visitor which will serialize or deserialize an
    ///instance of this class to a format dictated by the implementation of the ArchiverInOut.
    void serialize(ops::ArchiverInOut* archive)
    {
        ops::OPSObject::serialize(archive);
        archive->inout(std::string("value"), value);
    }



};

}


#endif
