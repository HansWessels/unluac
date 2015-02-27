package unluac.parse;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import unluac.decompile.Code;
import unluac.decompile.Code50;
import unluac.decompile.CodeExtract;

abstract public class LHeaderType extends BObjectType<LHeader> {

  public static final LHeaderType TYPE50 = new LHeaderType50();
  public static final LHeaderType TYPE51 = new LHeaderType51();
  public static final LHeaderType TYPE52 = new LHeaderType52();
  public static final LHeaderType TYPE53 = new LHeaderType53();
  
  private static final byte[] luacTail = {
    0x19, (byte) 0x93, 0x0D, 0x0A, 0x1A, 0x0A,
  };
  
  protected static class LHeaderParseState {
    BIntegerType integer;
    BSizeTType sizeT;
    LNumberType number;
    LNumberType linteger;
    LNumberType lfloat;
    LStringType string;
    LConstantType constant;
    LFunctionType function;
    CodeExtract extractor;
    
    int format;
    
    int lNumberSize;
    boolean lNumberIntegrality;
    
    int lIntegerSize;
    int lFloatSize;
  }
  
  @Override
  public LHeader parse(ByteBuffer buffer, BHeader header) {
    LHeaderParseState s = new LHeaderParseState();
    parse_main(buffer, header, s);
    LBooleanType bool = new LBooleanType();
    LLocalType local = new LLocalType();
    LUpvalueType upvalue = new LUpvalueType();
    return new LHeader(s.format, s.integer, s.sizeT, bool, s.number, s.linteger, s.lfloat, s.string, s.constant, local, upvalue, s.function, s.extractor);
  }
  
  abstract protected void parse_main(ByteBuffer buffer, BHeader header, LHeaderParseState s);
  
  protected void parse_format(ByteBuffer buffer, BHeader header, LHeaderParseState s) {
    // 1 byte Lua "format"
    int format = 0xFF & buffer.get();
    if(format != 0) {
      throw new IllegalStateException("The input chunk reports a non-standard lua format: " + format);
    }
    s.format = format;
    if(header.debug) {
      System.out.println("-- format: " + format);
    }
  }
  
  protected void parse_endianness(ByteBuffer buffer, BHeader header, LHeaderParseState s) {
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
    if(header.debug) {
      System.out.println("-- endianness: " + endianness + (endianness == 0 ? " (big)" : " (little)"));
    }
  }
  
  protected void parse_int_size(ByteBuffer buffer, BHeader header, LHeaderParseState s) {
    // 1 byte int size
    int intSize = 0xFF & buffer.get();
    if(header.debug) {
      System.out.println("-- int size: " + intSize);
    }
    s.integer = new BIntegerType(intSize);
  }
  
  protected void parse_size_t_size(ByteBuffer buffer, BHeader header, LHeaderParseState s) {
    // 1 byte sizeT size
    int sizeTSize = 0xFF & buffer.get();
    if(header.debug) {
      System.out.println("-- size_t size: " + sizeTSize);
    }
    s.sizeT = new BSizeTType(sizeTSize);
  }
  
  protected void parse_instruction_size(ByteBuffer buffer, BHeader header, LHeaderParseState s) {
    // 1 byte instruction size
    int instructionSize = 0xFF & buffer.get();
    if(header.debug) {
      System.out.println("-- instruction size: " + instructionSize);
    }
    if(instructionSize != 4) {
      throw new IllegalStateException("The input chunk reports an unsupported instruction size: " + instructionSize + " bytes");
    }
  }
  
  protected void parse_number_size(ByteBuffer buffer, BHeader header, LHeaderParseState s) {
    int lNumberSize = 0xFF & buffer.get();
    if(header.debug) {
      System.out.println("-- Lua number size: " + lNumberSize);
    }
    s.lNumberSize = lNumberSize;
  }
  
  protected void parse_number_integrality(ByteBuffer buffer, BHeader header, LHeaderParseState s) {
    int lNumberIntegralityCode = 0xFF & buffer.get();
    if(header.debug) {
      System.out.println("-- Lua number integrality code: " + lNumberIntegralityCode);
    }
    if(lNumberIntegralityCode > 1) {
      throw new IllegalStateException("The input chunk reports an invalid code for lua number integrality: " + lNumberIntegralityCode);
    }
    s.lNumberIntegrality = (lNumberIntegralityCode == 1);
  }
  
  protected void parse_extractor(ByteBuffer buffer, BHeader header, LHeaderParseState s) {
    int sizeOp = 0xFF & buffer.get();
    int sizeA = 0xFF & buffer.get();
    int sizeB = 0xFF & buffer.get();
    int sizeC = 0xFF & buffer.get();
    if(header.debug) {
      System.out.println("-- Lua opcode extractor sizeOp: " + sizeOp + ", sizeA: " + sizeA + ", sizeB: " + sizeB + ", sizeC: " + sizeC);
    }
    s.extractor = new Code50(sizeOp, sizeA, sizeB, sizeC);
  }
  
  protected void parse_tail(ByteBuffer buffer, BHeader header, LHeaderParseState s) {
    for(int i = 0; i < luacTail.length; i++) {
      if(buffer.get() != luacTail[i]) {
        throw new IllegalStateException("The input file does not have the header tail of a valid Lua file (it may be corrupted).");
      }
    }
  }
  
}

class LHeaderType50 extends LHeaderType {
  
  @Override
  protected void parse_main(ByteBuffer buffer, BHeader header, LHeaderParseState s) {
    s.format = 0;
    parse_endianness(buffer, header, s);
    parse_int_size(buffer, header, s);
    parse_size_t_size(buffer, header, s);
    parse_instruction_size(buffer, header, s);
    parse_extractor(buffer, header, s);
    parse_number_size(buffer, header, s);
    s.number = new LNumberType(s.lNumberSize, false);
    buffer.getDouble();
    s.function = LFunctionType.TYPE50;
    s.string = LStringType.getType50();
    s.constant = LConstantType.getType50();
  }
  
}

class LHeaderType51 extends LHeaderType {
  
  @Override
  protected void parse_main(ByteBuffer buffer, BHeader header, LHeaderParseState s) {
    parse_format(buffer, header, s);
    parse_endianness(buffer, header, s);
    parse_int_size(buffer, header, s);
    parse_size_t_size(buffer, header, s);
    parse_instruction_size(buffer, header, s);
    parse_number_size(buffer, header, s);
    parse_number_integrality(buffer, header, s);
    s.number = new LNumberType(s.lNumberSize, s.lNumberIntegrality);
    s.function = LFunctionType.TYPE51;
    s.string = LStringType.getType50();
    s.constant = LConstantType.getType50();
    s.extractor = Code.Code51;
  }
  
}

class LHeaderType52 extends LHeaderType {
  
  @Override
  protected void parse_main(ByteBuffer buffer, BHeader header, LHeaderParseState s) {
    parse_format(buffer, header, s);
    parse_endianness(buffer, header, s);
    parse_int_size(buffer, header, s);
    parse_size_t_size(buffer, header, s);
    parse_instruction_size(buffer, header, s);
    parse_number_size(buffer, header, s);
    parse_number_integrality(buffer, header, s);
    parse_tail(buffer, header, s);
    s.number = new LNumberType(s.lNumberSize, s.lNumberIntegrality);
    s.function = LFunctionType.TYPE52;
    s.string = LStringType.getType50();
    s.constant = LConstantType.getType50();
    s.extractor = Code.Code51;
  }
  
}

class LHeaderType53 extends LHeaderType {
  
  protected void parse_integer_size(ByteBuffer buffer, BHeader header, LHeaderParseState s) {
    int lIntegerSize = 0xFF & buffer.get();
    if(header.debug) {
      System.out.println("-- Lua integer size: " + lIntegerSize);
    }
    if(lIntegerSize < 2) {
      throw new IllegalStateException("The input chunk reports an integer size that is too small: " + lIntegerSize);
    }
    s.lIntegerSize = lIntegerSize;
  }
  
  protected void parse_float_size(ByteBuffer buffer, BHeader header, LHeaderParseState s) {
    int lFloatSize = 0xFF & buffer.get();
    if(header.debug) {
      System.out.println("-- Lua float size: " + lFloatSize);
    }
    s.lFloatSize = lFloatSize;
  }
  
  @Override
  protected void parse_main(ByteBuffer buffer, BHeader header, LHeaderParseState s) {
    parse_format(buffer, header, s);
    parse_tail(buffer, header, s);
    parse_int_size(buffer, header, s);
    parse_size_t_size(buffer, header, s);
    parse_instruction_size(buffer, header, s);
    parse_integer_size(buffer, header, s);
    parse_float_size(buffer, header, s);
    byte[] endianness = new byte[s.lIntegerSize];
    buffer.get(endianness);
    if(endianness[0] == 0x78 && endianness[1] == 0x56) {
      buffer.order(ByteOrder.LITTLE_ENDIAN);
    } else if(endianness[s.lIntegerSize - 1] == 0x78 && endianness[s.lIntegerSize - 2] == 0x56) {
      buffer.order(ByteOrder.BIG_ENDIAN);
    } else {
      throw new IllegalStateException("The input chunk reports an invalid endianness: " + Arrays.toString(endianness));
    }
    s.linteger = new LNumberType(s.lIntegerSize, true);
    s.lfloat = new LNumberType(s.lFloatSize, false);
    s.function = LFunctionType.TYPE53;
    s.string = LStringType.getType53();
    s.constant = LConstantType.getType53();
    s.extractor = Code.Code51;
    double floatcheck = s.lfloat.parse(buffer, header).value();
    if(floatcheck != 370.5) {
      throw new IllegalStateException("The input chunk is using an unrecognized floating point format: " + floatcheck);
    }
  }
  
}


