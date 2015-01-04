package unluac.decompile.expression;

import java.util.ArrayList;
import java.util.Collections;

import unluac.decompile.Output;

public class TableLiteral extends Expression {

  public static class Entry implements Comparable<Entry> {
    
    public final Expression key;
    public final Expression value;
    public final boolean isList;
    public final int timestamp;
    
    public Entry(Expression key, Expression value, boolean isList, int timestamp) {
      this.key = key;
      this.value = value;
      this.isList = isList;
      this.timestamp = timestamp;
    }
    
    @Override
    public int compareTo(Entry e) {
      return ((Integer) timestamp).compareTo(e.timestamp);
    }
  }
  
  private ArrayList<Entry> entries;
  
  private boolean isObject = true;
  private boolean isList = true;
  private int listLength = 1;
  private int capacity;
  
  public TableLiteral(int arraySize, int hashSize) {
    super(PRECEDENCE_ATOMIC);
    entries = new ArrayList<Entry>(arraySize + hashSize);
    capacity = arraySize + hashSize;
  }

  @Override
  public int getConstantIndex() {
    int index = -1;
    for(Entry entry : entries) {
      index = Math.max(entry.key.getConstantIndex(), index);
      index = Math.max(entry.value.getConstantIndex(), index);
    }
    return index;
  }
  
  @Override
  public void print(Output out) {
    Collections.sort(entries);
    listLength = 1;
    if(entries.isEmpty()) {
      out.print("{}");
    } else {
      boolean lineBreak = isList && entries.size() > 5 || isObject && entries.size() > 2 || !isObject;
      //System.out.println(" -- " + (isList && entries.size() > 5));
      //System.out.println(" -- " + (isObject && entries.size() > 2));
      //System.out.println(" -- " + (!isObject));
      if(!lineBreak) {
        for(Entry entry : entries) {
          Expression value = entry.value;
          if(!(value.isBrief())) {
            lineBreak = true;
            break;
          }
        }
      }
      out.print("{");
      if(lineBreak) {
        out.println();
        out.indent();
      }
      printEntry(0, out);
      if(!entries.get(0).value.isMultiple()) {
        for(int index = 1; index < entries.size(); index++) {
          out.print(",");
          if(lineBreak) {
            out.println();
          } else {
            out.print(" ");
          }
          printEntry(index, out);
          if(entries.get(index).value.isMultiple()) {
            break;
          }
        }
      }
      if(lineBreak) {
        out.println();
        out.dedent();
      }
      out.print("}");     
    }    
  }
  
  private void printEntry(int index, Output out) {
    Entry entry = entries.get(index);
    Expression key = entry.key;
    Expression value = entry.value;
    boolean isList = entry.isList;
    boolean multiple = index + 1 >= entries.size() || value.isMultiple();
    if(isList && key.isInteger() && listLength == key.asInteger()) {
      if(multiple) {
        value.printMultiple(out);
      } else {
        value.print(out);
      }
      listLength++;
    } else if(isObject && key.isIdentifier()) {
      out.print(key.asName());
      out.print(" = ");
      value.print(out);
    } else {
      out.print("[");
      key.printBraced(out);
      out.print("] = ");
      value.print(out);
    }
  }
  
  @Override
  public boolean isTableLiteral() {
    return true;
  }
  
  @Override
  public boolean isNewEntryAllowed() {
    return entries.size() < capacity;
  }
  
  @Override
  public void addEntry(Entry entry) {
    entries.add(entry);
    isObject = isObject && (entry.isList || entry.key.isIdentifier());
    isList = isList && entry.isList;
  }
  
  @Override
  public boolean isBrief() {
    return false;
  }
    
}
