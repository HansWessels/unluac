package unluac.decompile.expression;

import unluac.decompile.Output;

public class BinaryExpression extends Expression {

  private final String op;
  private final Expression left;
  private final Expression right;
  private final int associativity;
  
  public BinaryExpression(String op, Expression left, Expression right, int precedence, int associativity) {
    super(precedence);
    this.op = op;
    this.left = left;
    this.right = right;
    this.associativity = associativity;
  }

  @Override
  public int getConstantIndex() {
    return Math.max(left.getConstantIndex(), right.getConstantIndex());
  }
  
  @Override
  public void print(Output out) {
    final boolean leftGroup = precedence > left.precedence || (precedence == left.precedence && associativity == ASSOCIATIVITY_RIGHT);
    final boolean rightGroup = precedence > right.precedence || (precedence == right.precedence && associativity == ASSOCIATIVITY_LEFT);
    if(leftGroup) out.print("(");
    left.print(out);
    if(leftGroup) out.print(")");
    out.print(" ");
    out.print(op);
    out.print(" ");
    if(rightGroup) out.print("(");
    right.print(out);
    if(rightGroup) out.print(")");
  }
  
}
