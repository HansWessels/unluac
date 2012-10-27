package unluac.parse;

import java.nio.ByteBuffer;


public class LFunctionType extends BObjectType<LFunction> {
  
  private static class LFunctionParseState {
    
    public LString name;
    int lineBegin;
    int lineEnd;
    int lenUpvalues;
    int lenParameter;
    int vararg;
    int maximumStackSize;
    int length;
    int[] code;
    BList<LObject> constants;
    BList<LFunction> functions;
    BList<BInteger> lines;
    BList<LLocal> locals;
    LUpvalue upvalues[];
  }
  
  @Override
  public LFunction parse(ByteBuffer buffer, BHeader header) {
    if(header.debug) {
      System.out.println("-- beginning to parse function");
    }
    if(header.debug) {
      System.out.println("-- parsing name...start...end...upvalues...params...varargs...stack");
    }
    LFunctionParseState s = new LFunctionParseState();
    if(header.version == 0x51) {
      s.name = header.string.parse(buffer, header);
      s.lineBegin = header.integer.parse(buffer, header).asInt();
      s.lineEnd = header.integer.parse(buffer, header).asInt();
      s.lenUpvalues = 0xFF & buffer.get();
      s.lenParameter = 0xFF & buffer.get();
      s.vararg = 0xFF & buffer.get();
      s.maximumStackSize = 0xFF & buffer.get();
      parse_code(buffer, header, s);
      parse_constants(buffer, header, s);
      parse_upvalues(buffer, header, s);
      parse_debug(buffer, header, s);
    } else if(header.version == 0x52) {
      s.lineBegin = header.integer.parse(buffer, header).asInt();
      s.lineEnd = header.integer.parse(buffer, header).asInt();
      s.lenParameter = 0xFF & buffer.get();
      s.vararg = 0xFF & buffer.get();
      s.maximumStackSize = 0xFF & buffer.get();
      parse_code(buffer, header, s);
      parse_constants(buffer, header, s);
      parse_upvalues(buffer, header, s);
      parse_debug(buffer, header, s);
    }
    return new LFunction(header, s.code, s.locals.asArray(new LLocal[s.locals.length.asInt()]), s.constants.asArray(new LObject[s.constants.length.asInt()]), s.upvalues, s.functions.asArray(new LFunction[s.functions.length.asInt()]), s.maximumStackSize, s.lenUpvalues, s.lenParameter, s.vararg);
  }
  
  private void parse_code(ByteBuffer buffer, BHeader header, LFunctionParseState s) {
    if(header.debug) {
      System.out.println("-- beginning to parse bytecode list");
    }
    s.length = header.integer.parse(buffer, header).asInt();
    s.code = new int[s.length];
    for(int i = 0; i < s.length; i++) {
      s.code[i] = buffer.getInt();
      if(header.debug) {
        System.out.println("-- parsed codepoint " + Integer.toHexString(s.code[i]));
      }
    }
  }
  
  private void parse_constants(ByteBuffer buffer, BHeader header, LFunctionParseState s) {
    if(header.debug) {
      System.out.println("-- beginning to parse constants list");
    }
    s.constants = header.constant.parseList(buffer, header);
    if(header.debug) {
      System.out.println("-- beginning to parse functions list");
    }
    s.functions = header.function.parseList(buffer, header);
  }
  
  private void parse_debug(ByteBuffer buffer, BHeader header, LFunctionParseState s) {
    if(header.version == 0x52) {
      s.name = header.string.parse(buffer, header);
    }
    if(header.debug) {
      System.out.println("-- beginning to parse source lines list");
    }
    s.lines = header.integer.parseList(buffer, header);
    if(header.debug) {
      System.out.println("-- beginning to parse locals list");
    }
    s.locals = header.local.parseList(buffer, header);
    if(header.debug) {
      System.out.println("-- beginning to parse upvalues list");
    }
    BList<LString> upvalueNames = header.string.parseList(buffer, header);
    for(int i = 0; i < upvalueNames.length.asInt(); i++) {
      s.upvalues[i].name = upvalueNames.get(i).deref();
    }
  }
  
  // Only used for version 0x52
  private void parse_upvalues(ByteBuffer buffer, BHeader header, LFunctionParseState s) {
    if(header.version == 0x51) {
      s.upvalues = new LUpvalue[s.lenUpvalues];
      for(int i = 0; i < s.lenUpvalues; i++) {
        s.upvalues[i] = new LUpvalue();
      }
    } else {
      BList<LUpvalue> upvalues = header.upvalue.parseList(buffer, header);
      s.lenUpvalues = upvalues.length.asInt();
      s.upvalues = upvalues.asArray(new LUpvalue[s.lenUpvalues]);
    }
  }
  
}
