package unluac.decompile.branch;

import unluac.decompile.Registers;
import unluac.decompile.expression.BinaryExpression;
import unluac.decompile.expression.Expression;
import unluac.decompile.expression.UnaryExpression;

public class LTNode extends Branch {

  private final int left;
  private final int right;
  private final boolean invert;
  
  public LTNode(int left, int right, boolean invert, int line, int begin, int end) {
    super(line, begin, end);
    this.left = left;
    this.right = right;
    this.invert = invert;
  }
  
  @Override
  public Branch invert() {
    return new LTNode(left, right, !invert, line, end, begin);
  }
  
  @Override
  public int getRegister() {
    return -1;
  }
  
  @Override
  public Expression asExpression(Registers r) {
    boolean transpose = false;
    Expression leftExpression = r.getKExpression(left, line);
    Expression rightExpression = r.getKExpression(right, line);
    if(!leftExpression.isConstant() && !rightExpression.isConstant()) {
      transpose = r.getUpdated(left, line) > r.getUpdated(right, line);
    } else {
      transpose = rightExpression.getConstantIndex() < leftExpression.getConstantIndex();
    }
    String op = !transpose ? "<" : ">";
    Expression rtn = new BinaryExpression(op, !transpose ? leftExpression : rightExpression, !transpose ? rightExpression : leftExpression, Expression.PRECEDENCE_COMPARE, Expression.ASSOCIATIVITY_LEFT);
    if(invert) {
      rtn = new UnaryExpression("not ", rtn, Expression.PRECEDENCE_UNARY);
    }
    return rtn;
  }
  
  @Override
  public void useExpression(Expression expression) {
    /* Do nothing */
  }
  
}