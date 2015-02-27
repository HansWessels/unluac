package unluac.parse;

import java.nio.ByteBuffer;


public abstract class LConstantType extends BObjectType<LObject> {
  
  public static LConstantType50 getType50() {
    return new LConstantType50();
  }
  
  public static LConstantType53 getType53() {
    return new LConstantType53();
  }
  
}

class LConstantType50 extends LConstantType {

  @Override
  public LObject parse(ByteBuffer buffer, BHeader header) {
    int type = 0xFF & buffer.get();
    if(header.debug) {
      System.out.print("-- parsing <constant>, type is ");
      switch(type) {
        case 0:
          System.out.println("<nil>");
          break;
        case 1:
          System.out.println("<boolean>");
          break;
        case 3:
          System.out.println("<number>");
          break;
        case 4:
          System.out.println("<string>");
          break;
        default:
          System.out.println("illegal " + type);
          break;
      }
    }
    switch(type) {
      case 0:
        return LNil.NIL;
      case 1:
        return header.bool.parse(buffer, header);
      case 3:
        return header.number.parse(buffer, header);
      case 4:
        return header.string.parse(buffer, header);
      default:
        throw new IllegalStateException();
    }
  }
  
}

class LConstantType53 extends LConstantType {

  @Override
  public LObject parse(ByteBuffer buffer, BHeader header) {
    int type = 0xFF & buffer.get();
    if(header.debug) {
      System.out.print("-- parsing <constant>, type is ");
      switch(type) {
        case 0:
          System.out.println("<nil>");
          break;
        case 1:
          System.out.println("<boolean>");
          break;
        case 3:
          System.out.println("<float>");
          break;
        case 0x13:
          System.out.println("<integer>");
          break;
        case 4:
          System.out.println("<short string>");
          break;
        case 0x14:
          System.out.println("<long string>");
          break;
        default:
          System.out.println("illegal " + type);
          break;
      }
    }
    switch(type) {
      case 0:
        return LNil.NIL;
      case 1:
        return header.bool.parse(buffer, header);
      case 3:
        return header.lfloat.parse(buffer, header);
      case 0x13:
        return header.linteger.parse(buffer, header);
      case 4:
      case 0x14:
        return header.string.parse(buffer, header);
      default:
        throw new IllegalStateException();
    }
  }
  
}
