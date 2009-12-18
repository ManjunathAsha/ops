/**
 *
 * OPS generated code, DO NOT MODIFY!
 */
#ifndef PizzaProject_PizzaProjectTypeFactory_h
#define PizzaProject_PizzaProjectTypeFactory_h

#include "SerializableFactory.h"
#include <string>

#include "pizza/special/ExtraAllt.h"
#include "pizza/special/Cheese.h"
#include "pizza/special/LHCData.h"
#include "pizza/PizzaData.h"
#include "pizza/VessuvioData.h"
#include "pizza/CapricosaData.h"


namespace PizzaProject {


class PizzaProjectTypeFactory : public ops::SerializableFactory
{
public:
    ops::Serializable* create(std::string& type)
    {
		if(type == "pizza.special.ExtraAllt")
		{
			return new pizza::special::ExtraAllt();
		}
		if(type == "pizza.special.Cheese")
		{
			return new pizza::special::Cheese();
		}
		if(type == "pizza.special.LHCData")
		{
			return new pizza::special::LHCData();
		}
		if(type == "pizza.PizzaData")
		{
			return new pizza::PizzaData();
		}
		if(type == "pizza.VessuvioData")
		{
			return new pizza::VessuvioData();
		}
		if(type == "pizza.CapricosaData")
		{
			return new pizza::CapricosaData();
		}
		return NULL;

    }
};

}

//end namespaces

#endif