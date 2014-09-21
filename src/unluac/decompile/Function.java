package unluac.decompile;

import unluac.Version;
import unluac.decompile.expression.ConstantExpression;
import unluac.decompile.expression.GlobalExpression;
import unluac.parse.LFunction;

public class Function {

  private Constant[] constants;
  private final int constantsOffset;
  
  public Function(LFunction function) {
    constants = new Constant[function.constants.length];
    for(int i = 0; i < constants.length; i++) {
      constants[i] = new Constant(function.constants[i]);
    }
    if(function.header.version == Version.LUA50) {
      constantsOffset = 250;
    } else {
      constantsOffset = 256;
    }
  }
  
  public boolean isConstant(int register) {
    return register >= constantsOffset;
  }

  public int constantIndex(int register) {
    return register - constantsOffset;
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
