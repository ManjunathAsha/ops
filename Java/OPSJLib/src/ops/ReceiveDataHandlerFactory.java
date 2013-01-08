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

    ReceiveDataHandler getReceiveDataHandler(Topic top, Participant participant)
    {
        // In the case that we use the same port for several topics, we need to find the receiver for the transport::address::port used
        String key = top.getTransport() + "::" + top.getDomainAddress() + "::" + top.getPort();

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
        return null;
    }

}
