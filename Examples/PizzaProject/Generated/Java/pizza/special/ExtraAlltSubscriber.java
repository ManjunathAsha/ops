//Auto generated OPS-code. !DO NOT MODIFY!

package pizza.special;

import java.io.IOException;
import ops.Subscriber;
import ops.OPSObject;
import ops.Topic;
import ops.OPSTopicTypeMissmatchException;

public class ExtraAlltSubscriber extends Subscriber 
{
    public ExtraAlltSubscriber(Topic<ExtraAllt> t) throws ops.ConfigurationException
    {
        super(t);

        participant.addTypeSupport(ExtraAllt.getTypeFactory());
        
    }

    public ExtraAllt getData()
    {
        return (ExtraAllt)super.getData();
    }
}