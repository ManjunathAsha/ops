//Auto generated OPS-code. DO NOT MODIFY!

package TestAll;

import ops.OPSObject;
import configlib.ArchiverInOut;
import configlib.SerializableFactory;
import configlib.Serializable;
import java.io.IOException;

public class TestData extends OPSObject
{
	public String text = "";
	public double value;


    private static SerializableFactory factory = new TypeFactory();

    public static String getTypeName(){return "TestAll.TestData";}

    public static SerializableFactory getTypeFactory()
    {
        return factory;
    }

    public TestData()
    {
        super();
        appendType(getTypeName());

    }
    public void serialize(ArchiverInOut archive) throws IOException
    {
        super.serialize(archive);
		text = archive.inout("text", text);
		value = archive.inout("value", value);

    }
    @Override
    public Object clone()
    {
        TestData cloneResult = new TestData();
        fillClone(cloneResult);
        return cloneResult;
    }

    @Override
    public void fillClone(OPSObject cloneO)
    {
        super.fillClone(cloneO);
        TestData cloneResult = (TestData)cloneO;
        		cloneResult.text = this.text;
		cloneResult.value = this.value;

    }

    private static class TypeFactory implements SerializableFactory
    {
        public Serializable create(String type)
        {
            if (type.equals(TestData.getTypeName()))
            {
                return new TestData();
            }
            else
            {
                return null;
            }
        }
    }
}

