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

#ifndef ops_ReferenceHandler_h
#define ops_ReferenceHandler_h

#include <vector>
#include "Reservable.h"
#include "Lockable.h"

namespace ops
{
	class ReferenceHandler : Listener<ReserveInfo>, Lockable
	{
	public:
		ReferenceHandler()
		{
	
		}
		void addReservable(Reservable* res)
		{
			SafeLock lock(this);
			references.push_back(res);
			res->addListener(this);
		}
		void onNewEvent(Notifier<ReserveInfo>* notifier, ReserveInfo reserveInfo)
		{
			if(reserveInfo.nrOfReservations == 0)
			{
				removeReference(reserveInfo.reservable);
			}

		}
	private:
		std::vector<Reservable*> references;
		void removeReference(Reservable* reservable)
		{
			SafeLock lock(this);
			for(unsigned int i = 0; i < references.size(); i++)
			{
				if(references[i] == reservable)
				{
					if(references[i])
					{
						delete references[i];
						references[i] = NULL;
					}

					std::vector<Reservable*>::iterator p = references.begin();
					p += i;
					references.erase(p);
					return;
				}
			}

		}

	};

}

#endif