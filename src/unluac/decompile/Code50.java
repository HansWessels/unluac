package unluac.decompile;

public class Code50 implements CodeExtract {

  private int shiftA;
  private int shiftB;
  private int shiftBx;
  private int shiftC;
  private int maskOp;
  private int maskA;
  private int maskB;
  private int maskBx;
  private int maskC;
  private int excessK;

  public Code50(int sizeOp, int sizeA, int sizeB, int sizeC) {
    shiftC = sizeOp;
    shiftB = sizeC + sizeOp;
    shiftBx = sizeOp;
    shiftA = sizeB + sizeC + sizeOp;
    maskOp = (1 << sizeOp) - 1;
    maskA = (1 << sizeA) - 1;
    maskB = (1 << sizeB) - 1;
    maskBx = (1 << (sizeB + sizeC)) - 1;
    maskC = (1 << sizeC) - 1;
    excessK = maskBx / 2;
  }

  @Override
  public int extract_A(int codepoint) {
    return (codepoint >> shiftA) & maskA;
  }

  @Override
  public int extract_C(int codepoint) {
    return (codepoint >> shiftC) & maskC;
  }

  @Override
  public int extract_B(int codepoint) {
    return (codepoint >> shiftB) & maskB;
  }

  @Override
  public int extract_Bx(int codepoint) {
    return (codepoint >> shiftBx) & maskBx;
  }

  @Override
  public int extract_sBx(int codepoint) {
    return ((codepoint >> shiftBx) & maskBx) - excessK;
  }

  @Override
  public int extract_op(int codepoint) {
    return codepoint & maskOp;
  }
}
