package unluac.parse;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

abstract public class BObjectType<T extends BObject>  {
  
  abstract public T parse(ByteBuffer buffer, BHeader header);

  public final BList<T> parseList(final ByteBuffer buffer, final BHeader header) {
    BInteger length = header.integer.parse(buffer, header);
    final List<T> values = new ArrayList<T>();
    length.iterate(new Runnable() {
      
      @Override
      public void run() {
        values.add(parse(buffer, header));
      }
      
    });
    return new BList<T>(length, values);
  }
  
}
