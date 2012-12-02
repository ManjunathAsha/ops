///////////////////////////////////////////////////////////
//  OPSConfig.cs
//  Implementation of the Class OPSConfig
//  Created on:      12-nov-2011 09:25:33
//  Author:
///////////////////////////////////////////////////////////

using System.Collections.Generic;       // Needed for the "List"
using System.IO;
using System.Text;

namespace Ops 
{
	public class OPSConfig : OPSObject 
    {
        private List<Domain> domains = new List<Domain>();

        public static OPSConfig GetConfig() 
        {
            return GetConfig("ops_config.xml");
        }

        public static OPSConfig GetConfig(string configFile)
        {
            FileStream fis = File.OpenRead(configFile);
            XMLArchiverIn archiverIn = new XMLArchiverIn(fis, "root");
            archiverIn.Add(OPSObjectFactory.GetInstance());
            OPSConfig newConfig = null;
            newConfig = (OPSConfig)archiverIn.Inout("ops_config", newConfig);
            fis.Close();
            return newConfig;
        }

        public static void SaveConfig(OPSConfig conf, string configFile) 
        {
            FileStream fos = File.OpenWrite(configFile);
            BinaryWriter bw = new BinaryWriter(fos);
            XMLArchiverOut archiverOut = new XMLArchiverOut(bw, false);
            bw.Write(Encoding.ASCII.GetBytes("<root xmlns=ops>\n"));
            archiverOut.Inout("ops_config", conf);
            bw.Write(Encoding.ASCII.GetBytes("</root>"));
            fos.Close();
        }

        //---------------------------------------------------------------

        public OPSConfig()
        {
            AppendType("OPSConfig");
        }

        public Domain GetDomain(string domainID)
        {
            foreach (Domain domain in domains)
            {
                if (domain.GetDomainID().Equals(domainID))
                {
                    return domain;
                }
            }
            return null;
        }

        public override void Serialize(IArchiverInOut archive)
        {
            base.Serialize(archive);
            domains = (List<Domain>)archive.InoutSerializableList("domains", domains);
        }

        public List<Domain> getDomains()
        {
            return domains;
        }

	}

}