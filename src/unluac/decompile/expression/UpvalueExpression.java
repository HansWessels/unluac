package unluac.decompile.expression;

import unluac.decompile.Decompiler;
import unluac.decompile.Output;

public class UpvalueExpression extends Expression {

  private final String name;
  
  public UpvalueExpression(String name) {
    super(PRECEDENCE_ATOMIC);
    this.name = name;
  }

  @Override
  public int getConstantIndex() {
    return -1;
  }
  
  @Override
  public boolean isDotChain() {
    return true;
  }
  
  @Override
  public void print(Decompiler d, Output out) {
    out.print(name);
  }
  
  @Override
  public boolean isBrief() {
    return true;
  }
  
}
