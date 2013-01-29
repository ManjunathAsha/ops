//Auto generated OPS-code. !DO NOT MODIFY!

package TestAll;

import java.io.IOException;
import ops.Subscriber;
import ops.OPSObject;
import ops.Topic;
import ops.OPSTopicTypeMissmatchException;

public class ChildDataSubscriber extends Subscriber 
{
    public ChildDataSubscriber(Topic<ChildData> t) throws ops.ConfigurationException
    {
        super(t);

        participant.addTypeSupport(ChildData.getTypeFactory());
        
    }

    public ChildData getData()
    {
        return (ChildData)super.getData();
    }
}