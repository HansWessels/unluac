package unluac.parse;


public class LString extends LObject {

  public final BSizeT size;
  public final String value;
  
  public LString(BSizeT size, String value) {    
    this.size = size;
    this.value = value.length() == 0 ? "" : value.substring(0, value.length() - 1);
  }
  
  @Override
  public String deref() {
    return value;
  }
  
  @Override
  public String toString() {
    return "\"" + value + "\"";
  }
  
  @Override
  public boolean equals(Object o) {
    if(o instanceof LString) {
      LString os = (LString) o;
      return os.value.equals(value);
    }
    return false;
  }
  
}
