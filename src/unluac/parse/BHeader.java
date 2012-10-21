package unluac.parse;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class BHeader {

  private static final byte[] signature = {
    0x1B, 0x4C, 0x75, 0x61,
  };
  /*
    0x51, 0x00, 0x01, 0x04,
    0x04, 0x04, 0x08, 0x00,
  };*/

  public final boolean debug = false;
  
  public final BIntegerType integer;
  public final BSizeTType sizeT;
  public final LBooleanType bool;
  public final LNumberType number;
  public final LStringType string;
  public final LConstantType constant;
  public final LLocalType local;
  public final LFunctionType function;
  
  public BHeader(ByteBuffer buffer) {
    // 4 byte Lua signature
    for(int i = 0; i < signature.length; i++) {
      if(buffer.get() != signature[i]) {
        throw new IllegalStateException("The input file does not have the signature of a valid Lua file.");
      }
    }
    // 1 byte Lua version
    int version = 0xFF & buffer.get();
    if(version != 0x51) {
      int major = version >> 4;
      int minor = version & 0x0F;
      throw new IllegalStateException("The input chunk's Lua version is " + major + "." + minor + "; unluac can only handle Lua 5.1.");
    }
    // 1 byte Lua "format"
    int format = 0xFF & buffer.get();
    if(format != 0) {
      throw new IllegalStateException("The input chunk reports a non-standard lua format: " + format);
    }
    // 1 byte endianness
    int endianness = 0xFF & buffer.get();
    switch(endianness) {
      case 0:
        buffer.order(ByteOrder.BIG_ENDIAN);
        break;
      case 1:
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        break;
      default:
        throw new IllegalStateException("The input chunk reports an invalid endianness: " + endianness);
    }
    // 1 byte int size
    int intSize = 0xFF & buffer.get();
    integer = new BIntegerType(intSize);
    // 1 byte sizeT size
    int sizeTSize = 0xFF & buffer.get();
    sizeT = new BSizeTType(sizeTSize);
    // 1 byte instruction size
    int instructionSize = 0xFF & buffer.get();
    if(instructionSize != 4) {
      throw new IllegalStateException("The input chunk reports an unsupported instruction size: " + instructionSize + " bytes");
    }
    int lNumberSize = 0xFF & buffer.get();
    int lNumberIntegralCode = 0xFF & buffer.get();
    if(lNumberIntegralCode > 1) {
      throw new IllegalStateException("The input chunk reports an invalid code for lua number integralness: " + lNumberIntegralCode);
    }
    boolean lNumberIntegral = (lNumberIntegralCode == 1);
    number = new LNumberType(lNumberSize, lNumberIntegral);
    bool = new LBooleanType();
    string = new LStringType();
    constant = new LConstantType();
    local = new LLocalType();
    function = new LFunctionType();
  }
  
}
