package unluac.decompile.block;

import unluac.decompile.Decompiler;
import unluac.decompile.Output;
import unluac.decompile.statement.Statement;
import unluac.parse.LFunction;

public class BooleanIndicator extends Block {

  public BooleanIndicator(LFunction function, int line) {
    super(function, line, line);
  }

  @Override
  public void addStatement(Statement statement) {
    
  }

  @Override
  public boolean isContainer() {
    return false;
  }
  
  @Override
  public boolean isUnprotected() {
    return false;
  }

  @Override
  public boolean breakable() {
    return false;
  }
  
  @Override
  public int getLoopback() {
    throw new IllegalStateException();
  }

  @Override
  public void print(Decompiler d, Output out) {
    out.print("-- unhandled boolean indicator");
  }
  
}
