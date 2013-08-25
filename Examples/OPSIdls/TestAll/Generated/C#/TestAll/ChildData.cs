//Auto generated OPS-code. DO NOT MODIFY!

using Ops;
using System;
using System.Collections.Generic;
using System.ComponentModel;

namespace TestAll
{
    // Message

    [Serializable]
    public class ChildData : BaseData
    {
		/// 
		public bool bo { get; set; }

		/// 
		public byte b { get; set; }

		/// 
		public int i { get; set; }

		/// 
		public long l { get; set; }

		/// 
		public float f { get; set; }

		/// 
		public double d { get; set; }

		/// This string shall hold the word World 
		public string s { get; set; }

		/// 
		private TestData _test2 = new TestData();
		[System.ComponentModel.TypeConverter(typeof(System.ComponentModel.ExpandableObjectConverter))]
		public TestData test2 { get { return  _test2; } set { _test2 = value; } } 

		/// 
		private TestAll.TestData _testPointer = new TestAll.TestData();
		[System.ComponentModel.TypeConverter(typeof(System.ComponentModel.ExpandableObjectConverter))]
		public TestAll.TestData testPointer { get { return  _testPointer; } set { _testPointer = value; } } 

		/// 
		private Fruit _fruit = new Fruit();
		[System.ComponentModel.TypeConverter(typeof(System.ComponentModel.ExpandableObjectConverter))]
		public Fruit fruit { get { return  _fruit; } set { _fruit = value; } } 

		/// 
		private List<bool> _bos = new List<bool>();
		public List<bool> bos { get { return  _bos; } set { _bos = value; } } 

		/// 
		private List<byte> _bs = new List<byte>();
		public List<byte> bs { get { return  _bs; } set { _bs = value; } } 

		/// 
		private List<int> _is_ = new List<int>();
		public List<int> is_ { get { return  _is_; } set { _is_ = value; } } 

		/// 
		private List<long> _ls = new List<long>();
		public List<long> ls { get { return  _ls; } set { _ls = value; } } 

		/// 
		private List<float> _fs = new List<float>();
		public List<float> fs { get { return  _fs; } set { _fs = value; } } 

		/// 
		private List<double> _ds = new List<double>();
		public List<double> ds { get { return  _ds; } set { _ds = value; } } 

		/// 
		private List<string> _ss = new List<string>();
		[Editor(@"System.Windows.Forms.Design.StringCollectionEditor," +
			"System.Design, Version=2.0.0.0, Culture=neutral, PublicKeyToken=b03f5f7f11d50a3a",
			typeof(System.Drawing.Design.UITypeEditor))]
		public List<string> ss { get { return  _ss; } set { _ss = value; } } 

		/// 
		private List<TestData> _test2s = new List<TestData>();
		public List<TestData> test2s { get { return  _test2s; } set { _test2s = value; } } 

		/// 
		private List<TestData> _secondVirtArray = new List<TestData>();
		public List<TestData> secondVirtArray { get { return  _secondVirtArray; } set { _secondVirtArray = value; } } 

		/// 
		private List<TestData> _test2s2 = new List<TestData>();
		public List<TestData> test2s2 { get { return  _test2s2; } set { _test2s2 = value; } } 



        public static new string GetTypeName() { return "TestAll.ChildData"; }

        public ChildData() : base()
        {
            AppendType(GetTypeName());

        }

        public override void Serialize(IArchiverInOut archive)
        {
            base.Serialize(archive);
			bo = archive.Inout("bo", bo);
			b = archive.Inout("b", b);
			i = archive.Inout("i", i);
			l = archive.Inout("l", l);
			f = archive.Inout("f", f);
			d = archive.Inout("d", d);
			s = archive.Inout("s", s);
			_test2 = (TestData) archive.Inout("test2", _test2);
			_testPointer = (TestAll.TestData) archive.Inout("testPointer", _testPointer);
			_fruit = (Fruit) archive.Inout("fruit", _fruit);
			_bos = (List<bool>) archive.InoutBooleanList("bos", _bos);
			_bs = (List<byte>) archive.InoutByteList("bs", _bs);
			_is_ = (List<int>) archive.InoutIntegerList("is_", _is_);
			_ls = (List<long>) archive.InoutLongList("ls", _ls);
			_fs = (List<float>) archive.InoutFloatList("fs", _fs);
			_ds = (List<double>) archive.InoutDoubleList("ds", _ds);
			_ss = (List<string>) archive.InoutStringList("ss", _ss);
			_test2s = (List<TestData>) archive.InoutSerializableList("test2s", _test2s);
			_secondVirtArray = (List<TestData>) archive.InoutSerializableList("secondVirtArray", _secondVirtArray);
			_test2s2 = (List<TestData>) archive.InoutSerializableList("test2s2", _test2s2);

        }
    
        public override object Clone()
        {
            ChildData cloneResult = new ChildData();
            FillClone(cloneResult);
            return cloneResult;
        }

        public override void FillClone(OPSObject cloneO)
        {
            base.FillClone(cloneO);
            ChildData cloneResult = (ChildData)cloneO;
			cloneResult.bo = this.bo;
			cloneResult.b = this.b;
			cloneResult.i = this.i;
			cloneResult.l = this.l;
			cloneResult.f = this.f;
			cloneResult.d = this.d;
			cloneResult.s = this.s;
			cloneResult.test2 = (TestData)this.test2.Clone();
			cloneResult.testPointer = (TestAll.TestData)this.testPointer.Clone();
			cloneResult.fruit = (Fruit)this.fruit.Clone();
			cloneResult.bos = new List<bool>(this.bos);
			cloneResult.bs = new List<byte>(this.bs);
			cloneResult.is_ = new List<int>(this.is_);
			cloneResult.ls = new List<long>(this.ls);
			cloneResult.fs = new List<float>(this.fs);
			cloneResult.ds = new List<double>(this.ds);
			cloneResult.ss = new List<string>(this.ss);
			cloneResult.test2s = new List<TestData>(this.test2s.Count);
			this.test2s.ForEach((item) => { cloneResult.test2s.Add((TestData)item.Clone()); });
			cloneResult.secondVirtArray = new List<TestData>(this.secondVirtArray.Count);
			this.secondVirtArray.ForEach((item) => { cloneResult.secondVirtArray.Add((TestData)item.Clone()); });
			cloneResult.test2s2 = new List<TestData>(this.test2s2.Count);
			this.test2s2.ForEach((item) => { cloneResult.test2s2.Add((TestData)item.Clone()); });

        }
    }

    // Publisher

    public class ChildDataPublisher : Publisher
    {
        public ChildDataPublisher(Topic t) : base(t)
        {
            CheckTypeString(ChildData.GetTypeName());
        }

        public void Write(ChildData o)
        {
            base.Write(o);
        }
    }

    // Subscriber

    public delegate void ChildDataEventHandler(ChildDataSubscriber sender, ChildData data);

    public class ChildDataSubscriber : Subscriber
    {
        public event ChildDataEventHandler newData;

        public ChildDataSubscriber(Topic t) : base(t)
        {
            CheckTypeString(ChildData.GetTypeName());
        }

        protected override void NewDataArrived(OPSObject o)
        {
            if (newData != null) newData(this, (ChildData)o);
        }

        public ChildData WaitForNextData(long millis)
        {
            return (ChildData)WaitForNextOpsObjectData(millis);
        }
    }

}
