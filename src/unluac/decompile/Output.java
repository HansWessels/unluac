package unluac.decompile;

public class Output {

  private OutputProvider out;
  private int indentationLevel = 0;
  private int position = 0;
  
  public Output() {
    this(new OutputProvider() {

      @Override
      public void print(String s) {
        System.out.print(s);
      }

      @Override
      public void print(byte b) {
        System.out.write(b);
      }
      
      @Override
      public void println() {
        System.out.println();
      }
      
    });
  }
  
  public Output(OutputProvider out) {
    this.out = out;
  }
  
  public void indent() {
    indentationLevel += 2;
  }
  
  public void dedent() {
    indentationLevel -= 2;
  }
  
  public int getIndentationLevel() {
    return indentationLevel;
  }
  
  public int getPosition() {
    return position;
  }
  
  public void setIndentationLevel(int indentationLevel) {
    this.indentationLevel = indentationLevel;
  }
  
  private void start() {
    if(position == 0) {
      for(int i = indentationLevel; i != 0; i--) {
        out.print(" ");
        position++;
      }
    }
  }
  
  public void print(String s) {
    start();
    out.print(s);
    position += s.length();
  }
  
  public void print(byte b) {
    start();
    out.print(b);
    position += 1;
  }
  
  public void println() {
    start();
    out.println();
    position = 0;
  }
  
  public void println(String s) {
    print(s);
    println();
  }
  
}
