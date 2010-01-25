package helloworldjavaimpl;

import HelloWorld.HelloWorldTypeFactory;
import hello.HelloData;
import hello.HelloDataSubscriber;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import ops.Participant;

public class MainSubscribe
{

    public static void main(String[] args)
    {
        Participant participant = Participant.getInstance("HelloDomain");
        participant.addTypeSupport(new HelloWorldTypeFactory());

        final HelloDataSubscriber subscriber = new HelloDataSubscriber(participant.createTopic("HelloTopic"));

        subscriber.addObserver(new Observer()
        {

            public void update(Observable sub, Object o)
            {
                HelloData data = subscriber.getData();
                System.out.println("" + data.helloString);
            }
        });
        subscriber.start();

        //Just keep program alive, action takes place in update above...
        while (true)
        {
            try
            {
                Thread.sleep(100000);
            } catch (InterruptedException ex)
            {
                Logger.getLogger(MainSubscribe.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}

