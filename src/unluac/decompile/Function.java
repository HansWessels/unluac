package unluac.decompile;

import unluac.decompile.expression.ConstantExpression;
import unluac.decompile.expression.GlobalExpression;
import unluac.parse.LFunction;

public class Function {

  private Constant[] constants;
  
  public Function(LFunction function) {
    constants = new Constant[function.constants.length];
    for(int i = 0; i < constants.length; i++) {
      constants[i] = new Constant(function.constants[i]);
    }
  }
  
  public String getGlobalName(int constantIndex) {
    return constants[constantIndex].asName();
  }
  
  public ConstantExpression getConstantExpression(int constantIndex) {
    return new ConstantExpression(constants[constantIndex], constantIndex);
  }
  
  public GlobalExpression getGlobalExpression(int constantIndex) {
    return new GlobalExpression(getGlobalName(constantIndex), constantIndex);
  }
  
}
