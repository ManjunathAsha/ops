//Auto generated OPS-code. !DO NOT MODIFY!

package pizza.special;

import java.io.IOException;
import ops.Subscriber;
import ops.OPSObject;
import ops.Topic;
import ops.OPSTopicTypeMissmatchException;

public class LHCDataSubscriber extends Subscriber 
{
    public LHCDataSubscriber(Topic<LHCData> t) //throws ops.OPSTopicTypeMissmatchException
    {
        super(t);

        participant.addTypeSupport(LHCData.getTypeFactory());
        
    }

    public LHCData getData()
    {
        return (LHCData)super.getData();
    }
}