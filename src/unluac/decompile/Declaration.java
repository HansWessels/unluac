package unluac.decompile;

import unluac.parse.LLocal;

public class Declaration {

  public final String name;
  public final int begin;
  public final int end;
  public int register;
  
  /**
   * Whether this is an invisible for-loop book-keeping variable.
   */
  public boolean forLoop = false;
  
  /**
   * Whether this is an explicit for-loop declared variable.
   */
  public boolean forLoopExplicit = false;
  
  public Declaration(LLocal local) {
    this.name = local.toString();
    this.begin = local.start;
    this.end = local.end;
  }
  
  public Declaration(String name, int begin, int end) {
    this.name = name;
    this.begin = begin;
    this.end = end;
  }
  
}
