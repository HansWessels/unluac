package unluac.decompile.expression;

import unluac.decompile.Decompiler;
import unluac.decompile.Output;

public class TableReference extends Expression {

  private final Expression table;
  private final Expression index;
  
  public TableReference(Expression table, Expression index) {
    super(PRECEDENCE_ATOMIC);
    this.table = table;
    this.index = index;
  }

  @Override
  public int getConstantIndex() {
    return Math.max(table.getConstantIndex(), index.getConstantIndex());
  }
  
  @Override
  public void print(Decompiler d, Output out) {
    if(table.isUngrouped()) {
      out.print("(");
      table.print(d, out);
      out.print(")");
    }
    else
    {
      table.print(d, out);
    }
    if(index.isIdentifier()) {
      out.print(".");
      out.print(index.asName());
    } else {
      out.print("[");
      index.printBraced(d, out);
      out.print("]");
    }
  }

  @Override
  public boolean isDotChain() {
    return index.isIdentifier() && table.isDotChain();
  }
  
  @Override
  public boolean isMemberAccess() {
    return index.isIdentifier();
  }
  
  @Override
  public boolean beginsWithParen() {
    return table.isUngrouped() || table.beginsWithParen();
  }
  
  @Override
  public Expression getTable() {
    return table;
  }
  
  @Override
  public String getField() {
    return index.asName();
  }

  
}
