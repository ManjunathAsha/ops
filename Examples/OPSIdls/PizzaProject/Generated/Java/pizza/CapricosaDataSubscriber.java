//Auto generated OPS-code. !DO NOT MODIFY!

package pizza;

import java.io.IOException;
import ops.Subscriber;
import ops.OPSObject;
import ops.Topic;
import ops.OPSTopicTypeMissmatchException;

public class CapricosaDataSubscriber extends Subscriber 
{
    public CapricosaDataSubscriber(Topic<CapricosaData> t) throws ops.ConfigurationException
    {
        super(t);

        participant.addTypeSupport(CapricosaData.getTypeFactory());
        
    }

    public CapricosaData getData()
    {
        return (CapricosaData)super.getData();
    }
}