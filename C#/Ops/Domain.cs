///////////////////////////////////////////////////////////
//  Domain.cs
//  Implementation of the Class Domain
//  Created on:      12-nov-2011 09:25:30
//  Author:
///////////////////////////////////////////////////////////

using System.Collections.Generic;

namespace Ops 
{
	public class Domain : OPSObject 
    {
		private string domainAddress = "";
		private string domainID = "";
		private string localInterface = "0.0.0.0";
		protected List<Topic> topics = new List<Topic>();

        public Domain()
        {
            AppendType("Domain");
        }
        

        public Topic GetTopic(string name)
        {
            foreach (Topic topic in topics)
            {
                if (topic.GetName().Equals(name))
                {
                    if (topic.GetDomainAddress().Equals(""))
                    {
                        topic.SetDomainAddress(domainAddress);
                    }
                    return topic;
                }
            }
            return null;
        }

        public override void Serialize(IArchiverInOut archive)
        {
            domainAddress = archive.Inout("domainAddress", domainAddress);
            domainID = archive.Inout("domainID", domainID);
            topics = (List<Topic>)archive.InoutSerializableList("topics", topics);
            localInterface = archive.Inout("localInterface", localInterface);
        }

        public List<Topic> GetTopics() 
        {
            foreach (Topic topic in topics) 
            {
                if (topic.GetDomainAddress().Equals("")) 
                {
                    topic.SetDomainAddress(domainAddress);
                }
            }
            return topics;
        }

         public bool ExistTopic(string name) 
         {
            foreach (Topic topic in topics) 
            {
                if (topic.GetName().Equals(name)) 
                {
                    return true;
                }
            }
            return false;
        }

        public string GetDomainAddress() 
        {
            return this.domainAddress;
        }

        public string GetDomainID() 
        {
            return this.domainID;
        }

        public string GetLocalInterface() 
        {
            return this.localInterface;
        }

        public void SetDomainAddress(string domainAddress) 
        {
            this.domainAddress = domainAddress;
        }

        public void SetDomainID(string domainID) 
        {
            this.domainID = domainID;
        }

        public void SetLocalInterface(string localInterface) 
        {
            this.localInterface = localInterface;
        }

	}

}