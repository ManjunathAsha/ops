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

/* 
 * File:   Transport.h
 * Author: Anton Gravestam
 *
 * Created on den 22 oktober 2008, 20:01
 */

#ifndef ops_SenderH
#define	ops_SenderH

#include "ByteBuffer.h"
#include <string>

namespace ops
{
    ///Interface used to send data

    class Sender
    {
    public:

        virtual bool sendTo(char* buf, int size, std::string address, int port) = 0;
		virtual int receiveReply(char* buf, int size) = 0;
        virtual int getPort() = 0;
        virtual std::string getAddress() = 0;

		static Sender* create();

        virtual ~Sender()
        {
        }
    private:


    };
}



#endif	/* _TRANSPORT_H */

