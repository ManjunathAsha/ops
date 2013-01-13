/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ops;

import java.io.File;
import java.util.HashMap;

/**
 *
 * @author Anton Gravestam
 */
class ParticipantFactory
{

    private HashMap<String, Participant> instances = new HashMap<String, Participant>();

    Participant getParticipant(String domainID, String participantID) throws ConfigurationException
    {
        return getParticipant(domainID, participantID, null);
    }

    Participant getParticipant(String domainID, String participantID, File file) throws ConfigurationException
    {
        String hashKey = domainID + " " + participantID;

        if (!instances.containsKey(hashKey))
        {
            Participant newInst = new Participant(domainID, participantID, file);
            Domain tDomain = newInst.getDomain();

            if (tDomain != null)
            {
                instances.put(hashKey, newInst);
            }
            else
            {
                return null;
            }
        }
        return instances.get(hashKey);
    }

    Participant getParticipant(Domain domain, String participantID)
    {
        String hashKey = domain.getDomainID() + " " + participantID;

        if (!instances.containsKey(hashKey))
        {
            Participant newInst = new Participant(domain, participantID);
            Domain tDomain = newInst.getDomain();

            if (tDomain != null)
            {
                instances.put(hashKey, newInst);
            }
            else
            {
                return null;
            }
        }
        return instances.get(hashKey);
    }
}
