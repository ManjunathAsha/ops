//Auto generated OPS-code. !DO NOT MODIFY!

package TestAll;

import ops.CommException;
import ops.Publisher;
import ops.OPSObject;
import ops.Topic;
import ops.AckData;


public class ChildDataPublisher extends Publisher 
{
    public ChildDataPublisher(Topic<ChildData> t) 
    {
        super(t);
    }
    public void write(ChildData o) 
    {
        super.write(o);
    }    
}