///////////////////////////////////////////////////////////
//  SendDataHandler.cs
//  Implementation of the Interface SendDataHandler
//  Created on:      12-nov-2011 09:25:35
//  Author:
///////////////////////////////////////////////////////////

namespace Ops 
{
	public interface ISendDataHandler 
    {
        void AddPublisher(Publisher pub);
        bool RemovePublisher(Publisher pub);

		/// 
		/// <param name="bytes"></param>
		/// <param name="size"></param>
		/// <param name="t"></param>
		bool SendData(byte[] bytes, int size, Topic t);
	}

}