package unluac.decompile.operation;

import unluac.decompile.Registers;
import unluac.decompile.block.Block;
import unluac.decompile.expression.FunctionCall;
import unluac.decompile.statement.FunctionCallStatement;
import unluac.decompile.statement.Statement;

public class CallOperation extends Operation {

  private FunctionCall call;
  
  public CallOperation(int line, FunctionCall call) {
    super(line);
    this.call = call;
  }

  @Override
  public Statement process(Registers r, Block block) {
    return new FunctionCallStatement(call);
  }
  
}
