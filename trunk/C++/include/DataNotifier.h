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

#ifndef DataNotifier_h
#define DataNotifier_h
#include <vector>
#include "DataListener.h"
#include "OPSObject.h"

namespace ops
{
    ///class which in the conjunction with DataListener forms an implementation of the
    ///observer GoF-pattern. classes extending this class extends an interface to which
    ///DataListeners can register their interest to be notified when new OPSObjects are available.
    class DataNotifier
    {
    private:
        
        
    protected:
		///Vector that holds pointers to the DataListeners
        std::vector<DataListener*> listeners;

        ///Called by subclasses that wishes to notify its listeners of the arrival of new data.
		void notifyNewData();
    public:
        
        
        ///Register a DataListener
        void addDataListener(DataListener* listener);
        
        //Destructor:
        ~DataNotifier();
    };
}
#endif
