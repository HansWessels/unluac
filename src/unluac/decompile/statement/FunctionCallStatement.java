package unluac.decompile.statement;

import unluac.decompile.Output;
import unluac.decompile.expression.FunctionCall;

public class FunctionCallStatement extends Statement {

  private FunctionCall call;
  
  public FunctionCallStatement(FunctionCall call) {
    this.call = call;
  }

  @Override
  public void print(Output out) {
    call.print(out);
  }
  
  @Override
  public boolean beginsWithParen() {
    return call.beginsWithParen();
  }
  
}
