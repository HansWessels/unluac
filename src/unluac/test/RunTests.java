package unluac.test;

import java.io.IOException;

public class RunTests {

  public static void main(String[] args) throws IOException {
    boolean result = true;
    for(int version = 0x50; version <= 0x51; version++) {
      result = result & TestFiles.suite.run(new LuaSpec(version));
    }
    if(result) {
      System.exit(0);
    } else {
      System.exit(1);
    }
  }
  
}
