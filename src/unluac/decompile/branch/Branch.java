package unluac.decompile.branch;

import unluac.decompile.Registers;
import unluac.decompile.expression.Expression;

abstract public class Branch {
  
  public final int line;
  public int begin;
  public int end; //Might be modified to undo redirect
  
  public boolean isSet = false;
  public boolean isCompareSet = false;
  public boolean isTest = false;
  public int setTarget = -1;
  
  public Branch(int line, int begin, int end) {
    this.line = line;
    this.begin = begin;
    this.end = end;
  }
  
  abstract public Branch invert();
  
  abstract public int getRegister();
  
  abstract public Expression asExpression(Registers r);
  
  abstract public void useExpression(Expression expression);
  
}
