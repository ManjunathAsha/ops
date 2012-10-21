#ifndef ops_ReceiveDataHandlerFactory_h
#define ops_ReceiveDataHandlerFactory_h

#include "UDPReceiver.h"

namespace ops
{
    //Forward declaration..
    class ReceiveDataHandler;

    class ReceiveDataHandlerFactory
    {
    private:
        ///Receiver used to get a unigue port/id for this participant on the current machine
///not used        Receiver* udpRec;
        ReceiveDataHandler* udpReceiveDataHandler;

        ///By Singelton, one ReceiveDataHandler per Topic (name) on this Participant
        std::map<std::string, ReceiveDataHandler*> receiveDataHandlerInstances;

        ///By Singelton, one ReceiveDataHandler on multicast transport per port
        std::map<int, ReceiveDataHandler*> multicastReceiveDataHandlerInstances;

        ///By Singelton, one ReceiveDataHandler on tcp transport per port
        std::map<int, ReceiveDataHandler*> tcpReceiveDataHandlerInstances;

        ///Garbage vector for ReceiveDataHandlers, these can safely be deleted.
        std::vector<ReceiveDataHandler*> garbageReceiveDataHandlers;
        ops::Lockable garbageLock;

    public:
        ReceiveDataHandlerFactory(Participant* participant);
        ReceiveDataHandler* getReceiveDataHandler(Topic& top, Participant* participant);
        void cleanUpReceiveDataHandlers();
        void releaseReceiveDataHandler(Topic& top, Participant* participant);
        ~ReceiveDataHandlerFactory();

    };

}

#endif
