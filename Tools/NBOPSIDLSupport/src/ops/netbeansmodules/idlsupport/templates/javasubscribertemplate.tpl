//Auto generated OPS-code. !DO NOT MODIFY!

package __packageName;

import java.io.IOException;
import ops.Subscriber;
import ops.OPSObject;
import ops.Topic;
import ops.OPSTopicTypeMissmatchException;

public class __classNameSubscriber extends Subscriber 
{
    public __classNameSubscriber(Topic<__className> t) throws ops.ConfigurationException
    {
        super(t);

        participant.addTypeSupport(__className.getTypeFactory());
        
    }

    public __className getData()
    {
        return (__className)super.getData();
    }
}