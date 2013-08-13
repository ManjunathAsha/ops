/**
 *
 * OPS generated code, DO NOT MODIFY!
 */

using Ops;

namespace PizzaProject
{
    public class PizzaProjectTypeFactory : ISerializableFactory
    {
        public ISerializable Create(string type)
        {
			if (type.Equals("pizza.VessuvioData"))
			{
				return new pizza.VessuvioData();
			}
			if (type.Equals("pizza.CapricosaData"))
			{
				return new pizza.CapricosaData();
			}
			if (type.Equals("pizza.special.LHCData"))
			{
				return new pizza.special.LHCData();
			}
			if (type.Equals("pizza.special.ExtraAllt"))
			{
				return new pizza.special.ExtraAllt();
			}
			if (type.Equals("pizza.special.Cheese"))
			{
				return new pizza.special.Cheese();
			}
			if (type.Equals("pizza.PizzaData"))
			{
				return new pizza.PizzaData();
			}
			return null;

        }
    }
}
