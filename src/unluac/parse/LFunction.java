package unluac.parse;

public class LFunction extends BObject {
  
  public int[] code;
  public LLocal[] locals;
  public LObject[] constants;
  public String[] upvalues;
  public LFunction[] functions;
  public int maximumStackSize;
  public int numUpvalues;
  public int numParams;
  public int vararg;
  
  public LFunction(int[] code, LLocal[] locals, LObject[] constants, String[] upvalues, LFunction[] functions, int maximumStackSize, int numUpValues, int numParams, int vararg) {
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
