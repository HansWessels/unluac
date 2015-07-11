package unluac.util;

import java.util.ArrayList;
import java.util.Collections;

public class Stack<T> {

  private final ArrayList<T> data;
  
  public Stack() {
    data = new ArrayList<T>();
  }
  
  public boolean isEmpty() {
    return data.isEmpty();
  }
  
  public T peek() {
    return data.get(data.size() - 1);
  }
  
  public T pop() {
    return data.remove(data.size() - 1);
  }
  
  public void push(T item) {
    if (data.size() > 65536) {
      throw new IndexOutOfBoundsException("Trying to push more than 65536 items!");
    }
    data.add(item);
  }
  
  public int size() {
    return data.size();
  }
  
  public void reverse() {
    Collections.reverse(data);
  }
  
}
