package unluac.parse;

public class LUpvalue extends BObject {

  public boolean instack;
  public int idx;
  
  public String name;
  
  public boolean equals(Object obj) {
    if(obj instanceof LUpvalue) {
      LUpvalue upvalue = (LUpvalue) obj;
      if(!(instack == upvalue.instack && idx == upvalue.idx)) {
        return false;
      }
      if(name == upvalue.name) {
        return true;
      }
      return name != null && name.equals(upvalue.name);
    } else {
      return false;
    }
  }
  
}
