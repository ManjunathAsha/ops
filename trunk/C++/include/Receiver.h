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

#ifndef ops_ReceiverH
#define ops_ReceiverH

#include <string>
#include "Notifier.h"
#include "IOService.h" 
#include "BytesSizePair.h"


namespace ops
{
	class Receiver : public Notifier<BytesSizePair>
	{
	public:
		virtual ~Receiver() {}

		static Receiver* create(std::string ip, int bindPort, IOService* ioService, std::string localInterface = "0.0.0.0", int inSocketBufferSize = 16000000);
		//void setReceiveBuffer(char* bytes, int bufSize);
		virtual void asynchWait(char* bytes, int size) = 0;
		virtual void stop() = 0;

		
	};
}
#endif