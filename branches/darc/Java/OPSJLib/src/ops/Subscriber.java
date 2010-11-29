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

import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import ops.protocol.OPSMessage;
import ops.transport.inprocess.InProcessTransport;

/**
 *
 * @author Anton Gravestam
 */
public class Subscriber extends Observable
{

    public Event deadlineEvent = new Event();
    private Topic topic;
    private String identity = "";
    private ArrayList<FilterQoSPolicy> filterQoSPolicies = new ArrayList<FilterQoSPolicy>();
    private MessageFilterSet messageFilters = new MessageFilterSet();
    private final Object newDataEvent = new Object();
    private OPSObject data;
    private long deadlineTimeout;
    private long lastDeadlineTime;
    private long timeLastDataForTimeBase;
    private long timeBaseMinSeparationTime;
    private boolean reliable;
    private ReceiveDataHandler receiveDataHandler;
    protected Participant participant;
    private OPSMessage message;
    private final DeadlineNotifier deadlineNotifier;
    private volatile long sampleTime1;
    private volatile long sampleTime2;
    private final InProcessTransport inProcessTransport;

    public Subscriber(Topic t)
    {
        this.topic = t;
        this.participant = Participant.getInstance(topic.getDomainID(), topic.getParticipantID());
        receiveDataHandler = participant.getReceiveDataHandler(t);
        deadlineNotifier = DeadlineNotifier.getInstance();
        inProcessTransport = participant.getInProcessTransport();
    }

    public synchronized void setDeadlineQoS(long timeout)
    {
        deadlineTimeout = timeout;
    }

    public synchronized void setTimeBasedFilterQoS(long minSeparationTime)
    {
        timeBaseMinSeparationTime = minSeparationTime;
    }

    public long getTimeBaseFilterQos()
    {
        return timeBaseMinSeparationTime;
    }



    public synchronized void start()
    {

        lastDeadlineTime = System.currentTimeMillis();
        timeLastDataForTimeBase = System.currentTimeMillis();
        // Potential risk of deadlock? Start is synch add and underlying add is also synched. deadlineNotifier is singleton.
        deadlineNotifier.add(this);
        receiveDataHandler.addSubscriber(this);
        inProcessTransport.addSubscriber(this);

    }

    public synchronized boolean stop()
    {
        deadlineNotifier.remove(this);
        return receiveDataHandler.removeSubscriber(this);
    }

    public synchronized boolean isDeadlineMissed()
    {
        if (System.currentTimeMillis() - lastDeadlineTime > deadlineTimeout)
        {
            return true;
        }
        return false;
    }

    protected synchronized void checkDeadline()
    {
        if (System.currentTimeMillis() - lastDeadlineTime > deadlineTimeout)
        {
            deadlineEvent.fireEvent();
            lastDeadlineTime = System.currentTimeMillis();
        }

    }

    public synchronized boolean isReliable()
    {
        return reliable;
    }

    public synchronized String getIdentity()
    {
        return identity;
    }

    synchronized void notifyNewOPSObject(OPSObject o)
    {

        if (applyFilterQoSPolicies(o))
        {
            if (System.currentTimeMillis() - timeLastDataForTimeBase > timeBaseMinSeparationTime || timeBaseMinSeparationTime == 0)
            {
                long currentTime = System.currentTimeMillis();
                lastDeadlineTime = currentTime;
                timeLastDataForTimeBase = currentTime;

                sampleTime2 = sampleTime1;
                sampleTime1 = currentTime;
                setChanged();
                data = o;//(OPSObject) o.clone();
                notifyObservers(data);
                synchronized (newDataEvent)
                {
                    newDataEvent.notifyAll();
                }

            }
        }
    }

    public double getInboundRate()
    {
        double sampleRate = 1.0/((sampleTime1 - sampleTime2) / 1000.0);
        double fakeRate  = 1.0/((System.currentTimeMillis() - sampleTime1) / 1000.0);

        if(sampleRate < fakeRate)
        {
            return sampleRate;
        }
        else
        {
            return fakeRate;
        }
         
    }

    public OPSObject waitForNextData(long millis)
    {
        try
        {
            synchronized (newDataEvent)
            {
                newDataEvent.wait(millis);
            }
            return data;
        }
        catch (InterruptedException ex)
        {
            return null;
        }

    }

    public synchronized void addFilterQoSPolicy(FilterQoSPolicy qosPolicy)
    {
        synchronized (this)
        {
            getFilterQoSPolicies().add(qosPolicy);
        }
    }

    public synchronized void removeFilterQoSPolicy(FilterQoSPolicy qosPolicy)
    {
        synchronized (this)
        {
            getFilterQoSPolicies().remove(qosPolicy);
        }
    }

    public synchronized void notifyNewOPSMessage(OPSMessage message)
    {
        if (messageFilters.applyFilter(message))
        {
            this.message = message;
            notifyNewOPSObject(message.getData());
        }
    }

    private boolean applyFilterQoSPolicies(OPSObject o)
    {
        //throw new UnsupportedOperationException("Not yet implemented");
        for (FilterQoSPolicy filter : filterQoSPolicies)
        {
            if (!filter.applyFilter(o))
            {
                //Indicates that this data sample NOT should be delivered to the application.
                return false;
            }

        }
        return true;
    }

    public synchronized ArrayList<FilterQoSPolicy> getFilterQoSPolicies()
    {
        return filterQoSPolicies;
    }

    public synchronized OPSMessage getMessage()
    {
        return message;
    }

    protected synchronized OPSObject getData()
    {
        return data;
    }

    public synchronized void removeFilter(MessageFilter filter)
    {
        messageFilters.removeFilter(filter);
    }

    public synchronized void addFilter(MessageFilter filter)
    {
        messageFilters.addFilter(filter);
    }

    public synchronized MessageFilterSet getMessageFilters()
    {
        return messageFilters;
    }

    public Topic getTopic()
    {
        return topic;
    }

    
}
