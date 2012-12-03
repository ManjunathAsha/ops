//Auto generated OPS-code. !DO NOT MODIFY!

package pizza;

import java.io.IOException;
import ops.Subscriber;
import ops.OPSObject;
import ops.Topic;
import ops.OPSTopicTypeMissmatchException;

public class PizzaDataSubscriber extends Subscriber 
{
    public PizzaDataSubscriber(Topic<PizzaData> t) //throws ops.OPSTopicTypeMissmatchException
    {
        super(t);

        participant.addTypeSupport(PizzaData.getTypeFactory());
        
    }

    public PizzaData getData()
    {
        return (PizzaData)super.getData();
    }
}