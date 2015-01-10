package unluac.decompile.target;

import unluac.decompile.Decompiler;
import unluac.decompile.Output;
import unluac.decompile.expression.Expression;
import unluac.decompile.expression.TableReference;

public class TableTarget extends Target {

  private final Expression table;
  private final Expression index;
  
  public TableTarget(Expression table, Expression index) {
    this.table = table;
    this.index = index;
  }

  @Override
  public void print(Decompiler d, Output out) {
    new TableReference(table, index).print(d, out);
  }
  
  @Override
  public void printMethod(Decompiler d, Output out) {
    table.print(d, out);
    out.print(":");
    out.print(index.asName());
  }
  
  @Override
  public boolean isFunctionName() {
    if(!index.isIdentifier()) {
      return false;
    }
    if(!table.isDotChain()) {
      return false;
    }
    return true;
  }
  
  @Override
  public boolean beginsWithParen() {
    return table.isUngrouped() || table.beginsWithParen();
  }
  
}
