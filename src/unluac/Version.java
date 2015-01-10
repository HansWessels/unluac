package unluac;

import unluac.decompile.Op;
import unluac.decompile.OpcodeMap;
import unluac.parse.LFunctionType;

public abstract class Version {

  public static final Version LUA51 = new Version51();
  public static final Version LUA52 = new Version52();
  
  protected final int versionNumber;
  
  protected Version(int versionNumber) {
    this.versionNumber = versionNumber;
  }
  
  public abstract boolean hasHeaderTail();
  
  public abstract LFunctionType getLFunctionType();
  
  public OpcodeMap getOpcodeMap() {
    return new OpcodeMap(versionNumber);
  }
  
  public abstract int getOuterBlockScopeAdjustment();

  public abstract boolean usesOldLoadNilEncoding();
  
  public abstract boolean usesInlineUpvalueDeclarations();
  
  public abstract Op getTForTarget();
  
  public abstract boolean isBreakableLoopEnd(Op op);
  
  public abstract boolean isAllowedPreceedingSemicolon();
  
}

class Version51 extends Version {
  
  Version51() {
    super(0x51);
  }
  
  @Override
  public boolean hasHeaderTail() {
    return false;
  }
  
  @Override
  public LFunctionType getLFunctionType() {
    return LFunctionType.TYPE51;
  }
  
  @Override
  public int getOuterBlockScopeAdjustment() {
    return -1;
  }
  
  @Override
  public boolean usesOldLoadNilEncoding() {
    return true;
  }
  
  @Override
  public boolean usesInlineUpvalueDeclarations() {
    return true;
  }
  
  @Override
  public Op getTForTarget() {
    return Op.TFORLOOP;
  }
  
  @Override
  public boolean isBreakableLoopEnd(Op op) {
    return op == Op.JMP || op == Op.FORLOOP;
  }
  
  @Override
  public boolean isAllowedPreceedingSemicolon() {
    return false;
  }
  
}

class Version52 extends Version {
  
  Version52() {
    super(0x52);
  }
  
  @Override
  public boolean hasHeaderTail() {
    return true;
  }
  
  @Override
  public LFunctionType getLFunctionType() {
    return LFunctionType.TYPE52;
  }
  
  @Override
  public int getOuterBlockScopeAdjustment() {
    return 0;
  }
  
  @Override
  public boolean usesOldLoadNilEncoding() {
    return false;
  }
  
  @Override
  public boolean usesInlineUpvalueDeclarations() {
    return false;
  }

  @Override
  public Op getTForTarget() {
    return Op.TFORCALL;
  }
  
  @Override
  public boolean isBreakableLoopEnd(Op op) {
    return op == Op.JMP || op == Op.FORLOOP || op == Op.TFORLOOP;
  }
  
  @Override
  public boolean isAllowedPreceedingSemicolon() {
    return true;
  }
  
}