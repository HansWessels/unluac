package unluac.decompile.expression;

import unluac.decompile.Decompiler;
import unluac.decompile.Output;

public class UnaryExpression extends Expression {

  private final String op;
  private final Expression expression;
  
  public UnaryExpression(String op, Expression expression, int precedence) {
    super(precedence);
    this.op = op;
    this.expression = expression;
  }

  @Override
  public boolean isUngrouped() {
    return true;
  }
  
  @Override
  public int getConstantIndex() {
    return expression.getConstantIndex();
  }
  
  @Override
  public void print(Decompiler d, Output out) {
    out.print(op);
    if(precedence > expression.precedence) out.print("(");
    expression.print(d, out);
    if(precedence > expression.precedence) out.print(")");
  }
  
}
