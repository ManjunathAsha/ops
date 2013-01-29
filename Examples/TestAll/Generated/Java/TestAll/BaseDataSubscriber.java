//Auto generated OPS-code. !DO NOT MODIFY!

package TestAll;

import java.io.IOException;
import ops.Subscriber;
import ops.OPSObject;
import ops.Topic;
import ops.OPSTopicTypeMissmatchException;

public class BaseDataSubscriber extends Subscriber 
{
    public BaseDataSubscriber(Topic<BaseData> t) throws ops.ConfigurationException
    {
        super(t);

        participant.addTypeSupport(BaseData.getTypeFactory());
        
    }

    public BaseData getData()
    {
        return (BaseData)super.getData();
    }
}