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

#include "OPSTypeDefs.h"
#include "OPSConfig.h"
#include "DefaultOPSConfigImpl.h"
#include "XMLArchiverIn.h"
#include <fstream>

namespace ops
{

	OPSConfig* OPSConfig::getConfig(std::string configFile)
	{
		OPSConfig* theConfig = NULL;

		std::ifstream inStream(configFile.c_str());
		if (inStream.is_open()) {
			XMLArchiverIn archiver(inStream, "root");
			theConfig = (OPSConfig*)archiver.inout(std::string("ops_config"), theConfig);
		}

///		if (theConfig == NULL) {
///		}

		return theConfig;
	}

	OPSConfig* OPSConfig::getConfig()
	{
		static OPSConfig* theConfiguration = NULL;

		if (theConfiguration == NULL) {
			theConfiguration = getConfig("ops_config.xml");
			
			//std::ifstream inStream("ops_config.xml");
			//if(inStream.is_open())
			//{
			//	XMLArchiverIn archiver(inStream, "root");
			//	theConfiguration = (OPSConfig*)archiver.inout(std::string("ops_config"), theConfiguration);
			//}

			//if(theConfiguration == NULL)
			//	{
			//		theConfiguration = new DefaultOPSConfigImpl();
			//	}
			//}
			//else
			//{
			//	theConfiguration = new DefaultOPSConfigImpl();
			//}
			
		}
		return theConfiguration;
	}

}