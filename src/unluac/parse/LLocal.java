package unluac.parse;


public class LLocal extends BObject {

  public final LString name;
  public final int start;
  public final int end;
  
  /* Used by the decompiler for annotation. */
  public boolean forLoop = false;
  
  public LLocal(LString name, BInteger start, BInteger end) {
    this.name = name;
    this.start = start.asInt();
    this.end = end.asInt();
  }
  
  public String toString() {
    return name.deref();
  }
  
}
