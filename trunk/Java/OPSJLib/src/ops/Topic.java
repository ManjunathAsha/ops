/**
 *
 * Copyright (C) 2006-2009 Anton Gravestam.
 *
 * This file is part of OPS (Open Publish Subscribe).
 *
 * OPS (Open Publish Subscribe) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * OPS (Open Publish Subscribe) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OPS (Open Publish Subscribe).  If not, see <http://www.gnu.org/licenses/>.
 */
package ops;

import configlib.ArchiverInOut;
import java.io.IOException;

/**
 *
 * @author Owe
 */
public class Topic<T> extends OPSObject
{

    public static final String TRANSPORT_MC = "multicast";
    public static final String TRANSPORT_TCP = "tcp";
    public static final String TRANSPORT_UDP = "udp";
    
    private String name = "";
    private int port = -1;
    private int replyPort = 0;
    private String typeID = "";
    private String domainAddress = "";
    private int sampleMaxSize = StaticManager.MAX_SIZE;
    private String participantID;
    private String domainID;
    private int outSocketBufferSize;
    private int inSocketBufferSize;
    private String transport;

    /** Creates a new instance of Topic */
    public Topic(String name, int port, String typeID, String domainAddress)
    {
        this.name = name;
        this.port = port;
        this.typeID = typeID;
        this.domainAddress = domainAddress;
        this.sampleMaxSize = StaticManager.MAX_SIZE;
        appendType("Topic");

    }

    public Topic()
    {
        appendType("Topic");
    }

    public String getDomainAddress()
    {
        return domainAddress;
    }

    public void setDomainAddress(String domainAddress)
    {
        this.domainAddress = domainAddress;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public int getReplyPort()
    {
        return replyPort;
    }

    public int getPort()
    {
        return port;
    }

    public String getName()
    {
        return name;
    }

    public String getTypeID()
    {
        return typeID;
    }

    protected void setReplyPort(int replyPort)
    {
        this.replyPort = replyPort;
    }

    public void setTypeID(String typeID)
    {
        this.typeID = typeID;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDomainID()
    {
        return domainID;
    }

    public String getParticipantID()
    {
        return participantID;
    }

    public int getSampleMaxSize()
    {
        return sampleMaxSize;
    }

    public void setSampleMaxSize(int size)
    {
        if (size < StaticManager.MAX_SIZE)
        {
            sampleMaxSize = StaticManager.MAX_SIZE;
        } else
        {
            sampleMaxSize = size;
        }
    }

    @Override
    public void serialize(ArchiverInOut archive) throws IOException
    {
        super.serialize(archive);
        name = archive.inout("name", name);
        typeID = archive.inout("dataType", typeID);
        port = archive.inout("port", port);
        domainAddress = archive.inout("address", domainAddress);

        outSocketBufferSize = archive.inout("outSocketBufferSize", outSocketBufferSize);
        inSocketBufferSize = archive.inout("inSocketBufferSize", inSocketBufferSize);

        int tSampleMaxSize = getSampleMaxSize();
        tSampleMaxSize = archive.inout("sampleMaxSize", tSampleMaxSize);
        setSampleMaxSize(tSampleMaxSize);

        transport = archive.inout("transport", transport);
        if(transport.equals(""))
        {
            transport = TRANSPORT_MC;
        }

    }

    void setDomainID(String domainID)
    {
        this.domainID = domainID;
    }

    void setParticipantID(String participantID)
    {
        this.participantID = participantID;
    }

    public int getInSocketBufferSize()
    {
        return inSocketBufferSize;
    }

    public void setInSocketBufferSize(int inSocketBufferSize)
    {
        this.inSocketBufferSize = inSocketBufferSize;
    }

    public int getOutSocketBufferSize()
    {
        return outSocketBufferSize;
    }

    public void setOutSocketBufferSize(int outSocketBufferSize)
    {
        this.outSocketBufferSize = outSocketBufferSize;
    }

    public String getTransport()
    {
        return transport;
    }

    public void setTransport(String transport)
    {
        this.transport = transport;
    }
}
