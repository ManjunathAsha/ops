///////////////////////////////////////////////////////////
//  McSendDataHandler.cs
//  Implementation of the Class McSendDataHandler
//  Created on:      12-nov-2011 09:25:31
//  Author:
///////////////////////////////////////////////////////////

using System.Collections.Generic;
using System.Runtime.CompilerServices;  // Needed for the "MethodImpl" synchronization attribute

namespace Ops 
{
	public class McSendDataHandler : ISendDataHandler 
    {
		private ISender sender;
        private List<Publisher> publishers = new List<Publisher>();
        private readonly InetAddress sinkIP;

        public McSendDataHandler(Topic t, string localInterface) 
        {
            sender = new MulticastSender(0, localInterface, 0, t.GetOutSocketBufferSize());
            sinkIP = InetAddress.GetByName(t.GetDomainAddress());
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void AddPublisher(Publisher pub)
        {
            publishers.Add(pub);

            if (publishers.Count == 1)
            {
                // The first publisher
                sender.Open();
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public bool RemovePublisher(Publisher pub)
        {
            bool result = publishers.Remove(pub);

            if (publishers.Count == 0)
            {
                // No more publishers 
                sender.Close();
            }

            return result;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public bool SendData(byte[] bytes, int size, Topic t)
        {
            int nrSegmentsNeeded = (int)(size / Globals.MAX_SEGMENT_SIZE);
            if (size % Globals.MAX_SEGMENT_SIZE != 0)
            {
                nrSegmentsNeeded ++;
            }
            for (int i = 0; i < nrSegmentsNeeded; i++)
            {
                //If this is the last element, only send the bytes that remains, otherwise send a full package.
                int sizeToSend = (i == nrSegmentsNeeded - 1) ? size - (nrSegmentsNeeded - 1) * Globals.MAX_SEGMENT_SIZE : Globals.MAX_SEGMENT_SIZE;
                if (!sender.SendTo(bytes, i * Globals.MAX_SEGMENT_SIZE, sizeToSend, sinkIP, t.GetPort()))
                {
                    return false;
                }
            }
            return true;
        }

	}

}