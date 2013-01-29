/**
 *
 * OPS generated code, DO NOT MODIFY!
 */
package TestAll;

import configlib.Serializable;
import configlib.SerializableFactory;


public class TestAllTypeFactory implements SerializableFactory
{
    public Serializable create(String type)
    {
		if(type.equals("TestAll.ChildData"))
		{
			return new TestAll.ChildData();
		}
		if(type.equals("TestAll.BaseData"))
		{
			return new TestAll.BaseData();
		}
		if(type.equals("TestAll.TestData"))
		{
			return new TestAll.TestData();
		}
		if(type.equals("TestAll.Fruit"))
		{
			return new TestAll.Fruit();
		}
		return null;

    }
}
