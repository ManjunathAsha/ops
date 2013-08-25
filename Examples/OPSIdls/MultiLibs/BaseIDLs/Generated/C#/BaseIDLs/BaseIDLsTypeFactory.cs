/**
 *
 * OPS generated code, DO NOT MODIFY!
 */

using Ops;

namespace BaseIDLs
{
    public class BaseIDLsTypeFactory : ISerializableFactory
    {
        public ISerializable Create(string type)
        {
			if (type.Equals("BaseIDLs.BaseData"))
			{
				return new BaseIDLs.BaseData();
			}
			return null;

        }
    }
}
