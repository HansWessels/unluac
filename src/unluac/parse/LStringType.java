package unluac.parse;

import java.nio.ByteBuffer;


public class LStringType extends BObjectType<LString> {

  private ThreadLocal<StringBuilder> b = new ThreadLocal<StringBuilder>() {
    
    @Override
    protected StringBuilder initialValue() {
      return new StringBuilder();  
    }

    
  };
  
  @Override
  public LString parse(final ByteBuffer buffer, BHeader header) {
    BSizeT sizeT = header.sizeT.parse(buffer, header);
    final StringBuilder b = this.b.get();
    b.setLength(0);
    sizeT.iterate(new Runnable() {
      
      @Override
      public void run() {
        b.append((char) (0xFF & buffer.get()));
      }
      
    });
    String s = b.toString();
    if(header.debug) {
      System.out.println("-- parsed <string> \"" + s + "\"");
    }
    return new LString(sizeT, s);
  }

}
