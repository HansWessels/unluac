package unluac.decompile.block;

import unluac.decompile.Output;
import unluac.decompile.statement.Statement;

public class Break extends Block {

  public final int target;
  
  public Break(int line, int target) {
    super(line, line);
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
  public void print(Output out) {
    out.print("do break end");
  }
  
  @Override
  public void printTail(Output out) {
    out.print("break");
  }
  
}
