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
package ops;

import configlib.SerializableFactory;
import configlib.exception.FormatException;
import java.io.File;
import java.io.IOException;
import ops.archiver.OPSObjectFactory;
import ops.transport.inprocess.InProcessTransport;

/**
 *
 * @author Anton Gravestam
 */
public class Participant
{
    //This factory is used through static facade methods getInstance(...)
    private static ParticipantFactory participantFactory = new ParticipantFactory();

    protected String domainID;
    private String participantID;
    protected Domain domain;
    private ErrorService errorService = new ErrorService();
    private ReceiveDataHandlerFactory receiveDataHandlerFactory = new ReceiveDataHandlerFactory();
    private SendDataHandlerFactory sendDataHandlerFactory = new SendDataHandlerFactory();
    private InProcessTransport inProcessTransport = new InProcessTransport();

    ///LA Added
    private OPSConfig config = null;

    /**
     * Method for retreiving the default Participant instance for the @param domainID
     * @param domainID
     * @return a Participant or null if this method fails.
     */
    public static synchronized Participant getInstance(String domainID)
    {
        return participantFactory.getParticipant(domainID, "DEFAULT_PARTICIPANT");
    }

    /**
     * Method for retreiving the @param participantID Participant instance for @param domainID
     * @param domainID
     * @param participantID
     * @return a Participant or null if this method fails.
     */
    public static synchronized Participant getInstance(String domainID, String participantID)
    {
        return participantFactory.getParticipant(domainID, participantID, null);
    }

    /**
     * Method for retreiving the @param participantID Participant instance for
     * @param domainID using @param file if this participant has not yet been created.
     * @param domainID
     * @param participantID
     * @param file the file to use as configuration. Ignored if a participant for
     * participantID already exist.
     * @return a Participant or null if this method fails.
     */
    public static synchronized Participant getInstance(String domainID, String participantID, File file)
    {
        return participantFactory.getParticipant(domainID, participantID, file);
    }


    public static synchronized Participant getInstance(Domain domain, String participantID)
    {
        return participantFactory.getParticipant(domain, participantID);
    }

    

    protected Participant(String domainID, String participantID, File configFile)
    {
        this.domainID = domainID;
        this.participantID = participantID;
        try
        {
            ///LA made class member OPSConfig config;
            if (configFile == null)
            {
                config = OPSConfig.getConfig();
                domain = config.getDomain(domainID);
            } else
            {
                config = OPSConfig.getConfig(configFile);
                domain = config.getDomain(domainID);
            }
        } catch (IOException ex)
        {
            //TODO: rethrow
            //config = null;
        } catch (FormatException ex)
        {
            //config = null;
            //TODO: rethrow
        }
        inProcessTransport.start();

    }

    protected Participant(Domain domain, String participantID)
    {
        this.domainID = domain.getDomainID();
        this.participantID = participantID;
        
        this.domain = domain;

        inProcessTransport.start();

    }

    ///LA Added
    /**
     * Return the OPSConfig object used (if any).
     * Note that null can be returned if Participant is created from a Domain object
     */
    public OPSConfig getConfig()
    {
        return config;
    }

    /**
     * Remove listener for Error events.
     * @param listener
     */
    public void removeListener(Listener<Error> listener)
    {
        errorService.removeListener(listener);
    }

    /**
     * Add listener for Error events.
     * @param listener
     */
    public synchronized void addListener(Listener<Error> listener)
    {
        errorService.addListener(listener);
    }

    /**
     * Report a core Error.
     * @param className the class in which the error occured.
     * @param methodName the method in which the error occured.
     * @param errorMessage a message descriing the error.
     */
    void report(String className, String methodName, String errorMessage)
    {
        errorService.report(className, methodName, errorMessage);
    }

    /**
     * Report a core Error
     * @param error the Error to report.
     */
    void report(Error error)
    {
        errorService.report(error);
    }

    
    /**
     * Adds type support in form of a SerializablFactory to this participant.
     * Normally this is the IDL project generated TypeFactory (e.g. FooProject.FooProjectTypeFactory())
     * @param typeSupport
     */
    public void addTypeSupport(SerializableFactory typeSupport)
    {
        OPSObjectFactory.getInstance().add(typeSupport);
    }

    /**
     * Creates a Topic that can be used to create Publishers and/or Subscribers.
     * The fields of the Topic returned are fetched from the participants underlying config file.
     * @param name
     * @return a new Topic based on the config of this participant.
     */
    public Topic createTopic(String name)
    {
        Topic topic = domain.getTopic(name);
        
        if(topic == null)
            return null;

        topic.setParticipantID(participantID);
        topic.setDomainID(domainID);

        return topic;
    }

    ///By modified singelton
    synchronized ReceiveDataHandler getReceiveDataHandler(Topic top)
    {
        return receiveDataHandlerFactory.getReceiveDataHandler(top, this);
        
    }

    ///By modified singelton
    synchronized SendDataHandler getSendDataHandler(Topic t) throws CommException
    {
        return sendDataHandlerFactory.getSendDataHandler(t, this);
        
    }

    public Domain getDomain()
    {
        return domain;
    }

    public InProcessTransport getInProcessTransport()
    {
        return inProcessTransport;
    }

}
