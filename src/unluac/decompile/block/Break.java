package unluac.decompile.block;

import unluac.decompile.Decompiler;
import unluac.decompile.Output;
import unluac.decompile.statement.Statement;
import unluac.parse.LFunction;

public class Break extends Block {

  public final int target;
  
  public Break(LFunction function, int line, int target) {
    super(function, line, line);
    this.target = target;
  }

  @Override
  public void addStatement(Statement statement) {
    throw new IllegalStateException();
  }

  @Override
  public boolean isContainer() {
    return false;
  }
  
  @Override
  public boolean breakable() {
    return false;
  }
  
  @Override
  public boolean isUnprotected() {
    //Actually, it is unprotected, but not really a block
    return false;
  }

  @Override
  public int getLoopback() {
    throw new IllegalStateException();
  }

  @Override
  public void print(Decompiler d, Output out) {
    out.print("do break end");
  }
  
  @Override
  public void printTail(Decompiler d, Output out) {
    out.print("break");
  }
  
}
