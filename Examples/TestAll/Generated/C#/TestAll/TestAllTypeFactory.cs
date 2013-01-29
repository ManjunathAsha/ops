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
			if (type.Equals("TestAll.ChildData"))
			{
				return new TestAll.ChildData();
			}
			if (type.Equals("TestAll.BaseData"))
			{
				return new TestAll.BaseData();
			}
			if (type.Equals("TestAll.TestData"))
			{
				return new TestAll.TestData();
			}
			if (type.Equals("TestAll.Fruit"))
			{
				return new TestAll.Fruit();
			}
			return null;

        }
    }
}
