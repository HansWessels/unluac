package unluac.decompile.branch;

import unluac.decompile.Constant;
import unluac.decompile.Registers;
import unluac.decompile.expression.ConstantExpression;
import unluac.decompile.expression.Expression;
import unluac.parse.LBoolean;

public class TrueNode extends Branch {

  public final int register;
  public final boolean invert;
  
  public TrueNode(int register, boolean invert, int line, int begin, int end) {
    super(line, begin, end);
    this.register = register;
    this.invert = invert;
    this.setTarget = register;
    //isTest = true;
  }
  
  @Override
  public Branch invert() {
    return new TrueNode(register, !invert, line, end, begin);
  }
  
  @Override
  public int getRegister() {
    return register; 
  }
  
  @Override
  public Expression asExpression(Registers r) {
    return new ConstantExpression(new Constant(invert ? LBoolean.LTRUE : LBoolean.LFALSE), -1); 
  }
  
  @Override
  public void useExpression(Expression expression) {
    /* Do nothing */
  }
  
  @Override
  public String toString() {
    return "TrueNode[invert=" + invert + ";line=" + line + ";begin=" + begin + ";end=" + end + "]";
  }
  
}