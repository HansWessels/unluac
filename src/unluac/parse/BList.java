package unluac.parse;

import java.util.List;

public class BList<T extends BObject> extends BObject {

  public final BInteger length;
  private final List<T> values;
  
  public BList(BInteger length, List<T> values) {
    this.length = length;
    this.values = values;
  }
  
  public T get(int index) {
    return values.get(index);
  }
  
  public T[] asArray(final T[] array) {
    length.iterate(new Runnable() {
      
      private int i = 0;
      
      @Override
      public void run() {
        array[i] = values.get(i);
        i++;
      }
      
    });
    return array;
  }
  
}
