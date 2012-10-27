#ifndef ops_SendDataHandlerFactory_h
#define ops_SendDataHandlerFactory_h

#include <map>
#include "Topic.h"
#include "Lockable.h"
namespace ops
{
    class SendDataHandler;
    class ParticipantInfoDataListener;
    class Participant;

    class SendDataHandlerFactory
    {
    private:
        SendDataHandler* udpSendDataHandler;

        std::map<std::string, SendDataHandler*> sendDataHandlers;

        ParticipantInfoDataListener* partInfoListener;

        Lockable mutex;

    public:
        SendDataHandlerFactory();
        SendDataHandler* getSendDataHandler(Topic& top, Participant* participant);
        void releaseSendDataHandler(Topic& top, Participant* participant);
    };

}

#endif
