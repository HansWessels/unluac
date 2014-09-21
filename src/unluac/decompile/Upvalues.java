package unluac.decompile;

import unluac.decompile.expression.UpvalueExpression;
import unluac.parse.LUpvalue;

public class Upvalues {

  private final LUpvalue[] upvalues;
  
  public Upvalues(LUpvalue[] upvalues) {
    this.upvalues = upvalues;
  }
  
  public String getName(int index) {
    if(index < upvalues.length && upvalues[index].name != null) {
      return upvalues[index].name;
    } else {
      //TODO: SET ERROR
      return "_UPVALUE" + index + "_";
    }
  }
  
  public UpvalueExpression getExpression(int index) {
    return new UpvalueExpression(getName(index));
  }
  
}
