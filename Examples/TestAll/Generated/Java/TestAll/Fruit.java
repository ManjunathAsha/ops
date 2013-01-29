//Auto generated OPS-code. DO NOT MODIFY!

package TestAll;

public final class Fruit extends ops.OPSObject implements ops.OPSEnum
{
    public static String getTypeName(){return "TestAll.Fruit";}

    public static enum Value {APPLE,BANANA,PEAR, UNDEFINED};
    public Value value = Value.values()[0];

    public Fruit()
    {
        super();
        appendType(getTypeName());
    }
    public Fruit(Value value)
    {
        super();
        appendType(getTypeName());
    }
    public void setByValueString(String valueString)
    {
         for(Value val : Value.values())
         {
             if(val.name().equals(valueString))
                 value = val;
         }
    }
    public void serialize(configlib.ArchiverInOut archive) throws java.io.IOException
    {
        super.serialize(archive);
        value = fromInt(archive.inout("value", toInt(value)));
    }
    @Override
    public Object clone()
    {
        Fruit cloneResult = new Fruit();
        cloneResult.value = this.value;
        return cloneResult;
    }
    static Value fromInt(int i)
    {
        if(i < Value.values().length)
        {
            return Value.values()[i];
        }
        return Value.UNDEFINED;
    }
    static int toInt(Value value)
    {
        return value.ordinal();

    }
}

