package unluac.decompile.statement;

import java.util.ArrayList;
import java.util.List;

import unluac.decompile.Declaration;
import unluac.decompile.Decompiler;
import unluac.decompile.Output;
import unluac.decompile.expression.Expression;
import unluac.decompile.target.Target;

public class Assignment extends Statement {

  private final ArrayList<Target> targets = new ArrayList<Target>(5);
  private final ArrayList<Expression> values = new ArrayList<Expression>(5);

  private boolean allnil = true;
  private boolean declare = false;
  private int declareStart = 0;
  
  public Assignment() {
    
  }
  
  @Override
  public boolean beginsWithParen() {
    return targets.get(0).beginsWithParen();
  }
  
  public Target getFirstTarget() {
    return targets.get(0);
  }
  
  public Expression getFirstValue() {
    return values.get(0);
  }
  
  public boolean assignsTarget(Declaration decl) {
    for(Target target : targets) {
      if(target.isDeclaration(decl)) {
        return true;
      }
    }
    return false;
  }
  
  public int getArity() {
    return targets.size();
  }
  
  public Assignment(Target target, Expression value) {
    targets.add(target);
    values.add(value);
    allnil = allnil && value.isNil();
  }

  public void addFirst(Target target, Expression value) {
    targets.add(0, target);
    values.add(0, value);
    allnil = allnil && value.isNil();
  }
  
  public void addLast(Target target, Expression value) {
    if(targets.contains(target)) {
      int index = targets.indexOf(target);
      targets.remove(index);
      value = values.remove(index);
    }
    targets.add(target);
    values.add(value);
    allnil = allnil && value.isNil();
  }
  
  public boolean assignListEquals(List<Declaration> decls) {
    if(decls.size() != targets.size()) return false;
    for(Target target : targets) {
      boolean found = false;
      for(Declaration decl : decls) {
        if(target.isDeclaration(decl)) {
          found = true;
          break;
        }
      }
      if(!found) return false;
    }
    return true;
  }
  
  public void declare(int declareStart) {
    declare = true;
    this.declareStart = declareStart;
  }
  
  @Override
  public void print(Decompiler d, Output out) {
    if(!targets.isEmpty()) {
      if(declare) {
        out.print("local ");
      }
      boolean functionSugar = false;
      if(targets.size() == 1 && values.size() == 1 && values.get(0).isClosure() && targets.get(0).isFunctionName()) {
        Expression closure = values.get(0);
        //comment = "" + declareStart + " >= " + closure.closureUpvalueLine();
        //System.out.println("" + declareStart + " >= " + closure.closureUpvalueLine());
        // This check only works in Lua version 0x51
        if(!declare || declareStart >= closure.closureUpvalueLine()) {
          functionSugar = true;
        }
        if(targets.get(0).isLocal() && closure.isUpvalueOf(targets.get(0).getIndex())) {
          functionSugar = true;
        }
        //if(closure.isUpvalueOf(targets.get(0).))
      }
      if(!functionSugar) {
        targets.get(0).print(d, out);
        for(int i = 1; i < targets.size(); i++) {
          out.print(", ");
          targets.get(i).print(d, out);
        }
        if(!declare || !allnil) {
          out.print(" = ");
          Expression.printSequence(d, out, values, false, false);
        }
      } else {
        values.get(0).printClosure(d, out, targets.get(0));
      }
      if(comment != null) {
        out.print(" -- ");
        out.print(comment);
      }
    }
  }
  
}
