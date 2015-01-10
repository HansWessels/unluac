package unluac.decompile.statement;

import java.util.List;

import unluac.decompile.Declaration;
import unluac.decompile.Decompiler;
import unluac.decompile.Output;

public class Declare extends Statement {

  private final List<Declaration> decls;
  
  public Declare(List<Declaration> decls) {
    this.decls = decls;
  }

  @Override
  public void print(Decompiler d, Output out) {
    out.print("local ");
    out.print(decls.get(0).name);
    for(int i = 1; i < decls.size(); i++) {
      out.print(", ");
      out.print(decls.get(i).name);
    }
  }
  
}
