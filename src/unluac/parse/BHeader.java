package unluac.parse;

import java.nio.ByteBuffer;

import unluac.Configuration;
import unluac.Version;
import unluac.decompile.CodeExtract;


public class BHeader {

  private static final byte[] signature = {
    0x1B, 0x4C, 0x75, 0x61,
  };
  
  public final boolean debug = false;
  
  public final Configuration config;
  public final Version version;
  public final LHeader lheader;
  public final BIntegerType integer;
  public final BSizeTType sizeT;
  public final LBooleanType bool;
  public final LNumberType number;
  public final LNumberType linteger;
  public final LNumberType lfloat;
  public final LStringType string;
  public final LConstantType constant;
  public final LLocalType local;
  public final LUpvalueType upvalue;
  public final LFunctionType function;
  public final CodeExtract extractor;
  
  public final LFunction main;
  
  public BHeader(ByteBuffer buffer, Configuration config) {
    this.config = config;
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
      case 0x50:
        version = Version.LUA50;
        break;
      case 0x51:
        version = Version.LUA51;
        break;
      case 0x52:
        version = Version.LUA52;
        break;
      case 0x53:
        version = Version.LUA53;
        break;
      default: {
        int major = versionNumber >> 4;
        int minor = versionNumber & 0x0F;
        throw new IllegalStateException("The input chunk's Lua version is " + major + "." + minor + "; unluac can only handle Lua 5.0 - Lua 5.3.");
      }
    }
    lheader = version.getLHeaderType().parse(buffer, this);
    integer = lheader.integer;
    sizeT = lheader.sizeT;
    bool = lheader.bool;
    number = lheader.number;
    linteger = lheader.linteger;
    lfloat = lheader.lfloat;
    string = lheader.string;
    constant = lheader.constant;
    local = lheader.local;
    upvalue = lheader.upvalue;
    function = lheader.function;
    extractor = lheader.extractor;
    
    int upvalues = -1;
    if(versionNumber >= 0x53) {
      upvalues = 0xFF & buffer.get();
      if(debug) {
        System.out.println("-- main chunk upvalue count: " + upvalues);
      }
      // TODO: check this value
    }
    main = function.parse(buffer, this);
    if(upvalues >= 0) {
      if(main.numUpvalues != upvalues) {
        throw new IllegalStateException("The main chunk has the wrong number of upvalues: " + main.numUpvalues + " (" + upvalues + " expected)");
      }
    }
    if(main.numUpvalues >= 1 && versionNumber >= 0x52 && (main.upvalues[0].name == null || main.upvalues[0].name.isEmpty())) {
      main.upvalues[0].name = "_ENV";
    }
  }
  
}
