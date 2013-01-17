/*

*/

#ifndef __SdsSystemTime_H__
#define __SdsSystemTime_H__

namespace sds {

	// Returns time in SDS units (currently [ns])
	extern __int64 sdsSystemTime();
	extern __int64 msToSdsSystemTimeUnits(__int64 timeInMs);
	extern __int64 sdsSystemTimeUnitsToMs(__int64 timeInSdsUnits);
	extern void sdsSystemTimeInit();

	// Utility
	std::string sdsSystemTimeToLocalTime(__int64 time);
}

#endif // __SdsSystemTime_H__

