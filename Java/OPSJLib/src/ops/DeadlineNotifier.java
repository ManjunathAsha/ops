/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ops;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Anton
 */
public class DeadlineNotifier extends Thread
{

    private static DeadlineNotifier theInstance;

    public static synchronized DeadlineNotifier getInstance()
    {
        if (theInstance == null)
        {
            theInstance = new DeadlineNotifier();
            theInstance.start();
            //System.out.println("Created DeadlineNotifier()");
        }
        return theInstance;
    }

    private Vector<Subscriber> subscribers = new Vector<Subscriber>();
    private volatile boolean keepRunning;

    private DeadlineNotifier()
    {
        setName("DeadlineNotifierThread");
    }

    public synchronized boolean remove(Subscriber o)
    {
   
        boolean result = subscribers.remove(o);
        if (subscribers.size() == 0)
        {
            ///We don't stop the thread for now since that results in deadlines stop working
            ///TODO fix the thread start/stop handling
            ///stopRunning();
            ///theInstance = null;
        }
        return result;
    }

    public synchronized boolean add(Subscriber e)
    {
        boolean result = subscribers.add(e);
        //System.out.println("Deadline.add() for topic: " + e.getTopic().getName() + ". Num subs: " + subscribers.size());
        return result;
    }

    public synchronized boolean contains(Subscriber o)
    {
        return subscribers.contains(o);
    }

    public void stopRunning()
    {
        keepRunning = false;
    }

    @Override
    public void run()
    {
        keepRunning = true;
        while (keepRunning)
        {
            try
            {
                synchronized (this)
                {
                    for (Subscriber subscriber : subscribers)
                    {
                        subscriber.checkDeadline();
                    }
                }
                Thread.sleep(5);
            } catch (InterruptedException ex)
            {
                Logger.getLogger(DeadlineNotifier.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
