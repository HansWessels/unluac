package unluac.decompile.expression;

import unluac.decompile.Decompiler;
import unluac.decompile.Output;

public class Vararg extends Expression {

  private final int length;
  private final boolean multiple;
  
  public Vararg(int length, boolean multiple) {
    super(PRECEDENCE_ATOMIC);
    this.length = length;
    this.multiple = multiple;
  }

  @Override
  public int getConstantIndex() {
    return -1;
  }
  
  @Override
  public void print(Decompiler d, Output out) {
    //out.print("...");
    out.print(multiple ? "..." : "(...)");
  }
  
  @Override
  public void printMultiple(Decompiler d, Output out) {
    out.print(multiple ? "..." : "(...)");
  }
    
  @Override
  public boolean isMultiple() {
    return multiple;
  }
  
}
