package unluac.parse;

import java.nio.ByteBuffer;


public class LLocalType extends BObjectType<LLocal> {

  @Override
  public LLocal parse(ByteBuffer buffer, BHeader header) {
    LString name = header.string.parse(buffer, header);
    BInteger start = header.integer.parse(buffer, header);
    BInteger end = header.integer.parse(buffer, header);
    if(header.debug) {
      System.out.print("-- parsing local, name: ");
      System.out.print(name);
      System.out.print(" from " + start.asInt() + " to " + end.asInt());
      System.out.println();
    }
    return new LLocal(name, start, end);
  }

}
