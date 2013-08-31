//Auto generated OPS-code. !DO NOT MODIFY!

package DerivedIDLs;

import java.io.IOException;
import ops.Subscriber;
import ops.OPSObject;
import ops.Topic;
import ops.OPSTopicTypeMissmatchException;

public class FooDataSubscriber extends Subscriber 
{
    public FooDataSubscriber(Topic<FooData> t) throws ops.ConfigurationException
    {
        super(t);

        participant.addTypeSupport(FooData.getTypeFactory());
        
    }

    public FooData getData()
    {
        return (FooData)super.getData();
    }
}