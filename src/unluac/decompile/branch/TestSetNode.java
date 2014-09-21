package unluac.decompile.branch;

import unluac.decompile.Registers;
import unluac.decompile.expression.Expression;

public class TestSetNode extends Branch {

  public final int test;
  public final boolean invert;
    
  public TestSetNode(int target, int test, boolean invert, int line, int begin, int end) {
    super(line, begin, end);
    this.test = test;
    this.invert = invert;
    this.setTarget = target;
  }
  
  @Override
  public Branch invert() {
    return new TestSetNode(setTarget, test, !invert, line, end, begin);
  }

  @Override
  public int getRegister() {
    return setTarget;
  }
  
  @Override
  public Expression asExpression(Registers r) {
    return r.getExpression(test, line);
  }
 
  @Override
  public void useExpression(Expression expression) {
    /* Do nothing */
  }
 
  @Override
  public String toString() {
    return "TestSetNode[target=" + setTarget + ";test=" + test + ";invert=" + invert + ";line=" + line + ";begin=" + begin + ";end=" + end + "]";
  }
  
}