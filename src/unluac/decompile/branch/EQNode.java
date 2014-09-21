package unluac.decompile.branch;

import unluac.decompile.Registers;
import unluac.decompile.expression.BinaryExpression;
import unluac.decompile.expression.Expression;

public class EQNode extends Branch {

  private final int left;
  private final int right;
  private final boolean invert;
  
  public EQNode(int left, int right, boolean invert, int line, int begin, int end) {
    super(line, begin, end);
    this.left = left;
    this.right = right;
    this.invert = invert;
  }
  
  @Override
  public Branch invert() {
    return new EQNode(left, right, !invert, line, end, begin);
  }

  @Override
  public int getRegister() {
    return -1;
  }
  
  @Override
  public Expression asExpression(Registers r) {
    boolean transpose = false;
    String op = invert ? "~=" : "==";
    return new BinaryExpression(op, r.getKExpression(!transpose ? left : right, line), r.getKExpression(!transpose ? right : left, line), Expression.PRECEDENCE_COMPARE, Expression.ASSOCIATIVITY_LEFT);
  }
  
  @Override
  public void useExpression(Expression expression) {
    /* Do nothing */
  }
  
}
