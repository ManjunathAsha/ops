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

#ifndef ops_ThreadH
#define ops_ThreadH

//Forward declaration
namespace boost{class thread;}

namespace ops
{
	

	///Abstract Thread class for other classes that wishes to be "Active".
    class Thread
    {
    public:
        
        Thread();
        ~Thread();
        int start();
		void stop();
		bool join();
		virtual void run() = 0;
		//boost::thread* GetThreadHandle();
		static void Thread::EntryPoint(void* pthis);
    
    protected:
        bool started;
		bool threadRunning;
		boost::thread* thread;
        
    };
}


#endif
