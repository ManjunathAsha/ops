///////////////////////////////////////////////////////////
//  ParticipantInfoData.java
//  Implementation of the Class ParticipantInfoData
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
public class ParticipantInfoData extends OPSObject
{
    public String name;
    public String id;
    public String domain;
    public String ip;
    public String languageImplementation;
    public String opsVersion;
    public int mc_udp_port;
    public int mc_tcp_port;

    public java.util.Vector<TopicInfoData> subscribeTopics = new java.util.Vector<TopicInfoData>();

    public java.util.Vector<TopicInfoData> publishTopics = new java.util.Vector<TopicInfoData>();

    // C++ version: std::vector<string> knownTypes;
    public java.util.Vector<String> knownTypes = new java.util.Vector<String>();

		
    public ParticipantInfoData()
    {
        super();
	appendType("ops.ParticipantInfoData");
    }

    public void serialize(ArchiverInOut archive) throws IOException
    {
	super.serialize(archive);

	name = archive.inout("name", name);
	domain = archive.inout("domain", domain);
	id = archive.inout("id", id);
	ip = archive.inout("ip", ip);
	languageImplementation = archive.inout("languageImplementation", languageImplementation);
	opsVersion = archive.inout("opsVersion", opsVersion);
	mc_udp_port = archive.inout("mc_udp_port", mc_udp_port);
	mc_tcp_port = archive.inout("mc_tcp_port", mc_tcp_port);
        /// The following requires that TopicInfoData can be created by the OPSObjectFactory. Differs from C++ & C# that has a different solution.
        subscribeTopics = (java.util.Vector<TopicInfoData>)archive.inoutSerializableList("subscribeTopics", subscribeTopics);
        publishTopics = (java.util.Vector<TopicInfoData>)archive.inoutSerializableList("publishTopics", publishTopics);
	knownTypes = (java.util.Vector<String>) archive.inoutStringList("knownTypes", knownTypes);
    }

}

