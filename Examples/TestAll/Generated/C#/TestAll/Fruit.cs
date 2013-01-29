//Auto generated OPS-code. DO NOT MODIFY!

using Ops;

namespace TestAll
{
    public class Fruit : Ops.OPSObject
    {
        public enum Value {APPLE,BANANA,PEAR, UNDEFINED};

        public Value value = 0;

        public static new string GetTypeName() { return "TestAll.Fruit"; }

        public Fruit() : base()
        {
            AppendType(GetTypeName());
        }

        public override void Serialize(IArchiverInOut archive)
        {
            base.Serialize(archive);
            value = FromInt(archive.Inout("value", ToInt(value)));
        }
    
        public override object Clone()
        {
            Fruit cloneResult = new Fruit();
            cloneResult.value = this.value;
            return cloneResult;
        }

        private Value FromInt(int i)
        {
            return (Value)i;
            //return Value.UNDEFINED;
        }

        private int ToInt(Value value)
        {
            return (int)value;
        }
    }
}
