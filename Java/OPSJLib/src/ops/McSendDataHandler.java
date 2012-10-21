/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ops;

import java.io.IOException;
import java.net.InetAddress;

/**
 *
 * @author Anton Gravestam
 */
public class McSendDataHandler implements SendDataHandler
{
    private Sender sender;
    private final InetAddress sinkIP;
    

    public McSendDataHandler(Topic t, String localInterface) throws IOException
    {
        //TODO:
        sender = new MulticastSender(0, localInterface, 0, t.getOutSocketBufferSize());
        sinkIP = InetAddress.getByName(t.getDomainAddress());

    }

    public void addPublisher(Publisher pub)
    {
        // Nothing to do for multicast
    }

    public boolean removePublisher(Publisher pub)
    {
        // Nothing to do for multicast
        return true;
    }

    public synchronized boolean sendData(byte[] bytes, int size, Topic t)
    {
        int nrSegmentsNeeded = (int)(size / StaticManager.MAX_SIZE) ;
        if(size % StaticManager.MAX_SIZE != 0)
        {
            nrSegmentsNeeded ++;
        }
        for (int i = 0; i < nrSegmentsNeeded; i++)
        {
            //If this is the last element, only send the bytes that remains, otherwise send a full package.
            int sizeToSend = (i == nrSegmentsNeeded - 1) ? size - (nrSegmentsNeeded - 1) * StaticManager.MAX_SIZE : StaticManager.MAX_SIZE;
            if(!sender.sendTo(bytes, i * StaticManager.MAX_SIZE, sizeToSend, sinkIP, t.getPort()))
            {
                return false;
            }
        }
        return true;
       
    }

}
