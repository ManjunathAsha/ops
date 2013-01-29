//Auto generated OPS-code. DO NOT MODIFY!

package TestAll;

import ops.OPSObject;
import configlib.ArchiverInOut;
import configlib.SerializableFactory;
import configlib.Serializable;
import java.io.IOException;

public class ChildData extends BaseData
{
	/// 
	public boolean bo;
	/// 
	public byte b;
	/// 
	public int i;
	/// 
	public long l;
	/// 
	public float f;
	/// 
	public double d;
	/// This string shall hold the word World 
	public String s = "";
	/// 
	public TestData test2 = new TestData();
	/// 
	public TestAll.TestData testPointer = new TestAll.TestData();
	/// 
	public Fruit fruit = new Fruit();
	/// 
	public java.util.Vector<Boolean> bos = new java.util.Vector<Boolean>();
	/// 
	public java.util.Vector<Byte> bs = new java.util.Vector<Byte>();
	/// 
	public java.util.Vector<Integer> is_ = new java.util.Vector<Integer>();
	/// 
	public java.util.Vector<Long> ls = new java.util.Vector<Long>();
	/// 
	public java.util.Vector<Float> fs = new java.util.Vector<Float>();
	/// 
	public java.util.Vector<Double> ds = new java.util.Vector<Double>();
	/// 
	public java.util.Vector<String> ss = new java.util.Vector<String>();
	/// 
	public java.util.Vector<TestData> test2s = new java.util.Vector<TestData>();
	/// 
	public java.util.Vector<TestData> secondVirtArray = new java.util.Vector<TestData>();
	/// 
	public java.util.Vector<TestData> test2s2 = new java.util.Vector<TestData>();


    private static SerializableFactory factory = new TypeFactory();

    public static String getTypeName(){return "TestAll.ChildData";}

    public static SerializableFactory getTypeFactory()
    {
        return factory;
    }

    public ChildData()
    {
        super();
        appendType(getTypeName());

    }
    public void serialize(ArchiverInOut archive) throws IOException
    {
        super.serialize(archive);
		bo = archive.inout("bo", bo);
		b = archive.inout("b", b);
		i = archive.inout("i", i);
		l = archive.inout("l", l);
		f = archive.inout("f", f);
		d = archive.inout("d", d);
		s = archive.inout("s", s);
		test2 = (TestData) archive.inout("test2", test2);
		testPointer = (TestAll.TestData) archive.inout("testPointer", testPointer);
		fruit = (Fruit) archive.inout("fruit", fruit);
		bos = (java.util.Vector<Boolean>) archive.inoutBooleanList("bos", bos);
		bs = (java.util.Vector<Byte>) archive.inoutByteList("bs", bs);
		is_ = (java.util.Vector<Integer>) archive.inoutIntegerList("is_", is_);
		ls = (java.util.Vector<Long>) archive.inoutLongList("ls", ls);
		fs = (java.util.Vector<Float>) archive.inoutFloatList("fs", fs);
		ds = (java.util.Vector<Double>) archive.inoutDoubleList("ds", ds);
		ss = (java.util.Vector<String>) archive.inoutStringList("ss", ss);
		test2s = (java.util.Vector<TestData>) archive.inoutSerializableList("test2s", test2s);
		secondVirtArray = (java.util.Vector<TestData>) archive.inoutSerializableList("secondVirtArray", secondVirtArray);
		test2s2 = (java.util.Vector<TestData>) archive.inoutSerializableList("test2s2", test2s2);

    }
    @Override
    public Object clone()
    {
        ChildData cloneResult = new ChildData();
        fillClone(cloneResult);
        return cloneResult;
    }

    @Override
    public void fillClone(OPSObject cloneO)
    {
        super.fillClone(cloneO);
        ChildData cloneResult = (ChildData)cloneO;
        		cloneResult.bo = this.bo;
		cloneResult.b = this.b;
		cloneResult.i = this.i;
		cloneResult.l = this.l;
		cloneResult.f = this.f;
		cloneResult.d = this.d;
		cloneResult.s = this.s;
		cloneResult.test2 = (TestData)this.test2.clone();
		cloneResult.testPointer = (TestAll.TestData)this.testPointer.clone();
		cloneResult.fruit = (Fruit)this.fruit.clone();
		java.util.Collections.copy(cloneResult.bos, this.bos);
		java.util.Collections.copy(cloneResult.bs, this.bs);
		java.util.Collections.copy(cloneResult.is_, this.is_);
		java.util.Collections.copy(cloneResult.ls, this.ls);
		java.util.Collections.copy(cloneResult.fs, this.fs);
		java.util.Collections.copy(cloneResult.ds, this.ds);
		java.util.Collections.copy(cloneResult.ss, this.ss);
		java.util.Collections.copy(cloneResult.test2s, this.test2s);
		java.util.Collections.copy(cloneResult.secondVirtArray, this.secondVirtArray);
		java.util.Collections.copy(cloneResult.test2s2, this.test2s2);

    }

    private static class TypeFactory implements SerializableFactory
    {
        public Serializable create(String type)
        {
            if (type.equals(ChildData.getTypeName()))
            {
                return new ChildData();
            }
            else
            {
                return null;
            }
        }
    }
}

