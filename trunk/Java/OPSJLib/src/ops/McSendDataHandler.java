/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ops;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Anton Gravestam
 */
public class McSendDataHandler implements SendDataHandler
{
    private Sender sender;
    private final InetAddress sinkIP;
    private Vector<Publisher> publishers = new Vector<Publisher>();

    public McSendDataHandler(Topic t, String localInterface) throws IOException
    {
        //TODO:
        sender = new MulticastSender(0, localInterface, 0, t.getOutSocketBufferSize());
        sinkIP = InetAddress.getByName(t.getDomainAddress());

    }

    public synchronized void addPublisher(Publisher pub)
    {
        publishers.add(pub);

        if (publishers.size() == 1)
        {
            try {
                // The first publisher
                sender.open();
            } catch (IOException ex) {
                Logger.getLogger(McSendDataHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public synchronized boolean removePublisher(Publisher pub)
    {
        boolean result = publishers.remove(pub);

        if (publishers.isEmpty())
        {
            // No more publishers
            sender.close();
        }

        return result;
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
