package unluac.decompile.block;

import unluac.decompile.Decompiler;
import unluac.decompile.Op;
import unluac.decompile.Output;
import unluac.decompile.Registers;
import unluac.decompile.branch.Branch;
import unluac.decompile.expression.Expression;
import unluac.decompile.operation.Operation;
import unluac.decompile.operation.RegisterSet;
import unluac.decompile.statement.Assignment;
import unluac.decompile.statement.Statement;
import unluac.decompile.target.Target;
import unluac.parse.LFunction;

public class SetBlock extends Block {
  
  public final int target;
  private Assignment assign;
  public final Branch branch;  
  private Registers r;
  private boolean empty;
  private boolean finalize = false;
  
  public SetBlock(LFunction function, Branch branch, int target, int line, int begin, int end, boolean empty, Registers r) {
    super(function, begin, end);
    this.empty = empty;
    if(begin == end) this.begin -= 1;
    this.target = target;
    this.branch = branch;    
    this.r = r;
    //System.out.println("-- set block " + begin + " .. " + end);
  }

  @Override
  public void addStatement(Statement statement) {
    if(!finalize && statement instanceof Assignment) {
      this.assign = (Assignment) statement;
    } else if(statement instanceof BooleanIndicator) {
      finalize = true;
    }
  }

  @Override
  public boolean isUnprotected() {
    return false;
  }

  @Override
  public int getLoopback() {
    throw new IllegalStateException();
  }

  @Override
  public void print(Decompiler d, Output out) {
    if(assign != null && assign.getFirstTarget() != null) {
      Assignment assignOut = new Assignment(assign.getFirstTarget(), getValue());
      assignOut.print(d, out);
    } else {
      out.print("-- unhandled set block");
      out.println();
    }
  } 

  @Override
  public boolean breakable() {
    return false;
  }
  
  @Override
  public boolean isContainer() {
    return false;
  }
  
  public void useAssignment(Assignment assign) {
    this.assign = assign;
    branch.useExpression(assign.getFirstValue());
  }
  
  public Expression getValue() {
    return branch.asExpression(r);
  }
  
  @Override
  public Operation process(final Decompiler d) {
    if(empty) {
      Expression expression = r.getExpression(branch.setTarget, end);
      branch.useExpression(expression);      
      return new RegisterSet(end - 1, branch.setTarget, branch.asExpression(r));
    } else if(assign != null) {
      branch.useExpression(assign.getFirstValue());
      final Target target = assign.getFirstTarget();
      final Expression value = getValue();
      return new Operation(end - 1) {

        @Override
        public Statement process(Registers r, Block block) {
          //System.out.println(begin + " .. " + end);
          return new Assignment(target, value);
        }
        
      };
    } else {
      return new Operation(end - 1) {

        @Override
        public Statement process(Registers r, Block block) {
          //System.out.println("-- block " + begin + " .. " + end);
          Expression expr = null;
          int register = 0;
          for(; register < r.registers; register++) {
            if(r.getUpdated(register, branch.end - 1) == branch.end - 1) {
              expr = r.getValue(register, branch.end);
              break;
            }
          }
          if(d.code.op(branch.end - 2) == Op.LOADBOOL && d.code.C(branch.end - 2) != 0) {
            int target = d.code.A(branch.end - 2);
            if(d.code.op(branch.end - 3) == Op.JMP && d.code.sBx(branch.end - 3) == 2) {
              //System.out.println("-- Dropped boolean expression operand");
              expr = r.getValue(target, branch.end - 2);
            } else {
              expr = r.getValue(target, branch.begin);
            }
            branch.useExpression(expr);
            if(r.isLocal(target, branch.end - 1)) {
              return new Assignment(r.getTarget(target, branch.end - 1), branch.asExpression(r));
            }
            r.setValue(target, branch.end - 1, branch.asExpression(r));
          } else if(expr != null && target >= 0) {
            branch.useExpression(expr);
            if(r.isLocal(target, branch.end - 1)) {
              return new Assignment(r.getTarget(target, branch.end - 1), branch.asExpression(r));
            }
            r.setValue(target, branch.end - 1, branch.asExpression(r));
            //System.out.println("-- target = " + target + "@" + (branch.end - 1));
            //.print(new Output());
            //System.out.println();
          } else {
            System.out.println("-- fail " + (branch.end - 1));
            System.out.println(expr);
            System.out.println(target);
          }
          return null;
        }
        
      };
      //return super.process();
    }
    /*
    if(sblock.branch.begin == sblock.branch.end && r.isLocal(sblock.target, line)) {
      sblock.useAssignment(new Assignment(r.getTarget(sblock.target, line), r.getExpression(sblock.target, line)));
    } else if(sblock.branch.begin == sblock.branch.end) {
      sblock.useAssignment(new Assignment(null, r.getExpression(sblock.target, sblock.begin)));
      r.setValue(sblock.target, line, sblock.getValue());
    } else {
      //System.out.println("--sblock");
      Expression expr = null;
      int register = 0;
      for(; register < registers; register++) {
        if(updated[register] == line) {
          expr = r.getValue(register, line + 1);
          break;
        }
      }
      if(expr == null) {
        //System.out.println("-- null/null");
        expr = r.getExpression(sblock.target, line);
      }
      sblock.useAssignment(new Assignment(null, expr));
      r.setValue(sblock.target, line, sblock.getValue());
    }
  }
  */
  }
  
}
