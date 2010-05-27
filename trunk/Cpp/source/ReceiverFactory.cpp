
#include "OPSTypeDefs.h"
#include "ReceiverFactory.h"
#include "Domain.h"
#include "BasicError.h"


namespace ops
{

    Receiver* ReceiverFactory::getReceiver(Topic& top, Participant* participant)
    {
        Receiver* receiver = NULL;

        Domain* mcDomain = participant->getDomain();
        IOService* ioService = participant->getIOService();

        //This shopuld never happen, logg an internal error and return NULL;
        if (mcDomain == NULL || ioService == NULL)
        {
            participant->getErrorService()->report(&BasicError("ReceiverFactory", "getReceiver", "Unexpected error, mcDomain or ioServide == NULL"));
            return receiver;
        }

        if (top.getTransport() == Topic::TRANSPORT_MC)
        {
            receiver = Receiver::create(top.getDomainAddress(), top.getPort(), ioService, mcDomain->getLocalInterface(), top.getInSocketBufferSize());
        }
        else if (top.getTransport() == Topic::TRANSPORT_TCP)
        {
            receiver = Receiver::createTCPClient(top.getDomainAddress(), top.getPort(), ioService);
        }
        else if (top.getTransport() == Topic::TRANSPORT_UDP)
        {
            receiver = Receiver::createUDPReceiver(0, ioService);
        }
        return receiver;
    }



}
