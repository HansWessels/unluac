package unluac.decompile;

public class Code {
  
  private final int[] code;
  
  public Code(int[] code) {
    this.code = code;
  }
  
  public int op(int line) {
    return code[line - 1] & 0x0000003F;
  }
  
  public int A(int line) {
    return (code[line - 1] >> 6) & 0x0000000FF;
  }
  
  public int C(int line) {
    return (code[line - 1] >> 14) & 0x000001FF;
  }
  
  public int B(int line) {
    return code[line - 1] >>> 23;
  }
  
  public int Bx(int line) {
    return code[line - 1] >>> 14;
  }
  
  public int sBx(int line) {
    return (code[line - 1] >>> 14) - 131071;
  }

  public int codepoint(int line) {
    return code[line - 1];
  }
  
}
