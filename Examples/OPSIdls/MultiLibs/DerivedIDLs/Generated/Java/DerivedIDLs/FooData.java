//Auto generated OPS-code. DO NOT MODIFY!

package DerivedIDLs;

import ops.OPSObject;
import configlib.ArchiverInOut;
import configlib.SerializableFactory;
import configlib.Serializable;
import java.io.IOException;

public class FooData extends BaseIDLs.BaseData
{
	public String fooString = "";


    private static SerializableFactory factory = new TypeFactory();

    public static String getTypeName(){return "DerivedIDLs.FooData";}

    public static SerializableFactory getTypeFactory()
    {
        return factory;
    }

    public FooData()
    {
        super();
        appendType(getTypeName());

    }
    public void serialize(ArchiverInOut archive) throws IOException
    {
        super.serialize(archive);
		fooString = archive.inout("fooString", fooString);

    }
    @Override
    public Object clone()
    {
        FooData cloneResult = new FooData();
        fillClone(cloneResult);
        return cloneResult;
    }

    @Override
    public void fillClone(OPSObject cloneO)
    {
        super.fillClone(cloneO);
        FooData cloneResult = (FooData)cloneO;
        		cloneResult.fooString = this.fooString;

    }

    private static class TypeFactory implements SerializableFactory
    {
        public Serializable create(String type)
        {
            if (type.equals(FooData.getTypeName()))
            {
                return new FooData();
            }
            else
            {
                return null;
            }
        }
    }
}

