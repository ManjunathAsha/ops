/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ops;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Vector;
import java.util.HashMap;

/**
 *
 * @author Lelle
 */
public class McUdpSendDataHandler extends SendDataHandlerBase
{
    //              Topic           port
    private HashMap<String, HashMap<String, IpPortPair> > topicSinkMap = new HashMap<String,HashMap<String,IpPortPair>>();

    public McUdpSendDataHandler(int outSocketBufferSize, String localInterface) throws IOException
    {
        sender = new UDPSender(0, localInterface, outSocketBufferSize);
    }

    public synchronized boolean sendData(byte[] bytes, int size, Topic t)
    {
        boolean result = true;

        if (this.topicSinkMap.containsKey(t.getName()))
        {
            HashMap<String, IpPortPair> dict = this.topicSinkMap.get(t.getName());
            Vector<String> sinksToDelete = new Vector<String>();

            // Loop over all sinks and send data, remove items that isn't "alive".
            for (IpPortPair ipp : dict.values())
            {
                // Check if this sink is alive
                if (ipp.IsAlive())
                {
                    result &= sendData(bytes, size, ipp.ip, ipp.port);
                }
                else //Remove it.
                {
                    sinksToDelete.add(ipp.GetKey());
                }
            }
            for (String key : sinksToDelete)
            {
                dict.remove(key);
            }
        }
        return result;
    }

    public synchronized void addSink(String topic, String ip, int port) throws IOException
    {
        IpPortPair ipp = new IpPortPair(InetAddress.getByName(ip), port);

        if (this.topicSinkMap.containsKey(topic))
        {
            HashMap<String, IpPortPair> dict = this.topicSinkMap.get(topic);

            if (dict.containsKey(ipp.GetKey()))
            {
                dict.get(ipp.GetKey()).FeedWatchdog();
            }
            else
            {
                dict.put(ipp.GetKey(), ipp);
            }
        }
        else
        {
            HashMap<String, IpPortPair> dict = new HashMap<String,IpPortPair>();
            dict.put(ipp.GetKey(), ipp);
            this.topicSinkMap.put(topic, dict);
        }
    }

    private class IpPortPair
    {
        public InetAddress ip;
        public int port;

        private long lastTimeAlive;
        private long ALIVE_TIMEOUT = 3000;  // [ms]

        public IpPortPair(InetAddress ip, int port)
        {
            this.ip = ip;
            this.port = port;
            this.lastTimeAlive = System.currentTimeMillis();
        }

        public boolean IsAlive()
        {
            return (System.currentTimeMillis() - this.lastTimeAlive) < ALIVE_TIMEOUT;
        }

        public void FeedWatchdog()
        {
            this.lastTimeAlive = System.currentTimeMillis();
        }

        public String GetKey()
        {
            return "" + port;
        }
    }
    
}

