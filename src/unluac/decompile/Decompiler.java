package unluac.decompile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import unluac.Version;
import unluac.decompile.block.AlwaysLoop;
import unluac.decompile.block.Block;
import unluac.decompile.block.BooleanIndicator;
import unluac.decompile.block.Break;
import unluac.decompile.block.CompareBlock;
import unluac.decompile.block.DoEndBlock;
import unluac.decompile.block.ElseEndBlock;
import unluac.decompile.block.ForBlock;
import unluac.decompile.block.IfThenElseBlock;
import unluac.decompile.block.IfThenEndBlock;
import unluac.decompile.block.OuterBlock;
import unluac.decompile.block.RepeatBlock;
import unluac.decompile.block.SetBlock;
import unluac.decompile.block.TForBlock;
import unluac.decompile.block.WhileBlock;
import unluac.decompile.branch.AndBranch;
import unluac.decompile.branch.AssignNode;
import unluac.decompile.branch.Branch;
import unluac.decompile.branch.EQNode;
import unluac.decompile.branch.LENode;
import unluac.decompile.branch.LTNode;
import unluac.decompile.branch.OrBranch;
import unluac.decompile.branch.TestNode;
import unluac.decompile.branch.TestSetNode;
import unluac.decompile.branch.TrueNode;
import unluac.decompile.expression.ClosureExpression;
import unluac.decompile.expression.ConstantExpression;
import unluac.decompile.expression.Expression;
import unluac.decompile.expression.FunctionCall;
import unluac.decompile.expression.TableLiteral;
import unluac.decompile.expression.TableReference;
import unluac.decompile.expression.Vararg;
import unluac.decompile.operation.CallOperation;
import unluac.decompile.operation.GlobalSet;
import unluac.decompile.operation.Operation;
import unluac.decompile.operation.RegisterSet;
import unluac.decompile.operation.ReturnOperation;
import unluac.decompile.operation.TableSet;
import unluac.decompile.operation.UpvalueSet;
import unluac.decompile.statement.Assignment;
import unluac.decompile.statement.Statement;
import unluac.decompile.target.GlobalTarget;
import unluac.decompile.target.TableTarget;
import unluac.decompile.target.Target;
import unluac.decompile.target.UpvalueTarget;
import unluac.decompile.target.VariableTarget;
import unluac.parse.LBoolean;
import unluac.parse.LFunction;
import unluac.util.Stack;

public class Decompiler {
  
  private final int registers;
  private final int length;
  public final Code code;
  private final Upvalues upvalues;
  public final Declaration[] declList;
  
  protected Function f;
  protected LFunction function;
  private final LFunction[] functions;  
  private final int params;
  private final int vararg;
  
  private final Op tforTarget;
  private final Op forTarget;
  
  public Decompiler(LFunction function) {
    this.f = new Function(function);
    this.function = function;
    registers = function.maximumStackSize;
    length = function.code.length;
    code = new Code(function);
    if(function.locals.length >= function.numParams) {
      declList = new Declaration[function.locals.length];
      for(int i = 0; i < declList.length; i++) {
        declList[i] = new Declaration(function.locals[i]);
      }
    } else {
      //TODO: debug info missing;
      /*
      declList = new Declaration[function.numParams];
      for(int i = 0; i < declList.length; i++) {
        declList[i] = new Declaration("_ARG_" + i + "_", 0, length - 1);
      }
      */
      declList = VariableFinder.process(this, function.numParams, function.maximumStackSize);
      
    }
    upvalues = new Upvalues(function.upvalues);
    functions = function.functions;
    params = function.numParams;
    vararg = function.vararg;
    tforTarget = function.header.version.getTForTarget();
    forTarget = function.header.version.getForTarget();
  }
  
  public Version getVersion() {
    return function.header.version;
  }
  
  private Registers r;
  private Block outer;
  
  public void decompile() {
    r = new Registers(registers, length, declList, f);
    findReverseTargets();
    handleBranches(true);
    outer = handleBranches(false);
    processSequence(1, length);
  }
  
  public void print() {
    print(new Output());
  }
  
  public void print(OutputProvider out) {
    print(new Output(out));
  }
  
  public void print(Output out) {
    handleInitialDeclares(out);
    outer.print(this, out);
  }
  
  private void handleInitialDeclares(Output out) {
    List<Declaration> initdecls = new ArrayList<Declaration>(declList.length);
    for(int i = params + (vararg & 1); i < declList.length; i++) {
      if(declList[i].begin == 0) {
        initdecls.add(declList[i]);
      }
    }
    if(initdecls.size() > 0) {
      out.print("local ");
      out.print(initdecls.get(0).name);
      for(int i = 1; i < initdecls.size(); i++) {
        out.print(", ");
        out.print(initdecls.get(i).name);
      }
      out.println();
    }
  }
  
  private int fb2int50(int fb) {
    return (fb & 7) << (fb >> 3);
  }
  
  private int fb2int(int fb) {
    int exponent = (fb >> 3) & 0x1f;
    if(exponent == 0) {
      return fb;
    } else {
      return ((fb & 7) + 8) << (exponent - 1);
    }
  }
  
  private List<Operation> processLine(int line) {
    List<Operation> operations = new LinkedList<Operation>();
    int A = code.A(line);
    int B = code.B(line);
    int C = code.C(line);
    int Bx = code.Bx(line);
    switch(code.op(line)) {
      case MOVE:
        operations.add(new RegisterSet(line, A, r.getExpression(B, line)));
        break;
      case LOADK:
        operations.add(new RegisterSet(line, A, f.getConstantExpression(Bx)));
        break;
      case LOADBOOL:
        operations.add(new RegisterSet(line, A, new ConstantExpression(new Constant(B != 0 ? LBoolean.LTRUE : LBoolean.LFALSE), -1)));
        break;
      case LOADNIL: {
        int maximum;
        if(function.header.version.usesOldLoadNilEncoding()) {
          maximum = B;
        } else {
          maximum = A + B;
        }
        while(A <= maximum) {
          operations.add(new RegisterSet(line, A, Expression.NIL));
          A++;
        }
        break;
      }
      case GETUPVAL:
        operations.add(new RegisterSet(line, A, upvalues.getExpression(B)));
        break;
      case GETTABUP:
        operations.add(new RegisterSet(line, A, new TableReference(upvalues.getExpression(B), r.getKExpression(C, line))));
        break;
      case GETGLOBAL:
        operations.add(new RegisterSet(line, A, f.getGlobalExpression(Bx)));
        break;
      case GETTABLE:
        operations.add(new RegisterSet(line, A, new TableReference(r.getExpression(B, line), r.getKExpression(C, line))));
        break;
      case SETUPVAL:
        operations.add(new UpvalueSet(line, upvalues.getName(B), r.getExpression(A, line)));
        break;
      case SETTABUP:
        operations.add(new TableSet(line, upvalues.getExpression(A), r.getKExpression(B, line), r.getKExpression(C, line), true, line));
        break;
      case SETGLOBAL:
        operations.add(new GlobalSet(line, f.getGlobalName(Bx), r.getExpression(A, line)));
        break;
      case SETTABLE:
        operations.add(new TableSet(line, r.getExpression(A, line), r.getKExpression(B, line), r.getKExpression(C, line), true, line));
        break;
      case NEWTABLE:
        operations.add(new RegisterSet(line, A, new TableLiteral(fb2int(B), fb2int(C))));
        break;
      case NEWTABLE50:
        operations.add(new RegisterSet(line, A, new TableLiteral(fb2int50(B), 1 << C)));
        break;
      case SELF: {
        // We can later determine is : syntax was used by comparing subexpressions with ==
        Expression common = r.getExpression(B, line);
        operations.add(new RegisterSet(line, A + 1, common));
        operations.add(new RegisterSet(line, A, new TableReference(common, r.getKExpression(C, line))));
        break;
      }
      case ADD:
        operations.add(new RegisterSet(line, A, Expression.makeADD(r.getKExpression(B, line), r.getKExpression(C, line))));
        break;
      case SUB:
        operations.add(new RegisterSet(line, A, Expression.makeSUB(r.getKExpression(B, line), r.getKExpression(C, line))));
        break;
      case MUL:
        operations.add(new RegisterSet(line, A, Expression.makeMUL(r.getKExpression(B, line), r.getKExpression(C, line))));
        break;
      case DIV:
        operations.add(new RegisterSet(line, A, Expression.makeDIV(r.getKExpression(B, line), r.getKExpression(C, line))));
        break;
      case MOD:
        operations.add(new RegisterSet(line, A, Expression.makeMOD(r.getKExpression(B, line), r.getKExpression(C, line))));
        break;
      case POW:
        operations.add(new RegisterSet(line, A, Expression.makePOW(r.getKExpression(B, line), r.getKExpression(C, line))));
        break;
      case UNM:
        operations.add(new RegisterSet(line, A, Expression.makeUNM(r.getExpression(B, line))));
        break;
      case NOT:
        operations.add(new RegisterSet(line, A, Expression.makeNOT(r.getExpression(B, line))));
        break;
      case LEN:
        operations.add(new RegisterSet(line, A, Expression.makeLEN(r.getExpression(B, line))));
        break;
      case CONCAT: {
        Expression value = r.getExpression(C, line);
        //Remember that CONCAT is right associative.
        while(C-- > B) {
          value = Expression.makeCONCAT(r.getExpression(C, line), value);
        }
        operations.add(new RegisterSet(line, A, value));        
        break;
      }
      case JMP:
      case EQ:
      case LT:
      case LE:
      case TEST:
      case TESTSET:
      case TEST50:
        /* Do nothing ... handled with branches */
        break;
      case CALL: {
        boolean multiple = (C >= 3 || C == 0);
        if(B == 0) B = registers - A;
        if(C == 0) C = registers - A + 1;
        Expression function = r.getExpression(A, line);
        Expression[] arguments = new Expression[B - 1];
        for(int register = A + 1; register <= A + B - 1; register++) {
          arguments[register - A - 1] = r.getExpression(register, line);
        }
        FunctionCall value = new FunctionCall(function, arguments, multiple);
        if(C == 1) {
          operations.add(new CallOperation(line, value));
        } else {
          if(C == 2 && !multiple) {
            operations.add(new RegisterSet(line, A, value));
          } else {
            for(int register = A; register <= A + C - 2; register++) {
              operations.add(new RegisterSet(line, register, value));
            }
          }
        }
        break;
      }
      case TAILCALL: {
        if(B == 0) B = registers - A;
        Expression function = r.getExpression(A, line);
        Expression[] arguments = new Expression[B - 1];
        for(int register = A + 1; register <= A + B - 1; register++) {
          arguments[register - A - 1] = r.getExpression(register, line);
        }
        FunctionCall value = new FunctionCall(function, arguments, true);
        operations.add(new ReturnOperation(line, value));
        skip[line + 1] = true;
        break;
      }
      case RETURN: {
        if(B == 0) B = registers - A + 1;
        Expression[] values = new Expression[B - 1];
        for(int register = A; register <= A + B - 2; register++) {
          values[register - A] = r.getExpression(register, line);
        }
        operations.add(new ReturnOperation(line, values));
        break;
      }
      case FORLOOP:
      case FORPREP:
      case TFORPREP:
      case TFORCALL:
      case TFORLOOP:
        /* Do nothing ... handled with branches */
        break;
      case SETLIST50:
      case SETLISTO: {
        Expression table = r.getValue(A, line);
        int n = Bx % 32;
        for(int i = 1; i <= n + 1; i++) {
          operations.add(new TableSet(line, table, new ConstantExpression(new Constant(Bx - n + i), -1), r.getExpression(A + i, line), false, r.getUpdated(A + i, line)));
        }
        break;
      }
      case SETLIST: {
        if(C == 0) {
          C = code.codepoint(line + 1);
          skip[line + 1] = true;
        }
        if(B == 0) {
          B = registers - A - 1;
        }
        Expression table = r.getValue(A, line);
        for(int i = 1; i <= B; i++) {
          operations.add(new TableSet(line, table, new ConstantExpression(new Constant((C - 1) * 50 + i), -1), r.getExpression(A + i, line), false, r.getUpdated(A + i, line)));
        }
        break;
      }
      case CLOSE:
        break;
      case CLOSURE: {
        LFunction f = functions[Bx];
        operations.add(new RegisterSet(line, A, new ClosureExpression(f, declList, line + 1)));
        if(function.header.version.usesInlineUpvalueDeclarations()) {
          // Skip upvalue declarations
          for(int i = 0; i < f.numUpvalues; i++) {
            skip[line + 1 + i] = true;
          }
        }
        break;
      }
      case VARARG: {
        boolean multiple = (B != 2);
        if(B == 1) throw new IllegalStateException();
        if(B == 0) B = registers - A + 1;
        Expression value = new Vararg(B - 1, multiple);
        for(int register = A; register <= A + B - 2; register++) {
          operations.add(new RegisterSet(line, register, value));
        }
        break;
      }
      default:
        throw new IllegalStateException("Illegal instruction: " + code.op(line));
    }
    return operations;
  }
  
  /**
   * When lines are processed out of order, they are noted
   * here so they can be skipped when encountered normally.
   */
  boolean[] skip;

  /**
   * Precalculated array of which lines are the targets of
   * jump instructions that go backwards... such targets
   * must be at the statement/block level in the outputted
   * code (they cannot be mid-expression).
   */
  boolean[] reverseTarget;
  
  private void findReverseTargets() {
    reverseTarget = new boolean[length + 1];
    Arrays.fill(reverseTarget, false);
    for(int line = 1; line <= length; line++) {
      if(code.op(line) == Op.JMP && code.sBx(line) < 0) {
        reverseTarget[line + 1 + code.sBx(line)] = true;
      }
    }
  }
  
  private Assignment processOperation(Operation operation, int line, int nextLine, Block block) {
    Assignment assign = null;
    boolean wasMultiple = false;
    Statement stmt = operation.process(r, block);
    if(stmt != null) {
      if(stmt instanceof Assignment) {
        assign = (Assignment) stmt;
        if(!assign.getFirstValue().isMultiple()) {
          block.addStatement(stmt);
        } else {
          wasMultiple = true;
        }
      } else {
        block.addStatement(stmt);
      }
      //System.out.println("-- added statemtent @" + line);
      if(assign != null) {
        //System.out.println("-- checking for multiassign @" + nextLine);
        while(nextLine < block.end && isMoveIntoTarget(nextLine)) {
          //System.out.println("-- found multiassign @" + nextLine);
          Target target = getMoveIntoTargetTarget(nextLine, line + 1);
          Expression value = getMoveIntoTargetValue(nextLine, line + 1); //updated?
          assign.addFirst(target, value);
          skip[nextLine] = true;
          nextLine++;
        }
        if(wasMultiple && !assign.getFirstValue().isMultiple()) {
          block.addStatement(stmt);
        }
      }
    }
    return assign;
  }
  
  private void processSequence(int begin, int end) {
    int blockIndex = 1;
    Stack<Block> blockStack = new Stack<Block>();
    blockStack.push(blocks.get(0));
    skip = new boolean[end + 1];
    for(int line = begin; line <= end; line++) {
      /*
      System.out.print("-- line " + line + "; R[0] = ");
      r.getValue(0, line).print(new Output());
      System.out.println();
      System.out.print("-- line " + line + "; R[1] = ");
      r.getValue(1, line).print(new Output());
      System.out.println();
      System.out.print("-- line " + line + "; R[2] = ");
      r.getValue(2, line).print(new Output());
      System.out.println();
      */
      Operation blockHandler = null;
      while(blockStack.peek().end <= line) {
        Block block = blockStack.pop();
        blockHandler = block.process(this);
        if(blockHandler != null) {
          break;
        }
      }
      if(blockHandler == null) {
        while(blockIndex < blocks.size() && blocks.get(blockIndex).begin <= line) {
          blockStack.push(blocks.get(blockIndex++));
        }
      }
      Block block = blockStack.peek();
      r.startLine(line); //Must occur AFTER block.rewrite
      if(skip[line]) {
        List<Declaration> newLocals = r.getNewLocals(line);
        if(!newLocals.isEmpty()) {
          Assignment assign = new Assignment();
          assign.declare(newLocals.get(0).begin);
          for(Declaration decl : newLocals) {
            assign.addLast(new VariableTarget(decl), r.getValue(decl.register, line));
          }
          blockStack.peek().addStatement(assign);
        }
        continue;
      }
      List<Operation> operations = processLine(line);
      List<Declaration> newLocals = r.getNewLocals(blockHandler == null ? line : line - 1);
      //List<Declaration> newLocals = r.getNewLocals(line);
      Assignment assign = null;
      if(blockHandler == null) {
        if(code.op(line) == Op.LOADNIL) {
          assign = new Assignment();
          int count = 0;
          for(Operation operation : operations) {
            RegisterSet set = (RegisterSet) operation;
            operation.process(r, block);
            if(r.isAssignable(set.register, set.line)) {
              assign.addLast(r.getTarget(set.register, set.line), set.value);
              count++;
            }
          }
          if(count > 0) {
            block.addStatement(assign);
          }
        } else if(code.op(line) == Op.TFORPREP) {
          // Lua5.0 FORPREP - no assignments
          newLocals.clear();
        } else {
          //System.out.println("-- Process iterating ... ");
          for(Operation operation : operations) {
            //System.out.println("-- iter");
            Assignment temp = processOperation(operation, line, line + 1, block);
            if(temp != null) {
              assign = temp;
              //System.out.print("-- top assign -> "); temp.getFirstTarget().print(new Output()); System.out.println();
            }
          }
          if(assign != null && assign.getFirstValue().isMultiple()) {
            block.addStatement(assign);
          }
        }
      } else {
        assign = processOperation(blockHandler, line, line, block);
      }
      if(assign != null) {
        if(!newLocals.isEmpty()) {
          assign.declare(newLocals.get(0).begin);
          for(Declaration decl : newLocals) {
            //System.out.println("-- adding decl @" + line);
            assign.addLast(new VariableTarget(decl), r.getValue(decl.register, line + 1));
          }
          //blockStack.peek().addStatement(assign);
        }
      }
      if(blockHandler == null) {
        if(assign != null) {
          
        } else if(!newLocals.isEmpty() && code.op(line) != Op.FORPREP) {
          if(code.op(line) != Op.JMP || code.op(line + 1 + code.sBx(line)) != tforTarget) {
            assign = new Assignment();
            assign.declare(newLocals.get(0).begin);
            for(Declaration decl : newLocals) {
              assign.addLast(new VariableTarget(decl), r.getValue(decl.register, line));
            }
            blockStack.peek().addStatement(assign);
          }
        }
      }
      if(blockHandler != null) {
        //System.out.println("-- repeat @" + line);
        line--;
        continue;
      }
    }    
  }
  
  private boolean isMoveIntoTarget(int line) {
    switch(code.op(line)) {
      case MOVE:
        return r.isAssignable(code.A(line), line) && !r.isLocal(code.B(line), line);
      case SETUPVAL:
      case SETGLOBAL:
        return !r.isLocal(code.A(line), line);
      case SETTABLE:
      case SETTABUP: {
        int C = code.C(line);
        if(f.isConstant(C)) {
          return false;
        } else {
          return !r.isLocal(C, line);
        }
      }
      default:
        return false;
    }
  }
  
  private Target getMoveIntoTargetTarget(int line, int previous) {
    switch(code.op(line)) {
      case MOVE:
        return r.getTarget(code.A(line), line);
      case SETUPVAL:
        return new UpvalueTarget(upvalues.getName(code.B(line)));
      case SETGLOBAL:
        return new GlobalTarget(f.getGlobalName(code.Bx(line)));
      case SETTABLE:
        return new TableTarget(r.getExpression(code.A(line), previous), r.getKExpression(code.B(line), previous));
      case SETTABUP: {
        int A = code.A(line);
        int B = code.B(line);
        return new TableTarget(upvalues.getExpression(A), r.getKExpression(B, line));
      }
      default:
        throw new IllegalStateException();
    }
  }
  
  private Expression getMoveIntoTargetValue(int line, int previous) {
    int A = code.A(line);
    int B = code.B(line);
    int C = code.C(line);
    switch(code.op(line)) {
      case MOVE:
        return r.getValue(B, previous);
      case SETUPVAL:
      case SETGLOBAL:
        return r.getExpression(A, previous);
      case SETTABLE:
      case SETTABUP:
        if(f.isConstant(C)) {
          throw new IllegalStateException();
        } else {
          return r.getExpression(C, previous);
        }
      default:
        throw new IllegalStateException();
    }
  }
  
  private ArrayList<Block> blocks;
  
  private OuterBlock handleBranches(boolean first) {
    List<Block> oldBlocks = blocks;
    blocks = new ArrayList<Block>();
    OuterBlock outer = new OuterBlock(function, length);
    blocks.add(outer);
    boolean[] isBreak = new boolean[length + 1];
    boolean[] loopRemoved = new boolean[length + 1];
    if(!first) {
      for(Block block : oldBlocks) {
        if(block instanceof AlwaysLoop) {
          blocks.add(block);
        }
        if(block instanceof Break) {
          blocks.add(block);
          isBreak[block.begin] = true;
        }
      }
      List<Block> delete = new LinkedList<Block>();
      for(Block block : blocks) {
        if(block instanceof AlwaysLoop) {
          for(Block block2 : blocks) {
            if(block != block2) {
              if(block.begin == block2.begin) {
                if(block.end < block2.end) {
                  delete.add(block);
                  loopRemoved[block.end - 1] = true;
                } else {
                  delete.add(block2);
                  loopRemoved[block2.end - 1] = true;
                }
              }
            }
          }
        }
      }
      for(Block block : delete) {
        blocks.remove(block);
      }
    }
    skip = new boolean[length + 1];
    Stack<Branch> stack = new Stack<Branch>();
    boolean reduce = false;
    boolean testset = false;
    int testsetend = -1;
    for(int line = 1; line <= length; line++) {
      if(!skip[line]) {
        switch(code.op(line)) {
          case EQ: {
            EQNode node = new EQNode(code.B(line), code.C(line), code.A(line) != 0, line, line + 2, line + 2 + code.sBx(line + 1)); 
            stack.push(node);
            skip[line + 1] = true;
            if(code.op(node.end) == Op.LOADBOOL) {
              if(code.C(node.end) != 0) {
                node.isCompareSet = true;
                node.setTarget = code.A(node.end);
              } else if(node.end - 1 >= 1 && code.op(node.end - 1) == Op.LOADBOOL) {
                if(code.C(node.end - 1) != 0) {
                  node.isCompareSet = true;
                  node.setTarget = code.A(node.end);
                }
              }
            }
            continue;
          }
          case LT: {
            LTNode node = new LTNode(code.B(line), code.C(line), code.A(line) != 0, line, line + 2, line + 2 + code.sBx(line + 1)); 
            stack.push(node);
            skip[line + 1] = true;
            if(code.op(node.end) == Op.LOADBOOL) {
              if(code.C(node.end) != 0) {
                node.isCompareSet = true;
                node.setTarget = code.A(node.end);
              } else if(node.end - 1 >= 1 && code.op(node.end - 1) == Op.LOADBOOL) {
                if(code.C(node.end - 1) != 0) {
                  node.isCompareSet = true;
                  node.setTarget = code.A(node.end);
                }
              }
            }
            continue;
          }
          case LE: {
            LENode node = new LENode(code.B(line), code.C(line), code.A(line) != 0, line, line + 2, line + 2 + code.sBx(line + 1));
            stack.push(node);
            skip[line + 1] = true;
            if(code.op(node.end) == Op.LOADBOOL) {
              if(code.C(node.end) != 0) {
                node.isCompareSet = true;
                node.setTarget = code.A(node.end);
              } else if(node.end - 1 >= 1 && code.op(node.end - 1) == Op.LOADBOOL) {
                if(code.C(node.end - 1) != 0) {
                  node.isCompareSet = true;
                  node.setTarget = code.A(node.end);
                }
              }
            }
            continue;
          }
          case TEST:
            stack.push(new TestNode(code.A(line), code.C(line) != 0, line, line + 2, line + 2 + code.sBx(line + 1)));
            skip[line + 1] = true;
            continue;
          case TESTSET:
            testset = true;
            testsetend = line + 2 + code.sBx(line + 1);
            stack.push(new TestSetNode(code.A(line), code.B(line), code.C(line) != 0, line, line + 2, line + 2 + code.sBx(line + 1)));
            skip[line + 1] = true;            
            continue;
          case TEST50:
            if(code.A(line) == code.B(line)) {
              stack.push(new TestNode(code.A(line), code.C(line) != 0, line, line + 2, line + 2 + code.sBx(line + 1)));
            } else {
              testset = true;
              testsetend = line + 2 + code.sBx(line + 1);
              stack.push(new TestSetNode( code.A(line), code.B(line), code.C(line) != 0, line, line + 2, line + 2 + code.sBx(line + 1)));
            }
            skip[line + 1] = true;
            continue;
          case JMP: {
            reduce = true;
            int tline = line + 1 + code.sBx(line);
            if(tline >= 2 && code.op(tline - 1) == Op.LOADBOOL && code.C(tline - 1) != 0) {
              stack.push(new TrueNode(code.A(tline - 1), false, line, line + 1, tline));
              skip[line + 1] = true;
            } else if(code.op(tline) == tforTarget && !skip[tline]) {
              int A = code.A(tline);
              int C = code.C(tline);
              if(C == 0) throw new IllegalStateException();
              r.setInternalLoopVariable(A, tline, line + 1); //TODO: end?
              r.setInternalLoopVariable(A + 1, tline, line + 1);
              r.setInternalLoopVariable(A + 2, tline, line + 1);
              for(int index = 1; index <= C; index++) {
                r.setExplicitLoopVariable(A + 2 + index, line, tline + 2); //TODO: end?
              }
              skip[tline] = true;
              skip[tline + 1] = true;
              blocks.add(new TForBlock(function, line + 1, tline + 2, A, C, r));
            } else if(code.op(tline) == forTarget && !skip[tline]) {
              int A = code.A(tline);
              r.setInternalLoopVariable(A, tline, line + 1); //TODO: end?
              r.setInternalLoopVariable(A + 1, tline, line + 1);
              r.setInternalLoopVariable(A + 2, tline, line + 1);
              skip[tline] = true;
              skip[tline+1] = true;
              blocks.add(new ForBlock(function, line + 1, tline + 1, A, r));
            } else if(code.sBx(line) == 2 && code.op(line + 1) == Op.LOADBOOL && code.C(line + 1) != 0) {
              /* This is the tail of a boolean set with a compare node and assign node */
              blocks.add(new BooleanIndicator(function, line));
            } else if(code.op(tline) == Op.JMP && code.sBx(tline) + tline == line) {
              if(first)
                blocks.add(new AlwaysLoop(function, line, tline+1));
              skip[tline] = true;
            } else {
              /*
              for(Block block : blocks) {
                if(!block.breakable() && block.end == tline) {
                  block.end = line;
                }
              }
              */
              if(first || loopRemoved[line] || reverseTarget[line+1]) {
                if(tline > line) {
                  isBreak[line] = true;
                  blocks.add(new Break(function, line, tline));
                } else {
                  Block enclosing = enclosingBreakableBlock(line);
                  if(enclosing != null && enclosing.breakable() && code.op(enclosing.end) == Op.JMP && code.sBx(enclosing.end) + enclosing.end + 1 == tline) {
                    isBreak[line] = true;
                    blocks.add(new Break(function, line, enclosing.end));
                  } else {
                    blocks.add(new AlwaysLoop(function, tline, line + 1));
                  }
                  
                }
              }
            }
            break;
          }
          case FORPREP:
            reduce = true;
            blocks.add(new ForBlock(function, line + 1, line + 2 + code.sBx(line), code.A(line), r));
            skip[line + 1 + code.sBx(line)] = true;
            r.setInternalLoopVariable(code.A(line), line, line + 2 + code.sBx(line));
            r.setInternalLoopVariable(code.A(line) + 1, line, line + 2 + code.sBx(line));
            r.setInternalLoopVariable(code.A(line) + 2, line, line + 2 + code.sBx(line));
            r.setExplicitLoopVariable(code.A(line) + 3, line, line + 2 + code.sBx(line));
            break;
          case FORLOOP:
            /* Should be skipped by preceding FORPREP */
            throw new IllegalStateException();
          case TFORPREP: {
            reduce = true;
            int tline = line + 1 + code.sBx(line);
            int A = code.A(tline);
            int C = code.C(tline);
            r.setInternalLoopVariable(A, tline, line + 1); // TODO: end?
            r.setInternalLoopVariable(A + 1, tline, line + 1);
            r.setInternalLoopVariable(A + 2, tline, line + 1);
            for(int index = 1; index <= C; index++) {
              r.setExplicitLoopVariable(A + 2 + index, line, tline + 2); // TODO: end?
            }
            skip[tline] = true;
            skip[tline + 1] = true;
            blocks.add(new TForBlock(function, line + 1, tline + 2, A, C, r));
            break;
          }
          default:
            reduce = isStatement(line);
            break;
        }
      }

      if((line + 1) <= length && reverseTarget[line + 1]) {
        reduce = true;
      }
      if(testset && testsetend == line + 1) {
        reduce = true;
      }
      if(stack.isEmpty()) {
        reduce = false;
      }
      if(reduce) {
        reduce = false;
        Stack<Branch> conditions = new Stack<Branch>();
        Stack<Stack<Branch>> backups = new Stack<Stack<Branch>>();
        do {
          boolean isAssignNode = stack.peek() instanceof TestSetNode;
          int assignEnd = stack.peek().end;
          boolean compareCorrect = false;
          if(stack.peek() instanceof TrueNode) {
            isAssignNode = true;
            compareCorrect = true;
            //assignEnd = stack.peek().begin;
            if(code.C(assignEnd) != 0) {
              assignEnd += 2;
            } else {
              assignEnd += 1;
            }
            //System.exit(0);
          } else if(stack.peek().isCompareSet) {
            //System.err.println("c" + stack.peek().setTarget); 
            if(code.op(stack.peek().begin) != Op.LOADBOOL || code.C(stack.peek().begin) == 0) {
              isAssignNode = true;
              if(code.C(assignEnd) != 0) {
                assignEnd += 2;
              } else {
                assignEnd += 1;
              }
              compareCorrect = true;
            }
          } else if(assignEnd - 3 >= 1 && code.op(assignEnd - 2) == Op.LOADBOOL && code.C(assignEnd - 2) != 0 && code.op(assignEnd - 3) == Op.JMP && code.sBx(assignEnd - 3) == 2) {
            if(stack.peek() instanceof TestNode) {
              TestNode node = (TestNode) stack.peek();
              if(node.test == code.A(assignEnd - 2)) {
                isAssignNode = true;
              }
            }
          } else if(assignEnd - 2 >= 1 && code.op(assignEnd - 1) == Op.LOADBOOL && code.C(assignEnd - 1) != 0 && code.op(assignEnd - 2) == Op.JMP && code.sBx(assignEnd - 2) == 2) {
            if(stack.peek() instanceof TestNode) {
              isAssignNode = true;
              assignEnd += 1;
            }
          } else if(assignEnd - 1 >= 1 && code.op(assignEnd) == Op.LOADBOOL && code.C(assignEnd) != 0 && code.op(assignEnd - 1) == Op.JMP && code.sBx(assignEnd - 1) == 2) {
            if(stack.peek() instanceof TestNode) {
              isAssignNode = true;
              assignEnd += 2;
            }
          } else if(assignEnd - 1 >= 1 && r.isLocal(getAssignment(assignEnd - 1), assignEnd - 1) && assignEnd > stack.peek().line) {
            Declaration decl = r.getDeclaration(getAssignment(assignEnd - 1), assignEnd - 1);
            if(decl.begin == assignEnd - 1 && decl.end > assignEnd - 1) {
              isAssignNode = true;
            }
          }
          if(!compareCorrect && assignEnd - 1 == stack.peek().begin && code.op(stack.peek().begin) == Op.LOADBOOL && code.C(stack.peek().begin) != 0) {
            backup = null;
            int begin = stack.peek().begin;
            assignEnd = begin + 2;
            int target = code.A(begin);
            conditions.push(popCompareSetCondition(stack, assignEnd, target));
            conditions.peek().setTarget = target;
            conditions.peek().end = assignEnd;
            conditions.peek().begin = begin;
          } else if(isAssignNode) {
            backup = null;
            int target = stack.peek().setTarget;
            int begin = stack.peek().begin;
            conditions.push(popSetCondition(stack, assignEnd, target));
            conditions.peek().setTarget = target;
            conditions.peek().end = assignEnd;
            conditions.peek().begin = begin;
          } else {
            backup = new Stack<Branch>();
            conditions.push(popCondition(stack));
            backup.reverse();
          }
          backups.push(backup);
        } while(!stack.isEmpty());
        do {
          Branch cond = conditions.pop();
          Stack<Branch> backup = backups.pop();
          int breakTarget = breakTarget(cond.begin);
          boolean breakable = (breakTarget >= 1);
          if(breakable && code.op(breakTarget) == Op.JMP && function.header.version != Version.LUA50) {
            breakTarget += 1 + code.sBx(breakTarget);
          }
          if(breakable && breakTarget == cond.end) {
            Block immediateEnclosing = enclosingBlock(cond.begin);
            Block breakableEnclosing = enclosingBreakableBlock(cond.begin);
            int loopstart = immediateEnclosing.end;
            if(immediateEnclosing == breakableEnclosing) loopstart--;
            for(int iline = loopstart; iline >= Math.max(cond.begin, immediateEnclosing.begin); iline--) {
              if(code.op(iline) == Op.JMP && iline + 1 + code.sBx(iline) == breakTarget) {
                cond.end = iline;
                break;
              }
            }
          }
          /* A branch has a tail if the instruction just before the end target is JMP */
          boolean hasTail = cond.end >= 2 && code.op(cond.end - 1) == Op.JMP;
          /* This is the target of the tail JMP */
          int tail = hasTail ? cond.end + code.sBx(cond.end - 1) : -1;
          int originalTail = tail;
          Block enclosing = enclosingUnprotectedBlock(cond.begin);
          /* Checking enclosing unprotected block to undo JMP redirects. */
          if(enclosing != null) {
            //System.err.println("loopback: " + enclosing.getLoopback());
            //System.err.println("cond.end: " + cond.end);
            //System.err.println("tail    : " + tail);
            if(enclosing.getLoopback() == cond.end) {
              cond.end = enclosing.end - 1;
              hasTail = cond.end >= 2 && code.op(cond.end - 1) == Op.JMP;
              tail = hasTail ? cond.end + code.sBx(cond.end - 1) : -1;
            }
            if(hasTail && enclosing.getLoopback() == tail) {
              tail = enclosing.end - 1;
            }
          }
          if(cond.isSet) {
            boolean empty = cond.begin == cond.end;
            if(code.op(cond.begin) == Op.JMP && code.sBx(cond.begin) == 2 && code.op(cond.begin + 1) == Op.LOADBOOL && code.C(cond.begin + 1) != 0) {
              empty = true;
            }
            blocks.add(new SetBlock(function, cond, cond.setTarget, line, cond.begin, cond.end, empty, r));
          } else if(code.op(cond.begin) == Op.LOADBOOL && code.C(cond.begin) != 0) {
            int begin = cond.begin;
            int target = code.A(begin);
            if(code.B(begin) == 0) {
              cond = cond.invert();
            }
            blocks.add(new CompareBlock(function, begin, begin + 2, target, cond));
          } else if(cond.end < cond.begin) {
            if(isBreak[cond.end - 1]) {
              skip[cond.end - 1] = true;
              blocks.add(new WhileBlock(function, cond.invert(), originalTail, r));
            } else {
              blocks.add(new RepeatBlock(function, cond, r));
            }
          } else if(hasTail) {
            Op endOp = code.op(cond.end - 2);
            boolean isEndCondJump = endOp == Op.EQ || endOp == Op.LE || endOp == Op.LT || endOp == Op.TEST || endOp == Op.TESTSET || endOp == Op.TEST50;
            if(tail > cond.end || (tail == cond.end && !isEndCondJump)) {
              Op op = code.op(tail - 1);
              int sbx = code.sBx(tail - 1);
              int loopback2 = tail + sbx;
              boolean isBreakableLoopEnd = function.header.version.isBreakableLoopEnd(op);
              if(isBreakableLoopEnd && loopback2 <= cond.begin && !isBreak[tail - 1]) {
                /* (ends with break) */
                blocks.add(new IfThenEndBlock(function, cond, backup, r));
              } else {
                skip[cond.end - 1] = true; //Skip the JMP over the else block
                boolean emptyElse = tail == cond.end;
                IfThenElseBlock ifthen = new IfThenElseBlock(function, cond, originalTail, emptyElse, r);
                blocks.add(ifthen);
                
                if(!emptyElse) {
                  ElseEndBlock elseend = new ElseEndBlock(function, cond.end, tail);
                  blocks.add(elseend);
                }
              }
            } else {
              int loopback = tail;
              boolean existsStatement = false;
              for(int sl = loopback; sl < cond.begin; sl++) {
                if(!skip[sl] && isStatement(sl)) {
                  existsStatement = true;
                  break;
                }
              }
              //TODO: check for 5.2-style if cond then break end
              if(loopback >= cond.begin || existsStatement) {
                blocks.add(new IfThenEndBlock(function, cond, backup, r));
              } else {
                skip[cond.end - 1] = true;
                blocks.add(new WhileBlock(function, cond, originalTail, r));
              }
            }
          } else {
            blocks.add(new IfThenEndBlock(function, cond, backup, r));
          }
        } while(!conditions.isEmpty());
      }
    }
    //Find variables whose scope isn't controlled by existing blocks:
    for(Declaration decl : declList) {
      if(!decl.forLoop && !decl.forLoopExplicit) {
        boolean needsDoEnd = true;
        for(Block block : blocks) {
          if(block.contains(decl.begin)) {
            if(block.scopeEnd() == decl.end) {
              needsDoEnd = false;
              break;
            }
          }
        }
        if(needsDoEnd) {
          //Without accounting for the order of declarations, we might
          //create another do..end block later that would eliminate the
          //need for this one. But order of decls should fix this.
          blocks.add(new DoEndBlock(function, decl.begin, decl.end + 1));
        }
      }
    }
    // Remove breaks that were later parsed as else jumps
    ListIterator<Block> iter = blocks.listIterator();
    while(iter.hasNext()) {
      Block block = iter.next();
      if(skip[block.begin] && block instanceof Break) {
        iter.remove();
      }
    }
    Collections.sort(blocks);
    backup = null;
    return outer;
  }
  
  private int breakTarget(int line) {
    int tline = Integer.MAX_VALUE;
    for(Block block : blocks) {
      if(block.breakable() && block.contains(line)) {
        tline = Math.min(tline, block.end);
      }
    }
    if(tline == Integer.MAX_VALUE) return -1;
    return tline;
  }
  
  private Block enclosingBlock(int line) {
    //Assumes the outer block is first
    Block outer = blocks.get(0);
    Block enclosing = outer;
    for(int i = 1; i < blocks.size(); i++) {
      Block next = blocks.get(i);
      if(next.isContainer() && enclosing.contains(next) && next.contains(line) && !next.loopRedirectAdjustment) {
        enclosing = next;
      }
    }
    return enclosing;
  }
  
  private Block enclosingBlock(Block block) {
    //Assumes the outer block is first
    Block outer = blocks.get(0);
    Block enclosing = outer;
    for(int i = 1; i < blocks.size(); i++) {
      Block next = blocks.get(i);
      if(next == block) continue;
      if(next.contains(block) && enclosing.contains(next)) {
        enclosing = next;
      }
    }
    return enclosing;
  }
  
  private Block enclosingBreakableBlock(int line) {
    Block outer = blocks.get(0);
    Block enclosing = outer;
    for(int i = 1; i < blocks.size(); i++) {
      Block next = blocks.get(i);
      if(enclosing.contains(next) && next.contains(line) && next.breakable() && !next.loopRedirectAdjustment) {
        enclosing = next;
      }
    }
    return enclosing == outer ? null : enclosing;
  }
  
  private Block enclosingUnprotectedBlock(int line) {
    //Assumes the outer block is first
    Block outer = blocks.get(0);
    Block enclosing = outer;
    for(int i = 1; i < blocks.size(); i++) {
      Block next = blocks.get(i);
      if(enclosing.contains(next) && next.contains(line) && next.isUnprotected() && !next.loopRedirectAdjustment) {
        enclosing = next;
      }
    }
    return enclosing == outer ? null : enclosing;
  }
  
  private static Stack<Branch> backup;
  
  public Branch popCondition(Stack<Branch> stack) {
    Branch branch = stack.pop();
    if(backup != null) backup.push(branch);
    if(branch instanceof TestSetNode) {
      throw new IllegalStateException();
    }
    int begin = branch.begin;
    if(code.op(branch.begin) == Op.JMP) {
      begin += 1 + code.sBx(branch.begin);
    }
    while(!stack.isEmpty()) {
      Branch next = stack.peek();
      if(next instanceof TestSetNode) break;
      if(next.end == begin) {
        branch = new OrBranch(popCondition(stack).invert(), branch);
      } else if(next.end == branch.end) {
        branch = new AndBranch(popCondition(stack), branch);
      } else {
        break;
      }
    }
    return branch;
  }
  
  public Branch popSetCondition(Stack<Branch> stack, int assignEnd, int target) {
    //System.err.println("assign end " + assignEnd);
    stack.push(new AssignNode(assignEnd - 1, assignEnd, assignEnd));
    //Invert argument doesn't matter because begin == end
    Branch rtn = _helper_popSetCondition(stack, false, assignEnd, target);
    return rtn;
  }
  
  public Branch popCompareSetCondition(Stack<Branch> stack, int assignEnd, int target) {
    Branch top = stack.pop();
    boolean invert = false;
    if(code.B(top.begin) == 0) invert = true;//top = top.invert();
    top.begin = assignEnd;
    top.end = assignEnd;
    stack.push(top);
    //stack.pop();
    //stack.push(new AssignNode(assignEnd - 1, assignEnd, assignEnd));
    //Invert argument doesn't matter because begin == end
    Branch rtn = _helper_popSetCondition(stack, invert, assignEnd, target);
    return rtn;
  }
  
  private int _adjustLine(int line, int target) {
    int testline = line;
    while(testline >= 1 && code.op(testline) == Op.LOADBOOL && (target == -1 || code.A(testline) == target)) {
      testline--;
    }
    if(testline == line) {
      return testline;
    }
    testline++;
    if(code.C(testline) != 0) {
      return testline + 2;
    } else {
      return testline + 1;
    }
  }
  
  private Branch _helper_popSetCondition(Stack<Branch> stack, boolean invert, int assignEnd, int target) {
    Branch branch = stack.pop();
    int begin = branch.begin;
    int end = branch.end;
    //System.err.println(stack.size());
    //System.err.println("_helper_popSetCondition; count: " + count);
    //System.err.println("_helper_popSetCondition; begin: " + begin);
    //System.err.println("_helper_popSetCondition; end:   " + end);
    if(invert) {
      branch = branch.invert();
    }
    begin = _adjustLine(begin, target);
    end = _adjustLine(end, target);
    //System.err.println("_helper_popSetCondition; begin_adj: " + begin);
    //System.err.println("_helper_popSetCondition; end_adj:   " + end);
    //if(count >= 2) System.exit(1);
    int btarget = branch.setTarget;
    while(!stack.isEmpty()) {
      Branch next = stack.peek();
      //System.err.println("_helper_popSetCondition; next begin: " + next.begin);
      //System.err.println("_helper_popSetCondition; next end:   " + next.end);
      boolean ninvert;
      int nend = next.end;
      if(code.op(nend) == Op.LOADBOOL && (target == -1 || code.A(nend) == target)) {
        ninvert = code.B(nend) != 0;
        nend = _adjustLine(nend, target);
      } else if(next instanceof TestSetNode) {
        TestSetNode node = (TestSetNode) next;
        ninvert = node.invert;
      } else if(next instanceof TestNode) {
        TestNode node = (TestNode) next;
        ninvert = node.invert;
      } else {
        ninvert = false;
        if(nend >= assignEnd) {
          //System.err.println("break");
          break;
        }
      }
      int addr;
      if(ninvert == invert) {
        addr = end;
      } else {
        addr = begin;
      }
      
      //System.err.println(" addr: " + addr + "(" + begin + ", " + end + ")");
      //System.err.println(" nend: " + nend);
      //System.err.println(" ninv: " + ninvert);
      //System.err.println("-------------");
      //System.exit(0);
      
      if(addr == nend) {
        if(addr != nend) ninvert = !ninvert;
        if(ninvert) {
          branch = new OrBranch(_helper_popSetCondition(stack, ninvert, assignEnd, target), branch);
        } else {
          branch = new AndBranch(_helper_popSetCondition(stack, ninvert, assignEnd, target), branch);
        }
        branch.end = nend;
      } else {
        if(!(branch instanceof TestSetNode)) {
          stack.push(branch);
          branch = popCondition(stack);
        }
        //System.out.println("--break");
        break;
      }
    }
    branch.isSet = true;
    branch.setTarget = btarget;
    return branch;
  }
  
  private boolean isStatement(int line) {
    return isStatement(line, -1);
  }
  
  private boolean isStatement(int line, int testRegister) {
    switch(code.op(line)) {
      case MOVE:
      case LOADK:
      case LOADBOOL:
      case GETUPVAL:
      case GETTABUP:
      case GETGLOBAL:
      case GETTABLE:
      case NEWTABLE:
      case NEWTABLE50:
      case ADD:
      case SUB:
      case MUL:
      case DIV:
      case MOD:
      case POW:
      case UNM:
      case NOT:
      case LEN:
      case CONCAT:
      case CLOSURE:
        return r.isLocal(code.A(line), line) || code.A(line) == testRegister;
      case LOADNIL:
        for(int register = code.A(line); register <= code.B(line); register++) {
          if(r.isLocal(register, line)) {
            return true;
          }
        }
        return false;
      case SETGLOBAL:
      case SETUPVAL:
      case SETTABUP:
      case SETTABLE:
      case JMP:
      case TAILCALL:
      case RETURN:
      case FORLOOP:
      case FORPREP:
      case TFORPREP:
      case TFORCALL:
      case TFORLOOP:
      case CLOSE:
        return true;
      case SELF:
        return r.isLocal(code.A(line), line) || r.isLocal(code.A(line) + 1, line);
      case EQ:
      case LT:
      case LE:
      case TEST:
      case TESTSET:
      case TEST50:
      case SETLIST:
      case SETLISTO:
      case SETLIST50:
        return false;
      case CALL: {
        int a = code.A(line);
        int c = code.C(line);
        if(c == 1) {
          return true;
        }
        if(c == 0) c = registers - a + 1;
        for(int register = a; register < a + c - 1; register++) {
          if(r.isLocal(register, line)) {
            return true;
          }
        }
        return (c == 2 && a == testRegister);
      }
      case VARARG: {
        int a = code.A(line);
        int b = code.B(line);
        if(b == 0) b = registers - a + 1;
        for(int register = a; register < a + b - 1; register++) {
          if(r.isLocal(register, line)) {
            return true;
          }
        }
        return false;
      }
      default:
        throw new IllegalStateException("Illegal opcode: " + code.op(line));
    }
  }
  
  /**
   * Returns the single register assigned to at the line or
   * -1 if no register or multiple registers is/are assigned to.
   */
  private int getAssignment(int line) {
    switch(code.op(line)) {
      case MOVE:
      case LOADK:
      case LOADBOOL:
      case GETUPVAL:
      case GETTABUP:
      case GETGLOBAL:
      case GETTABLE:
      case NEWTABLE:
      case NEWTABLE50:
      case ADD:
      case SUB:
      case MUL:
      case DIV:
      case MOD:
      case POW:
      case UNM:
      case NOT:
      case LEN:
      case CONCAT:
      case CLOSURE:
        return code.A(line);
      case LOADNIL:
        if(code.A(line) == code.B(line)) {
          return code.A(line);
        } else {
          return -1;
        }
      case SETGLOBAL:
      case SETUPVAL:
      case SETTABUP:
      case SETTABLE:
      case JMP:
      case TAILCALL:
      case RETURN:
      case FORLOOP:
      case FORPREP:
      case TFORCALL:
      case TFORLOOP:
      case CLOSE:
        return -1;
      case SELF:
        return -1;
      case EQ:
      case LT:
      case LE:
      case TEST:
      case TESTSET:
      case SETLIST:
      case SETLIST50:
      case SETLISTO:
        return -1;
      case CALL: {
        if(code.C(line) == 2) {
          return code.A(line);
        } else {
          return -1;
        }
      }
      case VARARG: {
        if(code.C(line) == 2) {
          return code.B(line);
        } else {
          return -1;
        }
      }
      default:
        throw new IllegalStateException("Illegal opcode: " + code.op(line));
    }
  }
  
}
