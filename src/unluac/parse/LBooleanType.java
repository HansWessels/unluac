package unluac.parse;

import java.nio.ByteBuffer;


public class LBooleanType extends BObjectType<LBoolean> {

  @Override
  public LBoolean parse(ByteBuffer buffer, BHeader header) {
    int value = buffer.get();
    if((value & 0xFFFFFFFE) != 0) {
      throw new IllegalStateException();
    } else {
      LBoolean bool = value == 0 ? LBoolean.LFALSE : LBoolean.LTRUE;
      if(header.debug) {
        System.out.println("-- parsed <boolean> " + bool);
      }
      return bool;
    }
  }

}
