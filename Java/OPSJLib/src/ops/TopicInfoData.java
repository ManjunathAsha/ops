///////////////////////////////////////////////////////////
//  TopicInfoData.java
//  Implementation of the Class TopicInfoData
//  Created on:      10-jan-2013
//  Author:
///////////////////////////////////////////////////////////

package ops;

import ops.OPSObject;
import configlib.ArchiverInOut;
import configlib.SerializableFactory;
import configlib.Serializable;
import java.io.IOException;

// NOTE. Must be kept in sync with C++
public class TopicInfoData extends OPSObject
{
    public String name;
    public String type;
    public String transport;
    public String address;
    public int port;

    // C++ version: std::vector<string> keys;
    public java.util.Vector<String> keys = new java.util.Vector<String>();

    // C++ version: //std::vector<OPSObject*> filters;
		

    public TopicInfoData()
    {
        super();
	appendType("TopicInfoData");
    }

    public TopicInfoData(Topic topic)
    {
        super();
	appendType("TopicInfoData");
	name = topic.getName();
	type = topic.getTypeID();
	transport = topic.getTransport();
	address = topic.getDomainAddress();
	port = topic.getPort();
	//keys;
    }

    public void serialize(ArchiverInOut archive) throws IOException
    {
        super.serialize(archive);

        name = archive.inout("name", name);
        type = archive.inout("type", type);
        transport = archive.inout("transport", transport);
        address = archive.inout("address", address);
        port = archive.inout("port", port);
	keys = (java.util.Vector<String>) archive.inoutStringList("keys", keys);

        //archiver->inout(std::string("filters"), filters);
    }
		
}

