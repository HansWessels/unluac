package unluac.decompile.target;

import unluac.decompile.Output;

public class GlobalTarget extends Target {

  private final String name;
  
  public GlobalTarget(String name) {
    this.name = name;
  }

  @Override
  public void print(Output out) {
    out.print(name);
  }
  
  @Override
  public void printMethod(Output out) {
    throw new IllegalStateException();
  }
  
}
