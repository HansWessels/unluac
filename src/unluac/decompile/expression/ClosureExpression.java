package unluac.decompile.expression;

import unluac.decompile.Declaration;
import unluac.decompile.Decompiler;
import unluac.decompile.Output;
import unluac.decompile.target.TableTarget;
import unluac.decompile.target.Target;
import unluac.decompile.target.VariableTarget;
import unluac.parse.LFunction;
import unluac.parse.LUpvalue;

public class ClosureExpression extends Expression {

  private final LFunction function;
  private int upvalueLine;
  private Declaration[] declList;
  
  public ClosureExpression(LFunction function, Declaration[] declList, int upvalueLine) {
    super(PRECEDENCE_ATOMIC);
    this.function = function;
    this.upvalueLine = upvalueLine;
    this.declList = declList;
  }

  public int getConstantIndex() {
    return -1;
  }
  
  @Override
  public boolean isClosure() {
    return true;
  }
  
  @Override
  public boolean isUngrouped() {
    return true;
  }
  
  @Override
  public boolean isUpvalueOf(int register) {
    /*
    if(function.header.version == 0x51) {
      return false; //TODO:
    }
    */
    for(int i = 0; i < function.upvalues.length; i++) {
      LUpvalue upvalue = function.upvalues[i];
      if(upvalue.instack && upvalue.idx == register) {
        return true;
      }
    }
    return false;
  }
  
  @Override
  public int closureUpvalueLine() {
    return upvalueLine;
  }
  
  @Override
  public void print(Output out) {
    Decompiler d = new Decompiler(function);
    out.print("function");
    printMain(out, d, true);
  }
  
  @Override
  public void printClosure(Output out, Target name) {
    Decompiler d = new Decompiler(function);
    out.print("function ");
    if(function.numParams >= 1 && d.declList[0].name.equals("self") && name instanceof TableTarget) {
      name.printMethod(out);
      printMain(out, d, false);
    } else {
      name.print(out);
      printMain(out, d, true);
    }
  }
  
  private void printMain(Output out, Decompiler d, boolean includeFirst) {
    out.print("(");
    int start = includeFirst ? 0 : 1;
    if(function.numParams > start) {
      new VariableTarget(d.declList[start]).print(out);
      for(int i = start + 1; i < function.numParams; i++) {
        out.print(", ");
        new VariableTarget(d.declList[i]).print(out);
      }
    }
    if((function.vararg & 1) == 1) {
      if(function.numParams > start) {
        out.print(", ...");
      } else {
        out.print("...");
      }
    }
    out.print(")");
    out.println();
    out.indent();
    d.decompile();
    d.print(out);
    out.dedent();
    out.print("end");
    //out.println(); //This is an extra space for formatting
  }
  
}
