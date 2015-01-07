package unluac.decompile.expression;

import unluac.decompile.Constant;
import unluac.decompile.Output;

public class ConstantExpression extends Expression {

  private final Constant constant;
  private final int index;
  
  public ConstantExpression(Constant constant, int index) {
    super(PRECEDENCE_ATOMIC);
    this.constant = constant;
    this.index = index;
  }

  public int getConstantIndex() {
    return index;
  }
  
  @Override
  public void print(Output out) {
    constant.print(out, false);
  }
  
  @Override
  public void printBraced(Output out) {
    constant.print(out, true);
  }
  
  @Override
  public boolean isConstant() {
    return true;
  }
  
  @Override
  public boolean isUngrouped() {
    return true;
  }
  
  @Override
  public boolean isNil() {
    return constant.isNil();
  }
  
  @Override
  public boolean isBoolean() {
    return constant.isBoolean();
  }
  
  @Override
  public boolean isInteger() {
    return constant.isInteger();
  }
  
  @Override
  public int asInteger() {
    return constant.asInteger();
  }
  
  @Override
  public boolean isString() {
    return constant.isString();
  }
  
  @Override
  public boolean isIdentifier() {
    return constant.isIdentifier();
  }
    
  @Override
  public String asName() {
    return constant.asName();
  }
  
  @Override
  public boolean isBrief() {
    return !constant.isString() || constant.asName().length() <= 10;
  }
  
}
