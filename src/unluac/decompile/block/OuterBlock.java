package unluac.decompile.block;

import java.util.ArrayList;
import java.util.List;

import unluac.decompile.Decompiler;
import unluac.decompile.Output;
import unluac.decompile.statement.Return;
import unluac.decompile.statement.Statement;
import unluac.parse.LFunction;

public class OuterBlock extends Block {

  private final List<Statement> statements;
  
  public OuterBlock(LFunction function, int length) {
    super(function, 0, length + 1);
    statements = new ArrayList<Statement>(length);
  }

  @Override
  public void addStatement(Statement statement) {
    statements.add(statement);
  }
  
  @Override
  public boolean breakable() {
    return false;
  }
  
  @Override
  public boolean isContainer() {
    return true;
  }
  
  @Override
  public boolean isUnprotected() {
    return false;
  }
  
  @Override
  public int getLoopback() {
    throw new IllegalStateException();
  }
  
  @Override
  public int scopeEnd() {
    return (end - 1) + function.header.version.getOuterBlockScopeAdjustment();
  }
  
  @Override
  public void print(Decompiler d, Output out) {
    /* extra return statement */
    int last = statements.size() - 1;
    if(last < 0 || !(statements.get(last) instanceof Return)) {
      throw new IllegalStateException(statements.get(last).toString());
    }
    statements.remove(last);
    Statement.printSequence(d, out, statements);
  }
  
}
