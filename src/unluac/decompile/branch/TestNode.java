package unluac.decompile.branch;

import unluac.decompile.Registers;
import unluac.decompile.expression.Expression;

public class TestNode extends Branch {

  public final int test;
  public final boolean invert;
  
  public TestNode(int test, boolean invert, int line, int begin, int end) {
    super(line, begin, end);
    this.test = test;
    this.invert = invert;
    isTest = true;
  }
  
  @Override
  public Branch invert() {
    return new TestNode(test, !invert, line, end, begin);
  }
  
  @Override
  public int getRegister() {
    return test;
  }
  
  @Override
  public Expression asExpression(Registers r) {
    if(invert) {
      return (new NotBranch(this.invert())).asExpression(r);
    } else {
      return r.getExpression(test, line);
    } 
  }
  
  @Override
  public void useExpression(Expression expression) {
    /* Do nothing */
  }
  
  @Override
  public String toString() {
    return "TestNode[test=" + test + ";invert=" + invert + ";line=" + line + ";begin=" + begin + ";end=" + end + "]";
  }
  
}