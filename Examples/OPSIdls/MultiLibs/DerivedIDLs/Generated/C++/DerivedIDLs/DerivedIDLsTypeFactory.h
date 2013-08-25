/**
 *
 * OPS generated code, DO NOT MODIFY!
 */
#ifndef DerivedIDLs_DerivedIDLsTypeFactory_h
#define DerivedIDLs_DerivedIDLsTypeFactory_h

#include "SerializableFactory.h"
#include <string>

#include "DerivedIDLs/FooData.h"


namespace DerivedIDLs {


class DerivedIDLsTypeFactory : public ops::SerializableFactory
{
public:
    ops::Serializable* create(std::string& type)
    {
		if(type == "DerivedIDLs.FooData")
		{
			return new DerivedIDLs::FooData();
		}
		return NULL;

    }
};

}

//end namespaces

#endif