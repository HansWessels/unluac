package unluac.parse;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import unluac.Version;


public class BHeader {

  private static final byte[] signature = {
    0x1B, 0x4C, 0x75, 0x61,
  };

  private static final byte[] luacTail = {
    0x19, (byte) 0x93, 0x0D, 0x0A, 0x1A, 0x0A,
  };
  
  public final boolean debug = false;
  
  public final Version version;
  
  public final BIntegerType integer;
  public final BSizeTType sizeT;
  public final LBooleanType bool;
  public final LNumberType number;
  public final LStringType string;
  public final LConstantType constant;
  public final LLocalType local;
  public final LUpvalueType upvalue;
  public final LFunctionType function;
  
  public BHeader(ByteBuffer buffer) {
    // 4 byte Lua signature
    for(int i = 0; i < signature.length; i++) {
      if(buffer.get() != signature[i]) {
        throw new IllegalStateException("The input file does not have the signature of a valid Lua file.");
      }
    }
    // 1 byte Lua version
    int versionNumber = 0xFF & buffer.get();
    switch(versionNumber)
    {
      case 0x51:
        version = Version.LUA51;
        break;
      case 0x52:
        version = Version.LUA52;
        break;
      default: {
        int major = versionNumber >> 4;
        int minor = versionNumber & 0x0F;
        throw new IllegalStateException("The input chunk's Lua version is " + major + "." + minor + "; unluac can only handle Lua 5.1 and Lua 5.2.");
      }
    }
    if(debug) {
      System.out.println("-- version: 0x" + Integer.toHexString(versionNumber));
    }
    // 1 byte Lua "format"
    int format = 0xFF & buffer.get();
    if(format != 0) {
      throw new IllegalStateException("The input chunk reports a non-standard lua format: " + format);
    }
    if(debug) {
      System.out.println("-- format: " + format);
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
    if(debug) {
      System.out.println("-- endianness: " + endianness + (endianness == 0 ? " (big)" : " (little)"));
    }
    // 1 byte int size
    int intSize = 0xFF & buffer.get();
    if(debug) {
      System.out.println("-- int size: " + intSize);
    }
    integer = new BIntegerType(intSize);
    // 1 byte sizeT size
    int sizeTSize = 0xFF & buffer.get();
    if(debug) {
      System.out.println("-- size_t size: " + sizeTSize);
    }
    sizeT = new BSizeTType(sizeTSize);
    // 1 byte instruction size
    int instructionSize = 0xFF & buffer.get();
    if(debug) {
      System.out.println("-- instruction size: " + instructionSize);
    }
    if(instructionSize != 4) {
      throw new IllegalStateException("The input chunk reports an unsupported instruction size: " + instructionSize + " bytes");
    }
    int lNumberSize = 0xFF & buffer.get();
    if(debug) {
      System.out.println("-- Lua number size: " + lNumberSize);
    }
    int lNumberIntegralCode = 0xFF & buffer.get();
    if(debug) {
      System.out.println("-- Lua number integral code: " + lNumberIntegralCode);
    }
    if(lNumberIntegralCode > 1) {
      throw new IllegalStateException("The input chunk reports an invalid code for lua number integralness: " + lNumberIntegralCode);
    }
    boolean lNumberIntegral = (lNumberIntegralCode == 1);
    number = new LNumberType(lNumberSize, lNumberIntegral);
    bool = new LBooleanType();
    string = new LStringType();
    constant = new LConstantType();
    local = new LLocalType();
    upvalue = new LUpvalueType();
    function = version.getLFunctionType();
    if(version.hasHeaderTail()) {
      for(int i = 0; i < luacTail.length; i++) {
        if(buffer.get() != luacTail[i]) {
          throw new IllegalStateException("The input file does not have the header tail of a valid Lua file.");
        }
      }
    }
  }
  
}
