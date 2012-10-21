package unluac.decompile.operation;

import unluac.decompile.Registers;
import unluac.decompile.block.Block;
import unluac.decompile.statement.Statement;

abstract public class Operation {

  public final int line;
  
  public Operation(int line) {
    this.line = line;
  }
  
  abstract public Statement process(Registers r, Block block);
  
}
