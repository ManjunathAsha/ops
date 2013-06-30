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
#ifndef ops_BoostDeadlineTimerImpl_h
#define ops_BoostDeadlineTimerImpl_h

#include <boost/asio.hpp>
#include <boost/bind.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>
#include "Participant.h"
#include "BoostIOServiceImpl.h"
#include "Compatibility.h"

namespace ops
{
	class BoostDeadlineTimerImpl : public DeadlineTimer
	{
		boost::asio::deadline_timer deadlineTimer;
		// Counter to keep track of our outstanding requests, that will result in callbacks to us
		volatile long m_reqCounter;
		Lockable m_lock;
	public:
		BoostDeadlineTimerImpl(boost::asio::io_service* boostIOService) 
			: m_reqCounter(0),
			deadlineTimer(*boostIOService)
		{
		}
		virtual void start(__int64 timeout) 
		{
			//	if(deadlineTimer.expires_from_now(boost::posix_time::milliseconds(timeout)) > 0)
			//	{
			//		//Timer was canceled
			//		std::cout << "cancelling " << timeout << std::endl;
			//		deadlineTimer.async_wait(boost::bind(&BoostDeadlineTimerImpl::asynchHandleDeadlineTimeout, this, boost::asio::placeholders::error));
			//		isStarted = true;
			//	}
			//	else
			//	{
			//std::cout << "expired" << std::endl;
			deadlineTimer.cancel();
		    {
				SafeLock lock(&m_lock);
				m_reqCounter++;
			}
			deadlineTimer.expires_from_now(boost::posix_time::milliseconds(timeout));
			deadlineTimer.async_wait(boost::bind(&BoostDeadlineTimerImpl::asynchHandleDeadlineTimeout, this, boost::asio::placeholders::error));
			/*
			}/*
			/*if(!isStarted)
			{
			deadlineTimer.
			deadlineTimer.async_wait(boost::bind(&BoostDeadlineTimerImpl::asynchHandleDeadlineTimeout, this, boost::asio::placeholders::error));
			isStarted = true;
			}*/
		}
		virtual void cancel()
		{
			deadlineTimer.cancel();
		}
		void asynchHandleDeadlineTimeout(const boost::system::error_code& e)
		{
			if (e != boost::asio::error::operation_aborted)
			{
				// Timer was not cancelled, take necessary action.
				notifyNewEvent(0);
			}
			// We decrement the counter as the last thing in the callback, so we don't access the object any more 
			// in case the destructor is called and waiting for us to be finished.
		    {
				SafeLock lock(&m_lock);
				m_reqCounter--;
			}
		}
		~BoostDeadlineTimerImpl()
		{
			cancel();

			/// We must handle asynchronous callbacks that haven't finished yet.
			/// This approach works, but the recommended boost way is to use a shared pointer to the instance object
			/// between the "normal" code and the callbacks, so the callbacks can check if the object exists.
			while (m_reqCounter) {
				cancel();
				Sleep(1);
			}
			/// Take the lock as the last thing. This ensures that the lock is properly released
			/// in the callback so we finally can let the object be deleted.
			m_lock.lock();
			m_lock.unlock();	// Must also release it to not get errors from boost when deleting
		}
	};
}

#endif
