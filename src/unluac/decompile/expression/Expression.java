package unluac.decompile.expression;

import java.util.List;

import unluac.decompile.Constant;
import unluac.decompile.Decompiler;
import unluac.decompile.Output;
import unluac.decompile.target.Target;
import unluac.parse.LNil;

abstract public class Expression {

  public static final int PRECEDENCE_OR = 1;
  public static final int PRECEDENCE_AND = 2;
  public static final int PRECEDENCE_COMPARE = 3;
  public static final int PRECEDENCE_BOR = 4;
  public static final int PRECEDENCE_BXOR = 5;
  public static final int PRECEDENCE_BAND = 6;
  public static final int PRECEDENCE_SHIFT = 7;
  public static final int PRECEDENCE_CONCAT = 8;
  public static final int PRECEDENCE_ADD = 9;
  public static final int PRECEDENCE_MUL = 10;
  public static final int PRECEDENCE_UNARY = 11;
  public static final int PRECEDENCE_POW = 12;
  public static final int PRECEDENCE_ATOMIC = 13;
  
  public static final int ASSOCIATIVITY_NONE = 0;
  public static final int ASSOCIATIVITY_LEFT = 1;
  public static final int ASSOCIATIVITY_RIGHT = 2;
  
  public static final Expression NIL = new ConstantExpression(new Constant(LNil.NIL), -1);
  
  public static BinaryExpression makeCONCAT(Expression left, Expression right) {
    return new BinaryExpression("..", left, right, PRECEDENCE_CONCAT, ASSOCIATIVITY_RIGHT);
  }
  
  public static BinaryExpression makeADD(Expression left, Expression right) {
    return new BinaryExpression("+", left, right, PRECEDENCE_ADD, ASSOCIATIVITY_LEFT);
  }
  
  public static BinaryExpression makeSUB(Expression left, Expression right) {
    return new BinaryExpression("-", left, right, PRECEDENCE_ADD, ASSOCIATIVITY_LEFT);
  }
  
  public static BinaryExpression makeMUL(Expression left, Expression right) {
    return new BinaryExpression("*", left, right, PRECEDENCE_MUL, ASSOCIATIVITY_LEFT);
  }
  
  public static BinaryExpression makeDIV(Expression left, Expression right) {
    return new BinaryExpression("/", left, right, PRECEDENCE_MUL, ASSOCIATIVITY_LEFT);
  }
  
  public static BinaryExpression makeMOD(Expression left, Expression right) {
    return new BinaryExpression("%", left, right, PRECEDENCE_MUL, ASSOCIATIVITY_LEFT);
  }
  
  public static UnaryExpression makeUNM(Expression expression) {
    return new UnaryExpression("-", expression, PRECEDENCE_UNARY);
  }
  
  public static UnaryExpression makeNOT(Expression expression) {
    return new UnaryExpression("not ", expression, PRECEDENCE_UNARY);
  }
  
  public static UnaryExpression makeLEN(Expression expression) {
    return new UnaryExpression("#", expression, PRECEDENCE_UNARY);
  }
  
  public static BinaryExpression makePOW(Expression left, Expression right) {
    return new BinaryExpression("^", left, right, PRECEDENCE_POW, ASSOCIATIVITY_RIGHT);
  }
  
  public static BinaryExpression makeIDIV(Expression left, Expression right) {
    return new BinaryExpression("//", left, right, PRECEDENCE_MUL, ASSOCIATIVITY_LEFT);
  }
  
  public static BinaryExpression makeBAND(Expression left, Expression right) {
    return new BinaryExpression("&", left, right, PRECEDENCE_BAND, ASSOCIATIVITY_LEFT);
  }
  
  public static BinaryExpression makeBOR(Expression left, Expression right) {
    return new BinaryExpression("|", left, right, PRECEDENCE_BOR, ASSOCIATIVITY_LEFT);
  }
  
  public static BinaryExpression makeBXOR(Expression left, Expression right) {
    return new BinaryExpression("~", left, right, PRECEDENCE_BXOR, ASSOCIATIVITY_LEFT);
  }
  
  public static BinaryExpression makeSHL(Expression left, Expression right) {
    return new BinaryExpression("<<", left, right, PRECEDENCE_SHIFT, ASSOCIATIVITY_LEFT);
  }
  
  public static BinaryExpression makeSHR(Expression left, Expression right) {
    return new BinaryExpression(">>", left, right, PRECEDENCE_SHIFT, ASSOCIATIVITY_LEFT);
  }
  
  public static UnaryExpression makeBNOT(Expression expression) {
    return new UnaryExpression("~", expression, PRECEDENCE_UNARY);
  }
  
  /**
   * Prints out a sequences of expressions with commas, and optionally
   * handling multiple expressions and return value adjustment.
   */
  public static void printSequence(Decompiler d, Output out, List<Expression> exprs, boolean linebreak, boolean multiple) {
    int n = exprs.size();
    int i = 1;
    for(Expression expr : exprs) {
      boolean last = (i == n);
      if(expr.isMultiple()) {
        last = true;
      }
      if(last) {
        if(multiple) {
          expr.printMultiple(d, out);
        } else {
          expr.print(d, out);
        }
        break;
      } else {
        expr.print(d, out);
        out.print(",");
        if(linebreak) {
          out.println();
        } else {
          out.print(" ");
        }
      }
      i++;
    }
  }
  
  public final int precedence;
  
  public Expression(int precedence) {
    this.precedence = precedence;
  }
  
  protected static void printUnary(Decompiler d, Output out, String op, Expression expression) {
    out.print(op);
    expression.print(d, out);
  }
  
  protected static void printBinary(Decompiler d, Output out, String op, Expression left, Expression right) {
    left.print(d, out);
    out.print(" ");
    out.print(op);
    out.print(" ");
    right.print(d, out);
  }
  
  abstract public void print(Decompiler d, Output out);
  
  /**
   * Prints the expression in a context where it is surrounded by braces.
   * (Thus if the expression would begin with a brace, it must be enclosed
   * in parentheses to avoid ambiguity.)
   */
  public void printBraced(Decompiler d, Output out) {
    print(d, out);
  }
  
  /**
   * Prints the expression in a context that accepts multiple values.
   * (Thus, if an expression that normally could return multiple values
   * doesn't, it should use parens to adjust to 1.)
   */
  public void printMultiple(Decompiler d, Output out) {
    print(d, out);
  }
  
  /**
   * Determines the index of the last-declared constant in this expression.
   * If there is no constant in the expression, return -1.
   */
  abstract public int getConstantIndex();
  
  public boolean beginsWithParen() {
    return false;
  }
  
  public boolean isNil() {
    return false;
  }
  
  public boolean isClosure() {
    return false;
  }
  
  public boolean isConstant() {
    return false;
  }
  
  /**
   * An ungrouped expression is one that needs to be enclosed in parentheses
   * before it can be dereferenced. This doesn't apply to multiply-valued expressions
   * as those will be given parentheses automatically when converted to a single value.
   * e.g.
   *  (a+b).c; ("asdf"):gsub()
   */
  public boolean isUngrouped() {
    return false;
  }
  
  // Only supported for closures
  public boolean isUpvalueOf(int register) {
    throw new IllegalStateException();
  }
  
  public boolean isBoolean() {
    return false;
  }
  
  public boolean isInteger() {
    return false;
  }
  
  public int asInteger() {
    throw new IllegalStateException();
  }
  
  public boolean isString() {
    return false;
  }
  
  public boolean isIdentifier() {
    return false;
  }
  
  /**
   * Determines if this can be part of a function name.
   * Is it of the form: {Name . } Name
   */
  public boolean isDotChain() {
    return false;
  }
  
  public int closureUpvalueLine() {
    throw new IllegalStateException();
  }
  
  public void printClosure(Decompiler d, Output out, Target name) {
    throw new IllegalStateException();
  }
  
  public String asName() {
    throw new IllegalStateException();
  }
  
  public boolean isTableLiteral() {
    return false;
  }
  
  public boolean isNewEntryAllowed() {
    throw new IllegalStateException();
  }
  
  public void addEntry(TableLiteral.Entry entry) {
    throw new IllegalStateException();
  }
  
  /**
   * Whether the expression has more than one return stored into registers.
   */
  public boolean isMultiple() {
    return false;
  }
  
  public boolean isMemberAccess() {
    return false;
  }
  
  public Expression getTable() {
    throw new IllegalStateException();
  }
  
  public String getField() {
    throw new IllegalStateException();
  }  
  
  public boolean isBrief() {
    return false;
  }
  
  public boolean isEnvironmentTable(Decompiler d) {
    return false;
  }
  
}
