/**
 *
 * OPS generated code, DO NOT MODIFY!
 */
#ifndef TestAll_TestAllTypeFactory_h
#define TestAll_TestAllTypeFactory_h

#include "SerializableFactory.h"
#include <string>

#include "TestAll/TestData.h"
#include "TestAll/BaseData.h"
#include "TestAll/Fruit.h"
#include "TestAll/ChildData.h"


namespace TestAll {


class TestAllTypeFactory : public ops::SerializableFactory
{
public:
    ops::Serializable* create(std::string& type)
    {
		if(type == "TestAll.TestData")
		{
			return new TestAll::TestData();
		}
		if(type == "TestAll.BaseData")
		{
			return new TestAll::BaseData();
		}
		if(type == "TestAll.Fruit")
		{
			return new TestAll::Fruit();
		}
		if(type == "TestAll.ChildData")
		{
			return new TestAll::ChildData();
		}
		return NULL;

    }
};

}

//end namespaces

#endif