/**
* 
* Copyright (C) 2006-2009 Anton Gravestam.
*
* This file is part of OPS (Open Publish Subscribe).
*
* OPS (Open Publish Subscribe) is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* OPS (Open Publish Subscribe) is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with OPS (Open Publish Subscribe).  If not, see <http://www.gnu.org/licenses/>.
*/

#ifndef ops_SerializableCompositeFactoryH
#define ops_SerializableCompositeFactoryH

#include "SerializableFactory.h"
#include <vector>

namespace ops
{

class SerializableCompositeFactory : SerializableFactory
{

	std::vector<SerializableFactory*> childFactories;

public:
	bool remove(SerializableFactory* o)
    {
		std::vector<SerializableFactory*>::iterator it = childFactories.begin();
		for(unsigned int i = 0; i < childFactories.size(); i++ )
		{
			if(childFactories[i] == o)
			{
				it += i;
				childFactories.erase(it);
				return true;
			}
		}
		return false;
    }

    void add(SerializableFactory* o)
    {
		return childFactories.push_back(o);
    }

	Serializable* create(std::string& type)
    {
        Serializable* obj = NULL;

		for(unsigned int i = 0; i < childFactories.size(); i++ )
		{
			obj = childFactories[i]->create(type);
			if(obj != NULL)
			{
				return obj;
			}
		}
		return obj;
    }
};

}

#endif