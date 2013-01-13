/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ops;

import ops.OPSObject;
import ops.Publisher;
import ops.Subscriber;
import ops.Topic;

/**
 *
 * @author Anton
 */
public class RequestReply<ReqType extends Request, RepType extends Reply>
{

    long timeout;
    int nrResends = 1;
    Publisher publisher;
    Subscriber subscriber;
    RepType reply;
    static int reqInt = 0;
    private String key;

    public long getTimeout()
    {
        return timeout;
    }

    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }

    public int getNrResends()
    {
        return nrResends;
    }

    public void setNrResends(int nrResends)
    {
        this.nrResends = nrResends;
    }

    public RequestReply(Topic reqTopic, Topic repTopic, String key) throws CommException, ConfigurationException
    {
        publisher = new Publisher(reqTopic);
        subscriber = new Subscriber(repTopic);
        this.key = key;
        subscriber.addFilterQoSPolicy(new KeyFilterQoSPolicy(key));
        subscriber.start();
    }

    public RepType request(final ReqType request) throws Exception
    {
        reqInt ++;
        request.requestId = key + reqInt;
        reply = null;
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                reply = (RepType)subscriber.waitForNextData(timeout);
                if(!reply.requestId.equals(request.requestId))
                {
                    reply = null;
                }
            }
        });
        t.start();


        for (int i = 0; i < nrResends; i++)
        {
            publisher.writeAsOPSObject(request);
            try
            {
                t.join(timeout / nrResends);
                break;
            }
            catch(InterruptedException ex){}
        }
 
        //Reply will be null if we don't get a reply within timeout.
        return reply;
    }
}
