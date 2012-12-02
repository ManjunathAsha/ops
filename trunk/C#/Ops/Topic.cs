///////////////////////////////////////////////////////////
//  Topic.cs
//  Implementation of the Class Topic
//  Created on:      12-nov-2011 09:25:37
//  Author:
///////////////////////////////////////////////////////////

namespace Ops 
{
	public class Topic : OPSObject 
    {
		private string domainAddress = "";
		private string domainID;
		private int inSocketBufferSize;
		private string name = "";
		private int outSocketBufferSize;
		private string participantID;
		private int port = -1;
		private int replyPort = 0;
        private int sampleMaxSize = Globals.MAX_SEGMENT_SIZE;
		private string transport;
		public static readonly string TRANSPORT_MC = "multicast";
		public static readonly string TRANSPORT_TCP = "tcp";
		public static readonly string TRANSPORT_UDP = "udp";
		private string typeID = "";


        // Creates a new instance of Topic
        public Topic(string name, int port, string typeID, string domainAddress)
        {
            this.name = name;
            this.port = port;
            this.typeID = typeID;
            this.domainAddress = domainAddress;
            this.sampleMaxSize = Globals.MAX_SEGMENT_SIZE;
            AppendType("Topic");
        }

        public Topic()
        {
            AppendType("Topic");
        }

        public string GetDomainAddress()
        {
            return this.domainAddress;
        }

        public void SetDomainAddress(string domainAddress)
        {
            this.domainAddress = domainAddress;
        }

        public override string ToString()
        {
            return this.name;
        }

        public int GetReplyPort()
        {
            return this.replyPort;
        }

        public int GetPort()
        {
            return this.port;
        }

        public string GetName()
        {
            return this.name;
        }

        public string GetTypeID()
        {
            return this.typeID;
        }

        protected void SetReplyPort(int replyPort)
        {
            this.replyPort = replyPort;
        }

        public void SetTypeID(string typeID)
        {
            this.typeID = typeID;
        }

        public void SetPort(int port)
        {
            this.port = port;
        }

        public void SetName(string name)
        {
            this.name = name;
        }

        public string GetDomainID()
        {
            return this.domainID;
        }

        public string GetParticipantID()
        {
            return this.participantID;
        }

        public int GetSampleMaxSize()
        {
            return this.sampleMaxSize;
        }

        public void SetSampleMaxSize(int size)
        {
            if (size < Globals.MAX_SEGMENT_SIZE)
            {
                this.sampleMaxSize = Globals.MAX_SEGMENT_SIZE;
            } else
            {
                this.sampleMaxSize = size;
            }
        }

        public override void Serialize(IArchiverInOut archive)
        {
            base.Serialize(archive);
            this.name = archive.Inout("name", name);
            this.typeID = archive.Inout("dataType", typeID);
            this.port = archive.Inout("port", port);
            this.domainAddress = archive.Inout("address", domainAddress);

            this.outSocketBufferSize = archive.Inout("outSocketBufferSize", outSocketBufferSize);
            this.inSocketBufferSize = archive.Inout("inSocketBufferSize", inSocketBufferSize);

            int tSampleMaxSize = GetSampleMaxSize();
            tSampleMaxSize = archive.Inout("sampleMaxSize", tSampleMaxSize);
            SetSampleMaxSize(tSampleMaxSize);

            this.transport = archive.Inout("transport", transport);
            if (this.transport.Equals(""))
            {
                this.transport = TRANSPORT_MC;
            }
        }

        public void SetDomainID(string domainID)
        {
            this.domainID = domainID;
        }

        public void SetParticipantID(string participantID)
        {
            this.participantID = participantID;
        }

        public int GetInSocketBufferSize()
        {
            return this.inSocketBufferSize;
        }

        public void SetInSocketBufferSize(int inSocketBufferSize)
        {
            this.inSocketBufferSize = inSocketBufferSize;
        }

        public int GetOutSocketBufferSize()
        {
            return this.outSocketBufferSize;
        }

        public void SetOutSocketBufferSize(int outSocketBufferSize)
        {
            this.outSocketBufferSize = outSocketBufferSize;
        }

        public string GetTransport()
        {
            return this.transport;
        }

        public void SetTransport(string transport)
        {
            this.transport = transport;
        }

	}

}