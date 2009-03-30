#ifndef ops_ByteBufferH
#define ops_ByteBufferH
#include <string>
#include <vector>
#include "Heartbeat.h"
#include "OPSObject.h"
//#include "Manager.h"

//#define NETWORK_BYTE_ORDER


namespace ops
{ 
    ///This is a utility class used to read and write the OPS IDL types to and
    ///from a byte buffer. Usage is simple, create an instance with either an empty
    ///prealocated buffer(for writing) or a buffer containing the data (reading).
    ///Use the public methods to read and write to/from the buffer.
    //TODO: Decide if this class is to be devided in two, one for read and one for write.
	class ByteBuffer
	{
    private:
        ///Buffer used to store data to be writen or read.
        char* buffer;
        ///index pointing out where in the buffer to do the next read or write.
        ///This variable is automatically incremented by the read and write  operations.
        int index;
	public:
        ///Writes the length first chars from chars to the buffer and increments index by length.
        void WriteChars(char* chars, int length);
        ///Reads length number of chars to the buffer and icrements index by length.
        void ReadChars(char* chars, int length);
	private:
		///Utility method for swaping byte order of basic types (int float etc.)
        void ByteSwap(unsigned char * b, int n);
     
    public:
        
        ///Constructor: @arg buf is the prealocated buffer to be used for reading or writing
        ByteBuffer(char* buf);
        
        ///Only valid for a ByteBuffer instance used to write data.
        ///Returns the the number of bytes containing valid data in the buffer so far.
        int GetSize();

		///Resets the internal offset pointer to 0 (zero)
		void ResetIndex();

        char* GetBuffer(){return buffer;}
        
        ///Writes the 4 bytes making up f to the buffer and increments index by 4.
        ///Byte order is swaped before writing takes place.
        void WriteFloat(float f);
        ///Writes the 4 bytes making up i to the buffer and increments index by 4.
        ///Byte order is swaped before writing takes place.
        void WriteInt(int i);
        ///Writes the 8 bytes making up l to the buffer and increments index by 8.
        ///Byte order is swaped before writing takes place.
        void WriteLong(long long l);
        ///Writes the 8 bytes making up d to the buffer and increments index by 4.
        ///Byte order is swaped before writing takes place.
        void WriteDouble(double d);
        ///Writes c to the buffer and increments index by 1.
        void WriteChar(char c);
        ///Writes s.size() followed by s to the buffer as a c-string (8-bit chars) and increments the buffer by s.size() + 4.
        void WriteString(std::string s);
        ///Writes size(o) followed by the bytes making up o to the buffer and increments index by size(o) + 4
        ///IMPORTANT: oh must be an OPSObjectHelper for sub type of o. No check that this is the case is performed.
        //void WriteOPSObject(OPSObject* o, OPSObjectHelper* oh);
        
        ///Reads size of and an OPSObject from the buffer and increments index by the first read size.
        ///oh must be an OPSObjectHelper for the corresponding type that wants to be read.
        //OPSObject* ReadOPSObject(OPSObjectHelper* oh);
        ///Reads 4 bytes from the buffer and returns them as a float. Index is increased by 4.
        ///Byte order is swaped before return.
        float ReadFloat();
        ///Reads 8 bytes from the buffer and returns them as a double. Index is increased by 8.
        ///Byte order is swaped before return.
        double ReadDouble();
        ///Reads 4 bytes from the buffer and returns them as an int. Index is increased by 4.
        ///Byte order is swaped before return.
        int ReadInt();
        ///Reads 8 bytes from the buffer and returns them as a long long (__int64). Index is increased by 8.
        ///Byte order is swaped before return.
        long long ReadLong();
        ///Reads 1 byte from the buffer and returns it as a char. Index is increased by 1.
        char ReadChar();
        ///Reads an int (length) from the buffer followed by length number of chars returned as a sdt::string. Index is increased by length + 4.
        std::string ReadString();
        //void ReadOPSObjectFields(ops::OPSObject* o);
        //void WriteOPSObjectFields(OPSObject* o);


		///Reads std::vector of corresponding type and increments index accordingly
		void ReadBooleans(std::vector<bool>& out);
		void ReadBytes(std::vector<char>& out);
		void ReadDoubles(std::vector<double>& out);
		void ReadInts(std::vector<int>& out);
		void ReadFloats(std::vector<float>& out);		
		void ReadLongs(std::vector<__int64>& out);		
		void ReadStrings(std::vector<std::string>& out);
		
		///Writes std::vector of corresponding type and increments index accordingly
		void WriteStrings(std::vector<std::string>& out);
		void WriteBooleans(std::vector<bool>& out);
		void WriteBytes(std::vector<char>& out);
		void WriteDoubles(std::vector<double>& out);
		void WriteInts(std::vector<int>& out);
		void WriteFloats(std::vector<float>& out);
		void WriteLongs(std::vector<__int64>& out);

		void writeProtocol();
		bool checkProtocol();
        //OPSObject* readDataHeader();
        //void writeDataHeader(const OPSObject* header);
        
        //Destructor does not do anything buffer should be created and deleted by user of this class.
        ~ByteBuffer(void);
    };
    
}
#endif
