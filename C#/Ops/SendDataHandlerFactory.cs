///////////////////////////////////////////////////////////
//  SendDataHandlerFactory.cs
//  Implementation of the Class SendDataHandlerFactory
//  Created on:      12-nov-2011 09:25:35
//  Author:
///////////////////////////////////////////////////////////

using System;
using System.IO;
using System.Collections.Generic;

namespace Ops 
{
	internal class SendDataHandlerFactory 
    {
        private Dictionary<string, ISendDataHandler> SendDataHandlers = new Dictionary<string, ISendDataHandler>();

        ///LA TODO Protection ?? What if several subscribers at the same time
        /// Not needed since all calls go through the participant which is synched
        public ISendDataHandler GetSendDataHandler(Topic t, Participant participant)
        {
            // In the case that we use the same port for several topics, we need to find the sender for the transport::address::port used
            string key = t.GetTransport() + "::" + t.GetDomainAddress() + "::" + t.GetPort();
            
            if (SendDataHandlers.ContainsKey(key))
            {
                return SendDataHandlers[key];
            }

            try
            {
                ISendDataHandler sender = null;
                if (t.GetTransport().Equals(Topic.TRANSPORT_MC))
                {
                    sender = new McSendDataHandler(t, participant.getDomain().GetLocalInterface());
                }
                if (t.GetTransport().Equals(Topic.TRANSPORT_TCP))
                {
                    sender = new TcpSendDataHandler(t, participant.getDomain().GetLocalInterface());
                }
                if (sender != null)
                {
                    SendDataHandlers.Add(key, sender);
                    return sender;
                }
                throw new CommException("No such Transport " + t.GetTransport());
            }
            catch (System.IO.IOException ex)
            {
                throw new CommException("Error creating SendDataHandler. IOException -->" + ex.Message);
            }
        }

	}

}