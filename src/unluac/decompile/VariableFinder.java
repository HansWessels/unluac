package unluac.decompile;

import java.util.ArrayList;
import java.util.List;

public class VariableFinder {

  static class RegisterState {
    
    public RegisterState() {
      temporary = false;
      local = false;
      read = false;
      written = false;
    }
    
    boolean temporary;
    boolean local;
    boolean read;
    boolean written;
  }
  
  static class RegisterStates {
    
    RegisterStates(int registers, int lines) {
      this.registers = registers;
      this.lines = lines;
      states = new RegisterState[lines][registers];
      for(int line = 0; line < lines; line++) {
        for(int register = 0; register < registers; register++) {
          states[line][register] = new RegisterState();
        }
      }
    }
    
    public RegisterState get(int register, int line) {
      return states[line - 1][register];
    }
    
    public void setLocal(int register, int line) {
      for(int r = 0; r <= register; r++) {
        get(register, line).local = true;
      }
    }
    
    public void setTemporary(int register, int line) {
      for(int r = register; r < registers; r++) {
        get(register, line).temporary = true;
      }
    }
    
    private int registers;
    private int lines;
    private RegisterState[][] states;
    
  }
  
  private static boolean isConstantReference(int value) {
    return (value & 0x100) != 0;
  }
  
  public static Declaration[] process(Decompiler d, int args, int registers) {
    Code code = d.code;
    RegisterStates states = new RegisterStates(registers, code.length());
    boolean[] skip = new boolean[code.length()];
    for(int line = 1; line <= code.length(); line++) {
      if(skip[line - 1]) continue;
      switch(code.op(line)) {
        case MOVE:
          states.get(code.A(line), line).written = true;
          states.get(code.B(line), line).read = true;
          states.setLocal(code.B(line), line);
          break;
        case LOADK:
        case LOADBOOL:
        case GETUPVAL:
        case GETGLOBAL:
        case NEWTABLE:
          states.get(code.A(line), line).written = true;
          break;
        case LOADNIL:
          for(int register = code.A(line); register <= code.B(line); register++) {
            states.get(register, line).written = true;
          }
          break;
        case GETTABLE:
          states.get(code.A(line), line).written = true;
          if(!isConstantReference(code.B(line))) states.get(code.B(line), line).read = true;
          if(!isConstantReference(code.C(line))) states.get(code.C(line), line).read = true;
          break;
        case SETGLOBAL:
        case SETUPVAL:
          states.get(code.A(line), line).read = true;
          break;
        case SETTABLE:
        case ADD:
        case SUB:
        case MUL:
        case DIV:
        case MOD:
        case POW:
          states.get(code.A(line), line).read = true;
          if(!isConstantReference(code.B(line))) states.get(code.B(line), line).read = true;
          if(!isConstantReference(code.C(line))) states.get(code.C(line), line).read = true;
          break;
        case SELF:
          states.get(code.A(line), line).written = true;
          states.get(code.A(line) + 1, line).written = true;
          states.get(code.B(line), line).read = true;
          if(!isConstantReference(code.C(line))) states.get(code.C(line), line).read = true;
          break;
        case UNM:
        case NOT:
        case LEN:
          states.get(code.A(line), line).written = true;
          states.get(code.B(line), line).read = true;
          break;
        case CONCAT:
          states.get(code.A(line), line).written = true;
          for(int register = code.B(line); register <= code.C(line); register++) {
            states.get(register, line).read = true;
            states.setTemporary(register, line);
          }
          break;
        case JMP:
          break;
        case EQ:
        case LT:
        case LE:
          if(!isConstantReference(code.B(line))) states.get(code.B(line), line).read = true;
          if(!isConstantReference(code.C(line))) states.get(code.C(line), line).read = true;
          break;
        case TEST:
          states.get(code.A(line), line).read = true;
          break;
        case TESTSET:
          states.get(code.A(line), line).written = true;
          states.get(code.B(line), line).read = true;
          break;
        case CALL:
        case TAILCALL: {
          int B = code.B(line);
          int C = code.C(line);
          if(code.op(line) != Op.TAILCALL) {
            if(C >= 2) {
              for(int register = code.A(line); register <= code.A(line) + C - 2; register++) {
                states.get(register, line).written = true;
              }
            }
          }
          for(int register = code.A(line); register <= code.A(line) + B - 1; register++) {
            states.get(code.A(line), line).read = true;
            states.setTemporary(code.A(line), line);
          }
          if(C >= 2) {
            int nline = line + 1;
            int register = code.A(line) + C - 2;
            while(register >= code.A(line) && nline <= code.length()) {
              if(code.op(nline) == Op.MOVE && code.B(nline) == register) {
                states.get(code.A(nline), nline).written = true;
                states.get(code.B(nline), nline).read = true;
                states.setLocal(code.A(nline), nline);
                skip[nline - 1] = true;
              }
              register--;
              nline++;
            }
          }
          break;
        }
      }
    }
    List<Declaration> declList = new ArrayList<Declaration>(registers); 
    for(int register = 0; register < registers; register++) {
      String id = "L";
      boolean local = false;
      boolean temporary = false;
      int read = 0;
      int written = 0;
      if(register < args) {
        local = true;
        id = "A";
      }
      if(!local && !temporary) {
        for(int line = 1; line <= code.length(); line++) {
          RegisterState state = states.get(register, line);
          if(state.local) local = true;
          if(state.temporary) temporary = true;
          if(state.read) read++;
          if(state.written) written++;
        }
      }
      if(!local && !temporary) {
        if(read >= 2 || read == 0) {
          local = true;
        }
      }
      if(local) {
        Declaration decl = new Declaration(id + register + "_" + lc, 0, code.length() - 1);
        decl.register = register;
        lc++;
        declList.add(decl);
      }
    }
    //DEBUG
    /*
    for(Declaration decl : declList) {
      System.out.println("decl: " + decl.name + " " + decl.begin + " " + decl.end);
    }*/
    return declList.toArray(new Declaration[declList.size()]);
  }
  
  static int lc = 0;
  
  private VariableFinder() {}
  
}
