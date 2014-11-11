package unluac.decompile.operation;

import unluac.decompile.Registers;
import unluac.decompile.block.Block;
import unluac.decompile.expression.Expression;
import unluac.decompile.statement.Assignment;
import unluac.decompile.statement.Statement;
import unluac.decompile.target.UpvalueTarget;

public class UpvalueSet extends Operation {

  private UpvalueTarget target;
  private Expression value;
  
  public UpvalueSet(int line, String upvalue, Expression value) {
    super(line);
    target = new UpvalueTarget(upvalue);
    this.value = value;
  }

  @Override
  public Statement process(Registers r, Block block) {
    return new Assignment(target, value);
  }
  
}
