package unluac.parse;

import java.nio.ByteBuffer;


public class LConstantType extends BObjectType<LObject> {

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
          System.out.println("<strin>");
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
