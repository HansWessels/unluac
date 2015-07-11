package unluac.decompile.block;

import java.util.ArrayList;
import java.util.List;

import unluac.decompile.Decompiler;
import unluac.decompile.Output;
import unluac.decompile.Registers;
import unluac.decompile.branch.Branch;
import unluac.decompile.statement.Statement;
import unluac.parse.LFunction;

public class RepeatBlock extends Block {

  private final Branch branch;
  private final Registers r;
  private final List<Statement> statements;
  
  public RepeatBlock(LFunction function, Branch branch, Registers r) {
    super(function, branch.end, branch.begin);
    //System.out.println("-- creating repeat block " + branch.end + " .. " + branch.begin);
    this.branch = branch;
    this.r = r;
    statements = new ArrayList<Statement>(branch.begin - branch.end + 1);
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
    return false;
  }
  
  @Override
  public int getLoopback() {
    throw new IllegalStateException();
  }
  
  @Override
  public void print(Decompiler d, Output out) {
    out.print("repeat");
    out.println();
    out.indent();
    Statement.printSequence(d, out, statements);
    out.dedent();
    out.print("until ");
    branch.asExpression(r).print(d, out);
  }
  
}
