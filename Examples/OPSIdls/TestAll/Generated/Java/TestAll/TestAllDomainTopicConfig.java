//Auto generated OPS-code. DO NOT MODIFY!

package TestAll;

import ops.Topic;
import ops.Participant;
import ops.Domain;


public class TestAllDomainTopicConfig 
{

    final String domainAddress;
    final Participant participant;
    public TestAllDomainTopicConfig(String domainAddress)
    {
        this.domainAddress = domainAddress;
      Domain domain = new Domain();
      domain.setDomainID("TestAllDomain");
      domain.setDomainAddress("234.5.6.8");
      domain.setLocalInterface("127.0.0.1");
      Topic topic = null;
      topic = new Topic();
      topic.setTypeID("TestAll.ChildData");
      topic.setName("ChildTopic");
      topic.setPort(6689);
      topic.setDomainAddress(domainAddress);
      topic.setDomainID(domain.getDomainID());
      topic.setTransport("multicast");
      topic.setInSocketBufferSize(0);
      topic.setOutSocketBufferSize(0);
      topic.setSampleMaxSize(2100000);
      domain.getTopics().add(topic);
         
      topic = new Topic();
      topic.setTypeID("TestAll.BaseData");
      topic.setName("BaseTopic");
      topic.setPort(6687);
      topic.setDomainAddress(domainAddress);
      topic.setDomainID(domain.getDomainID());
      topic.setTransport("multicast");
      topic.setInSocketBufferSize(0);
      topic.setOutSocketBufferSize(0);
      topic.setSampleMaxSize(60000);
      domain.getTopics().add(topic);
         
      participant = Participant.getInstance(domain, domain.getDomainID());

    }

      public Topic getChildTopic()
      {
         return participant.createTopic("ChildTopic");
      }

      public Topic getBaseTopic()
      {
         return participant.createTopic("BaseTopic");
      }


}

