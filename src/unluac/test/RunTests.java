package unluac.test;

import java.io.IOException;

public class RunTests {

  public static void main(String[] args) throws IOException {
    if(TestFiles.suite.run()) {
      System.exit(0);
    } else {
      System.exit(1);
    }
  }
  
}
