package unluac.decompile.operation;

import unluac.decompile.Registers;
import unluac.decompile.block.Block;
import unluac.decompile.expression.Expression;
import unluac.decompile.expression.TableLiteral;
import unluac.decompile.statement.Assignment;
import unluac.decompile.statement.Statement;
import unluac.decompile.target.TableTarget;

public class TableSet extends Operation {
  
  private Expression table;
  private Expression index;
  private Expression value;
  private boolean isTable;
  private int timestamp;
  
  public TableSet(int line, Expression table, Expression index, Expression value, boolean isTable, int timestamp) {
    super(line);
    this.table = table;
    this.index = index;
    this.value = value;
    this.isTable = isTable;
    this.timestamp = timestamp;
  }

  @Override
  public Statement process(Registers r, Block block) {
    // .isTableLiteral() is sufficient when there is debugging info
    if(table.isTableLiteral() && (value.isMultiple() || table.isNewEntryAllowed())) {
      table.addEntry(new TableLiteral.Entry(index, value, !isTable, timestamp));
      return null;
    } else {
      return new Assignment(new TableTarget(table, index), value);
    }
  }

}
