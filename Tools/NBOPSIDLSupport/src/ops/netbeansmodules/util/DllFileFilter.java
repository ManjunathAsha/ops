/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ops.netbeansmodules.util;

import java.io.File;
import javax.swing.filechooser.FileFilter;


/**
 *
 * @author angr
 */
public class DllFileFilter extends FileFilter
{

    public boolean accept(File f)
    {
        String extension = FileHelper.getExtension(f);
        if(f.isDirectory())
            return true;
        else if(extension == null)
            return false;
        else if(extension.equals("dll"))
            return true;
        else
            return false;
    }

    public String getDescription()
    {
        return "C# Library files (.dll)";
    }
}
