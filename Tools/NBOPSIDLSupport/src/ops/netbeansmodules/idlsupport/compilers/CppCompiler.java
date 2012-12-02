/*
 * NewCppCompiler.java
 *
 * Created on den 12 november 2007, 15:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package ops.netbeansmodules.idlsupport.compilers;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JOptionPane;
import parsing.AbstractTemplateBasedIDLCompiler;
import parsing.IDLClass;
import parsing.IDLField;
import parsing.TopicInfo;

/**
 *
 * @author angr
 */
public class CppCompiler extends AbstractTemplateBasedIDLCompiler//implements IDLCompiler
{

    final static String CONSTRUCTOR_BODY_REGEX = "__constructorBody";
    final static String CONSTRUCTOR_HEAD_REGEX = "__constructorHead";
    final static String DESTRUCTOR_BODY_REGEX = "__destructorBody";
    final static String DECLARATIONS_REGEX = "__declarations";
    final static String SERIALIZE_REGEX = "__serialize";
    final static String CLONE_REGEX = "__clone";
    final static String FILL_CLONE_REGEX = "__fillClone";
    final static String UNDERSCORED_PACK_NAME_REGEX = "__underscoredPackName";
    final static String PACKAGE_DECLARATION_REGEX = "__packageDeclaration";
    final static String PACKAGE_CLOSER_REGEX = "__packageCloser";
    final static String IMPORTS_REGEX = "__imports";
    final static String CLASS_COMMENT_REGEX = "__classComment";
    final static String SIZE_REGEX = "__size";
    final static String Cpp_DIR = "C++";
    Vector<IDLClass> idlClasses;
    private String projectDirectory;
    private static String BASE_CLASS_NAME_REGEX = "__baseClassName";
    private static String CREATE_BODY_REGEX = "__createBody";
    String createdFiles = "";

    public void compileDataClasses(Vector<IDLClass> idlClasses, String projectDirectory)
    {
        try
        {
            this.idlClasses = idlClasses;
            this.projectDirectory = projectDirectory;
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
                    compileSubscriber(iDLClass);
                    compilePublisher(iDLClass);
                }
//            compileHelper(iDLClass);
            }
            compileTypeSupport(idlClasses, extractProjectName(projectDirectory).replace(" ", "_"));
        } catch (IOException iOException)
        {
             JOptionPane.showMessageDialog(null, "Generating C++ failed with the following exception: " + iOException.getMessage());
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

    void compileDataClass(IDLClass idlClass) throws IOException
    {
        String className = idlClass.getClassName();
        String baseClassName = "ops::OPSObject";
        if (idlClass.getBaseClassName() != null)
        {
            baseClassName = idlClass.getBaseClassName();
        }
        String packageName = idlClass.getPackageName();

        String packageFilePart = packageName.replace(".", "/");
        setOutputFileName(projectDirectory + Cpp_DIR + "/" + packageFilePart + "/" + className + ".h");
        String resource = "/ops/netbeansmodules/idlsupport/templates/cpptemplate.tpl";
        setTemplateTextFromResource(resource);
        //setTemplateFileName("templates/cpptemplate.tpl");
        setTabString("    ");//Default is "\t"
        setEndlString("\n");//Default is "\r\n"

        //Get the template file as a String
        String templateText = getTemplateText();

        //Replace regular expressions in the template file.
        templateText = templateText.replace(CLASS_NAME_REGEX, className);
        templateText = templateText.replace(CLASS_COMMENT_REGEX, "");
        templateText = templateText.replace(PACKAGE_NAME_REGEX, packageName);
        templateText = templateText.replace(BASE_CLASS_NAME_REGEX, applyLanguagePackageSeparator(baseClassName));
        templateText = templateText.replace(UNDERSCORED_PACK_NAME_REGEX, getUnderscoredPackName(packageName));
        templateText = templateText.replace(PACKAGE_DECLARATION_REGEX, getPackageDeclaration(packageName));
        templateText = templateText.replace(PACKAGE_CLOSER_REGEX, getPackageCloser(packageName));
        templateText = templateText.replace(IMPORTS_REGEX, getImports(idlClass));
        templateText = templateText.replace(CONSTRUCTOR_HEAD_REGEX, getConstructorHead(idlClass));
        templateText = templateText.replace(CONSTRUCTOR_BODY_REGEX, getConstructorBody(idlClass));
        templateText = templateText.replace(DESTRUCTOR_BODY_REGEX, getDestructorBody(idlClass));
        templateText = templateText.replace(DECLARATIONS_REGEX, getDeclarations(idlClass));
        templateText = templateText.replace(SERIALIZE_REGEX, getSerialize(idlClass));
        templateText = templateText.replace(CLONE_REGEX, getClone(idlClass));
        templateText = templateText.replace(FILL_CLONE_REGEX, getFillClone(idlClass));


        //Save the modified text to the output file.
        saveOutputText(templateText);

        createdFiles += "\"" + getOutputFileName() + "\"\n";

    }
     private void compileEnum(IDLClass idlClass) throws IOException
    {
        String className = idlClass.getClassName();

        String packageName = idlClass.getPackageName();
        String packageFilePart = packageName.replace(".", "/");
        setOutputFileName(projectDirectory + Cpp_DIR + "/" + packageFilePart + "/" + className + ".h");

        String resource = "/ops/netbeansmodules/idlsupport/templates/cppenumtemplate.tpl";
        setTemplateTextFromResource(resource);
        //setTemplateFileName("templates/javatemplate.tpl");
        setTabString("    "); //Default is "\t"
        setEndlString("\n"); //Default is "\r\n"
        //Get the template file as a String
        String templateText = getTemplateText();
        //Replace regular expressions in the template file.
        templateText = templateText.replace(CLASS_NAME_REGEX, className);
        templateText = templateText.replace(CLASS_COMMENT_REGEX, "");
        templateText = templateText.replace(PACKAGE_DECLARATION_REGEX, getPackageDeclaration(packageName));
        templateText = templateText.replace(PACKAGE_CLOSER_REGEX, getPackageCloser(packageName));
        templateText = templateText.replace(PACKAGE_NAME_REGEX, packageName);
        templateText = templateText.replace(UNDERSCORED_PACK_NAME_REGEX, getUnderscoredPackName(packageName));

        templateText = templateText.replace(DECLARATIONS_REGEX, getEnumDeclarations(idlClass));

        //Save the modified text to the output file.
        saveOutputText(templateText);
        createdFiles += "\"" + getOutputFileName() + "\"\n";
    }

    private String getEnumDeclarations(IDLClass idlClass)
    {
        String ret = "";
        for (int i = 0; i < idlClass.getEnumNames().size(); i++)
        {
            ret += tab(1) + "const static int " + idlClass.getEnumNames().get(i) + " = " + i + ";" + endl();
            
        }
        ret += tab(1) + "const static int UNDEFINED = " + idlClass.getEnumNames().size() + ";" + endl();
        return ret;
    }


    public String getName()
    {
        return "CppFactoryIDLCompiler";
    }

    private void compilePublisher(IDLClass idlClass) throws IOException
    {
        String className = idlClass.getClassName();
        String packageName = idlClass.getPackageName();

        String packageFilePart = packageName.replace(".", "/");
        setOutputFileName(projectDirectory + Cpp_DIR + "/" + packageFilePart + "/" + className + "Publisher.h");
        String resource = "/ops/netbeansmodules/idlsupport/templates/cpppublishertemplate.tpl";
        setTemplateTextFromResource(resource);
        //setTemplateFileName("templates/cpppublishertemplate.tpl");
        setTabString("    ");//Default is "\t"
        setEndlString("\n");//Default is "\r\n"

        //Get the template file as a String
        String templateText = getTemplateText();

        //Replace regular expressions in the template file.
        templateText = templateText.replace(CLASS_NAME_REGEX, className);
        templateText = templateText.replace(PACKAGE_NAME_REGEX, packageName);
        templateText = templateText.replace(UNDERSCORED_PACK_NAME_REGEX, getUnderscoredPackName(packageName));
        templateText = templateText.replace(PACKAGE_DECLARATION_REGEX, getPackageDeclaration(packageName));
        templateText = templateText.replace(PACKAGE_CLOSER_REGEX, getPackageCloser(packageName));

        //Save the modified text to the output file.
        saveOutputText(templateText);

        createdFiles += "\"" + getOutputFileName() + "\"\n";
    }

    private void compileSubscriber(IDLClass idlClass) throws IOException
    {
        String className = idlClass.getClassName();
        String packageName = idlClass.getPackageName();

        String packageFilePart = packageName.replace(".", "/");
        setOutputFileName(projectDirectory + Cpp_DIR + "/" + packageFilePart + "/" + className + "Subscriber.h");
        String resource = "/ops/netbeansmodules/idlsupport/templates/cppsubscribertemplate.tpl";
        setTemplateTextFromResource(resource);
        //setTemplateFileName("templates/cppsubscribertemplate.tpl");
        setTabString("    ");//Default is "\t"
        setEndlString("\n");//Default is "\r\n"

        //Get the template file as a String
        String templateText = getTemplateText();

        //Replace regular expressions in the template file.
        templateText = templateText.replace(CLASS_NAME_REGEX, className);
        templateText = templateText.replace(PACKAGE_NAME_REGEX, packageName);
        templateText = templateText.replace(UNDERSCORED_PACK_NAME_REGEX, getUnderscoredPackName(packageName));
        templateText = templateText.replace(PACKAGE_DECLARATION_REGEX, getPackageDeclaration(packageName));
        templateText = templateText.replace(PACKAGE_CLOSER_REGEX, getPackageCloser(packageName));

        //Save the modified text to the output file.
        saveOutputText(templateText);

        createdFiles += "\"" + getOutputFileName() + "\"\n";

    }

    private void compileTypeSupport(Vector<IDLClass> idlClasses, String projectName) throws IOException
    {
        String className = projectName + "TypeFactory";
        String packageName = projectName;

        String packageFilePart = packageName.replace(".", "/");
        setOutputFileName(projectDirectory + Cpp_DIR + "/" + projectName + "/" + className + ".h");
        String resource = "/ops/netbeansmodules/idlsupport/templates/cpptypefactorytemplate.tpl";
        setTemplateTextFromResource(resource);
        //setTemplateFileName("templates/cpptypefactorytemplate.tpl");
        setTabString("    ");//Default is "\t"
        setEndlString("\n");//Default is "\r\n"

        //Get the template file as a String
        String templateText = getTemplateText();

        //Replace regular expressions in the template file.
        templateText = templateText.replace(CLASS_NAME_REGEX, className);
        templateText = templateText.replace(PACKAGE_NAME_REGEX, packageName);
        templateText = templateText.replace(UNDERSCORED_PACK_NAME_REGEX, getUnderscoredPackName(packageName));
        templateText = templateText.replace(PACKAGE_DECLARATION_REGEX, getPackageDeclaration(packageName));
        templateText = templateText.replace(PACKAGE_CLOSER_REGEX, getPackageCloser(packageName));

        //Save the modified text to the output file.

        String createBodyText = "";
        String includes = "";

        for (IDLClass iDLClass : idlClasses)
        {
            createBodyText += tab(2) + "if(type == \"" + iDLClass.getPackageName() + "." + iDLClass.getClassName() + "\")" + endl();
            createBodyText += tab(2) + "{" + endl();
            createBodyText += tab(3) + "return new " + applyLanguagePackageSeparator(iDLClass.getPackageName()) + "::" + iDLClass.getClassName() + "();" + endl();
            createBodyText += tab(2) + "}" + endl();

            includes += tab(0) + "#include \"" + getSlashedType(iDLClass.getPackageName()) + "/" + getSlashedType(iDLClass.getClassName()) + ".h\"" + endl();
        }
        createBodyText += tab(2) + "return NULL;" + endl();

        templateText = templateText.replace(CREATE_BODY_REGEX, createBodyText);
        templateText = templateText.replace(IMPORTS_REGEX, includes);
        saveOutputText(templateText);

        createdFiles += "\"" + getOutputFileName() + "\"\n";
    }

    private String elementType(String type)
    {
        return languageType(type.replace("[]", ""));
    }

    private String extractProjectName(String projectDirectory)
    {
        String projectName = projectDirectory.substring(0, projectDirectory.lastIndexOf("/Generated/"));
        projectName = projectDirectory.substring(projectName.lastIndexOf("/") + 1, projectName.length());
        return projectName;

    }

    private String getClone(IDLClass idlClass)
    {
        String ret = tab(2) + idlClass.getClassName() + "* ret = new " + languageType(idlClass.getClassName()) + ";" + endl();
        ret += tab(2) + "this->fillClone(ret);" + endl();
        ret += tab(2) + "return ret;" + endl();

        return ret;

    }

    private String getFillClone(IDLClass idlClass)
    {
        String ret = tab(2) + idlClass.getClassName() + "* narrRet = (" + languageType(idlClass.getClassName()) + "*)obj;" + endl();

        if (idlClass.getBaseClassName() != null)
        {
            ret += tab(2) + languageType(idlClass.getBaseClassName()) + "::fillClone(narrRet);" + endl();
        } else
        {
            ret += tab(2) + "ops::OPSObject::fillClone(narrRet);" + endl();
        }
        for (IDLField field : idlClass.getFields())
        {
            if (field.isIdlType())
            {
                if (!field.isArray() && field.isAbstract())
                {
                    ret += tab(2) + "if(narrRet->" + field.getName() + ") delete narrRet->" + field.getName() + ";" + endl();
                    ret += tab(2) + "narrRet->" + field.getName() + " = (" + languageType(field.getType()) + "*)" + field.getName() + "->clone();" + endl();
                } else
                {
                    if (!field.isArray())
                    {
                        ret += tab(2) + "narrRet->" + field.getName() + " = " + field.getName() + ";" + endl();
                    } else
                    {
                        if (field.isArray())
                        {
                            if (!field.isAbstract())
                            {
                                ret += tab(2) + "narrRet->" + field.getName() + " = " + field.getName() + ";" + endl();
                            } else
                            {
                                ret += tab(2) + "for(unsigned int __i = 0; __i < " + "" + field.getName() + ".size(); __i++)" + endl();
                                ret += tab(2) + "{" + endl();
                                ret += tab(3) + "if(" + "narrRet->" + field.getName() + ".size() >= __i + 1)" + endl();
                                ret += tab(3) + "{" + endl();
                                ret += tab(4) + "if(" + "narrRet->" + field.getName() + "[__i])" + " delete " + "narrRet->" + field.getName() + "[__i];" + endl();
                                ret += tab(4) + "narrRet->" + field.getName() + "[__i] = " + "(" + elementType(field.getType()) + "*)" + field.getName() + "[__i]->clone();" + endl();
                                ret += tab(3) + "}" + endl();
                                ret += tab(3) + "else" + endl();
                                ret += tab(3) + "{" + endl();
                                ret += tab(4) + "narrRet->" + field.getName() + ".push_back((" + elementType(field.getType()) + "*)" + field.getName() + "[__i]->clone()); " + endl();
                                ret += tab(3) + "}" + endl();
                                ret += tab(2) + "}" + endl();
                            }
                        }
                    }
                }
            } else
            {
                if (field.isArray())
                {

                    ret += tab(2) + "narrRet->" + field.getName() + " = " + field.getName() + ";" + endl();

                } else
                {
                    ret += tab(2) + "narrRet->" + field.getName() + " = " + field.getName() + ";" + endl();
                }
            }

        }
        return ret;
    }

    private String getConstructorBody(IDLClass idlClass)
    {
        String ret = "";
        for (IDLField field : idlClass.getFields())
        {
            if (field.isIdlType() && !field.isArray() && field.isAbstract())
            {
                ret += tab(2) + field.getName() + " = new " + languageType(field.getType()).replace("*", "()") + ";" + endl();
            }
        }
        return ret;
    }

    private CharSequence getConstructorHead(IDLClass idlClass)
    {
        String ret = tab(2) + "";

        for (IDLField field : idlClass.getFields())
        {
            if (field.getType().equals("boolean"))
            {
                ret += ", " + field.getName() + "(false)";
            } else
            {
                if (field.getType().equals("string") || field.isArray() || field.isIdlType())
                {
                    //Do nothing in head
                } else
                {
                    //Numeric
                    ret += ", " + field.getName() + "(0)";
                }
            }

        }
//        if(ret.endsWith("," + endl()))
//        {
//            ret = ret.substring(0, ret.lastIndexOf(","));
//        }
        return ret;

    }

    private String getDeclarations(IDLClass idlClass)
    {
        String ret = "";
        for (IDLField field : idlClass.getFields())
        {

            if (!field.getComment().equals(""))
            {
                String comment = field.getComment();
                int idx;
                while ((idx = comment.indexOf('\n')) >= 0) {
                  ret += tab(1) + "///" + comment.substring(0,idx).replace("/*", "").replace("*/", "") + endl();
                  comment = comment.substring(idx+1);
                }
                ret += tab(1) + "///" + comment.replace("/*", "").replace("*/", "") + endl();
///                ret += tab(1) + "///" + field.getComment().replace("/*", "").replace("*/", "") + endl();
            }
            if (field.isArray())
            {
                ret += tab(1) + "" + getDeclareVector(field);
            } else
            {
                if (field.getType().equals("string"))
                {
                    ret += tab(1) + "" + languageType(field.getType()) + " " + field.getName() + ";" + endl();
                } else
                {
                    if (field.isIdlType())
                    {
                        if (field.isAbstract())
                        {
                            ret += tab(1) + "" + languageType(field.getType()) + "* " + field.getName() + ";" + endl();
                        } else
                        {
                            ret += tab(1) + "" + languageType(field.getType()) + " " + field.getName() + ";" + endl();
                        }
                    } else //Simple primitive type
                    {
                        ret += tab(1) + "" + languageType(field.getType()) + " " + field.getName() + ";" + endl();
                    }
                }
            }

        }
        return ret;
    }

    private String getDeclareVector(IDLField field)
    {
        String ret = "";
        if (field.isAbstract())
        {
            ret += "std::vector<" + languageType(elementType(field.getType())) + "*> " + field.getName() + ";" + endl();
        } else
        {
            ret += "std::vector<" + languageType(elementType(field.getType())) + "> " + field.getName() + ";" + endl();
        }
        return ret;
    }

    protected String languageType(String s)
    {
        if (s.equals("string"))
        {
            return "std::string";
        } else
        {
            if (s.equals("boolean"))
            {
                return "bool";
            } else
            {
                if (s.equals("int"))
                {
                    return "int";
                } else
                {
                    if (s.equals("long"))
                    {
                        return "__int64";
                    } else
                    {
                        if (s.equals("double"))
                        {
                            return "double";
                        } else
                        {
                            if (s.equals("float"))
                            {
                                return "float";
                            } else
                            {
                                if (s.equals("byte"))
                                {
                                    return "char";
                                } else
                                {
                                    if (s.equals("string[]"))
                                    {
                                        return "std::vector<std::string>";
                                    } else
                                    {
                                        if (s.equals("int[]"))
                                        {
                                            return "std::vector<int>";
                                        } else
                                        {
                                            if (s.equals("long[]"))
                                            {
                                                return "std::vector<__int64>";
                                            } else
                                            {
                                                if (s.equals("double[]"))
                                                {
                                                    return "std::vector<double>";
                                                } else
                                                {
                                                    if (s.equals("float[]"))
                                                    {
                                                        return "std::vector<float>";
                                                    } else
                                                    {
                                                        if (s.equals("byte[]"))
                                                        {
                                                            return "std::vector<char>";
                                                        } else
                                                        {
                                                            if (s.equals("boolean[]"))
                                                            {
                                                                return "std::vector<bool>";
                                                            } else
                                                            {
                                                                if (s.endsWith("[]"))
                                                                {
                                                                    return "std::vector<" + applyLanguagePackageSeparator(s.substring(0, s.indexOf('['))) + "*>";
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return applyLanguagePackageSeparator(s) + "";

    }

    protected String applyLanguagePackageSeparator(String packageName)
    {
        return packageName.replace(".", "::");
    }

    private CharSequence getImports(IDLClass idlClass)
    {
        HashMap<String, String> typesToInclude = new HashMap();
        String ret = "";
        if (idlClass.getBaseClassName() != null)
        {
            typesToInclude.put(idlClass.getBaseClassName(), idlClass.getBaseClassName());
            //ret += tab(0) + "#include \"" + getSlashedType(idlClass.getBaseClassName()) + ".h\"" + endl();
        }
        for (IDLField field : idlClass.getFields())
        {

            if (field.isIdlType())
            {
                String type = field.getType();
                if (field.isArray())
                {
                    type = type.substring(0, type.length() - 2);
                }
                typesToInclude.put(type, type);

                //ret += tab(0) + "#include \"" + getSlashedType(type) + ".h\"" + endl();
            }
        }
        for (String includeType : typesToInclude.values())
        {
            ret += tab(0) + "#include \"" + getSlashedType(includeType) + ".h\"" + endl();

        }
        return ret;
    }

    private String getSlashedType(String type)
    {
        return type.replace(".", "/");
    }

    private String getUnderscoredPackName(String packageName)
    {
        return packageName.replaceAll("\\.", "_");
    }

    private CharSequence getDestructorBody(IDLClass idlClass)
    {
        String ret = "";
        for (IDLField field : idlClass.getFields())
        {
            if (field.isIdlType())
            {
                if (!field.isArray() && field.isAbstract())
                {
                    ret += tab(2) + "if(" + field.getName() + ") delete " + field.getName() + ";" + endl();
                } else
                {
                    if (field.isArray())
                    {
                        if (field.isAbstract())
                        {
                            ret += tab(2) + "for(unsigned int __i = 0; __i < " + field.getName() + ".size(); __i++){ if(" + field.getName() + "[__i]) delete " + field.getName() + "[__i];}" + endl();

                        }
                    }
                }
            }
        }
        return ret;
    }

    private String getPackageCloser(String packageName)
    {
        String ret = "";
        for (int i = 0; i < packageName.length(); i++)
        {
            if (packageName.charAt(i) == '.')
            {
                ret += "}";
            }

        }
        ret += "}\n";
        return ret;
    }

    private CharSequence getPackageDeclaration(String packageName)
    {
        String ret = "namespace " + packageName.replaceAll("\\.", " { namespace ");
        ret += " {";
        return ret;
    }

    private String getSerialize(IDLClass idlClass)
    {
        String ret = "";
        if (idlClass.getBaseClassName() != null)
        {
            ret += tab(2) + languageType(idlClass.getBaseClassName()).replace("*", "") + "::serialize(archive);" + endl();
        } else
        {
            ret += tab(2) + "ops::OPSObject::serialize(archive);" + endl();
        }
        for (IDLField field : idlClass.getFields())
        {
            if (field.isIdlType())
            {
                if (!field.isArray() && field.isAbstract())
                {
                    ret += tab(2) + field.getName() + " = (" + languageType(field.getType()) + "*) archive->inout(std::string(\"" + field.getName() + "\"), " + field.getName() + ");" + endl();
                } else
                {
                    if (!field.isArray())
                    {
                        ret += tab(2) + "archive->inout(std::string(\"" + field.getName() + "\"), " + field.getName() + ");" + endl();
                    } else
                    {
                        if (field.isArray() && field.isAbstract())
                        {
                            ret += tab(2) + "archive->inout<" + languageType(elementType(field.getType())) + ">(std::string(\"" + field.getName() + "\"), " + field.getName() + ");" + endl();
                        } else
                        {
                            ret += tab(2) + "archive->inout<" + languageType(elementType(field.getType())) + ">(std::string(\"" + field.getName() + "\"), " + field.getName() + ", " + languageType(elementType(field.getType())) + "());" + endl();
                        }
                    }
                }
            } else
            {
                if (field.isArray())
                {

                    ret += tab(2) + "archive->inout(std::string(\"" + field.getName() + "\"), " + field.getName() + ");" + endl();

                } else
                {
                    ret += tab(2) + "archive->inout(std::string(\"" + field.getName() + "\"), " + field.getName() + ");" + endl();
                }
            }

        }
        return ret;
    }
}
