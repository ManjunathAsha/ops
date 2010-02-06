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
import java.util.Vector;

/**
 *
 * @author angr
 */
public class Domain extends OPSObject
{

    private String domainAddress = "";
    private String domainID = "";
    private String localInterface = "0.0.0.0";
    protected Vector<Topic> topics = new Vector<Topic>();

    public Domain()
    {
        appendType("Domain");
    }
    

    public Topic getTopic(String name)
    {
        for (Topic topic : topics)
        {
            if(topic.getName().equals(name))
            {
                return topic;
            }
        }
        return null;
    }

    @Override
    public void serialize(ArchiverInOut archive) throws IOException
    {
        domainAddress = archive.inout("domainAddress", domainAddress);
        domainID = archive.inout("domainID", domainID);
        topics = (Vector<Topic>) archive.inoutSerializableList("topics", topics);
        localInterface = archive.inout("localInterface", localInterface);
    }


    public Vector<Topic> getTopics() {
        for (Topic topic : topics) {
            if (topic.getDomainAddress().equals("")) {
                topic.setDomainAddress(domainAddress);
            }
        }
        return topics;
    }

     public boolean existTopic(String name) {
        for (Topic topic : topics) {
            if (topic.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public String getDomainAddress() {
        return domainAddress;
    }

    public String getDomainID() {
        return domainID;
    }

    public String getLocalInterface() {
        return localInterface;
    }

    public void setDomainAddress(String domainAddress) {
        this.domainAddress = domainAddress;
    }

    public void setDomainID(String domainID) {
        this.domainID = domainID;
    }

    public void setLocalInterface(String localInterface) {
        this.localInterface = localInterface;
    }




}
