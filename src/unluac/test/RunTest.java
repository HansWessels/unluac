package unluac.test;

import java.io.IOException;

public class RunTest {

  public static void main(String[] args) throws IOException {
    if(TestFiles.suite.run(args[0])) {
      System.exit(0);
    } else {
      System.exit(1);
    }
  }
}
