package unluac.parse;

import java.nio.ByteBuffer;


public class LFunctionType extends BObjectType<LFunction> {

  @Override
  public LFunction parse(ByteBuffer buffer, BHeader header) {
    if(header.debug) {
      System.out.println("-- beginning to parse function");
    }
    if(header.debug) {
      System.out.println("-- parsing name...start...end...upvalues...params...varargs...stack");
    }
    LString name = header.string.parse(buffer, header);
    int lineBegin = header.integer.parse(buffer, header).asInt();
    int lineEnd = header.integer.parse(buffer, header).asInt();
    int lenUpvalues = 0xFF & buffer.get();
    int lenParameter = 0xFF & buffer.get();
    int vararg = 0xFF & buffer.get();
    int maximumStackSize = 0xFF & buffer.get();
    if(header.debug) {
      System.out.println("-- beginning to parse bytecode list");
    }
    int length = header.integer.parse(buffer, header).asInt();
    int[] code = new int[length];
    for(int i = 0; i < length; i++) {
      code[i] = buffer.getInt();
      if(header.debug) {
        System.out.println("-- parsed codepoint " + Integer.toHexString(code[i]));
      }
    }
    if(header.debug) {
      System.out.println("-- beginning to parse constants list");
    }
    BList<LObject> constants = header.constant.parseList(buffer, header);
    if(header.debug) {
      System.out.println("-- beginning to parse functions list");
    }
    BList<LFunction> functions = header.function.parseList(buffer, header);
    if(header.debug) {
      System.out.println("-- beginning to parse source lines list");
    }
    BList<BInteger> lines = header.integer.parseList(buffer, header);
    if(header.debug) {
      System.out.println("-- beginning to parse locals list");
    }
    BList<LLocal> locals = header.local.parseList(buffer, header);
    if(header.debug) {
      System.out.println("-- beginning to parse upvalues list");
    }
    BList<LString> upvalues = header.string.parseList(buffer, header);
    String[] ups = new String[upvalues.length.asInt()];
    for(int i = 0; i < ups.length; i++) {
      ups[i] = upvalues.get(i).deref();
    }
    return new LFunction(code, locals.asArray(new LLocal[locals.length.asInt()]), constants.asArray(new LObject[constants.length.asInt()]), ups, functions.asArray(new LFunction[functions.length.asInt()]), maximumStackSize, lenUpvalues, lenParameter, vararg);
  }
  
}
