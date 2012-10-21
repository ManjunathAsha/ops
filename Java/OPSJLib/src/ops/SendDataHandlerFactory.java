/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ops;

import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author Anton Gravestam
 */
class SendDataHandlerFactory
{
    private HashMap<String, SendDataHandler> sendDataHandlers = new HashMap<String, SendDataHandler>();

    SendDataHandler getSendDataHandler(Topic t, Participant participant) throws CommException
    {
        // In the case that we use the same port for several topics, we need to find the sender for the transport::address::port used
        String key = t.getTransport() + "::" + t.getDomainAddress() + "::" + t.getPort();

        if (sendDataHandlers.containsKey(key))
        {
            return sendDataHandlers.get(key);
        }

        SendDataHandler sender = null;
        try
        {
            if(t.getTransport().equals(Topic.TRANSPORT_MC))
            {
                sender = new McSendDataHandler(t, participant.getDomain().getLocalInterface());
            }
            if(t.getTransport().equals(Topic.TRANSPORT_TCP))
            {
                sender = new TcpSendDataHandler(t, participant.getDomain().getLocalInterface());
            }
            if (sender != null)
            {
                sendDataHandlers.put(key, sender);
                return sender;
            }
            throw new CommException("No such Transport " + t.getTransport());
        }
        catch (IOException ex)
        {
            throw new CommException("Error creating SendDataHndler. IOException -->" + ex.getMessage());
        }
    }

}
