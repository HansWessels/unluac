package unluac.decompile.block;

import java.util.ArrayList;
import java.util.List;

import unluac.decompile.Decompiler;
import unluac.decompile.Output;
import unluac.decompile.statement.Statement;
import unluac.parse.LFunction;

public class DoEndBlock extends Block {

  private final List<Statement> statements;
  
  public DoEndBlock(LFunction function, int begin, int end) {
    super(function, begin, end);
    statements = new ArrayList<Statement>(end - begin + 1);
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
  public void print(Decompiler d, Output out) {
    out.println("do");
    out.indent();
    Statement.printSequence(d, out, statements);
    out.dedent();
    out.print("end");
  }
  
}
