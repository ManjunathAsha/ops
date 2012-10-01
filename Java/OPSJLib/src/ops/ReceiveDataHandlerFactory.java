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
    private HashMap<String, ReceiveDataHandler> multicastReceiveDataHandlers = new HashMap<String, ReceiveDataHandler>();
    private HashMap<String, ReceiveDataHandler> tcpReceiveDataHandlers = new HashMap<String, ReceiveDataHandler>();

    ReceiveDataHandler getReceiveDataHandler(Topic top, Participant participant)
    {
        // In the case that we use the same port for several topics, we need to find the receiver for the address::port used
        String key = top.getDomainAddress() + "::" + top.getPort();

        if(top.getTransport().equals(Topic.TRANSPORT_MC))
        {
            if (!multicastReceiveDataHandlers.containsKey(key))
            {
                multicastReceiveDataHandlers.put(key, new ReceiveDataHandler(top, participant, ReceiverFactory.createReceiver(top, participant.getDomain().getLocalInterface())));

            }
            return multicastReceiveDataHandlers.get(key);
        }
        if(top.getTransport().equals(Topic.TRANSPORT_TCP))
        {
            if (!tcpReceiveDataHandlers.containsKey(key))
            {
                tcpReceiveDataHandlers.put(key, new ReceiveDataHandler(top, participant, ReceiverFactory.createReceiver(top, participant.getDomain().getLocalInterface())));

            }
            return tcpReceiveDataHandlers.get(key);
            
        }
        return null;

        
    }

}
