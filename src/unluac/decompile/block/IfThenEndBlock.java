package unluac.decompile.block;

import java.util.ArrayList;
import java.util.List;

import unluac.decompile.Declaration;
import unluac.decompile.Decompiler;
import unluac.decompile.Output;
import unluac.decompile.Registers;
import unluac.decompile.branch.Branch;
import unluac.decompile.branch.TestNode;
import unluac.decompile.expression.BinaryExpression;
import unluac.decompile.expression.Expression;
import unluac.decompile.expression.LocalVariable;
import unluac.decompile.operation.Operation;
import unluac.decompile.statement.Assignment;
import unluac.decompile.statement.Statement;
import unluac.parse.LFunction;
import unluac.util.Stack;

public class IfThenEndBlock extends Block {

  private final Branch branch;
  private final Stack<Branch> stack;
  private final Registers r;
  private final List<Statement> statements;
  
  public IfThenEndBlock(LFunction function, Branch branch, Registers r) {
    this(function, branch, null, r);
  }
   
  public IfThenEndBlock(LFunction function, Branch branch, Stack<Branch> stack, Registers r) {
    super(function, branch.begin == branch.end ? branch.begin - 1 : branch.begin, branch.begin == branch.end ? branch.begin - 1 : branch.end);
    this.branch = branch;
    this.stack = stack;
    this.r = r;
    statements = new ArrayList<Statement>(branch.end - branch.begin + 1);
  }
  
  @Override
  public void addStatement(Statement statement) {
    statements.add(statement);
  }
  
  @Override
  public boolean breakable() {
    return false;
  }
  
  @Override
  public boolean isContainer() {
    return true;
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
    out.print("if ");
    branch.asExpression(r).print(d, out);
    out.print(" then");
    out.println();
    out.indent();
    Statement.printSequence(d, out, statements);
    out.dedent();
    out.print("end");
  }
  
  @Override
  public Operation process(Decompiler d) {
    if(statements.size() == 1) {
      Statement stmt = statements.get(0);
      if(stmt instanceof Assignment) {
        final Assignment assign = (Assignment) stmt;
        if(assign.getArity() == 1) {
          if(branch instanceof TestNode) {
            TestNode node = (TestNode) branch;
            Declaration decl = r.getDeclaration(node.test, node.line);
            if(assign.getFirstTarget().isDeclaration(decl)) {
              final Expression expr;
              if(node.invert) {
                expr = new BinaryExpression("or", new LocalVariable(decl), assign.getFirstValue(), Expression.PRECEDENCE_OR, Expression.ASSOCIATIVITY_NONE);
              } else {
                expr = new BinaryExpression("and", new LocalVariable(decl), assign.getFirstValue(), Expression.PRECEDENCE_AND, Expression.ASSOCIATIVITY_NONE);
              }
              return new Operation(end - 1) {

                @Override
                public Statement process(Registers r, Block block) {
                  return new Assignment(assign.getFirstTarget(), expr);
                }
                                
              };
            }
          }
        }
      }
    } else if(statements.size() == 0 && stack != null) {
      int test = branch.getRegister();
      if(test < 0) {
        for(int reg = 0; reg < r.registers; reg++) {
          if(r.getUpdated(reg, branch.end - 1) >= branch.begin) {
            if(test >= 0) {
              test = -1;
              break;
            }
            test = reg;
          }
        }
      }
      if(test >= 0) {
        if(r.getUpdated(test, branch.end - 1) >= branch.begin) {
          Expression right = r.getValue(test, branch.end);
          final Branch setb = d.popSetCondition(stack, stack.peek().end, test);
          setb.useExpression(right);
          final int testreg = test;
          return new Operation(end - 1) {
            
            @Override
            public Statement process(Registers r, Block block) {
              r.setValue(testreg, branch.end - 1, setb.asExpression(r));
              return null;
            }
            
          };
        }
      }
    }
    return super.process(d);
  }
  
}