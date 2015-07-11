package unluac.decompile.expression;

import unluac.decompile.Declaration;
import unluac.decompile.Decompiler;
import unluac.decompile.Output;

public class LocalVariable extends Expression {
  
  private final Declaration decl;
  
  public LocalVariable(Declaration decl) {
    super(PRECEDENCE_ATOMIC);
    this.decl = decl;
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
    out.print(decl.name);
  }
  
  @Override
  public boolean isBrief() {
    return true;
  }
  
}
