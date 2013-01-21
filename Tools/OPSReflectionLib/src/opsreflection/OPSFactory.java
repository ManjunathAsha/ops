/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package opsreflection;

import configlib.Serializable;
import configlib.SerializableFactory;
import configlib.XMLArchiverIn;
import configlib.XMLArchiverOut;
import configlib.exception.FormatException;
import jarsearch.JarSearcher;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Vector;
import ops.ConfigurationException;
import ops.Domain;
import ops.OPSObject;
import ops.Participant;
import ops.Publisher;
import ops.Subscriber;
import ops.Topic;

/**
 *
 * @author angr
 */
public class OPSFactory
{
    OPSFactoryConfig factoryConfig = new OPSFactoryConfig();
    JarSearcher jarSearcher = new JarSearcher(OPSObject.class.getClassLoader());
    private String domainID;
    private Participant participant;

    public OPSFactory()
    {
        
        TypeSupportImpl typeSupport = new TypeSupportImpl();
        try
        {
            participant = Participant.getInstance(domainID);
            participant.addTypeSupport(typeSupport);
        }
        catch (ConfigurationException e)
        {
        }
        this.domainID = factoryConfig.domainID;
    }
    public OPSFactory(File configFile) throws FileNotFoundException, FormatException, IOException
    {

        XMLArchiverIn archiverIn = new XMLArchiverIn(new FileInputStream(configFile), "root");
        archiverIn.add(new OPSReflectionSerializableFactory());
        factoryConfig = (OPSFactoryConfig) archiverIn.inout("factoryConfig", factoryConfig);
        this.domainID = factoryConfig.domainID;
        for (File file : factoryConfig.getJarFiles())
        {
            jarSearcher.addJar(new File(configFile.getParentFile().getPath() + "/" + file.getPath()));
        }
        try
        {
            participant = Participant.getInstance(domainID, "DEFAULT_PARTICIPANT", new File(configFile.getParentFile().getPath() + "/" + factoryConfig.opsConfigRelativePath));
        
            TypeSupportImpl typeSupport = new TypeSupportImpl();
            participant.addTypeSupport(typeSupport);
        }
        catch (ConfigurationException e)
        {
        }
    }



    public OPSObject createOPSObject(String string)
    {
        try
        {
            return (OPSObject) jarSearcher.loadClass(string).getConstructor().newInstance();
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public String getDomainAdrress()
    {
        return participant.getDomain().getDomainAddress();
    }

    public void save(File f) throws FileNotFoundException, IOException
    {
        XMLArchiverOut archiverOut = new XMLArchiverOut(new FileOutputStream(f), true);
        archiverOut.setWriteTypes(false);

        factoryConfig = (OPSFactoryConfig) archiverOut.inout("factoryConfig", factoryConfig);

    }


    public void addJar(File jarFile) throws MalformedURLException, IOException
    {
        jarSearcher.addJar(jarFile);
        factoryConfig.jarFilePaths.add(new JarPathConfig(jarFile.getPath()));

    }


    public ArrayList<String> listTopicNames()
    {
        ArrayList<String> ret = new ArrayList<String>();
    
        Vector<Topic> topics = participant.getDomain().getTopics();
        for (Topic topic : topics)
        {
            ret.add(topic.getName());
        }
        return ret;
    }
    public ArrayList<String> listLoadableTopicNames()
    {
        return null;
    }

    public Topic createTopic(String topicName)
    {
        return participant.createTopic(topicName);
    }
    public Subscriber createSubscriber(String topicName)
    {
        try
        {
            Topic t = createTopic(topicName);
            return (Subscriber) jarSearcher.loadClass(t.getTypeID() + "Subscriber").getConstructor(t.getClass()).newInstance(t);
        }
        catch (InstantiationException ex)
        {
            return null;
        }
        catch (IllegalAccessException ex)
        {
            return null;
        }
        catch (IllegalArgumentException ex)
        {
            return null;
        }
        catch (InvocationTargetException ex)
        {
            return null;
        }
        catch (ClassNotFoundException ex)
        {
            return null;
        }
        catch (NoSuchMethodException ex)
        {
            return null;
        }
        catch (SecurityException ex)
        {
            return null;
        }
    }
    public Publisher createPublisher(String topicName)
    {
        try
        {
            Topic t = createTopic(topicName);
            return (Publisher) jarSearcher.loadClass(t.getTypeID() + "Publisher").getConstructor(t.getClass()).newInstance(t);
        }
        catch (InstantiationException ex)
        {
            return null;
        }
        catch (IllegalAccessException ex)
        {
            return null;
        }
        catch (IllegalArgumentException ex)
        {
            return null;
        }
        catch (InvocationTargetException ex)
        {
            return null;
        }
        catch (ClassNotFoundException ex)
        {
            return null;
        }
        catch (NoSuchMethodException ex)
        {
            return null;
        }
        catch (SecurityException ex)
        {
            return null;
        }
    }

     class TypeSupportImpl implements SerializableFactory
    {
        public Serializable create(String type)
        {
            try
            {
                return (Serializable) jarSearcher.loadClass(type).getConstructor().newInstance();
            }
            catch (ClassNotFoundException classNotFoundException)
            {
                return null;
            }
            catch (NoSuchMethodException noSuchMethodException)
            {
                return null;
            }
            catch (SecurityException securityException)
            {
                return null;
            }
            catch (InstantiationException instantiationException)
            {
                return null;
            }
            catch (IllegalAccessException illegalAccessException)
            {
                return null;
            }
            catch (IllegalArgumentException illegalArgumentException)
            {
                return null;
            }
            catch (InvocationTargetException invocationTargetException)
            {
                return null;
            }
        }

    }

}
