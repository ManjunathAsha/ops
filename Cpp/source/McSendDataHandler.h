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
#ifndef ops_McSendDataHandler_h
#define	ops_McSendDataHandler_h


//#include "Participant.h"
#include "SendDataHandler.h"

#include "Sender.h"
#include "OPSObject.h"


namespace ops
{

    class McSendDataHandler : public SendDataHandler
    {
    public:

        McSendDataHandler(Topic& topic, std::string localInterface, int ttl)
        {
            sender = Sender::create(localInterface, ttl, topic.getOutSocketBufferSize());
        }

		void addPublisher(void* client) 
		{
            SafeLock lock(&mutex);
			// Check that it isn't already in the list
			for (unsigned int i = 0; i < publishers.size(); i++) {
				if (publishers[i] == client) return;
			}
			// Save client in the list
			publishers.push_back(client);
			// For the first client, we open the sender
			if (publishers.size() == 1) sender->open();
		}

		void removePublisher(void* client)
		{
            SafeLock lock(&mutex);
			// Remove it from the list
			std::vector<void*>::iterator Iter;
			for (Iter = publishers.begin(); Iter != publishers.end(); Iter++) {
				if (*Iter == client) {
					publishers.erase(Iter);
					break;
				}
			}
			if (publishers.size() == 0) sender->close();
		}

        bool sendData(char* buf, int bufSize, Topic& topic)
        {
            bool result = sender->sendTo(buf, bufSize, topic.getDomainAddress(), topic.getPort());
            return result;
        }

        virtual ~McSendDataHandler()
        {
            SafeLock lock(&mutex);
            delete sender;
			sender = NULL;
        }

    private:

        Sender* sender;
        Lockable mutex;

		std::vector<void*> publishers;

    };


}

#endif
