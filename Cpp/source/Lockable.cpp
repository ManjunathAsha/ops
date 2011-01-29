#include "OPSTypeDefs.h"
///LA #include <boost/thread/mutex.hpp>
#include <boost/thread/recursive_mutex.hpp>
#include "Lockable.h"
namespace ops
{

    Lockable::Lockable()
    {
        ///LA mutex = new boost::mutex();
        mutex = new boost::recursive_mutex();
    }

    Lockable::Lockable(const Lockable& l)
    {
        ///LA mutex = new boost::mutex();
        mutex = new boost::recursive_mutex();
    }

    Lockable & Lockable::operator =(const Lockable& l)
    {
        ///LA mutex = new boost::mutex();
        mutex = new boost::recursive_mutex();
        return *this;
    }

    /*Lockable& Lockable::operator=(const Lockable& l)
    {
      CopyObj(rhs);
      return *this;
    }*/

    bool Lockable::lock()
    {
		mutex->lock();
		return true;
        /*if (mutex->lock())
        {
            return true;
        }
        return false;*/
    }

    void Lockable::unlock()
    {
        mutex->unlock();
    }

    Lockable::~Lockable()
    {
        //mutex->unlock();
        delete mutex;
    }
}
