//Auto generated OPS-code. DO NOT MODIFY!

package TestAll;

import ops.OPSObject;
import configlib.ArchiverInOut;
import configlib.SerializableFactory;
import configlib.Serializable;
import java.io.IOException;

public class BaseData extends OPSObject
{
	public String baseText = "";


    private static SerializableFactory factory = new TypeFactory();

    public static String getTypeName(){return "TestAll.BaseData";}

    public static SerializableFactory getTypeFactory()
    {
        return factory;
    }

    public BaseData()
    {
        super();
        appendType(getTypeName());

    }
    public void serialize(ArchiverInOut archive) throws IOException
    {
        super.serialize(archive);
		baseText = archive.inout("baseText", baseText);

    }
    @Override
    public Object clone()
    {
        BaseData cloneResult = new BaseData();
        fillClone(cloneResult);
        return cloneResult;
    }

    @Override
    public void fillClone(OPSObject cloneO)
    {
        super.fillClone(cloneO);
        BaseData cloneResult = (BaseData)cloneO;
        		cloneResult.baseText = this.baseText;

    }

    private static class TypeFactory implements SerializableFactory
    {
        public Serializable create(String type)
        {
            if (type.equals(BaseData.getTypeName()))
            {
                return new BaseData();
            }
            else
            {
                return null;
            }
        }
    }
}

