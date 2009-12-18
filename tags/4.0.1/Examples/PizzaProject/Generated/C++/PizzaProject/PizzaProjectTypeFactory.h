/**
 *
 * OPS generated code, DO NOT MODIFY!
 */
#ifndef PizzaProject_PizzaProjectTypeFactory_h
#define PizzaProject_PizzaProjectTypeFactory_h

#include "SerializableFactory.h"
#include <string>

#include "pizza/CapricosaData.h"
#include "pizza/VessuvioData.h"
#include "pizza/PizzaData.h"
#include "pizza/special/LHCData.h"
#include "pizza/special/Cheese.h"
#include "pizza/special/ExtraAllt.h"


namespace PizzaProject {


class PizzaProjectTypeFactory : public ops::SerializableFactory
{
public:
    ops::Serializable* create(std::string& type)
    {
		if(type == "pizza.CapricosaData")
		{
			return new pizza::CapricosaData();
		}
		if(type == "pizza.VessuvioData")
		{
			return new pizza::VessuvioData();
		}
		if(type == "pizza.PizzaData")
		{
			return new pizza::PizzaData();
		}
		if(type == "pizza.special.LHCData")
		{
			return new pizza::special::LHCData();
		}
		if(type == "pizza.special.Cheese")
		{
			return new pizza::special::Cheese();
		}
		if(type == "pizza.special.ExtraAllt")
		{
			return new pizza::special::ExtraAllt();
		}
		return NULL;

    }
};

}

//end namespaces

#endif