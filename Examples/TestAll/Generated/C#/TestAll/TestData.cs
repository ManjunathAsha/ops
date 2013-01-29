//Auto generated OPS-code. DO NOT MODIFY!

using Ops;
using System;
using System.Collections.Generic;
using System.ComponentModel;

namespace TestAll
{
    // Message

    [Serializable]
    public class TestData : OPSObject
    {
		public string text { get; set; }

		public double value { get; set; }



        public static new string GetTypeName() { return "TestAll.TestData"; }

        public TestData() : base()
        {
            AppendType(GetTypeName());

        }

        public override void Serialize(IArchiverInOut archive)
        {
            base.Serialize(archive);
			text = archive.Inout("text", text);
			value = archive.Inout("value", value);

        }
    
        public override object Clone()
        {
            TestData cloneResult = new TestData();
            FillClone(cloneResult);
            return cloneResult;
        }

        public override void FillClone(OPSObject cloneO)
        {
            base.FillClone(cloneO);
            TestData cloneResult = (TestData)cloneO;
			cloneResult.text = this.text;
			cloneResult.value = this.value;

        }
    }

    // Publisher

    public class TestDataPublisher : Publisher
    {
        public TestDataPublisher(Topic t) : base(t)
        {
            CheckTypeString(TestData.GetTypeName());
        }

        public void Write(TestData o)
        {
            base.Write(o);
        }
    }

    // Subscriber

    public delegate void TestDataEventHandler(TestDataSubscriber sender, TestData data);

    public class TestDataSubscriber : Subscriber
    {
        public event TestDataEventHandler newData;

        public TestDataSubscriber(Topic t) : base(t)
        {
            CheckTypeString(TestData.GetTypeName());
        }

        protected override void NewDataArrived(OPSObject o)
        {
            if (newData != null) newData(this, (TestData)o);
        }

        public TestData WaitForNextData(long millis)
        {
            return (TestData)WaitForNextOpsObjectData(millis);
        }
    }

}
