///////////////////////////////////////////////////////////
//  ReceiveDataHandlerFactory.cs
//  Implementation of the Class ReceiveDataHandlerFactory
//  Created on:      12-nov-2011 09:25:34
//  Author:
///////////////////////////////////////////////////////////

using System;
using System.Collections.Generic;

namespace Ops 
{
	public class ReceiveDataHandlerFactory 
    {
        private Dictionary<string, ReceiveDataHandler> ReceiveDataHandlers = new Dictionary<string, ReceiveDataHandler>();

        ///LA TODO Protection ?? What if several subscribers at the same time
        /// Not needed since all calls go through the participant which is synched
        public ReceiveDataHandler GetReceiveDataHandler(Topic top, Participant participant)
        {
            // In the case that we use the same port for several topics, we need to find the receiver for the transport::address::port used
            string key = top.GetTransport() + "::" + top.GetDomainAddress() + "::" + top.GetPort();

            if ( (top.GetTransport().Equals(Topic.TRANSPORT_MC)) || (top.GetTransport().Equals(Topic.TRANSPORT_TCP)) )
            {
                if (!ReceiveDataHandlers.ContainsKey(key))
                {
                    ReceiveDataHandlers.Add(key, 
                        new ReceiveDataHandler(top, participant, ReceiverFactory.CreateReceiver(top, participant.getDomain().GetLocalInterface())));
                }
                return ReceiveDataHandlers[key];
            }
            return null;
        }

	}

}