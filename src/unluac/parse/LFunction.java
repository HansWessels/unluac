package unluac.parse;

public class LFunction extends BObject {
  
  public BHeader header;
  public LFunction parent;
  public int[] code;
  public LLocal[] locals;
  public LObject[] constants;
  public LUpvalue[] upvalues;
  public LFunction[] functions;
  public int maximumStackSize;
  public int numUpvalues;
  public int numParams;
  public int vararg;
  
  public LFunction(BHeader header, int[] code, LLocal[] locals, LObject[] constants, LUpvalue[] upvalues, LFunction[] functions, int maximumStackSize, int numUpValues, int numParams, int vararg) {
    this.header = header;
    this.code = code;
    this.locals = locals;
    this.constants = constants;
    this.upvalues = upvalues;
    this.functions = functions;
    this.maximumStackSize = maximumStackSize;
    this.numUpvalues = numUpValues;
    this.numParams = numParams;
    this.vararg = vararg;
  }
  
}
