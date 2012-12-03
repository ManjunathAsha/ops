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

        /// Protection is not needed since all calls go through the participant which is synched
        public ReceiveDataHandler GetReceiveDataHandler(Topic top, Participant participant)
        {
            // In the case that we use the same port for several topics, we need to find the receiver for the transport::address::port used
            string key = top.GetTransport() + "::" + top.GetDomainAddress() + "::" + top.GetPort();

            if (ReceiveDataHandlers.ContainsKey(key))
            {
                ReceiveDataHandler rdh = ReceiveDataHandlers[key];

                // Check if any of the topics have a sample size larger than MAX_SEGMENT_SIZE
                // This will lead to a problem when using the same port, if samples becomes > MAX_SEGMENT_SIZE
                if ((rdh.GetSampleMaxSize() > Globals.MAX_SEGMENT_SIZE) || (top.GetSampleMaxSize() > Globals.MAX_SEGMENT_SIZE))
                {
                    Logger.ExceptionLogger.LogMessage("Warning: Same port (" + top.GetPort() + ") is used with Topics larger than MAX_SEGMENT_SIZE");
                }
                return rdh;
            }

            // Didn't exist, create one if transport is known
            if ( (top.GetTransport().Equals(Topic.TRANSPORT_MC)) || (top.GetTransport().Equals(Topic.TRANSPORT_TCP)) )
            {
                ReceiveDataHandlers.Add(key, 
                    new ReceiveDataHandler(top, participant, ReceiverFactory.CreateReceiver(top, participant.getDomain().GetLocalInterface())));
                return ReceiveDataHandlers[key];
            }
            return null;
        }

	}

}