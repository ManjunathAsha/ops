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

#ifndef ops_TimeHelperH
#define	ops_TimeHelperH

#include <string>


namespace ops
{
class TimeHelper
{

public:
	///Returns the current time as a number of milliseconds since Epoch 1970-01-01.
	static __int64 currentTimeMillis();
	///Sleeps the given number of milliseconds (millis).
    static void sleep(__int64 millis);
	///Returns current system time as a string to be used as user output, file names etc...
    static std::string getTimeToString();
	///Returns the current time as a number of milliseconds since Epoch 1970-01-01.
    static __int64 getEpochTime();

    const static int infinite = 0x0fffffff; 


private:


};

}
#endif
