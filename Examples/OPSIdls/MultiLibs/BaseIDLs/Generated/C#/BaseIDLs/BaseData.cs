//Auto generated OPS-code. DO NOT MODIFY!

using Ops;
using System;
using System.Collections.Generic;
using System.ComponentModel;

namespace BaseIDLs
{
    // Message

    [Serializable]
    public class BaseData : OPSObject
    {
		/// Comment for Id, Line 1 
		/// Comment for Id, Line 2 
		/// Comment for Id, Line 3 
		public int Id { get; set; }

		/// Comment for Name, Line 1 
		/// Comment for Name, Line 2 
		/// Comment for Name, Line 3 
		public string Name { get; set; }



        public static new string GetTypeName() { return "BaseIDLs.BaseData"; }

        public BaseData() : base()
        {
            AppendType(GetTypeName());

        }

        public override void Serialize(IArchiverInOut archive)
        {
            base.Serialize(archive);
			Id = archive.Inout("Id", Id);
			Name = archive.Inout("Name", Name);

        }
    
        public override object Clone()
        {
            BaseData cloneResult = new BaseData();
            FillClone(cloneResult);
            return cloneResult;
        }

        public override void FillClone(OPSObject cloneO)
        {
            base.FillClone(cloneO);
            BaseData cloneResult = (BaseData)cloneO;
			cloneResult.Id = this.Id;
			cloneResult.Name = this.Name;

        }
    }

    // Publisher

    public class BaseDataPublisher : Publisher
    {
        public BaseDataPublisher(Topic t) : base(t)
        {
            CheckTypeString(BaseData.GetTypeName());
        }

        public void Write(BaseData o)
        {
            base.Write(o);
        }
    }

    // Subscriber

    public delegate void BaseDataEventHandler(BaseDataSubscriber sender, BaseData data);

    public class BaseDataSubscriber : Subscriber
    {
        public event BaseDataEventHandler newData;

        public BaseDataSubscriber(Topic t) : base(t)
        {
            CheckTypeString(BaseData.GetTypeName());
        }

        protected override void NewDataArrived(OPSObject o)
        {
            if (newData != null) newData(this, (BaseData)o);
        }

        public BaseData WaitForNextData(long millis)
        {
            return (BaseData)WaitForNextOpsObjectData(millis);
        }
    }

}
