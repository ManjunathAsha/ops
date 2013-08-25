/**
 *
 * OPS generated code, DO NOT MODIFY!
 */
package DerivedIDLs;

import configlib.Serializable;
import configlib.SerializableFactory;


public class DerivedIDLsTypeFactory implements SerializableFactory
{
    public Serializable create(String type)
    {
		if(type.equals("DerivedIDLs.FooData"))
		{
			return new DerivedIDLs.FooData();
		}
		return null;

    }
}
