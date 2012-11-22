package unluac.parse;

import java.nio.ByteBuffer;

public class LUpvalueType extends BObjectType<LUpvalue> {

  @Override
  public LUpvalue parse(ByteBuffer buffer, BHeader header) {
    LUpvalue upvalue = new LUpvalue();
    upvalue.instack = buffer.get() != 0;
    upvalue.idx = 0xFF & buffer.get();
    return upvalue;
  }

}