package unluac.decompile;

import unluac.decompile.expression.UpvalueExpression;

public class Upvalues {

  private final String[] names;
  
  public Upvalues(String[] names) {
    this.names = names;
  }
  
  public String getName(int index) {
    if(index < names.length) {
      return names[index];
    } else {
      //TODO: SET ERROR
      return "_UPVALUE" + index + "_";
    }
  }
  
  public UpvalueExpression getExpression(int index) {
    return new UpvalueExpression(getName(index));
  }
  
}
