/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ops;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Anton Gravestam
 */
class ReceiverFactory
{
    static Receiver createReceiver(Topic topic, String localInterface)
    {
        try
        {
            if (topic.getTransport().equals(Topic.TRANSPORT_MC))
            {
                return new MulticastReceiver(topic.getDomainAddress(), topic.getPort(), localInterface, topic.getInSocketBufferSize());
            }
            else if (topic.getTransport().equals(Topic.TRANSPORT_TCP))
            {
                return new TcpClientReceiver(topic.getDomainAddress(), topic.getPort(), topic.getInSocketBufferSize());
            }
            else if (topic.getTransport().equals(Topic.TRANSPORT_UDP))
            {
                // We need the system to allocate a port (port == 0 means let system allocate a free one)
                return new UDPReceiver(0, localInterface, topic.getInSocketBufferSize());
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(ReceiverFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
