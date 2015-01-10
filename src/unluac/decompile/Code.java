package unluac.decompile;

import unluac.parse.LFunction;

public class Code {
  
  public static int extract_A(int codepoint) {
    return (codepoint >> 6) & 0x0000000FF;
  }
  
  public static int extract_C(int codepoint) {
    return (codepoint >> 14) & 0x000001FF;
  }
  
  public static int extract_B(int codepoint) {
    return codepoint >>> 23;
  }
  
  public static int extract_Bx(int codepoint) {
    return codepoint >>> 14;
  }
  
  public static int extract_sBx(int codepoint) {
    return (codepoint >>> 14) - 131071;
  }
  private final OpcodeMap map;
  private final int[] code;
  
  public Code(LFunction function) {
    this.code = function.code;
    map = function.header.version.getOpcodeMap();
  }
  
  //public boolean reentered = false;
  
  public Op op(int line) {
    /*if(!reentered) {
      reentered = true;
      System.out.println("line " + line + ": " + toString(line));
      reentered = false;
    }*/
    return map.get(opcode(line));
  }
  
  public int opcode(int line) {
    return code[line - 1] & 0x0000003F;
  }
  
  public int A(int line) {
    return extract_A(code[line - 1]);
  }
  
  public int C(int line) {
    return extract_C(code[line - 1]);
  }
  
  public int B(int line) {
    return extract_B(code[line - 1]);
  }
  
  public int Bx(int line) {
    return extract_Bx(code[line - 1]);
  }
  
  public int sBx(int line) {
    return extract_sBx(code[line - 1]);
  }

  public int codepoint(int line) {
    return code[line - 1];
  }
  
  public String toString(int line) {
    return op(line).codePointToString(codepoint(line));
  }
  
}
