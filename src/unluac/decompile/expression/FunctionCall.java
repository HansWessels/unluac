package unluac.decompile.expression;

import java.util.ArrayList;

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
  public void printMultiple(Output out) {
    if(!multiple) {
      out.print("(");
    }
    print(out);
    if(!multiple) {
      out.print(")");
    }
  }
  
  @Override
  public void print(Output out) {
    ArrayList<Expression> args = new ArrayList<Expression>(arguments.length);
    if(function.isMemberAccess() && arguments.length > 0 && function.getTable() == arguments[0]) {
      function.getTable().print(out);
      out.print(":");
      out.print(function.getField());
      for(int i = 1; i < arguments.length; i++) {
        args.add(arguments[i]);
      }
    } else {
      if(function.isClosure()) {
        out.print("(");
        function.print(out);
        out.print(")");
      } else {
        function.print(out);
      }
      for(int i = 0; i < arguments.length; i++) {
        args.add(arguments[i]);
      }
    }
    out.print("(");
    Expression.printSequence(out, args, false, true);
    out.print(")");
  }
  
}
