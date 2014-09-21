package unluac.decompile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import unluac.decompile.expression.Expression;
import unluac.decompile.expression.LocalVariable;
import unluac.decompile.target.Target;
import unluac.decompile.target.VariableTarget;

public class Registers {

  public final int registers;
  public final int length;
  
  private final Declaration[][] decls;
  private final Function f;
  private final Expression[][] values;
  private final int[][] updated;
  
  public Registers(int registers, int length, Declaration[] declList, Function f) {
    this.registers = registers;
    this.length = length;
    decls = new Declaration[registers][length + 1];
    for(int i = 0; i < declList.length; i++) {
      Declaration decl = declList[i];
      int register = 0;
      while(decls[register][decl.begin] != null) {
        register++;
      }
      decl.register = register;
      for(int line = decl.begin; line <= decl.end; line++) {
        decls[register][line] = decl; 
      }
    }
    values = new Expression[registers][length + 1];
    for(int register = 0; register < registers; register++) {
      values[register][0] = Expression.NIL;
    }
    updated = new int[registers][length + 1];
    startedLines = new boolean[length + 1];
    Arrays.fill(startedLines, false);
    this.f = f;
  }
  
  public boolean isAssignable(int register, int line) {
    return isLocal(register, line) && !decls[register][line].forLoop;
  }
  
  public boolean isLocal(int register, int line) {
    if(register < 0) return false;
    return decls[register][line] != null;
  }
  
  public boolean isNewLocal(int register, int line) {
    Declaration decl = decls[register][line];
    return decl != null && decl.begin == line && !decl.forLoop;
  }
    
  public List<Declaration> getNewLocals(int line) {
    ArrayList<Declaration> locals = new ArrayList<Declaration>(registers);
    for(int register = 0; register < registers; register++) {
      if(isNewLocal(register, line)) {
        locals.add(getDeclaration(register, line));
      }
    }
    return locals;
  }
  
  public Declaration getDeclaration(int register, int line) {
    return decls[register][line];
  }
  
  private boolean[] startedLines;
  
  public void startLine(int line) {
    //if(startedLines[line]) return;
    startedLines[line] = true;
    for(int register = 0; register < registers; register++) {
      values[register][line] = values[register][line - 1];
      updated[register][line] = updated[register][line - 1];
    }
  }
  
  public Expression getExpression(int register, int line) {
    if(isLocal(register, line - 1)) {
      return new LocalVariable(getDeclaration(register, line - 1));
    } else {
      return values[register][line - 1];
    }
  }
  
  public Expression getKExpression(int register, int line) {
    if(f.isConstant(register)) {
      return f.getConstantExpression(f.constantIndex(register));
    } else {
      return getExpression(register, line);
    }
  }
  
  public Expression getValue(int register, int line) {
    return values[register][line - 1];
  }

  public int getUpdated(int register, int line) {
    return updated[register][line];
  }
  
  public void setValue(int register, int line, Expression expression) {
    values[register][line] = expression;
    updated[register][line] = line;
  }
  
  public Target getTarget(int register, int line) {
    if(!isLocal(register, line)) {
      throw new IllegalStateException("No declaration exists in register " + register + " at line " + line);
    }
    return new VariableTarget(decls[register][line]);
  }
  
  public void setInternalLoopVariable(int register, int begin, int end) {
    Declaration decl = getDeclaration(register, begin);
    if(decl == null) {
      decl = new Declaration("_FOR_", begin, end);
      decl.register = register;
      newDeclaration(decl, register, begin, end);
    }
    decl.forLoop = true;
  }
  
  public void setExplicitLoopVariable(int register, int begin, int end) {
    Declaration decl = getDeclaration(register, begin);
    if(decl == null) {
      decl = new Declaration("_FORV_" + register + "_", begin, end);
      decl.register = register;
      newDeclaration(decl, register, begin, end);
    }
    decl.forLoopExplicit = true;
  }
  
  private void newDeclaration(Declaration decl, int register, int begin, int end) {
    for(int line = begin; line <= end; line++) {
      decls[register][line] = decl;
    }
  }
  
}
