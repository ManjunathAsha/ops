/*
 * CsCompiler.java
 *
 * Created on den 12 november 2007, 15:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ops.netbeansmodules.idlsupport.compilers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;
import ops.netbeansmodules.idlsupport.projectproperties.JarDependency;
import ops.netbeansmodules.util.FileHelper;
import org.openide.windows.InputOutput;
import parsing.AbstractTemplateBasedIDLCompiler;
import parsing.IDLClass;
import parsing.IDLField;
import parsing.TopicInfo;

/**
 *
 * @author Lelle
 */
public class CsCompiler extends AbstractTemplateBasedIDLCompiler//implements IDLCompiler
{
    final static String CONSTRUCTOR_BODY_REGEX = "__constructorBody";
    final static String DECLARATIONS_REGEX = "__declarations";
    final static String SERIALIZE_REGEX = "__serialize";
    final static String DESERIALIZE_REGEX = "__deserialize";
    final static String CLONE_BODY_REGEX = "__cloneBody";
    final static String SIZE_REGEX = "__size";
    final static String CS_DIR = "C#";
    Vector<IDLClass> idlClasses;
    private String projectDirectory;
    private static String BASE_CLASS_NAME_REGEX = "__baseClassName";
    private static String CREATE_OBJECT_BODY_REGEX = "__createObjectBody";
    private static String CREATE_STRING_BODY_REGEX = "__createStringBody";

    String createdFiles = "";
    private Vector<JarDependency> dllDependencies;


    public void compileDataClasses(Vector<IDLClass> idlClasses, String projectDirectory)
    {
        createdFiles = "";
        this.idlClasses = idlClasses;
        this.projectDirectory = projectDirectory;
        try
        {
            for (IDLClass iDLClass : idlClasses)
            {
                if (iDLClass.getType() == IDLClass.ENUM_TYPE)
                {
                    System.out.println("Compile enum");
                    compileEnum(iDLClass);
                }
                else
                {
                compileDataClass(iDLClass);
                //compileSubscriber(iDLClass);      // We put theese in the same file as the data class
                //compilePublisher(iDLClass);
                }
//            compileHelper(iDLClass);
            }
            compileTypeSupport(idlClasses, extractProjectName(projectDirectory).replace(" ", "_"));
        } catch (IOException iOException)
        {
            JOptionPane.showMessageDialog(null, "Generating C# failed with the following exception: " + iOException.getMessage());
        }

    }

    public void compileTopicConfig(Vector<TopicInfo> topics, String name, String packageString, String projectDirectory)
    {

    }

    protected void setTemplateTextFromResource(String resource) throws IOException
    {
        InputStream templateStream = this.getClass().getResourceAsStream(resource);
        byte[] templateBytes = new byte[templateStream.available()];
        templateStream.read(templateBytes);
        setTemplateText(new String(templateBytes));
    }

    private void compileEnum(IDLClass idlClass) throws IOException
    {
        String className = idlClass.getClassName();

        String packageName = idlClass.getPackageName();
        String packageFilePart = packageName.replace(".", "/");
        setOutputFileName(projectDirectory + CS_DIR + "/" + packageFilePart + "/" + className + ".cs");

        String resource = "/ops/netbeansmodules/idlsupport/templates/csenumtemplate.tpl";
        setTemplateTextFromResource(resource);
        //setTemplateFileName("templates/javatemplate.tpl");
        setTabString("    "); //Default is "\t"
        setEndlString("\n"); //Default is "\r\n"
        //Get the template file as a String
        String templateText = getTemplateText();
        //Replace regular expressions in the template file.
        templateText = templateText.replace(CLASS_NAME_REGEX, className);

        templateText = templateText.replace(PACKAGE_NAME_REGEX, packageName);

        templateText = templateText.replace(DECLARATIONS_REGEX, getEnumDeclarations(idlClass));

        //Save the modified text to the output file.
        saveOutputText(templateText);
        createdFiles += "\"" + getOutputFileName() + "\"\n";
    }

    void compileDataClass(IDLClass idlClass) throws IOException
    {

       
            String className = idlClass.getClassName();
            String baseClassName = "OPSObject";
            if (idlClass.getBaseClassName() != null)
            {
                baseClassName = idlClass.getBaseClassName();
            }
            String packageName = idlClass.getPackageName();
            String packageFilePart = packageName.replace(".", "/");
            setOutputFileName(projectDirectory + CS_DIR + "/" + packageFilePart + "/" + className + ".cs");

            String resource = "/ops/netbeansmodules/idlsupport/templates/cstemplate.tpl";
            setTemplateTextFromResource(resource);
            //setTemplateFileName("templates/javatemplate.tpl");
            setTabString("    "); //Default is "\t"
            setEndlString("\n"); //Default is "\r\n"
            //Get the template file as a String
            String templateText = getTemplateText();
            //Replace regular expressions in the template file.
            templateText = templateText.replace(CLASS_NAME_REGEX, className);
            templateText = templateText.replace(BASE_CLASS_NAME_REGEX, baseClassName);
            templateText = templateText.replace(PACKAGE_NAME_REGEX, packageName);
            templateText = templateText.replace(CONSTRUCTOR_BODY_REGEX, getConstructorBody(idlClass));
            templateText = templateText.replace(DECLARATIONS_REGEX, getDeclarations(idlClass));
            templateText = templateText.replace(SERIALIZE_REGEX, getSerialize(idlClass));
            templateText = templateText.replace(CLONE_BODY_REGEX, getCloneBody(idlClass));
            //Save the modified text to the output file.
            saveOutputText(templateText);
            createdFiles += "\"" + getOutputFileName() + "\"\n";
    }

    public String getName()
    {
        return "CsFactoryIDLCompiler";
    }

//    private void compilePublisher(IDLClass idlClass) throws IOException
//    {
//        String className = idlClass.getClassName();
//        String packageName = idlClass.getPackageName();
//
//        String packageFilePart = packageName.replace(".", "/");
//        setOutputFileName(projectDirectory + CS_DIR + "/" + packageFilePart + "/" + className + "Publisher.cs");
//        String resource = "/ops/netbeansmodules/idlsupport/templates/cspublishertemplate.tpl";
//        setTemplateTextFromResource(resource);
//        //setTemplateFileName("templates/javapublishertemplate.tpl");
//        setTabString("    ");//Default is "\t"
//        setEndlString("\n");//Default is "\r\n"
//
//        //Get the template file as a String
//        String templateText = getTemplateText();
//
//        //Replace regular expressions in the template file.
//        templateText = templateText.replace(CLASS_NAME_REGEX, className);
//        templateText = templateText.replace(PACKAGE_NAME_REGEX, packageName);
//
//        //Save the modified text to the output file.
//        saveOutputText(templateText);
//
//        createdFiles += "\"" + getOutputFileName() + "\"\n";
//    }

//    private void compileSubscriber(IDLClass idlClass) throws IOException
//    {
//        String className = idlClass.getClassName();
//        String packageName = idlClass.getPackageName();
//
//        String packageFilePart = packageName.replace(".", "/");
//        setOutputFileName(projectDirectory + CS_DIR + "/" + packageFilePart + "/" + className + "Subscriber.cs");
//        String resource = "/ops/netbeansmodules/idlsupport/templates/cssubscribertemplate.tpl";
//        setTemplateTextFromResource(resource);
//        //setTemplateFileName("templates/javasubscribertemplate.tpl");
//        setTabString("    ");//Default is "\t"
//        setEndlString("\n");//Default is "\r\n"
//
//        //Get the template file as a String
//        String templateText = getTemplateText();
//
//        //Replace regular expressions in the template file.
//        templateText = templateText.replace(CLASS_NAME_REGEX, className);
//        templateText = templateText.replace(PACKAGE_NAME_REGEX, packageName);
//
//        //Save the modified text to the output file.
//        saveOutputText(templateText);
//
//        createdFiles += "\"" + getOutputFileName() + "\"\n";
//    }

    private void compileTypeSupport(Vector<IDLClass> idlClasses, String projectName) throws IOException
    {
        String className = projectName + "TypeFactory";
        String packageName = projectName;

        String packageFilePart = packageName.replace(".", "/");
        setOutputFileName(projectDirectory + CS_DIR + "/" + projectName + "/" + className + ".cs");
         String resource = "/ops/netbeansmodules/idlsupport/templates/cstypefactorytemplate.tpl";
        setTemplateTextFromResource(resource);
        //setTemplateFileName("templates/javatypefactorytemplate.tpl");
        setTabString("    ");//Default is "\t"
        setEndlString("\n");//Default is "\r\n"

        //Get the template file as a String
        String templateText = getTemplateText();

        //Replace regular expressions in the template file.
        templateText = templateText.replace(CLASS_NAME_REGEX, className);
        templateText = templateText.replace(PACKAGE_NAME_REGEX, packageName);

        //Save the modified text to the output file.

        String createObjectBodyText = "";
        String createStringBodyText = "";

        for (IDLClass iDLClass : idlClasses)
        {
            createObjectBodyText += tab(3) + "if (type.Equals(\"" + iDLClass.getPackageName() + "." + iDLClass.getClassName() + "\"))" + endl();
            createObjectBodyText += tab(3) + "{" + endl();
            createObjectBodyText += tab(4) +      "return new " + iDLClass.getPackageName() + "." + iDLClass.getClassName() + "();" + endl();
            createObjectBodyText += tab(3) + "}" + endl();

            createStringBodyText += tab(3) + "if (obj is " + iDLClass.getPackageName() + "." + iDLClass.getClassName() + ")" + endl();
            createStringBodyText += tab(3) + "{" + endl();
            createStringBodyText += tab(4) +    "return \"" + iDLClass.getPackageName() + "." + iDLClass.getClassName() + "\";" + endl();
            createStringBodyText += tab(3) + "}" + endl();
        }
        createObjectBodyText += tab(3) + "return null;" + endl();
        createStringBodyText += tab(3) + "return null;" + endl();

        templateText = templateText.replace(CREATE_OBJECT_BODY_REGEX, createObjectBodyText);
        templateText = templateText.replace(CREATE_STRING_BODY_REGEX, createStringBodyText);
        saveOutputText(templateText);

        createdFiles += "\"" + getOutputFileName() + "\"\n";
    }

    private String extractProjectName(String projectDirectory)
    {
        String projectName = projectDirectory.substring(0, projectDirectory.lastIndexOf("/Generated/"));
        projectName = projectDirectory.substring(projectName.lastIndexOf("/") + 1 , projectName.length());
        return projectName;
    }

    private String getConstructorBody(IDLClass idlClass)
    {
        String ret = "";
        return ret;
    }

    private String getCloneBody(IDLClass idlClass)
    {
        String ret = "";
        for (IDLField field : idlClass.getFields())
        {
            if (field.isIdlType())
            {
                if (!field.isArray())
                {
                    ret += tab(3) + "cloneResult." +  field.getName() + " = (" + field.getType() + ")this." + field.getName() + ".Clone();" + endl();
                }
                else
                {
                    String s = field.getType();
                    s = s.substring(0, s.indexOf('['));
                    ret += tab(3) + "cloneResult." +  field.getName() + " = new " + languageType(field.getType()) + "(this." + field.getName() + ".Count);" + endl();
                    ret += tab(3) + "this." +  field.getName() + ".ForEach((item) => { cloneResult." + field.getName() + ".Add((" + s + ")item.Clone()); });" + endl();
                }
            }

            //"Arrays.copyOf(original, newLength)";
            else if (field.isArray())
            {

                ret += tab(3) + "cloneResult." +  field.getName() + " = new " + languageType(field.getType()) + "(this." + field.getName() + ");" + endl();

            }
            else
            {
                ret += tab(3) + "cloneResult." +  field.getName() + " = this." + field.getName() + ";" + endl();
            }

        }
        return ret;
    }



    private String getEnumDeclarations(IDLClass idlClass)
    {
        String ret = "";
        for (int i = 0; i < idlClass.getEnumNames().size(); i++)
        {
            ret += idlClass.getEnumNames().get(i);
            ret += ",";
        }
        return ret;
    }

    private String getDeclarations(IDLClass idlClass)
    {
        String ret = "";
        for (IDLField field : idlClass.getFields())
        {
            if(!field.getComment().equals(""))
            {
                String comment = field.getComment();
                int idx;
                while ((idx = comment.indexOf('\n')) >= 0) {
                  ret += tab(2) + "///" + comment.substring(0,idx).replace("/*", "").replace("*/", "") + endl();
                  comment = comment.substring(idx+1);
                }
                ret += tab(2) + "///" + comment.replace("/*", "").replace("*/", "") + endl();
///                ret += tab(2) + "///" + field.getComment().replace("/*", "").replace("*/", "") + endl();
///                ret += tab(2) + "///" + field.getComment() + endl();
            }
            if (field.isArray())
            {
                ret += tab(2) + "private " + languageType(field.getType()) + " _" + field.getName() +
                        " = new " + languageType(field.getType()) + "();" + endl();
                if(field.getType().equals("string[]")) {
                    ret += tab(2) + "[Editor(@\"System.Windows.Forms.Design.StringCollectionEditor,\" +" + endl() +
                           tab(3) + "\"System.Design, Version=2.0.0.0, Culture=neutral, PublicKeyToken=b03f5f7f11d50a3a\"," + endl() +
                           tab(3) + "typeof(System.Drawing.Design.UITypeEditor))]" + endl();
                }
                ret += tab(2) + "public " + languageType(field.getType()) + " " + field.getName() +
                        " { get { return " + " _" + field.getName() + "; } set { _" + field.getName() + " = value; } } " + endl() + endl();
            }
            else if(field.getType().equals("string"))
            {
                ///TEST gives a description and category in a propertygrid
                /// ret += tab(2) + "[Description(\"TBD\"), Category(\"" + idlClass.getClassName() + "\")]" + endl();
                ///TEST
                ret += tab(2) + "public " + languageType(field.getType()) + " " + field.getName() + " { get; set; }" + endl() + endl();
            }
            else if(field.isIdlType())
            {
                    ret += tab(2) + "private " + languageType(field.getType()) + " _" + field.getName() +
                            " = new " + languageType(field.getType()) + "();" + endl();
                    ret += tab(2) + "[System.ComponentModel.TypeConverter(typeof(System.ComponentModel.ExpandableObjectConverter))]" + endl();
                    ret += tab(2) + "public " + languageType(field.getType()) + " " + field.getName() +
                            " { get { return " + " _" + field.getName() + "; } set { _" + field.getName() + " = value; } } " + endl() + endl();
            }
            else //Simple primitive type
            {
                    ret += tab(2) + "public " + languageType(field.getType()) + " " + field.getName() + " { get; set; }" + endl() + endl();
            }

        }
        return ret;
    }

    protected String languageType(String s)
    {
        if (s.equals("string"))
        {
            return "string";
        }
        else if (s.equals("boolean"))
        {
            return "bool";
        }
        else if (s.equals("int"))
        {
            return "int";
        }
        else if (s.equals("long"))
        {
            return "long";
        }
        else if (s.equals("double"))
        {
            return "double";
        }
        else if (s.equals("float"))
        {
            return "float";
        }
        else if (s.equals("byte"))
        {
            return "byte";
        }
        else if (s.equals("string[]"))
        {
            return "List<string>";
        }
        else if (s.equals("int[]"))
        {
            return "List<int>";
        }
        else if (s.equals("long[]"))
        {
            return "List<long>";
        }
        else if (s.equals("double[]"))
        {
            return "List<double>";
        }
        else if (s.equals("float[]"))
        {
            return "List<float>";
        }
        else if (s.equals("byte[]"))
        {
            return "List<byte>";
        }
        else if (s.equals("boolean[]"))
        {
            return "List<bool>";
        }
        else if (s.endsWith("[]"))
        {
            return "List<" + s.substring(0, s.indexOf('[')) + ">";
        }
        else if (s.equals("static string"))
        {
            return "static const string";
        }
        return s;
    }


    private String getSerialize(IDLClass idlClass)
    {
        String ret = "";
        for (IDLField field : idlClass.getFields())
        {
            if(field.isIdlType())
            {
                if(!field.isArray())
                {
                    ret += tab(3) + "_" + field.getName() + " = (" + field.getType() + ") archive.Inout(\"" + field.getName() + "\", _" + field.getName() + ");" + endl();
                }
                else
                {
                    ret += tab(3) + "_" + field.getName() + " = (" + languageType(field.getType()) + ") archive.InoutSerializableList(\"" + field.getName() + "\", _" + field.getName() + ");" + endl();

                }
            }
            else if(field.isArray())
            {
                if(field.getType().equals("int[]"))
                {
                    ret += tab(3) + "_" + field.getName() + " = (" + languageType(field.getType()) + ") archive.InoutIntegerList(\"" + field.getName() + "\", _" + field.getName() + ");" + endl();
                }
                else if(field.getType().equals("byte[]"))
                {
                    ret += tab(3) + "_" + field.getName() + " = (" + languageType(field.getType()) + ") archive.InoutByteList(\"" + field.getName() + "\", _" + field.getName() + ");" + endl();
                }
                else if(field.getType().equals("long[]"))
                {
                    ret += tab(3) + "_" + field.getName() + " = (" + languageType(field.getType()) + ") archive.InoutLongList(\"" + field.getName() + "\", _" + field.getName() + ");" + endl();
                }
                else if(field.getType().equals("boolean[]"))
                {
                    ret += tab(3) + "_" + field.getName() + " = (" + languageType(field.getType()) + ") archive.InoutBooleanList(\"" + field.getName() + "\", _" + field.getName() + ");" + endl();
                }
                else if(field.getType().equals("float[]"))
                {
                    ret += tab(3) + "_" + field.getName() + " = (" + languageType(field.getType()) + ") archive.InoutFloatList(\"" + field.getName() + "\", _" + field.getName() + ");" + endl();
                }
                else if(field.getType().equals("double[]"))
                {
                    ret += tab(3) + "_" + field.getName() + " = (" + languageType(field.getType()) + ") archive.InoutDoubleList(\"" + field.getName() + "\", _" + field.getName() + ");" + endl();
                }
                else if(field.getType().equals("string[]"))
                {
                    ret += tab(3) + "_" + field.getName() + " = (" + languageType(field.getType()) + ") archive.InoutStringList(\"" + field.getName() + "\", _" + field.getName() + ");" + endl();
                }
            }
            else
            {
                ret += tab(3) + field.getName() + " = archive.Inout(\"" + field.getName() + "\", " + field.getName() + ");" + endl();
            }

        }
        return ret;
    }

    public void setDllDependencies(Vector<JarDependency> dllDeps)
    {
        this.dllDependencies = dllDeps;
    }

    public void buildDll(String projectDir, InputOutput io) throws IOException, InterruptedException, IOException, IOException
    {
        String projDirUp = projectDirectory.substring(0, projectDirectory.lastIndexOf("/Generated"));
        String projectName = projectDirectory.substring(projDirUp.lastIndexOf("/")+1, projDirUp.length());

        String dllDepString = ""; ///"/r:\"" + projDirUp + "\\OpsLibrary.dll\" ";

        if (dllDependencies != null) {
            for (JarDependency dllDep : dllDependencies)
            {
                dllDepString += "/r:\"" + dllDep.path + "\" ";

//            File jarToBeCopied = new File(projectDir + "/" + jarDep.path + "");
//            File jarCopy = new File(projectDir + "/" + "Generated" + "/" + jarToBeCopied.getName());
//            jarCopy.createNewFile();
//            FileHelper.copyFile(jarToBeCopied, jarCopy);
            }
        }

        String cscPath = System.getenv("OPS_CSC_PATH");
        if (cscPath == null) {
            io.getOut().println("Info: Path to C# compiler \"csc.exe\" can be set using env. symbol OPS_CSC_PATH");
            cscPath = "csc.exe";
        } else {
            cscPath += "\\csc.exe";
            io.getOut().println("Info: C# compiler \"" + cscPath + "\" used (from env. symbol OPS_CSC_PATH)");
        }

        String  execString = "\"" + cscPath + "\" /target:library " + 
                "/out:\"" + projectDirectory + CS_DIR + "\\" + projectName + ".dll\" " +
                dllDepString + " /recurse:\"" + projectDirectory + CS_DIR + "\\*.cs\"";
        String  batFileText = "echo Building C#..."  + "\r\n";
        batFileText += execString + "\r\n";
        batFileText += "echo done."  + "\r\n";
        batFileText += "pause\r\n";
        batFileText += "exit\r\n";

        projDirUp = projDirUp.replace('/', '\\');

        FileHelper.createAndWriteFile(projDirUp + "/cs_build_script.bat", batFileText);

//        //Process p = Runtime.getRuntime().exec(jarPackString);
//        //Process p = Runtime.getRuntime().exec("java_build_script.bat");
//        //Thread.sleep((1000));
//        //p.waitFor();
        Runtime rTime = Runtime.getRuntime();
        io.getOut().println("Info: cmd /c start /D \"" + projDirUp + "\" cs_build_script.bat");
        Process process = rTime.exec("cmd /c start /D \"" + projDirUp + "\" cs_build_script.bat");

//            InputStream p_in = process.getInputStream();
//            OutputStream p_out = process.getOutputStream();
//            InputStream p_err = process.getErrorStream();
        process.waitFor();
//            p_in.close();
//            p_out.close();
//            p_err.close();
    }

}
