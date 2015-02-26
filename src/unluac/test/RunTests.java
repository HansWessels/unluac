package unluac.test;

import java.io.IOException;

public class RunTests {

  public static void main(String[] args) throws IOException {
    LuaSpec spec = new LuaSpec();
    if(TestFiles.suite.run(spec)) {
      System.exit(0);
    } else {
      System.exit(1);
    }
  }
  
}
