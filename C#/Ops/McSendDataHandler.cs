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
	public class McSendDataHandler : SendDataHandler 
    {
        private readonly InetAddress sinkIP;

        public McSendDataHandler(Topic t, string localInterface) 
        {
            sender = new MulticastSender(0, localInterface, 0, t.GetOutSocketBufferSize());     // Make ttl configurable
            sinkIP = InetAddress.GetByName(t.GetDomainAddress());
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public override bool SendData(byte[] bytes, int size, Topic t)
        {
            return SendData(bytes, size, sinkIP, t.GetPort());
        }

	}

}