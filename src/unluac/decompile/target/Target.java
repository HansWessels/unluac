package unluac.decompile.target;

import unluac.decompile.Declaration;
import unluac.decompile.Output;

abstract public class Target {

  abstract public void print(Output out);
  
  abstract public void printMethod(Output out);
  
  public boolean isDeclaration(Declaration decl) {
    return false;
  }
  
  public boolean isLocal() {
    return false;
  }
  
  public int getIndex() {
    throw new IllegalStateException();
  }
  
  public boolean isFunctionName() {
    return true;
  }
  
  public boolean beginsWithParen() {
    return false;
  }
  
}
