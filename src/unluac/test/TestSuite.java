package unluac.test;

import java.io.File;
import java.io.IOException;

import unluac.Main;

public class TestSuite {
  
  private static String working_dir = "./test/working/";
  private static String compiled = "luac.out";
  private static String decompiled = "unluac.out";
  private static String recompiled = "test.out";
  
  private String[] files;
  private String path;
  private String ext = ".lua";
  
  public TestSuite(String path, String[] files) {
    this.files = files;
    this.path = path;
  }
  
  private boolean test(String file) throws IOException {
    LuaC.compile(file, working_dir + compiled);
    Main.decompile(working_dir + compiled, working_dir + decompiled);
    LuaC.compile(working_dir + decompiled, working_dir + recompiled);
    return Compare.bytecode_equal(working_dir + compiled, working_dir + recompiled);
  }
  
  public boolean run() throws IOException {
    int passed = 0;
    int failed = 0;
    File working = new File(working_dir);
    if(!working.exists()) {
      working.mkdir();
    }
    for(String name : files) {
      if(test(path + name + ext)) {
        System.out.println("Passed: " + name);
        passed++;
      } else {
        System.out.println("Failed: " + name);
        failed++;
      }
    }
    if(failed == 0) {
      System.out.println("All tests passed!");
      return true;
    } else {
      System.out.println("Failed " + failed + " of " + (failed + passed) + " tests.");
      return false;
    }
  }
  
}
