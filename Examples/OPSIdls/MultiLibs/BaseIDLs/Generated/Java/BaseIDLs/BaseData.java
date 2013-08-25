//Auto generated OPS-code. DO NOT MODIFY!

package BaseIDLs;

import ops.OPSObject;
import configlib.ArchiverInOut;
import configlib.SerializableFactory;
import configlib.Serializable;
import java.io.IOException;

public class BaseData extends OPSObject
{
	/// Comment for Id, Line 1 
	/// Comment for Id, Line 2 
	/// Comment for Id, Line 3 
	public int Id;
	/// Comment for Name, Line 1 
	/// Comment for Name, Line 2 
	/// Comment for Name, Line 3 
	public String Name = "";


    private static SerializableFactory factory = new TypeFactory();

    public static String getTypeName(){return "BaseIDLs.BaseData";}

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
		Id = archive.inout("Id", Id);
		Name = archive.inout("Name", Name);

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
        		cloneResult.Id = this.Id;
		cloneResult.Name = this.Name;

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

