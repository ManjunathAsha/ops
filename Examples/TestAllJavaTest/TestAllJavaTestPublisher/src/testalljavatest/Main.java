/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testalljavatest;

import TestAll.ChildData;
import TestAll.ChildDataPublisher;
import java.util.logging.Level;
import java.util.logging.Logger;
import ops.CommException;
import ops.ConfigurationException;
import ops.Participant;
import ops.Topic;

/**
 * Example showing how to publish data on OPS from Java
 * @author angr
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        try
        {
            Participant participant = Participant.getInstance("TestAllDomain");
            participant.addTypeSupport(new TestAll.TestAllTypeFactory());
            Topic topic = participant.createTopic("ChildTopic");
            ChildDataPublisher pub = new ChildDataPublisher(topic);
            ChildData data = new ChildData();
            data.baseText = "Hello from Java";
            pub.setName("TestAll Java ChildPublisher");
            data.s = "TestString";
            data.b = 0;
            data.l = 1;
            data.f = 2.0f;
            data.d = 3.0;
            data.bs.add((byte) 4);
            data.is_.add(5);
            data.ls.add((long) 7);
            data.ss.add("TestString in Array.");
            for (int i = 0; i < 50; i++)
            {
                data.fs.add((float) i);
            }
            while (true)
            {
                data.i++;
                pub.write(data);
                sleep(1000);
            }
       } catch (ops.ConfigurationException ex)
       {
           Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
       }

    }

    private static void sleep(int i)
    {
        try
        {
            Thread.sleep(i);
        } catch (InterruptedException ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
