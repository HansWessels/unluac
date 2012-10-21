package unluac.parse;


abstract public class LObject extends BObject {

  public String deref() {
    throw new IllegalStateException();
  }
  
  abstract public boolean equals(Object o);
  
}
