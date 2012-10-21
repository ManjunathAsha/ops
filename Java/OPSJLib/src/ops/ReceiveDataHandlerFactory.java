/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ops;

import java.util.HashMap;

/**
 *
 * @author Anton Gravestam
 */
class ReceiveDataHandlerFactory
{
    private HashMap<String, ReceiveDataHandler> receiveDataHandlers = new HashMap<String, ReceiveDataHandler>();

    ReceiveDataHandler getReceiveDataHandler(Topic top, Participant participant)
    {
        // In the case that we use the same port for several topics, we need to find the receiver for the transport::address::port used
        String key = top.getTransport() + "::" + top.getDomainAddress() + "::" + top.getPort();

        if(top.getTransport().equals(Topic.TRANSPORT_MC) || top.getTransport().equals(Topic.TRANSPORT_TCP))
        {
            if (!receiveDataHandlers.containsKey(key))
            {
                receiveDataHandlers.put(key, new ReceiveDataHandler(top, participant, ReceiverFactory.createReceiver(top, participant.getDomain().getLocalInterface())));

            }
            return receiveDataHandlers.get(key);
        }
        return null;

        
    }

}
