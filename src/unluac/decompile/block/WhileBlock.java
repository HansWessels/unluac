package unluac.decompile.block;

import java.util.ArrayList;
import java.util.List;

import unluac.decompile.Output;
import unluac.decompile.Registers;
import unluac.decompile.branch.Branch;
import unluac.decompile.statement.Statement;
import unluac.parse.LFunction;

public class WhileBlock extends Block {

  private final Branch branch;
  private final int loopback;
  private final Registers r;
  private final List<Statement> statements;
  
  public WhileBlock(LFunction function, Branch branch, int loopback, Registers r) {
    super(function, branch.begin, branch.end);
    this.branch = branch;
    this.loopback = loopback;
    this.r = r;
    statements = new ArrayList<Statement>(branch.end - branch.begin + 1);
  }
  
  @Override
  public int scopeEnd() {
    return end - 2;
  }
  
  @Override
  public boolean breakable() {
    return true;
  }
  
  @Override
  public boolean isContainer() {
    return true;
  }
  
  @Override
  public void addStatement(Statement statement) {
    statements.add(statement);
  }
  
  @Override
  public boolean isUnprotected() {
    return true;
  }
  
  @Override
  public int getLoopback() {
    return loopback;
  }
  
  @Override
  public void print(Output out) {
    out.print("while ");
    branch.asExpression(r).print(out);
    out.print(" do");
    out.println();
    out.indent();
    Statement.printSequence(out, statements);
    out.dedent();
    out.print("end");
  }
  
}