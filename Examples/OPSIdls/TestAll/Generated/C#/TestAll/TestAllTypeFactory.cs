/**
 *
 * OPS generated code, DO NOT MODIFY!
 */

using Ops;

namespace TestAll
{
    public class TestAllTypeFactory : ISerializableFactory
    {
        public ISerializable Create(string type)
        {
			if (type.Equals("TestAll.TestData"))
			{
				return new TestAll.TestData();
			}
			if (type.Equals("TestAll.BaseData"))
			{
				return new TestAll.BaseData();
			}
			if (type.Equals("TestAll.Fruit"))
			{
				return new TestAll.Fruit();
			}
			if (type.Equals("TestAll.ChildData"))
			{
				return new TestAll.ChildData();
			}
			return null;

        }

        public string Create(object obj)
        {
			if (obj is TestAll.TestData)
			{
				return "TestAll.TestData";
			}
			if (obj is TestAll.BaseData)
			{
				return "TestAll.BaseData";
			}
			if (obj is TestAll.Fruit)
			{
				return "TestAll.Fruit";
			}
			if (obj is TestAll.ChildData)
			{
				return "TestAll.ChildData";
			}
			return null;

        }

    }
}
