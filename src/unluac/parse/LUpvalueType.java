package unluac.parse;

import java.nio.ByteBuffer;

public class LUpvalueType extends BObjectType<LUpvalue> {

  @Override
  public LUpvalue parse(ByteBuffer buffer, BHeader header) {
    if(header.version != 0x52) {
      throw new IllegalStateException();
    }
    LUpvalue upvalue = new LUpvalue();
    upvalue.instack = buffer.get() != 0;
    upvalue.idx = 0xFF & buffer.get();
    return upvalue;
  }

}