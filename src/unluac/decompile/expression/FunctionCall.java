package unluac.decompile.expression;

import java.util.ArrayList;

import unluac.decompile.Decompiler;
import unluac.decompile.Output;

public class FunctionCall extends Expression {

  private final Expression function;
  private final Expression[] arguments;
  private final boolean multiple;
  
  public FunctionCall(Expression function, Expression[] arguments, boolean multiple) {
    super(PRECEDENCE_ATOMIC);
    this.function = function;
    this.arguments = arguments;
    this.multiple = multiple;
  }

  @Override
  public int getConstantIndex() {
    int index = function.getConstantIndex();
    for(Expression argument : arguments) {
      index = Math.max(argument.getConstantIndex(), index);
    }
    return index;
  }
  
  @Override
  public boolean isMultiple() {
    return multiple;
  }
  
  @Override
  public void printMultiple(Decompiler d, Output out) {
    if(!multiple) {
      out.print("(");
    }
    print(d, out);
    if(!multiple) {
      out.print(")");
    }
  }
  
  private boolean isMethodCall() {
    return function.isMemberAccess() && arguments.length > 0 && function.getTable() == arguments[0];
  }
  
  @Override
  public boolean beginsWithParen() {
    if(isMethodCall()) {
      Expression obj = function.getTable();
      return obj.isUngrouped() || obj.beginsWithParen();
    } else {
      return function.isUngrouped() || function.beginsWithParen();
    }
  }
  
  @Override
  public void print(Decompiler d, Output out) {
    ArrayList<Expression> args = new ArrayList<Expression>(arguments.length);
    if(isMethodCall()) {
      Expression obj = function.getTable();
      if(obj.isUngrouped()) {
        out.print("(");
        obj.print(d, out);
        out.print(")");
      } else {
        obj.print(d, out);
      }
      out.print(":");
      out.print(function.getField());
      for(int i = 1; i < arguments.length; i++) {
        args.add(arguments[i]);
      }
    } else {
      if(function.isUngrouped()) {
        out.print("(");
        function.print(d, out);
        out.print(")");
      } else {
        function.print(d, out);
      }
      for(int i = 0; i < arguments.length; i++) {
        args.add(arguments[i]);
      }
    }
    out.print("(");
    Expression.printSequence(d, out, args, false, true);
    out.print(")");
  }
  
}
