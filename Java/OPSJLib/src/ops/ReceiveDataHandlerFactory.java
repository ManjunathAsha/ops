/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ops;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Anton Gravestam
 */
class ReceiveDataHandlerFactory
{
    private HashMap<String, ReceiveDataHandler> receiveDataHandlers = new HashMap<String, ReceiveDataHandler>();

    // Since topics can use the same port for transports multicast & tcp, or
    // use transport udp which always use a single ReceiveDataHandler,
    // we need to return the same ReceiveDataHandler in these cases.
    // Make a key with the transport info that uniquely defines the receiver.
    private String makeKey(Topic top)
    {
        if (top.getTransport().equals(Topic.TRANSPORT_UDP))
        {
            return top.getTransport();
        }
        else
        {
            return top.getTransport() + "::" + top.getDomainAddress() + "::" + top.getPort();
        }
    }

    ReceiveDataHandler getReceiveDataHandler(Topic top, Participant participant)
    {
        // In the case that we use the same port for several topics, we need to find the receiver for the transport::address::port used
        String key = makeKey(top);

        if (receiveDataHandlers.containsKey(key))
        {
            ReceiveDataHandler rdh = receiveDataHandlers.get(key);

            // Check if any of the topics have a sample size larger than MAX_SIZE
            // This will lead to a problem when using the same port, if samples becomes > MAX_SIZE
            if ((rdh.getSampleMaxSize() > StaticManager.MAX_SIZE) || (top.getSampleMaxSize() > StaticManager.MAX_SIZE))
            {
                Logger.getLogger(ReceiveDataHandlerFactory.class.getName()).log(
                        Level.WARNING, 
                        "Same port ({0}) is used with Topics with ''sampleMaxSize'' > {1}",
                        new Object[]{top.getPort(), StaticManager.MAX_SIZE});
            }
            return rdh;
        }

        if(top.getTransport().equals(Topic.TRANSPORT_MC) || top.getTransport().equals(Topic.TRANSPORT_TCP))
        {
            receiveDataHandlers.put(key, new ReceiveDataHandler(top, participant, ReceiverFactory.createReceiver(top, participant.getDomain().getLocalInterface())));
            return receiveDataHandlers.get(key);
        }
        else if (top.getTransport().equals(Topic.TRANSPORT_UDP))
        {
//TODO            IReceiver rec = ReceiverFactory.CreateReceiver(top, participant.getDomain().GetLocalInterface());
//            ReceiveDataHandlers.Add(key, new ReceiveDataHandler(top, participant, rec));
//
//            participant.setUdpTransportInfo(((UdpReceiver)rec).IP, ((UdpReceiver)rec).Port);
//
//            return receiveDataHandlers.get(key);
        }
        return null;
    }

    /// Protection is not needed since all calls go through the participant which is synched
    public void ReleaseReceiveDataHandler(Topic top, Participant participant)
    {
        // In the case that we use the same port for several topics, we need to find the receiver for the transport::address::port used
        String key = makeKey(top);

        if (receiveDataHandlers.containsKey(key))
        {
            ReceiveDataHandler rdh = receiveDataHandlers.get(key);

            if (rdh.getNrOfSubscribers() == 0)
            {
                receiveDataHandlers.remove(key);

                if (rdh.getTransport().equals(Topic.TRANSPORT_UDP))
                {
                    participant.setUdpTransportInfo("", 0);
                }
            }
        }
    }

}
