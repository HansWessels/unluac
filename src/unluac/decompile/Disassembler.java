package unluac.decompile;

import unluac.parse.LFunction;

public class Disassembler {

  private final LFunction function;
  private final Code code;
  
  public Disassembler(LFunction function) {
    this.function = function;
    this.code = new Code(function);
  }
  
  public void disassemble(Output out) {
    disassemble(out, 0, 0);
  }
  
  private void disassemble(Output out, int level, int index) {
    out.println("function " + level + " " + index);
    for(int line = 1; line <= function.code.length; line++) {
      out.println("\t" + code.opcode(line) + " " + code.A(line) + " " + code.B(line) + " " + code.C(line) + " " + code.Bx(line) + " " + code.sBx(line) + " " + code.codepoint(line));
    }
    out.println();
    for(int constant = 1; constant <= function.constants.length; constant++) {
      out.println("\t" + constant + " " + function.constants[constant - 1]);
    }
    out.println();
    int subindex = 0;
    for(LFunction child : function.functions) {
      new Disassembler(child).disassemble(out, level + 1, subindex++);
    }
  }
  
}
