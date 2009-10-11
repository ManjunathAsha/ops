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

#ifndef ops_OPSObjectFactoryH
#define ops_OPSObjectFactoryH

#include "SerializableCompositeFactory.h"
#include "OPSMessage.h"
#include <string>

namespace ops
{

class OPSObjectFactory : public SerializableCompositeFactory
{
    
public:
    /**
     * Create singelton instance of OPSObjectFactory.
     * @return
     */
    static OPSObjectFactory* getInstance();
    
    /**
     * Tries to construct the most specialized version of the given typeString
     */
	virtual Serializable* create(std::string& typeString) = 0;
    
	virtual ~OPSObjectFactory(){}

};
	

}

#endif