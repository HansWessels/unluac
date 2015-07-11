package unluac.decompile.target;

import unluac.decompile.Decompiler;
import unluac.decompile.Output;

public class GlobalTarget extends Target {

  private final String name;
  
  public GlobalTarget(String name) {
    this.name = name;
  }

  @Override
  public void print(Decompiler d, Output out) {
    out.print(name);
  }
  
  @Override
  public void printMethod(Decompiler d, Output out) {
    throw new IllegalStateException();
  }
  
}
