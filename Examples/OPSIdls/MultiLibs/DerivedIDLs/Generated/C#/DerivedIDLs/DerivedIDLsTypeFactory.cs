/**
 *
 * OPS generated code, DO NOT MODIFY!
 */

using Ops;

namespace DerivedIDLs
{
    public class DerivedIDLsTypeFactory : ISerializableFactory
    {
        public ISerializable Create(string type)
        {
			if (type.Equals("DerivedIDLs.FooData"))
			{
				return new DerivedIDLs.FooData();
			}
			return null;

        }
    }
}
