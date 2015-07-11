package unluac.decompile.block;

import java.util.ArrayList;
import java.util.List;

import unluac.decompile.Decompiler;
import unluac.decompile.Output;
import unluac.decompile.statement.Statement;
import unluac.parse.LFunction;

public class ElseEndBlock extends Block {

  private final List<Statement> statements;
  public IfThenElseBlock partner;
  
  public ElseEndBlock(LFunction function, int begin, int end) {
    super(function, begin, end);
    statements = new ArrayList<Statement>(end - begin + 1);
  }
    
  @Override
  public int compareTo(Block block) {
    if(block == partner) {
      return 1;
    } else {
      int result = super.compareTo(block);
      if(result == 0 && block instanceof ElseEndBlock) {
        System.out.println("HEY HEY HEY");
      }
      return result;
    }
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
    if(statements.size() == 1 && statements.get(0) instanceof IfThenEndBlock) {
      out.print("else");
      statements.get(0).print(d, out);
    } else if(statements.size() == 2 && statements.get(0) instanceof IfThenElseBlock && statements.get(1) instanceof ElseEndBlock) {
      out.print("else");
      statements.get(0).print(d, out);
      statements.get(1).print(d, out);
    } else {
      out.print("else");
      out.println();
      out.indent();
      Statement.printSequence(d, out, statements);
      out.dedent();
      out.print("end");
    }
  }
  
}