/**
 *
 * OPS generated code, DO NOT MODIFY!
 */
package BaseIDLs;

import configlib.Serializable;
import configlib.SerializableFactory;


public class BaseIDLsTypeFactory implements SerializableFactory
{
    public Serializable create(String type)
    {
		if(type.equals("BaseIDLs.BaseData"))
		{
			return new BaseIDLs.BaseData();
		}
		return null;

    }
}
