package unluac.decompile.statement;

import java.util.List;

import unluac.decompile.Output;
import unluac.decompile.block.IfThenElseBlock;

abstract public class Statement {

  /**
   * Prints out a sequences of statements on separate lines. Correctly
   * informs the last statement that it is last in a block.
   */
  public static void printSequence(Output out, List<Statement> stmts) {
    int n = stmts.size();
    int i = 1;
    for(Statement stmt : stmts) {
      if(i == n) {
        stmt.printTail(out);
      } else {
        stmt.print(out);
      }
      if(!(stmt instanceof IfThenElseBlock)) {
        out.println();
      }
      i++;
    }
  }
    
  abstract public void print(Output out);
  
  public void printTail(Output out) {
    print(out);
  }
  
  public String comment;
  
  public void addComment(String comment) {
    this.comment = comment;
  }
  
}
