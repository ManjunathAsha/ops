using System;
using System.Collections.Generic;
using System.Runtime.CompilerServices;  // Needed for the "MethodImpl" synchronization attribute
using System.Linq;
using System.Text;

namespace Ops
{
    class TcpSendDataHandler : ISendDataHandler
    {
        private TcpServerSender sender;
        private readonly InetAddress sinkIP;
        private List<Publisher> publishers = new List<Publisher>();

        public TcpSendDataHandler(Topic t, string localInterface)
        {
            sender = new TcpServerSender(t.GetDomainAddress(), t.GetPort(), t.GetOutSocketBufferSize());
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
                // No more publishers, stop the TCP listener
                sender.Close();
            }

            return result;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public bool SendData(byte[] bytes, int size, Topic t)
        {
            ///TODO How to handle one slow receiver when others are fast
            /// A separate thread for each connection and buffers ??

            int nrSegmentsNeeded = (int)(size / Globals.MAX_SEGMENT_SIZE);
            if (size % Globals.MAX_SEGMENT_SIZE != 0)
            {
                nrSegmentsNeeded++;
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
