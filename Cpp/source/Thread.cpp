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
#include "Thread.h"
#include <boost/thread.hpp>
namespace ops
{
	Thread::Thread() : started(false), thread(NULL), threadRunning(false)
    {
	}
    
    Thread::~Thread()
    {
		if (thread) {
			thread->interrupt();
			// Wait for thread to exit before we delete it
			thread->join();
			delete thread;
		}
	}
    
    int Thread::start()
	{
		if(!started)
		{
			thread = new boost::thread(&Thread::EntryPoint, this);
			started = true;
		}
        return 0;
	}

	bool Thread::join()
	{
		if (thread) thread->join();
		return true;
    }
   /* 
	boost::thread* Thread::GetThreadHandle()
    {
		return thread;
    }*/
    
    void Thread::stop()
    {
		if (thread) thread->interrupt();
    }
    
    /*static */ void  Thread::EntryPoint(void* pthis)
    {
        Thread* pt = (Thread*)pthis;
        pt->run();
    }
}



