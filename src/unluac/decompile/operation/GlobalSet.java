package unluac.decompile.operation;

import unluac.decompile.Registers;
import unluac.decompile.block.Block;
import unluac.decompile.expression.Expression;
import unluac.decompile.statement.Assignment;
import unluac.decompile.statement.Statement;
import unluac.decompile.target.GlobalTarget;

public class GlobalSet extends Operation {

  private String global;
  private Expression value;
  
  public GlobalSet(int line, String global, Expression value) {
    super(line);
    this.global = global;
    this.value = value;
  }

  @Override
  public Statement process(Registers r, Block block) {
    return new Assignment(new GlobalTarget(global), value);
  }
  
}
