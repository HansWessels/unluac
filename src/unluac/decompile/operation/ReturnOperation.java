package unluac.decompile.operation;

import unluac.decompile.Registers;
import unluac.decompile.block.Block;
import unluac.decompile.expression.Expression;
import unluac.decompile.statement.Return;
import unluac.decompile.statement.Statement;

public class ReturnOperation extends Operation {

  private Expression[] values;
  
  public ReturnOperation(int line, Expression value) {
    super(line);
    values = new Expression[1];
    values[0] = value;
  }
  
  public ReturnOperation(int line, Expression[] values) {
    super(line);
    this.values = values;
  }

  @Override
  public Statement process(Registers r, Block block) {    
    return new Return(values);
  }
  
}
