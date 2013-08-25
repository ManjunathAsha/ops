//Auto generated OPS-code. DO NOT MODIFY!

package DerivedIDLs;

import ops.Topic;
import ops.Participant;
import ops.Domain;


public class FooDomainTopicConfig 
{

    final String domainAddress;
    final Participant participant;
    public FooDomainTopicConfig(String domainAddress)
    {
        this.domainAddress = domainAddress;
      Domain domain = new Domain();
      domain.setDomainID("FooDomain");
      domain.setDomainAddress("234.5.6.8");
      domain.setLocalInterface("");
      Topic topic = null;
      topic = new Topic();
      topic.setTypeID("foopackage.FooData");
      topic.setName("FooTopic");
      topic.setPort(6686);
      topic.setDomainAddress(domainAddress);
      topic.setDomainID(domain.getDomainID());
      topic.setTransport("multicast");
      topic.setInSocketBufferSize(0);
      topic.setOutSocketBufferSize(0);
      topic.setSampleMaxSize(60000);
      domain.getTopics().add(topic);
         
      participant = Participant.getInstance(domain, domain.getDomainID());

    }

      public Topic getFooTopic()
      {
         return participant.createTopic("FooTopic");
      }


}

