//Auto generated OPS-code. !DO NOT MODIFY!

package TestAll;

import ops.Publisher;
import ops.OPSObject;
import ops.Topic;

public class ChildDataPublisher extends Publisher 
{
    public ChildDataPublisher(Topic<ChildData> t) throws ops.ConfigurationException
    {
        super(t);
    }
    public void write(ChildData o) 
    {
        super.write(o);
    }    
}