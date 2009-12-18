/*
 * IDLField.java
 *
 * Created on den 12 november 2007, 08:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package parsing;

/**
 *
 * @author angr
 */
public class IDLField
{
    private String name;
    private String type;
    private String comment;
    private String value;
    private boolean idlType;
    private boolean _static;
    private boolean array;
    private boolean _abstract;
    
    /** Creates a new instance of IDLField */
    public IDLField(String name, String type, String comment, String value)
    {
        this.name = name;
        this.type = type;
        this.comment = comment;
        this.value = value;
    }
    public IDLField(String name, String type, String comment)
    {
        this(name, type, comment, "");
        
    }
    public IDLField(String name, String type)
    {
        this(name, type, "", "");
        
    }
    public IDLField()
    {
        this(null, "", "", "");
        
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public boolean isIdlType()
    {
        return idlType;
    }

    public void setIdlType(boolean idlType)
    {
        this.idlType = idlType;
    }

    public boolean isArray()
    {
        return type.endsWith("[]");
    }
    public String toString()
    {
        return getType() + " " + getName();
    }

    public boolean isStatic()
    {
        return _static;
    }

    public void setStatic(boolean _static)
    {
        this._static = _static;
    }

    public boolean isAbstract()
    {
        return _abstract;
    }

    public void setAbstract(boolean _abstract)
    {
        this._abstract = _abstract;
    }


    
}
