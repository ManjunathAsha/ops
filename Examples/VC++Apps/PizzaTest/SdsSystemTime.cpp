/*

*/

#include "windows.h"
#include <string>
#include <time.h>

#include "SdsSystemTime.h"

namespace sds {

__int64	iBaseTime = 0;

// Must be called before sdsSystemTime() is used 
void sdsSystemTimeInit()
{
	SYSTEMTIME time70;
	FILETIME file70, fileNow;
	LARGE_INTEGER lTime70, lOffsetTime;
	DWORD dwTimeNow;

	time70.wDay = 1;
	time70.wDayOfWeek = 0;
	time70.wHour = 0;
	time70.wMilliseconds = 0;
	time70.wMinute = 0;
	time70.wMonth = 1;
	time70.wSecond = 0;
	time70.wYear = 1970;
	SystemTimeToFileTime(&time70, &file70);

	// Contains a 64-bit value representing the number of 100-nanosecond intervals since January 1, 1601 (UTC).
	lTime70.HighPart = file70.dwHighDateTime;
	lTime70.LowPart = file70.dwLowDateTime;

	// Synchronize
	GetSystemTimeAsFileTime(&fileNow);		// 100-nanosecond intervals
	dwTimeNow = timeGetTime();				// in [ms]

	lOffsetTime.HighPart = fileNow.dwHighDateTime;
	lOffsetTime.LowPart = fileNow.dwLowDateTime;

	// Calculate offset since 1 jan 1970 and make it 1 ns resolution
	iBaseTime = (lOffsetTime.QuadPart - lTime70.QuadPart) * 100;

	// Substract timeGetTime() offset
	iBaseTime -= msToSdsSystemTimeUnits((__int64)timeGetTime());
}

__int64 msToSdsSystemTimeUnits(__int64 timeInMs)
{
	return timeInMs * 1000000;			// [ns]
}

__int64 sdsSystemTimeUnitsToMs(__int64 timeInSdsUnits)
{
	return timeInSdsUnits / 1000000;	// [ns]
}

__int64 sdsSystemTime()
{
//old	return msToSdsSystemTimeUnits((__int64)GetTickCount());
	return iBaseTime + msToSdsSystemTimeUnits((__int64)timeGetTime());
}

std::string sdsSystemTimeToLocalTime(__int64 time)
{
	__int64 frac;
	struct tm tmTime;

	frac = time;
	time /= msToSdsSystemTimeUnits(1000);
	frac -= (time * sds::msToSdsSystemTimeUnits(1000));
	frac /= msToSdsSystemTimeUnits(1);

	// Convert to local time.
	_localtime64_s( &tmTime, &time ); 

	char tmp[256];
	sprintf_s(tmp, sizeof(tmp), "%.2d:%.2d:%.2d.%.3I64d", tmTime.tm_hour, tmTime.tm_min, tmTime.tm_sec, frac);

	return tmp;
}

}
