/* Generated By:JavaCC: Do not edit this line. IDLParser.java */
package parsing.javaccparser;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import parsing.IDLField;

public class IDLParser implements IDLParserConstants {

    private IDLField currentIDLField;

    public ParserEvent<String> packageDeclareEvent = new ParserEvent<String>();
    public ParserEvent<String> idlDeclareEvent = new ParserEvent<String>();
    public ParserEvent<String> enumDeclareEvent = new ParserEvent<String>();
    public ParserEvent<String> idlCloseEvent = new ParserEvent<String>();
    public ParserEvent<String> enumCloseEvent = new ParserEvent<String>();
    public ParserEvent<IDLField> fieldDeclareEvent = new ParserEvent<IDLField>();
    public ParserEvent<String> extendsEvent = new ParserEvent<String>();
    public ParserEvent<String> enumElementEvent = new ParserEvent<String>();
    public ParserEvent<String> commentEvent = new ParserEvent<String>();

  public static void main(String args[])
  {
    try
    {
        IDLParser parser;
        parser = new IDLParser(new java.io.FileInputStream("TestIDL.idl"));

        parser.idlDeclareEvent.add(new ParserEventCallback<String>() {

                public void onEvent(String eventData, ParserEvent e)
                {
                    System.out.println("class " + eventData + " declared.");
                }
            });
        parser.idlCloseEvent.add(new ParserEventCallback<String>() {

                public void onEvent(String eventData, ParserEvent e)
                {
                    System.out.println("class " + eventData + " done.");
                }
            });

        parser.packageDeclareEvent.add(new ParserEventCallback<String>() {

                public void onEvent(String eventData, ParserEvent e)
                {
                    System.out.println("package " + eventData + " declared.");
                }
            });

        parser.enumDeclareEvent.add(new ParserEventCallback<String>() {

                public void onEvent(String eventData, ParserEvent e)
                {
                    System.out.println("enum " + eventData + " declared.");
                }
            });
        parser.enumCloseEvent.add(new ParserEventCallback<String>() {

                public void onEvent(String eventData, ParserEvent e)
                {
                    System.out.println("enum " + eventData + " done.");
                }
            });
        parser.enumElementEvent.add(new ParserEventCallback<String>() {

                public void onEvent(String eventData, ParserEvent e)
                {
                    System.out.println("enum element " + eventData + ".");
                }
            });
        parser.fieldDeclareEvent.add(new ParserEventCallback<IDLField>() {

                public void onEvent(IDLField eventData, ParserEvent e)
                {
                    System.out.println("field  " + eventData + ".");
                }
            });
        parser.extendsEvent.add(new ParserEventCallback<String>() {

                public void onEvent(String eventData, ParserEvent e)
                {
                    System.out.println("extends  " + eventData + ".");
                }
            });


        parser.specification();
    }
    catch (ParseException ex)
    {
        Logger.getLogger(IDLParser.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (FileNotFoundException ex)
    {
        Logger.getLogger(IDLParser.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

/* Production 1 */
  final public void specification() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 8:
      definition();
      break;
    case ANOTATION:
      label_1:
      while (true) {
        jj_consume_token(ANOTATION);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case ANOTATION:
          ;
          break;
        default:
          jj_la1[0] = jj_gen;
          break label_1;
        }
      }
      break;
    default:
      jj_la1[1] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

/* Production 2 */
  final public void definition() throws ParseException {
    jj_consume_token(8);
    module();
    jj_consume_token(9);
    label_2:
    while (true) {
      body_declare();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 11:
      case 12:
      case ANOTATION:
        ;
        break;
      default:
        jj_la1[2] = jj_gen;
        break label_2;
      }
    }
    jj_consume_token(0);
  }

  final public void body_declare() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 11:
    case 12:
      type_declare();
      break;
    case ANOTATION:
      jj_consume_token(ANOTATION);
      break;
    default:
      jj_la1[3] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void type_declare() throws ParseException {
    if (jj_2_1(2)) {
      struct_type();
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 12:
        enum_type();
        break;
      default:
        jj_la1[4] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
  }

/* Production 3 */
  final public String module() throws ParseException {
  String packName = ""; String tName = "";
    tName = identifier();
                         packName += tName;
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 10:
        ;
        break;
      default:
        jj_la1[5] = jj_gen;
        break label_3;
      }
      jj_consume_token(10);
                                                     packName += ".";
      tName = identifier();
                                                                                             packName += tName;
    }
        packageDeclareEvent.fireEvent(packName);
        {if (true) return packName;}
    throw new Error("Missing return statement in function");
  }

  final public String struct_type() throws ParseException {
    String name = "";
    String parName = "";
    jj_consume_token(11);
    name = identifier();
                             idlDeclareEvent.fireEvent(name);
    member_list();
    {idlCloseEvent.fireEvent(name);}
    System.out.println("struct " + name);
    {if (true) return name;}
    throw new Error("Missing return statement in function");
  }

  final public String enum_type() throws ParseException {
 String name = "";
    jj_consume_token(12);
    name = identifier();
                               enumDeclareEvent.fireEvent(name);
    jj_consume_token(13);
    enum_body();
    jj_consume_token(14);
        enumCloseEvent.fireEvent(name);
        {if (true) return name;}
    throw new Error("Missing return statement in function");
  }

  final public void enum_body() throws ParseException {
 String tName = "";String ANOTATION = ""; String tANOTATION = "";
    tName = identifier();
                        enumElementEvent.fireEvent(tName);
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 15:
        ;
        break;
      default:
        jj_la1[6] = jj_gen;
        break label_4;
      }
      jj_consume_token(15);
      tName = identifier();
                                                                                        enumElementEvent.fireEvent(tName);
    }
  }

/* Production 51 */
  final public void member_list() throws ParseException {
 String tName = "";
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 16:
      jj_consume_token(16);
      tName = idl_type();
                                extendsEvent.fireEvent(tName);
      jj_consume_token(13);
      label_5:
      while (true) {
        member();
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 19:
        case 20:
        case 21:
        case 22:
        case 23:
        case 24:
        case 25:
        case 26:
        case 27:
        case ANOTATION:
          ;
          break;
        default:
          jj_la1[7] = jj_gen;
          break label_5;
        }
      }
      jj_consume_token(14);
      break;
    case 13:
      jj_consume_token(13);
      label_6:
      while (true) {
        member();
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 19:
        case 20:
        case 21:
        case 22:
        case 23:
        case 24:
        case 25:
        case 26:
        case 27:
        case ANOTATION:
          ;
          break;
        default:
          jj_la1[8] = jj_gen;
          break label_6;
        }
      }
      jj_consume_token(14);
      break;
    default:
      jj_la1[9] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

/* Production 52 */
  final public void member() throws ParseException {
 String typeName = ""; String fieldName = "";String comment = ""; String tComment = "";
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 19:
    case 20:
    case 21:
    case 22:
    case 23:
    case 24:
    case 25:
    case 26:
    case 27:
   currentIDLField = new IDLField(fieldName, "");
      typeName = type_spec();
      fieldName = declarators();
      jj_consume_token(9);
    if(fieldName.contains("[]"))
    {
        typeName = typeName.trim() + "[]";
        fieldName = fieldName.replace("[]", "").trim();
    }
    currentIDLField.setName(fieldName);
    currentIDLField.setType(typeName);
    fieldDeclareEvent.fireEvent(currentIDLField);
      break;
    case ANOTATION:
      comment = jj_consume_token(ANOTATION).image;
     commentEvent.fireEvent(comment);
      break;
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public String identifier() throws ParseException {
  String name = "";
    name = jj_consume_token(ID).image;
    {if (true) return name;}
    throw new Error("Missing return statement in function");
  }

  final public String declarators() throws ParseException {
 String ret = "";
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ID:
      ret = declarator();
   {if (true) return ret;}
      break;
    case 17:
      jj_consume_token(17);
      jj_consume_token(18);
      ret = declarator();
   {if (true) return "[] " + ret;}
      break;
    default:
      jj_la1[11] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/* Production 35 */
  final public String declarator() throws ParseException {
 String ret = "";
    ret = simple_declarator();
   {if (true) return ret;}
    throw new Error("Missing return statement in function");
  }

  final public String type_spec() throws ParseException {
 String ret = "";
    ret = simple_type_spec();
  {if (true) return ret;}
    throw new Error("Missing return statement in function");
  }

  final public String simple_type_spec() throws ParseException {
 String ret = "";
    ret = base_type_spec();
   {if (true) return ret;}
    throw new Error("Missing return statement in function");
  }

  final public String complex_type_spec() throws ParseException {
 String ret = "";
    if (jj_2_2(2)) {

    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
    ret = base_type_spec();
    fixed_array_size();
 {if (true) return ret;}
    throw new Error("Missing return statement in function");
  }

  final public String base_type_spec() throws ParseException {
 String typeName = "";
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 20:
    case 21:
      typeName = floating_pt_type();
   {if (true) return typeName;}
      break;
    case 22:
    case 23:
    case 24:
    case 25:
      typeName = integer_type();
   {if (true) return typeName;}
      break;
    case 26:
      typeName = boolean_type();
   {if (true) return typeName;}
      break;
    case 27:
      typeName = string_type();
   {if (true) return typeName;}
      break;
    case 19:
      typeName = idl_type();
    currentIDLField.setIdlType(true);
    {if (true) return typeName;}
      break;
    default:
      jj_la1[12] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public String idl_type() throws ParseException {
  String className = ""; String tName = ""; String fieldName = "";
    jj_consume_token(19);
    tName = identifier();
                                   className += tName;
    label_7:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 10:
        ;
        break;
      default:
        jj_la1[13] = jj_gen;
        break label_7;
      }
      jj_consume_token(10);
                                                                className += ".";
      tName = identifier();
                                                                                                         className += tName;
    }
        {if (true) return className;}
    throw new Error("Missing return statement in function");
  }

  final public String idl_type_array() throws ParseException {
  String className = ""; String tName = ""; String fieldName = "";
    className = idl_type();
    jj_consume_token(17);
    jj_consume_token(18);
        {if (true) return className;}
    throw new Error("Missing return statement in function");
  }

  final public String simple_declarator() throws ParseException {
 String ret = "";
    ret = identifier();
   {if (true) return ret;}
    throw new Error("Missing return statement in function");
  }

  final public String complex_declarator() throws ParseException {
 String ret = "";
    ret = array_declarator();
    {if (true) return ret;}
    throw new Error("Missing return statement in function");
  }

  final public String array_declarator() throws ParseException {
 String ret = "";
    fixed_array_size();
    ret = identifier();
    {if (true) return ret ;}
    throw new Error("Missing return statement in function");
  }

/* Production 64 */
  final public void fixed_array_size() throws ParseException {
    jj_consume_token(17);
    jj_consume_token(18);
  }

/* Production 38 */
  final public String floating_pt_type() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 20:
      jj_consume_token(20);
   {if (true) return "float" ;}
      break;
    case 21:
      jj_consume_token(21);
   {if (true) return "double" ;}
      break;
    default:
      jj_la1[14] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/* Production 39 */
  final public String integer_type() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 22:
      jj_consume_token(22);
   {if (true) return "int" ;}
      break;
    case 23:
      jj_consume_token(23);
   {if (true) return "short" ;}
      break;
    case 24:
      jj_consume_token(24);
   {if (true) return "long" ;}
      break;
    case 25:
      jj_consume_token(25);
   {if (true) return "byte" ;}
      break;
    default:
      jj_la1[15] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

/* Production 47 */
  final public String boolean_type() throws ParseException {
    jj_consume_token(26);
   {if (true) return "boolean" ;}
    throw new Error("Missing return statement in function");
  }

  final public String string_type() throws ParseException {
    jj_consume_token(27);
   {if (true) return "string" ;}
    throw new Error("Missing return statement in function");
  }

/* Production 48 */
  final public void integer_literal() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OCTALINT:
      jj_consume_token(OCTALINT);
      break;
    case DECIMALINT:
      jj_consume_token(DECIMALINT);
      break;
    case HEXADECIMALINT:
      jj_consume_token(HEXADECIMALINT);
      break;
    default:
      jj_la1[16] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void string_literal() throws ParseException {
    jj_consume_token(STRING);
  }

  final public void character_literal() throws ParseException {
    jj_consume_token(CHARACTER);
  }

  final public void floating_pt_literal() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case FLOATONE:
      jj_consume_token(FLOATONE);
      break;
    case FLOATTWO:
      jj_consume_token(FLOATTWO);
      break;
    default:
      jj_la1[17] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_3R_8() {
    if (jj_scan_token(11)) return true;
    if (jj_3R_9()) return true;
    return false;
  }

  private boolean jj_3_1() {
    if (jj_3R_8()) return true;
    return false;
  }

  private boolean jj_3R_9() {
    if (jj_scan_token(ID)) return true;
    return false;
  }

  private boolean jj_3_2() {
    if (jj_scan_token(0)) return true;
    return false;
  }

  /** Generated Token Manager. */
  public IDLParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[18];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x0,0x100,0x1800,0x1800,0x1000,0x400,0x8000,0xff80000,0xff80000,0x12000,0xff80000,0x10020000,0xff80000,0x400,0x300000,0x3c00000,0xe0000000,0x0,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x10,0x10,0x10,0x10,0x0,0x0,0x0,0x10,0x10,0x0,0x10,0x0,0x0,0x0,0x0,0x0,0x0,0x3,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[2];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public IDLParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public IDLParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new IDLParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 18; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 18; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public IDLParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new IDLParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 18; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 18; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public IDLParser(IDLParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 18; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(IDLParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 18; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List jj_expentries = new java.util.ArrayList();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[37];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 18; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 37; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 2; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
