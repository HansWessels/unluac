package unluac.decompile.block;

import java.util.ArrayList;
import java.util.List;

import unluac.decompile.Decompiler;
import unluac.decompile.Output;
import unluac.decompile.Registers;
import unluac.decompile.expression.Expression;
import unluac.decompile.statement.Statement;
import unluac.parse.LFunction;

public class ForBlock extends Block {

  private final int register;
  private final Registers r;
  private final List<Statement> statements;
  
  public ForBlock(LFunction function, int begin, int end, int register, Registers r) {
    super(function, begin, end);
    this.register = register;
    this.r = r;
    statements = new ArrayList<Statement>(end - begin + 1);
  }

  @Override
  public int scopeEnd() {
    return end - 2;
  }
  
  @Override
  public void addStatement(Statement statement) {
    statements.add(statement);    
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
  public boolean isUnprotected() {
    return false;
  }

  @Override
  public int getLoopback() {
    throw new IllegalStateException();
  }

  @Override
  public void print(Decompiler d, Output out) {
    out.print("for ");
    r.getTarget(register + 3, begin - 1).print(d, out);
    out.print(" = ");
    r.getValue(register, begin - 1).print(d, out);
    out.print(", ");
    r.getValue(register + 1, begin - 1).print(d, out);
    Expression step = r.getValue(register + 2, begin - 1);
    if(!step.isInteger() || step.asInteger() != 1) {
      out.print(", ");
      step.print(d, out);
    }
    out.print(" do");
    out.println();
    out.indent();
    Statement.printSequence(d, out, statements);
    out.dedent();
    out.print("end");
  }
  
}
