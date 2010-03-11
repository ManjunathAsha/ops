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

package ops.transport.inprocess;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import ops.protocol.OPSMessage;

/**
 *
 * @author Anton Gravestam
 */
public class InProcessTransport
{
    BlockingQueue<OPSMessage> blockingQueue = new LinkedBlockingQueue<OPSMessage>();

    /**
     * 
     */
    public InProcessTransport()
    {
    }
    /**
     *
     * @param message will be out on the underlying queue
     */
    public void putMessage(OPSMessage message)
    {
        try
        {
            blockingQueue.put(message);
        } catch (InterruptedException ex)
        {
            //Should never block
            Logger.getLogger(InProcessTransport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     *
     * @param originalMessage will be copied and put on the underlying queue.
     */
    public void copyAndPutMessage(OPSMessage originalMessage)
    {
        putMessage((OPSMessage) originalMessage.clone());
    }

    /**
     *
     * @return
     */
    public OPSMessage takeMessage()
    {
        try
        {
            return blockingQueue.take();
        } catch (InterruptedException ex)
        {
            //Should never block
            Logger.getLogger(InProcessTransport.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

}
