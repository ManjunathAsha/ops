#include "Reservable.h"
#include "ReferenceHandler.h"
namespace ops
{

	Reservable::Reservable():
		nrOfReservations(0)
	{
		referenceHandler = NULL;
	}
	void Reservable::setReferenceHandler(ReferenceHandler* refHandler)
	{
		referenceHandler = refHandler;
	}
	ReferenceHandler* Reservable::getReferenceHandler()
	{
		return referenceHandler;
	}
	void Reservable::reserve()
	{
		incLock.lock();
		nrOfReservations++;
		incLock.unlock();
		if(referenceHandler)
		{
			referenceHandler->onNewEvent(this, ReserveInfo(this, nrOfReservations));
		}
	}
	void Reservable::unreserve()
	{
		incLock.lock();
		nrOfReservations--;
		incLock.unlock();
		if(referenceHandler)
		{
			referenceHandler->onNewEvent(this, ReserveInfo(this, nrOfReservations));
		}
	}
	int Reservable::getNrOfReservations()
	{
		return nrOfReservations;
	}
	Reservable::~Reservable()
	{
		//TODO: Should we do a check here?
	}



}