/**
 *
 * OPS generated code, DO NOT MODIFY!
 */
#ifndef BaseIDLs_BaseIDLsTypeFactory_h
#define BaseIDLs_BaseIDLsTypeFactory_h

#include "SerializableFactory.h"
#include <string>

#include "BaseIDLs/BaseData.h"


namespace BaseIDLs {


class BaseIDLsTypeFactory : public ops::SerializableFactory
{
public:
    ops::Serializable* create(std::string& type)
    {
		if(type == "BaseIDLs.BaseData")
		{
			return new BaseIDLs::BaseData();
		}
		return NULL;

    }
};

}

//end namespaces

#endif