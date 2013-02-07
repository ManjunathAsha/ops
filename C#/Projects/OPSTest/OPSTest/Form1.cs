﻿using System;
using System.Collections.Generic;
using System.Collections.Concurrent;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using Ops;
using pizza;

namespace OPSTest
{
    public partial class Form_TestOps : Form, ILogListener, IOpsHelperCallback<VessuvioData>, IOpsHelperCallback<PizzaData>, IOpsHelperCallback<Ops.ParticipantInfoData>
    {
        private Participant myParticipant = null;
        private Participant OtherParticipant = null;
        private int myMessageCounter = 0;
        private int deadLineEventInterval;

        private ConcurrentQueue<string> LogList = new ConcurrentQueue<string>();

        //------------------------------------------------------------------------------------
        // Help class and list to handle many topics in a generic way

        private class MyTopicInfo
        {
            // These are set by constructor
            public String DomainName;
            public String TopicName;
            public String TypeName;
            public int Index;           // Index into the list(s) with helper objects

            // These are updated later 
            public Participant Part;
            public IOpsHelper OpsHelper;
            public bool SubExists { get; set; }
            public bool PubExists { get; set; }
            
            public MyTopicInfo(String dName, String topName, String typName) 
            { 
                DomainName = dName; 
                TopicName = topName;
                TypeName = typName;
            }

            // Since objects of this class is added to the listbox, the return value
            // from the ToString() method is shown in the listbox.
            override public String ToString() 
            {
                String P = PubExists ? "P" : "  ";
                String S = SubExists ? "S" : "  ";
                return P + S + " " + DomainName + "::" + TopicName; 
            }
        };
        private List<MyTopicInfo> MyTopicInfoList = new List<MyTopicInfo>();

        //------------------------------------------------------------------------------------
        // Lists for the different Ops messages that we can handle

        private List<COpsHelper<PizzaData>> Pizza = new List<COpsHelper<PizzaData>>();
        private List<COpsHelper<VessuvioData>> Vessuvio = new List<COpsHelper<VessuvioData>>();

        //private COpsHelperPartInfoData partInfoHelper = null;

        //
        public Form_TestOps()
        {
            InitializeComponent();
        }

        private void Form_TestOps_Load(object sender, EventArgs e)
        {
            // This application will unlikely detect data fragment lost errors.
            // But the statement provides a sample of the global OPS settings.
            Ops.Globals.REPORT_DATA_FRAGMENT_LOST_ERRORS = true;

            // Set up a logger for errors reported from OPS, to capture all errors and show them in our logarea
            new OpsErrorLogger(this);

            // Set up a text file logger for errors reported from OPS.
            ///Logger.ExceptionLogger.AddLogger(new TextFileLogger(Path.GetDirectoryName(Application.ExecutablePath) + "\\Log.txt"));

            ///TODO to be filled from existing items in config file??
            MyTopicInfoList.Add(new MyTopicInfo("PizzaDomain",      "PizzaTopic",         "pizza.PizzaData"));
            MyTopicInfoList.Add(new MyTopicInfo("PizzaDomain",      "VessuvioTopic",      "pizza.VessuvioData"));
            MyTopicInfoList.Add(new MyTopicInfo("PizzaDomain",      "PizzaTopic2",        "pizza.PizzaData"));
            MyTopicInfoList.Add(new MyTopicInfo("PizzaDomain",      "VessuvioTopic2",     "pizza.VessuvioData"));
            MyTopicInfoList.Add(new MyTopicInfo("PizzaDomain",      "TcpPizzaTopic",      "pizza.PizzaData"));
            MyTopicInfoList.Add(new MyTopicInfo("PizzaDomain",      "TcpVessuvioTopic",   "pizza.VessuvioData"));
            MyTopicInfoList.Add(new MyTopicInfo("PizzaDomain",      "TcpPizzaTopic2",     "pizza.PizzaData"));
            MyTopicInfoList.Add(new MyTopicInfo("PizzaDomain",      "TcpVessuvioTopic2",  "pizza.VessuvioData"));
            MyTopicInfoList.Add(new MyTopicInfo("PizzaDomain",      "UdpPizzaTopic",      "pizza.PizzaData"));
            MyTopicInfoList.Add(new MyTopicInfo("PizzaDomain",      "UdpVessuvioTopic",   "pizza.VessuvioData"));
            MyTopicInfoList.Add(new MyTopicInfo("OtherPizzaDomain", "OtherPizzaTopic",    "pizza.PizzaData"));
            MyTopicInfoList.Add(new MyTopicInfo("OtherPizzaDomain", "OtherVessuvioTopic", "pizza.VessuvioData"));

            // Fill in the listbox and create the needed helper objects
            foreach (MyTopicInfo info in MyTopicInfoList)
            {
                checkedListBoxTopics.Items.Add(info);

                if (info.TypeName == PizzaData.GetTypeName())
                {
                    Pizza.Add(new COpsHelper<PizzaData>(this));
                    info.Index = Pizza.Count - 1;
                    info.OpsHelper = Pizza[info.Index];
                }
                if (info.TypeName == VessuvioData.GetTypeName())
                {
                    Vessuvio.Add(new COpsHelper<VessuvioData>(this));
                    info.Index = Vessuvio.Count - 1;
                    info.OpsHelper = Vessuvio[info.Index];
                }

            }
            checkedListBoxTopics.SetItemChecked(0, true);
        }

        private void Form_TestOps_FormClosed(object sender, FormClosedEventArgs e)
        {
            // Important to ensure that event handling from subscribers are disabled
            // when the application closes. Incoming messages or dead line notifications 
            // may otherwise give exceptions when we are destroyed.
            foreach (COpsHelper<PizzaData> p in Pizza) p.StopSubscriber(false);
            foreach (COpsHelper<VessuvioData> v in Vessuvio) v.StopSubscriber(false);
        }

        private void buttonGCCollect_Click(object sender, EventArgs e)
        {
            GC.Collect();
        }

        private void buttonCreateAll_Click(object sender, EventArgs e)
        {
            buttonCreateParticipant_Click(null, null);
            buttonCreateSubscriber_Click(null, null);
            buttonCreatePublisher_Click(null, null);
        }

        private void buttonCreateParticipant_Click(object sender, EventArgs e)
        {
            if (myParticipant == null)
            {
                try
                {
                    myParticipant = Participant.GetInstance("PizzaDomain", "partId", "ops_config.xml");
                    OtherParticipant = Participant.GetInstance("OtherPizzaDomain", "partId", "ops_config.xml");
                    if (myParticipant == null)
                    {
                        myParticipant = Participant.GetInstance("PizzaDomain", "partId", "..\\..\\ops_config.xml");
                        OtherParticipant = Participant.GetInstance("OtherPizzaDomain", "partId", "..\\..\\ops_config.xml");
                    }
                    if (myParticipant == null)
                    {
                        Log("Failed to create Participant!");
                        return;
                    }
                    myParticipant.AddTypeSupport(new PizzaProject.PizzaProjectTypeFactory());
                    OtherParticipant.AddTypeSupport(new PizzaProject.PizzaProjectTypeFactory());

                    // Setup the correct participant for each topic in our helper list
                    foreach (MyTopicInfo info in MyTopicInfoList)
                    {
                        if (info.DomainName == "PizzaDomain")
                        {
                            info.Part = myParticipant;
                        }
                        if (info.DomainName == "OtherPizzaDomain")
                        {
                            info.Part = OtherParticipant;
                        }
                    }

                    //TEST partInfoHelper = new COpsHelperPartInfoData(this);
                    // partInfoHelper.CreateSubscriber(myParticipant);

                }
                catch (Exception ex)
                {
                    Logger.ExceptionLogger.LogException(ex);
                }
            }
            else
            {
                Log("Participant is already created!");
            }
        }

        private bool verifyParticipants()
        {
            bool res = (myParticipant != null) && (OtherParticipant != null);
            if (!res)
            {
                Log("Participant(s) must be created first!");
            }
            return res;
        }

        private void buttonSaveParticipantConfiguration_Click(object sender, EventArgs e)
        {
            if (myParticipant != null)
            {
                saveFileDialog.OverwritePrompt = true;
                saveFileDialog.FileName = "OpsConfig";
                saveFileDialog.DefaultExt = "xml";
                saveFileDialog.Filter = "XML files (*.xml)|*.xml";

                if (saveFileDialog.ShowDialog() == DialogResult.OK)
                {
                    myParticipant.SaveConfig(saveFileDialog.FileName);
                }
            }
            else
            {
                Log("Participant must be created!");
            }
        }

        private void buttonStopTransport_Click(object sender, EventArgs e)
        {
            if (!verifyParticipants()) return;

            myParticipant.GetInProcessTransport().StopTransport();
        }

        // ---------------------------------------------------------------------------

        private void buttonCreateSubscriber_Click(object sender, EventArgs e)
        {
            if (!verifyParticipants()) return;

            System.Collections.IEnumerator myEnumerator;
            myEnumerator = checkedListBoxTopics.CheckedIndices.GetEnumerator();
            while (myEnumerator.MoveNext() != false)
            {
                int idx = (int)myEnumerator.Current;
                MyTopicInfo info = MyTopicInfoList[idx];
                info.SubExists = info.OpsHelper.CreateSubscriber(info.Part, info.TopicName, deadLineEventInterval);
            }
            checkedListBoxTopics.Invalidate();
        }

        private void buttonDeleteSubscriber_Click(object sender, EventArgs e)
        {
            System.Collections.IEnumerator myEnumerator;
            myEnumerator = checkedListBoxTopics.CheckedIndices.GetEnumerator();
            while (myEnumerator.MoveNext() != false)
            {
                int idx = (int)myEnumerator.Current;
                MyTopicInfo info = MyTopicInfoList[idx];
                info.OpsHelper.DeleteSubscriber();
                info.SubExists = false;
            }
            checkedListBoxTopics.Invalidate();
        }

        private void buttonCreatePublisher_Click(object sender, EventArgs e)
        {
            if (!verifyParticipants()) return;

            System.Collections.IEnumerator myEnumerator;
            myEnumerator = checkedListBoxTopics.CheckedIndices.GetEnumerator();
            while (myEnumerator.MoveNext() != false)
            {
                int idx = (int)myEnumerator.Current;
                MyTopicInfo info = MyTopicInfoList[idx];
                info.PubExists = info.OpsHelper.CreatePublisher(info.Part, info.TopicName);
            }
            checkedListBoxTopics.Invalidate();
        }

        private void buttonDeletePublisher_Click(object sender, EventArgs e)
        {
            System.Collections.IEnumerator myEnumerator;
            myEnumerator = checkedListBoxTopics.CheckedIndices.GetEnumerator();
            while (myEnumerator.MoveNext() != false)
            {
                int idx = (int)myEnumerator.Current;
                MyTopicInfo info = MyTopicInfoList[idx];
                info.OpsHelper.DeletePublisher();
                info.PubExists = false;
            }
            checkedListBoxTopics.Invalidate();
        }

        private void buttonSendMessage_Click(object sender, EventArgs e)
        {
            System.Collections.IEnumerator myEnumerator;
            myEnumerator = checkedListBoxTopics.CheckedIndices.GetEnumerator();
            while (myEnumerator.MoveNext() != false)
            {
                int idx = (int)myEnumerator.Current;
                MyTopicInfo info = MyTopicInfoList[idx];
                int idx2 = info.Index;
                if (info.TypeName == PizzaData.GetTypeName())
                {
                    Pizza[idx2].Data.cheese = "From C# " + Convert.ToString(myMessageCounter);
                    Pizza[idx2].Data.tomatoSauce = "tomatosauce " + Convert.ToString(myMessageCounter);
                    Pizza[idx2].Write();
                }
                else
                {
                    Vessuvio[idx2].Data.cheese = "cheese " + Convert.ToString(myMessageCounter);
                    Vessuvio[idx2].Data.tomatoSauce = "tomatosauce " + Convert.ToString(myMessageCounter);
                    int numBytes = 0;
                    int.TryParse(textBoxNumBytes.Text, out numBytes);
                    StringBuilder strb = new StringBuilder();
                    strb.Length = numBytes;
                    Vessuvio[idx2].Data.ham = strb.ToString();
                    Vessuvio[idx2].Write();
                }
                myMessageCounter++;
            }
        }

        private void timerCyclicWrite_Tick(object sender, EventArgs e)
        {
            buttonSendMessage_Click(null, e);
        }

        private void checkBox1_CheckedChanged(object sender, EventArgs e)
        {
            int sendPeriod = 1000;
            int.TryParse(textBoxSendPeriod.Text, out sendPeriod);
            timerCyclicWrite.Interval = sendPeriod;
            timerCyclicWrite.Enabled = checkBox1.Checked;
        }

        private void buttonStartSubscriber_Click(object sender, EventArgs e)
        {
            System.Collections.IEnumerator myEnumerator;
            myEnumerator = checkedListBoxTopics.CheckedIndices.GetEnumerator();
            while (myEnumerator.MoveNext() != false)
            {
                int idx = (int)myEnumerator.Current;
                MyTopicInfo info = MyTopicInfoList[idx];
                info.OpsHelper.StartSubscriber(deadLineEventInterval);
            }
        }

        private void buttonStopSubscriber_Click(object sender, EventArgs e)
        {
            System.Collections.IEnumerator myEnumerator;
            myEnumerator = checkedListBoxTopics.CheckedIndices.GetEnumerator();
            while (myEnumerator.MoveNext() != false)
            {
                int idx = (int)myEnumerator.Current;
                MyTopicInfo info = MyTopicInfoList[idx];
                info.OpsHelper.StopSubscriber();
            }
        }

        private void buttonSetDeadLineInterval_Click(object sender, EventArgs e)
        {
            if (int.TryParse(textBoxDeadLineInterval.Text, out deadLineEventInterval))
            {
                System.Collections.IEnumerator myEnumerator;
                myEnumerator = checkedListBoxTopics.CheckedIndices.GetEnumerator();
                while (myEnumerator.MoveNext() != false)
                {
                    int idx = (int)myEnumerator.Current;
                    MyTopicInfo info = MyTopicInfoList[idx];
                    info.OpsHelper.SetDeadLineInterval(deadLineEventInterval);
                }
            }
        }

        private void checkBoxEnableTrace_CheckedChanged(object sender, EventArgs e)
        {
            Globals.TRACE_RECEIVE = checkBoxEnableTrace.Checked;
        }

        // ---------------------------------------------------------------------------
        // Logging

        public void SafeLog(string str)
        {
            LogList.Enqueue(str);
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            string str;
            int counter = 0;
            while (LogList.TryDequeue(out str))
            {
                textBoxMessages.AppendText(str);
                if (++counter > 100) break;         // Exit to let other events have their chance
            }
        }

        public void Log(string text)
        {
            /// !!! NOTE !!! THE REMOVED CODE IS A DANGEROUS CONSTRUCTION IF Log() IS CALLED
            /// FROM THE CALLBACKS. DEADLOCK IS LIKELY TO HAPPEN WHEN eg. Subscriber.Stop() 
            /// IS CALLED FROM THE UI THREAD.
            /// A better solution is to use a protected list and let a timer update the UI.
            /// See SafeLog() call below
            
            //// One way to ensure that the message box is updated within the UI thread.
            //this.Invoke((MethodInvoker)delegate
            //{
            //    textBoxMessages.AppendText(DateTime.Now.ToString("HH:mm:ss.fff") + "  " + text + Environment.NewLine); // runs on UI thread
            //});

            //// Another way (also dangerous) to ensure that the message box is updated within the UI thread.
            //// textBoxMessages.Invoke((Action)delegate { textBoxMessages.AppendText(text); });

            SafeLog(DateTime.Now.ToString("HH:mm:ss.fff") + "  " + text + Environment.NewLine);
        }

        // Implementation of interface ILogListener used by some helper classes
        public void OnLog(string arg)
        {
            Log(arg);
        }

        // ---------------------------------------------------------------------------
        // Callbacks for the Data Classes that we subscribe to (from COpsHelper<T> classes)

        public void SubscriberNewData(Subscriber sender, PizzaData data)
        {
            Log("[Topic: " + sender.GetTopic().GetName() + "] Pizza:: Cheese: " + data.cheese + ",  Tomato sauce: " + data.tomatoSauce);
        }

        public void SubscriberNewData(Subscriber sender, VessuvioData data)
        {
            Log("[Topic: " + sender.GetTopic().GetName() + "] Vessuvio:: Cheese: " + data.cheese + ",  Tomato sauce: " + data.tomatoSauce + ", Ham length: " + data.ham.Length);
        }

        public void SubscriberNewData(Subscriber sender, Ops.ParticipantInfoData data)
        {
            Log("[Topic: " + sender.GetTopic().GetName() + "] IP: " + data.ip);
        }

        private void test(string IP)
        {
            Log("Test: Domain.DoSubnetTranslation(" + IP + ") = " + Domain.DoSubnetTranslation(IP));
        }

        private void button1_Click(object sender, EventArgs e)
        {
            //System.Net.NetworkInformation.IPGlobalProperties gp = System.Net.NetworkInformation.IPGlobalProperties.GetIPGlobalProperties();
            //System.Net.NetworkInformation.UnicastIPAddressInformationCollection x = gp.GetUnicastAddresses();
            //for (int i = 0; i < x.Count; i++)
            //{
            //    if (x[i].Address.AddressFamily == System.Net.Sockets.AddressFamily.InterNetwork) //IPV4
            //    {
            //        byte[] addr = x[i].Address.GetAddressBytes(); 
            //        byte[] mask = x[i].IPv4Mask.GetAddressBytes();
            //        for (int j = 0; j < addr.Count(); j++) addr[j] = (byte)((int)addr[j] & (int)mask[j]);

            //        System.Net.IPAddress Subnet = new System.Net.IPAddress(addr);

            //        Log("IPV4 Address: " + x[i].Address.ToString() +
            //            ", Mask: " + x[i].IPv4Mask.ToString() +
            //            ", Subnet: " + Subnet.ToString()
            //            );
            //    }
            //    else if (x[i].Address.AddressFamily == System.Net.Sockets.AddressFamily.InterNetworkV6) //IPV6
            //    {
            //        //Log("IPV6 Address: " + x[i].Address.ToString());
            //    }
            //    else
            //    {
            //        //Log("OTHER Address: " + x[i].Address.ToString());
            //    }
            //}

            test("0.0.0.0");
            test("127.0.0.1");
            test("192.168.0.0/255.255.255.0");
            test("192.168.0.34");


            System.Net.NetworkInformation.IPGlobalProperties computerProperties = System.Net.NetworkInformation.IPGlobalProperties.GetIPGlobalProperties();
            System.Net.NetworkInformation.NetworkInterface[] nics = System.Net.NetworkInformation.NetworkInterface.GetAllNetworkInterfaces();

            Log("Interface information for " + computerProperties.HostName + " . " + computerProperties.DomainName);
            if (nics == null || nics.Length < 1)
            {
                Log("  No network interfaces found.");
                return;
            }
  
            Log("  Number of interfaces .................... : " + nics.Length);
            foreach (System.Net.NetworkInformation.NetworkInterface adapter in nics)
            {
                System.Net.NetworkInformation.IPInterfaceProperties properties = adapter.GetIPProperties();
                Log(adapter.Description);
                Log(String.Empty.PadLeft(adapter.Description.Length, '='));
                Log("  Interface type .......................... : " + adapter.NetworkInterfaceType);
                //Log("  Physical Address ........................ : " + adapter.GetPhysicalAddress().ToString());
                //Log("  Operational status ...................... : " + adapter.OperationalStatus);

                //string versions = "";

                // Create a display string for the supported IP versions.

                //if (adapter.Supports(System.Net.NetworkInformation.NetworkInterfaceComponent.IPv4))
                //{
                //    versions = "IPv4";
                //}
                //if (adapter.Supports(System.Net.NetworkInformation.NetworkInterfaceComponent.IPv6))
                //{
                //    if (versions.Length > 0)
                //    {
                //        versions += " ";
                //    }
                //    versions += "IPv6";
                //}
                //Log("  IP version .............................. : " + versions);

                ShowIPAddresses(properties);

                // The following information is not useful for loopback adapters.

                //if (adapter.NetworkInterfaceType == System.Net.NetworkInformation.NetworkInterfaceType.Loopback)
                //{
                //    continue;
                //}
                //Log("  DNS suffix .............................. : " + properties.DnsSuffix);

                //if (adapter.Supports(System.Net.NetworkInformation.NetworkInterfaceComponent.IPv4))
                //{
                //    System.Net.NetworkInformation.IPv4InterfaceProperties ipv4 = properties.GetIPv4Properties();
                //    Log("  MTU...................................... : " + ipv4.Mtu);
                //}

            }
        }

        public void ShowIPAddresses(System.Net.NetworkInformation.IPInterfaceProperties adapterProperties)
        {
            //System.Net.NetworkInformation.IPAddressCollection dnsServers = adapterProperties.DnsAddresses;
            //if (dnsServers != null)
            //{
            //    foreach (System.Net.IPAddress dns in dnsServers)
            //    {
            //        Log("  DNS Servers ............................. : " + dns.ToString()
            //       );
            //    }
            //}

            //System.Net.NetworkInformation.IPAddressInformationCollection anyCast = adapterProperties.AnycastAddresses;
            //if (anyCast != null)
            //{
            //    foreach (System.Net.NetworkInformation.IPAddressInformation any in anyCast)
            //    {
            //        if (any.Address.AddressFamily == System.Net.Sockets.AddressFamily.InterNetwork) //IPV4
            //            Log("  Anycast Address .......................... : " + any.Address + " " + (any.IsTransient ? "Transient" : "") + " " + (any.IsDnsEligible ? "DNS Eligible" : ""));
            //    }
            //    Console.WriteLine();
            //}

            //System.Net.NetworkInformation.MulticastIPAddressInformationCollection multiCast = adapterProperties.MulticastAddresses;
            //if (multiCast != null)
            //{
            //    foreach (System.Net.NetworkInformation.IPAddressInformation multi in multiCast)
            //    {
            //        if (multi.Address.AddressFamily == System.Net.Sockets.AddressFamily.InterNetwork) //IPV4
            //            Log("  Multicast Address ....................... : " +
            //                multi.Address + " " +
            //                (multi.IsTransient ? "Transient" : "") + " " +
            //                (multi.IsDnsEligible ? "DNS Eligible" : "")
            //            );
            //    }
            //    Console.WriteLine();
            //}

            System.Net.NetworkInformation.UnicastIPAddressInformationCollection uniCast = adapterProperties.UnicastAddresses;
            if (uniCast != null)
            {
                //string lifeTimeFormat = "dddd, MMMM dd, yyyy  hh:mm:ss tt";
                foreach (System.Net.NetworkInformation.UnicastIPAddressInformation uni in uniCast)
                {
                    //DateTime when;

                    if (uni.Address.AddressFamily != System.Net.Sockets.AddressFamily.InterNetwork) continue; //IPV4
                    
                    Log("  Unicast Address ......................... : " + uni.Address);
                    //Log("     Prefix Origin ........................ : " + uni.PrefixOrigin);
                    //Log("     Suffix Origin ........................ : " + uni.SuffixOrigin);
                    //Log("     Duplicate Address Detection .......... : " + uni.DuplicateAddressDetectionState);

                    // Format the lifetimes as Sunday, February 16, 2003 11:33:44 PM

                    // if en-us is the current culture.


                    //// Calculate the date and time at the end of the lifetimes.    

                    //when = DateTime.UtcNow + TimeSpan.FromSeconds(uni.AddressValidLifetime);
                    //when = when.ToLocalTime();
                    //Console.WriteLine("     Valid Life Time ...................... : {0}",
                    //    when.ToString(lifeTimeFormat, System.Globalization.CultureInfo.CurrentCulture)
                    //);
                    //when = DateTime.UtcNow + TimeSpan.FromSeconds(uni.AddressPreferredLifetime);
                    //when = when.ToLocalTime();
                    //Console.WriteLine("     Preferred life time .................. : {0}",
                    //    when.ToString(lifeTimeFormat, System.Globalization.CultureInfo.CurrentCulture)
                    //);

                    //when = DateTime.UtcNow + TimeSpan.FromSeconds(uni.DhcpLeaseLifetime);
                    //when = when.ToLocalTime();
                    //Console.WriteLine("     DHCP Leased Life Time ................ : {0}",
                    //    when.ToString(lifeTimeFormat, System.Globalization.CultureInfo.CurrentCulture)
                    //);
                }
            }
        }
    }


    /// ==============================================================================
    /// ==============================================================================
    /// ==============================================================================

    // ----------------------------------------------------------------------
    /// <summary>
    /// Generic logging interface
    /// </summary>
    public interface ILogListener
    {
        void OnLog(string arg);
    }

    // ----------------------------------------------------------------------
    /// <summary>
    /// Specialized logger class added to Ops.Exceptionlogger to catch internal OPS errors
    /// </summary>
    public class OpsErrorLogger : Ops.LoggerImplementation
    {
        private ILogListener listener;

        public OpsErrorLogger(ILogListener _listener)
        {
            listener = _listener;
            Ops.Logger.ExceptionLogger.AddLogger(this);
        }

        public override void LogError(string error)
        {
            if (listener != null) listener.OnLog("#### OPS Message: " + error);
        }
    }

    // ----------------------------------------------------------------------
    /// <summary>
    /// Interface for callbacks from the COpsHelper class
    /// </summary>
    /// <typeparam name="T">Ops data class</typeparam>
    interface IOpsHelperCallback<T> : ILogListener
        where T : OPSObject
    {
        void SubscriberNewData(Subscriber sender, T data);
    }

    // ----------------------------------------------------------------------
    /// <summary>
    /// Non template based interface definition for the COpsHelper template class,
    /// enabling users to have a uniform handling of different template instances
    /// </summary>
    interface IOpsHelper
    {
        bool CreateSubscriber(Participant part, string topicName, int deadLineEventIntervall);
        void DeleteSubscriber();
        bool CreatePublisher(Participant part, string topicName);
        void DeletePublisher();
        void StartSubscriber(int deadLineEventInterval);
        void StopSubscriber(bool doLog = true);
        void SetDeadLineInterval(int deadLineEventInterval);
        void Write();
    }

    // ----------------------------------------------------------------------
    /// <summary>
    /// Template class for simplifying handling of many topics in the test program
    /// </summary>
    /// <typeparam name="T">Ops data class</typeparam>
    class COpsHelper<T> : IOpsHelper, IObserver
        where T : OPSObject, new()
    {
        private IOpsHelperCallback<T> client = null;
        private Publisher myPub = null;
        private Subscriber mySub = null;
        private T myData = null;

        public T Data { get { return myData; } }

        // 
        public COpsHelper(IOpsHelperCallback<T> client)
        {
            this.client = client;

            // Create data object for publishing
            myData = new T();
        }

        // Internal logging method
        private void Log(String str)
        {
            if (client != null) client.OnLog(str);
        }

        // 
        public bool CreateSubscriber(Participant part, string topicName, int deadLineEventIntervall)
        {
            if (mySub == null)
            {
                Topic topic = part.CreateTopic(topicName);
                if (topic == null)
                {
                    Log("No such Topic '" + topicName + "'. Failed to create Subscriber!");
                    return false;
                }
                else
                {
                    Log("[Topic: " + topic.GetName() + "] Subscriber on Transport: " + topic.GetTransport() + ", " + topic.GetDomainAddress() + "::" + topic.GetPort());
                }
                mySub = new Subscriber(topic);
                mySub.newDataDefault += new NewDataDefaultEventHandler(SubscriberNewData);
                mySub.deadlineEvent.AddObserver(this);
                StartSubscriber(deadLineEventIntervall);
            }
            else
            {
                Log("Subscriber for Topic '" + topicName + "' is already created!");
            }
            return true;    // mySub exists
        }

        // 
        public void DeleteSubscriber()
        {
            if (mySub != null)
            {
                StopSubscriber();
                mySub = null;
            }
            else
            {
                Log("Subscriber must be created first!");
            }
        }

        // 
        public bool CreatePublisher(Participant part, string topicName)
        {
            if (myPub == null)
            {
                Topic topic = part.CreateTopic(topicName);
                if (topic == null)
                {
                    Log("No such Topic '" + topicName + "'. Failed to create Publisher!");
                    return false;
                }
                else
                {
                    Log("[Topic: " + topic.GetName() + "] Publisher on Transport: " + topic.GetTransport() + ", " + topic.GetDomainAddress() + "::" + topic.GetPort());
                }
                myPub = new Publisher(topic);
                myPub.SetName("This is a publisher");
            }
            else
            {
                Log("Publisher for Topic '" + topicName + "' is already created!");
            }
            return true;    // myPub exist
        }

        // 
        public void DeletePublisher()
        {
            if (myPub != null)
            {
                // The Stop() call is necessary when we use TCP as transport. 
                // Otherwise the listening socket and all connections will remain available to others.
                myPub.Stop();
                myPub = null;
            }
            else
            {
                Log("Publisher must be created first!");
            }
        }

        // 
        public void StartSubscriber(int deadLineEventInterval)
        {
            if (mySub != null)
            {
                if (!mySub.Active)
                {
                    Log("Subscriber started for Topic '" + mySub.GetTopic().GetName() + "'");
                    mySub.SetDeadlineQoS(deadLineEventInterval * 1000);
                    mySub.Start();
                }
                else
                {
                    Log("Subscriber is already started for Topic '" + mySub.GetTopic().GetName() + "'");
                }
            }
            else
            {
                Log("Subscriber must be created first!");
            }
        }

        //
        public void StopSubscriber(bool doLog = true)
        {
            if (mySub != null)
            {
                if (mySub.Active)
                {
                    if (doLog) Log("Subscriber stopped for Topic '" + mySub.GetTopic().GetName() + "'");
                    mySub.Stop();
                }
                else
                {
                    if (doLog) Log("Subscriber is already stopped for Topic '" + mySub.GetTopic().GetName() + "'");
                }
            }
            else
            {
                if (doLog) Log("Subscriber must be created first!");
            }
        }

        // 
        public void SetDeadLineInterval(int deadLineEventInterval)
        {
            if (mySub != null)
            {
                if (deadLineEventInterval > 0)
                {
                    Log("Dead Line Notification is enabled for Topic '" + mySub.GetTopic().GetName() + "'");
                }
                else
                {
                    Log("Dead Line Notification is disabled for Topic '" + mySub.GetTopic().GetName() + "'");
                }
                mySub.SetDeadlineQoS(deadLineEventInterval);
            }
            else
            {
                Log("Subscriber must be created first!");
            }
        }

        //
        public void Write()
        {
            if (myPub != null)
            {
                myPub.WriteAsOPSObject(myData);
            }
            else
            {
                Log("Publisher must be created first!");
            }
        }

        //
        public void Update(IObservable o, object arg)
        {
            Log("Dead line event received for Topic '" + mySub.GetTopic().GetName() + "'");
        }

        //
        private void SubscriberNewData(Subscriber sender, OPSObject data)
        {
            if (client != null) client.SubscriberNewData(sender, (T)data);
        }
    }

    // ----------------------------------------------------------------------
    /// <summary>
    /// Helper class for listening to Participant info data
    /// </summary>
    class COpsHelperPartInfoData
    {
        private IOpsHelperCallback<Ops.ParticipantInfoData> client = null;
        private Subscriber mySub = null;

        // 
        public COpsHelperPartInfoData(IOpsHelperCallback<Ops.ParticipantInfoData> client)
        {
            this.client = client;
        }

        // Internal logging method
        private void Log(String str)
        {
            if (client != null) client.OnLog(str);
        }

        // 
        public bool CreateSubscriber(Participant part)
        {
            if (mySub == null)
            {
                Topic topic = part.CreateParticipantInfoTopic();
                if (topic == null)
                {
                    Log("No such Topic 'TBD'. Failed to create Subscriber!");
                    return false;
                }
                else
                {
                    Log("[Topic: " + topic.GetName() + "] Subscriber on Transport: " + topic.GetTransport() + ", " + topic.GetDomainAddress() + "::" + topic.GetPort());
                }
                mySub = new Subscriber(topic);
                mySub.newDataDefault += new NewDataDefaultEventHandler(SubscriberNewData);
                StartSubscriber();
            }
            else
            {
                Log("Subscriber for Topic 'TBD' is already created!");
            }
            return true;    // mySub exists
        }

        // 
        public void DeleteSubscriber()
        {
            if (mySub != null)
            {
                StopSubscriber();
                mySub = null;
            }
            else
            {
                Log("Subscriber must be created first!");
            }
        }

        // 
        public void StartSubscriber()
        {
            if (mySub != null)
            {
                if (!mySub.Active)
                {
                    Log("Subscriber started for Topic '" + mySub.GetTopic().GetName() + "'");
                    mySub.Start();
                }
                else
                {
                    Log("Subscriber is already started for Topic '" + mySub.GetTopic().GetName() + "'");
                }
            }
            else
            {
                Log("Subscriber must be created first!");
            }
        }

        //
        public void StopSubscriber(bool doLog = true)
        {
            if (mySub != null)
            {
                if (mySub.Active)
                {
                    if (doLog) Log("Subscriber stopped for Topic '" + mySub.GetTopic().GetName() + "'");
                    mySub.Stop();
                }
                else
                {
                    if (doLog) Log("Subscriber is already stopped for Topic '" + mySub.GetTopic().GetName() + "'");
                }
            }
            else
            {
                if (doLog) Log("Subscriber must be created first!");
            }
        }

        //
        private void SubscriberNewData(Subscriber sender, OPSObject data)
        {
            if (client != null) client.SubscriberNewData(sender, (Ops.ParticipantInfoData)data);
        }
    }

}
