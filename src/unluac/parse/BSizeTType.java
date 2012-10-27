package unluac.parse;

import java.nio.ByteBuffer;

public class BSizeTType extends BObjectType<BSizeT> {
  
  public final int sizeTSize;
  
  private BIntegerType integerType;
  
  public BSizeTType(int sizeTSize) {
    this.sizeTSize = sizeTSize;
    integerType = new BIntegerType(sizeTSize);
  }
  
  public BSizeT parse(ByteBuffer buffer, BHeader header) {
    BSizeT value = new BSizeT(integerType.raw_parse(buffer, header));
    if(header.debug) {
      System.out.println("-- parsed <size_t> " + value.asInt());
    }
    return value;
  }
  
}
