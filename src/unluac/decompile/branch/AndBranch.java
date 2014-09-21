package unluac.decompile.branch;

import unluac.decompile.Registers;
import unluac.decompile.expression.BinaryExpression;
import unluac.decompile.expression.Expression;

public class AndBranch extends Branch {

  private final Branch left;
  private final Branch right;
  
  public AndBranch(Branch left, Branch right) {
    super(right.line, right.begin, right.end);
    this.left = left;
    this.right = right;
  }
  
  @Override
  public Branch invert() {
    return new OrBranch(left.invert(), right.invert());
  }
  
  /*
  @Override
  public Branch invert() {
    return new NotBranch(new OrBranch(left.invert(), right.invert()));
  }
  */

  @Override
  public int getRegister() {
    int rleft = left.getRegister();
    int rright = right.getRegister();
    return rleft == rright ? rleft : -1;
  }
  
  @Override
  public Expression asExpression(Registers r) {
    return new BinaryExpression("and", left.asExpression(r), right.asExpression(r), Expression.PRECEDENCE_AND, Expression.ASSOCIATIVITY_NONE);
  }
  
  @Override
  public void useExpression(Expression expression) {
    left.useExpression(expression);
    right.useExpression(expression);
  }
  
}
