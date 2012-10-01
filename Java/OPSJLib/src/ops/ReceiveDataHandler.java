/**
 *
 * Copyright (C) 2006-2009 Anton Gravestam.
 *
 * This file is part of OPS (Open Publish Subscribe).
 *
 * OPS (Open Publish Subscribe) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * OPS (Open Publish Subscribe) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OPS (Open Publish Subscribe).  If not, see <http://www.gnu.org/licenses/>.
 */
package ops;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import ops.archiver.OPSArchiverIn;
import ops.protocol.OPSMessage;

/**
 *
 * @author angr
 */
public class ReceiveDataHandler
{

    private boolean hasSubscribers;
    //private boolean hasPublishers;
    //private HashMap<String, MessageBuffer> messageBuffers = new HashMap<String, MessageBuffer>();
    //private Topic topic;
    private Receiver receiver;
    private Vector<Subscriber> subscribers = new Vector<Subscriber>();
    private ObserverImpl bytesListener = new ObserverImpl();
    private Participant participant;
    private int expectedFragment = 0;
    private int fragmentSize;
    private final byte[] bytes;
    private byte[] headerBytes;
    private int bytesReceived;
    //private byte[] trimmedBytes;
    //private int byteOffset = 0;
    private static int FRAGMENT_HEADER_SIZE = 14;
    private final Topic topic;
    private static int THREAD_COUNTER = 1;

    public ReceiveDataHandler(Topic t, Participant part, Receiver receiver)
    {
        topic = t;
        bytes = new byte[t.getSampleMaxSize()];
        headerBytes = new byte[FRAGMENT_HEADER_SIZE];
        //trimmedBytes = new byte[t.getSampleMaxSize()];
        participant = part;
        //topic = t;
        //MulticastDomain domain = (MulticastDomain) participant.getConfig().getDomain(topic.getDomainID());
        fragmentSize = StaticManager.MAX_SIZE;
        this.receiver = receiver; //new MulticastReceiver(t.getDomainAddress(), t.getPort(), domain.getLocalInterface(), t.getInSocketBufferSize());


    }

    public synchronized void addSubscriber(Subscriber sub)
    {
        subscribers.add(sub);

        if (!hasSubscribers)
        {
            addNewBytesEventListener();
            try {
                receiver.Open();
            } catch (IOException ex) {
                Logger.getLogger(ReceiveDataHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            hasSubscribers = true;
            setupTransportThread();
        }
    }

    public synchronized boolean removeSubscriber(Subscriber sub)
    {
        boolean result = subscribers.remove(sub);

        if (subscribers.size() == 0)
        {
            receiver.getNewBytesEvent().deleteObserver(bytesListener);
            receiver.Close();
            hasSubscribers = false;
        }

        return result;
    }

    private synchronized void onNewBytes(Integer size)
    {
        try
        {
            bytesReceived += size - headerBytes.length;

            //System.arraycopy(bytes, expectedFragment*fragmentSize, by, 0, p.getLength());
//            ReadByteBuffer readBuf = new ReadByteBuffer(bytes, expectedFragment*fragmentSize, fragmentSize);
            ReadByteBuffer readBuf = new ReadByteBuffer(headerBytes, 0, FRAGMENT_HEADER_SIZE);

            if (readBuf.checkProtocol())
            {
                int nrOfFragments = readBuf.readint();
                int currentFragment = readBuf.readint();

                if (currentFragment == (nrOfFragments - 1) && currentFragment == expectedFragment)
                {
                    //We have received a full message, let's deserialize it and send it to subscribers.
                    sendBytesToSubscribers(new ReadByteBuffer(bytes));
                    expectedFragment = 0;
                    bytesReceived = 0;

                } else
                {
                    if (currentFragment == expectedFragment)
                    {
                        expectedFragment++;
                    } else
                    {
                        //Sample will be lost here, add error handling
                        System.out.println("___________________Fragment error, sample lost__________________");
                        expectedFragment = 0;
                        bytesReceived = 0;
                    }
                }
            }

        } catch (IOException ex)
        {
            expectedFragment = 0;
        }
    }

    private void addNewBytesEventListener()
    {
        receiver.getNewBytesEvent().addObserver(bytesListener);
    }

    private void sendBytesToSubscribers(ReadByteBuffer readBuf) throws IOException
    {
        OPSArchiverIn archiverIn = new OPSArchiverIn(readBuf);
        OPSMessage message = null;

        // If the proper typesupport has not been added, the message may have content, but some fields may be null. How do we handle this
        // How do we make the user realize that he needs to add typesupport?
        message = (OPSMessage) archiverIn.inout("message", message);

        //readBuf.inBuffer.asCharBuffer().
        //System.out.println("Bytes received = " + bytesReceived);
        //System.out.println("Bytes read     = " + readBuf.position());

        if (message == null)
        {
            Logger.getLogger(ReceiveDataHandler.class.getName()).log(Level.SEVERE, "message was unexpectadly null");
            return;
        }
        if (message.getData() == null)
        {
            Logger.getLogger(ReceiveDataHandler.class.getName()).log(Level.SEVERE, "message.getData() was unexpectadly null");
            return;
        }

        calculateAndSetSpareBytes(message, readBuf, FRAGMENT_HEADER_SIZE, bytesReceived);


        //TODO: error checking
        for (Subscriber subscriber : subscribers)
        {
            try
            {
                subscriber.notifyNewOPSMessage(message);
            } catch (Throwable t)
            {
                Logger.getLogger(ReceiveDataHandler.class.getName()).log(Level.WARNING, "Exception thrown in event notification thread" + t.getMessage());
            }
        }
    }

    private void calculateAndSetSpareBytes(OPSMessage message, ReadByteBuffer readBuf, int segmentPaddingSize, int bytesReceived)
    {
        //We must calculate how many unserialized segment headers we have and substract that total header size from the size of spareBytes.
        int nrOfSerializedBytes = readBuf.position();
        int totalNrOfSegments = (int) (bytesReceived /fragmentSize);
        int nrOfSerializedSegements = (int) (nrOfSerializedBytes /fragmentSize);
        int nrOfUnserializedSegments = totalNrOfSegments - nrOfSerializedSegements;

        int nrOfSpareBytes = bytesReceived - readBuf.position() - (nrOfUnserializedSegments * segmentPaddingSize);

        if (nrOfSpareBytes > 0)
        {
            message.getData().spareBytes = new byte[nrOfSpareBytes];
            //This will read the rest of the bytes as raw bytes and put them into sparBytes field of data.
            readBuf.readBytes(message.getData().spareBytes);  
        }
    }

    private void setupTransportThread()
    {

        Thread thread = new Thread(new Runnable()
        {

            public void run()
            {
                while (hasSubscribers)
                {
                    boolean recOK = receiver.receive(headerBytes, bytes, expectedFragment * (fragmentSize - FRAGMENT_HEADER_SIZE));
                }
                System.out.println("Leaving transport thread...");

            }
        });

        thread.setName("TransportThread_" + topic.getTransport()+"_" +THREAD_COUNTER);
        THREAD_COUNTER++;
        thread.start();
    }

    private class ObserverImpl implements Observer
    {

        public void update(Observable o, Object arg)
        {
            onNewBytes((Integer) arg);
        }
    }
}
