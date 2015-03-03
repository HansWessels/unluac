package unluac.decompile;

import unluac.decompile.expression.UpvalueExpression;
import unluac.parse.LFunction;
import unluac.parse.LUpvalue;

public class Upvalues {

  private final LUpvalue[] upvalues;
  
  public Upvalues(LFunction func, LUpvalue[] upvalues) {
    this.upvalues = upvalues;
    for(LUpvalue upvalue : upvalues) {
      if(upvalue.name.isEmpty() && !upvalue.instack) {
        LUpvalue[] parentvals = func.parent.upvalues;
        if(upvalue.idx >= 0 && upvalue.idx < parentvals.length) {
          upvalue.name = parentvals[upvalue.idx].name;
        }
      }
    }
  }
  
  public String getName(int index) {
    if(index < upvalues.length && upvalues[index].name != null && !upvalues[index].name.isEmpty()) {
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
