package unluac.test;

import java.io.IOException;

public class RunTest {

  public static void main(String[] args) throws IOException {
    LuaSpec spec = new LuaSpec();
    if(TestFiles.suite.run(spec, args[0])) {
      System.exit(0);
    } else {
      System.exit(1);
    }
  }
}
