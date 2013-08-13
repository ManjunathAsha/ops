//Auto generated OPS-code. !DO NOT MODIFY!

package pizza;

import java.io.IOException;
import ops.Subscriber;
import ops.OPSObject;
import ops.Topic;
import ops.OPSTopicTypeMissmatchException;

public class VessuvioDataSubscriber extends Subscriber 
{
    public VessuvioDataSubscriber(Topic<VessuvioData> t) throws ops.ConfigurationException
    {
        super(t);

        participant.addTypeSupport(VessuvioData.getTypeFactory());
        
    }

    public VessuvioData getData()
    {
        return (VessuvioData)super.getData();
    }
}