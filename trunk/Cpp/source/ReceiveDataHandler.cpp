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
#include "OPSTypeDefs.h"
#include "ReceiveDataHandler.h"
#include "BasicError.h" 
#include "Domain.h"
#include "Participant.h"
#include "ReceiverFactory.h"
#include "CommException.h"
namespace ops
{
    ///Constructor.

    ReceiveDataHandler::ReceiveDataHandler(Topic top, Participant* part) :
    expectedSegment(0),
    memMap(top.getSampleMaxSize() / OPSConstants::PACKET_MAX_SIZE + 1, OPSConstants::PACKET_MAX_SIZE),
    firstReceived(false),
    currentMessageSize(0)
    {
        message = NULL;
        participant = part;


        receiver = ReceiverFactory::getReceiver(top, participant);

        if (receiver == NULL)
        {
            throw exceptions::CommException("Could not crete receiver");
        }

        receiver->addListener(this);

    }
    ///Destructor

    ReceiveDataHandler::~ReceiveDataHandler()
    {
        delete receiver;
    }

	// Overridden from Notifier<OPSMessage*>
	void ReceiveDataHandler::addListener(Listener<OPSMessage*>* listener)
    {
        SafeLock lock(&messageLock);
		Notifier<OPSMessage*>::addListener(listener);
		if (getNrOfListeners() == 1) {
			receiver->start();
			expectedSegment = 0;
			currentMessageSize = 0;
	        receiver->asynchWait(memMap.getSegment(expectedSegment), memMap.getSegmentSize());
		}
	}

	// Overridden from Notifier<OPSMessage*>
    void ReceiveDataHandler::removeListener(Listener<OPSMessage*>* listener)
    {
        SafeLock lock(&messageLock);
		Notifier<OPSMessage*>::removeListener(listener);
		if (getNrOfListeners() == 0) receiver->stop();
	}

    ///Override from Listener
    ///Called whenever the receiver has new data.

    void ReceiveDataHandler::onNewEvent(Notifier<BytesSizePair>* sender, BytesSizePair byteSizePair)
    {
        if (byteSizePair.size <= 0)
        {
            //Inform participant that we had an error waiting for data,
            //this means the underlying socket is down but hopefully it will reconnect, so no need to do anything.
            //Only happens with tcp connections so far.

            if (byteSizePair.size == -5)
            {
                BasicError err("ReceiveDataHandler", "onNewEvent", "Connection was lost but is now reconnected.");
                participant->reportError(&err);
            }
            else
            {
                BasicError err("ReceiveDataHandler", "onNewEvent", "Empty message or error.");
                participant->reportError(&err);
            }

            receiver->asynchWait(memMap.getSegment(expectedSegment), memMap.getSegmentSize());
            return;
        }

        //Create a temporay map and buf to peek data before putting it in to memMap
        MemoryMap tMap(memMap.getSegment(expectedSegment), memMap.getSegmentSize());
        ByteBuffer tBuf(&tMap);

        //Check protocol
        if (tBuf.checkProtocol())
        {

            //Read of message ID and fragmentation info, this is ignored so far.
            //std::string messageID = tBuf.ReadString();
            int nrOfFragments = tBuf.ReadInt();
            int currentFragment = tBuf.ReadInt();

            if (currentFragment != (nrOfFragments - 1) && byteSizePair.size != OPSConstants::PACKET_MAX_SIZE)
            {
                BasicError err("ReceiveDataHandler", "onNewEvent", "Debug: Received broken package.");
                participant->reportError(&err);
            }

            currentMessageSize += byteSizePair.size; // - tBuf.GetSize();

            if (currentFragment != expectedSegment)
            {//For testing only...
                if (firstReceived)
                {
                    BasicError err("ReceiveDataHandler", "onNewEvent", "Segment Error, sample will be lost.");
                    participant->reportError(&err);
                    firstReceived = false;
                }
                expectedSegment = 0;
                currentMessageSize = 0;
                receiver->asynchWait(memMap.getSegment(expectedSegment), memMap.getSegmentSize());
                return;
            }

            if (currentFragment == (nrOfFragments - 1))
            {
                firstReceived = true;
                expectedSegment = 0;
                ByteBuffer buf(&memMap);

                buf.checkProtocol();
                int i1 = buf.ReadInt();
                int i2 = buf.ReadInt();

                int segmentPaddingSize = buf.GetSize();

                //Read of the actual OPSMessage
                OPSArchiverIn archiver(&buf, participant->getObjectFactory());

                SafeLock lock(&messageLock);

                OPSMessage* oldMessage = message;

                message = NULL;
                message = dynamic_cast<OPSMessage*> (archiver.inout(std::string("message"), message));
                if (message)
                {
					//Check that we succeded in creating the actual data message
					if (message->getData()) 
					{
						//Put spare bytes in data of message
						calculateAndSetSpareBytes(buf, segmentPaddingSize);

						//Add message to a reference handler that will keep the message until it is no longer needed.
						messageReferenceHandler.addReservable(message);
						message->reserve();
						//Send it to Subscribers
						notifyNewEvent(message);
						//This will delete this message if no one reserved it in the application layer.
						if (oldMessage) oldMessage->unreserve();
						currentMessageSize = 0;
					}
					else
					{
	                    BasicError err("ReceiveDataHandler", "onNewEvent", "Failed to deserialize message. Check added Factories.");
		                participant->reportError(&err);
						delete message;
	                    message = oldMessage;
					}
                }
                else
                {
                    //Inform participant that invalid data is on the network.
                    BasicError err("ReceiveDataHandler", "onNewEvent", "Unexpected type received. Type creation failed.");
                    participant->reportError(&err);
                    message = oldMessage;
                }
            }
            else
            {
                expectedSegment++;

				if (expectedSegment >= memMap.getNrOfSegments()) {
					BasicError err("ReceiveDataHandler", "onNewEvent", "Buffer too small for received message.");
					participant->reportError(&err);
					expectedSegment = 0;
					currentMessageSize = 0;
				}

            }
            receiver->asynchWait(memMap.getSegment(expectedSegment), memMap.getSegmentSize());
        }
        else
        {
            //Inform participant that invalid data is on the network.
            BasicError err("ReceiveDataHandler", "onNewEvent", "Protocol ERROR.");
            participant->reportError(&err);
            receiver->asynchWait(memMap.getSegment(expectedSegment), memMap.getSegmentSize());
        }

    }

	// Called when there are no more listeners and we are about to be put on the garbage-list for later removal
    void ReceiveDataHandler::stop()
    {
        receiver->removeListener(this);

		// Need to release the last message we received, if any.
		// (We always keep a reference to the last message received)
		// If we don't, the garbage-cleaner won't delete us.
		if (message) message->unreserve();
		message = NULL;

///LA ///TTTTT        receiver->stop();
    }

    bool ReceiveDataHandler::aquireMessageLock()
    {
        return messageLock.lock();
    }

    void ReceiveDataHandler::releaseMessageLock()
    {
        messageLock.unlock();
    }

    void ReceiveDataHandler::calculateAndSetSpareBytes(ByteBuffer &buf, int segmentPaddingSize)
    {
        //We must calculate how many unserialized segment headers we have and substract that total header size from the size of spareBytes.
        int nrOfSerializedBytes = buf.GetSize();
        int totalNrOfSegments = (int) (currentMessageSize / memMap.getSegmentSize());
        int nrOfSerializedSegements = (int) (nrOfSerializedBytes / memMap.getSegmentSize());
        int nrOfUnserializedSegments = totalNrOfSegments - nrOfSerializedSegements;

        int nrOfSpareBytes = currentMessageSize - buf.GetSize() - (nrOfUnserializedSegments * segmentPaddingSize);

        if (nrOfSpareBytes > 0)
		{

            message->getData()->spareBytes.reserve(nrOfSpareBytes);
            message->getData()->spareBytes.resize(nrOfSpareBytes, 0);

            //This will read the rest of the bytes as raw bytes and put them into sparBytes field of data.
            buf.ReadChars(&(message->getData()->spareBytes[0]), nrOfSpareBytes);

        }
    }



}
