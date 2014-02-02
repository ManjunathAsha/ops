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

#ifndef ops_NotifierH
#define ops_NotifierH
#include <vector>
#include "Listener.h"
#include "Lockable.h"

namespace ops
{
    ///class which in the conjunction with Listener forms an implementation of the
    ///observer GoF-pattern. classes extending this class extends an interface to which
    ///Listeners can register their interest to be notified when new events are available.

    template<typename ArgType>
    class Notifier
    {
    private:
        ///Vector that holds pointers to the Listeners
        std::vector<Listener<ArgType>*> listeners;

        //
        Lockable mutex;

    protected:
        ///Called by subclasses that wishes to notify its listeners of the arrival of new events.
        virtual void notifyNewEvent(ArgType arg)
        {
            // Methods addListener(), removeListener() and calling of registered callback need to be protected.
            // This also ensures that when a client returns from removeListener(), he can't be called
            // anymore and there can't be an ongoing call in his callback.
            SafeLock lock(&mutex);
            for (unsigned int i = 0; i < listeners.size(); i++)
            {
                listeners[i]->onNewEvent(this, arg);
            }
        }
    public:

        ///Register a Listener
        virtual void addListener(Listener<ArgType>* listener)
        {
            SafeLock lock(&mutex);
            listeners.push_back(listener);
        }

        virtual void removeListener(Listener<ArgType>* listener)
        {
            SafeLock lock(&mutex);
            for (unsigned int i = 0; i < listeners.size(); i++)
            {
                if (listeners[i] == listener)
                {
                    typename std::vector<Listener< ArgType> * >::iterator p;

                    p = listeners.begin();
                    p += i;
                    listeners.erase(p);
                }
            }
        }

        int getNrOfListeners()
        {
            SafeLock lock(&mutex);
            return listeners.size();
        }

        //Destructor:
        virtual ~Notifier()
        {
            SafeLock lock(&mutex);
            listeners.clear();
        }
    };
}
#endif
