//Auto generated OPS-code. DO NOT MODIFY!

using Ops;
using System;
using System.Collections.Generic;
using System.ComponentModel;

namespace DerivedIDLs
{
    // Message

    [Serializable]
    public class FooData : BaseIDLs.BaseData
    {
		public string fooString { get; set; }



        public static new string GetTypeName() { return "DerivedIDLs.FooData"; }

        public FooData() : base()
        {
            AppendType(GetTypeName());

        }

        public override void Serialize(IArchiverInOut archive)
        {
            base.Serialize(archive);
			fooString = archive.Inout("fooString", fooString);

        }
    
        public override object Clone()
        {
            FooData cloneResult = new FooData();
            FillClone(cloneResult);
            return cloneResult;
        }

        public override void FillClone(OPSObject cloneO)
        {
            base.FillClone(cloneO);
            FooData cloneResult = (FooData)cloneO;
			cloneResult.fooString = this.fooString;

        }
    }

    // Publisher

    public class FooDataPublisher : Publisher
    {
        public FooDataPublisher(Topic t) : base(t)
        {
            CheckTypeString(FooData.GetTypeName());
        }

        public void Write(FooData o)
        {
            base.Write(o);
        }
    }

    // Subscriber

    public delegate void FooDataEventHandler(FooDataSubscriber sender, FooData data);

    public class FooDataSubscriber : Subscriber
    {
        public event FooDataEventHandler newData;

        public FooDataSubscriber(Topic t) : base(t)
        {
            CheckTypeString(FooData.GetTypeName());
        }

        protected override void NewDataArrived(OPSObject o)
        {
            if (newData != null) newData(this, (FooData)o);
        }

        public FooData WaitForNextData(long millis)
        {
            return (FooData)WaitForNextOpsObjectData(millis);
        }
    }

}
