//Auto generated OPS-code. !DO NOT MODIFY!

package pizza.special;

import java.io.IOException;
import ops.Subscriber;
import ops.OPSObject;
import ops.Topic;
import ops.OPSTopicTypeMissmatchException;

public class CheeseSubscriber extends Subscriber 
{
    public CheeseSubscriber(Topic<Cheese> t) throws ops.ConfigurationException
    {
        super(t);

        participant.addTypeSupport(Cheese.getTypeFactory());
        
    }

    public Cheese getData()
    {
        return (Cheese)super.getData();
    }
}