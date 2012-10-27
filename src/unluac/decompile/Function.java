package unluac.decompile;

import unluac.parse.LFunction;
import unluac.parse.LObject;

public class Function {

  public final int numUpvalues;
  public final int numParams;
  public final int vararg;
  public final int registers;
  //public final Code code;
  public final Constant[] constants;
  
  //private Declaration 
  
  public Function(LFunction function) {
    numUpvalues = function.numUpvalues;
    numParams = function.numParams;
    vararg = function.vararg;
    registers = function.maximumStackSize;
    //code = new Code(function.code);
    LObject[] bconstants = function.constants;
    constants = new Constant[bconstants.length];
    for(int index = 0; index < bconstants.length; index++) {
      constants[index] = new Constant(bconstants[index]);
    }
    
  }
  
}
