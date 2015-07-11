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
  
  private TestResult test(LuaSpec spec, String file) throws IOException {
    try {
      LuaC.compile(spec, file, working_dir + compiled);
    } catch (IOException e) {
      return TestResult.SKIPPED;
    }
    try {
      Main.decompile(working_dir + compiled, working_dir + decompiled);
      LuaC.compile(spec, working_dir + decompiled, working_dir + recompiled);
      return Compare.bytecode_equal(working_dir + compiled, working_dir + recompiled) ? TestResult.OK : TestResult.FAILED;
    } catch (IOException e) {
      return TestResult.FAILED;
    } catch (RuntimeException e) {
      e.printStackTrace();
      return TestResult.FAILED;
    }
  }
  
  public boolean run(LuaSpec spec) throws IOException {
    int passed = 0;
    int skipped = 0;
    int failed = 0;
    File working = new File(working_dir);
    if(!working.exists()) {
      working.mkdir();
    }
    for(String name : files) {
      switch (test(spec, path + name + ext)) {
        case OK:
          System.out.println("Passed: " + name);
          passed++;
          break;
        case SKIPPED:
          System.out.println("Skipped: " + name);
          skipped++;
          break;
        default:
          System.out.println("Failed: " + name);
          failed++;
      }
    }
    if(failed == 0 && skipped == 0) {
      System.out.println("All tests passed!");
    } else {
      System.out.println("Failed " + failed + " of " + (failed + passed) + " tests, skipped "+skipped+" tests.");
    }
    return failed == 0;
  }
  
  public boolean run(LuaSpec spec, String file) throws IOException {
    int passed = 0;
    int skipped = 0;
    int failed = 0;
    File working = new File(working_dir);
    if(!working.exists()) {
      working.mkdir();
    }
    {
      String name = file;
      switch (test(spec, path + name + ext)) {
        case OK:
          System.out.println("Passed: " + name);
          passed++;
          break;
        case SKIPPED:
          System.out.println("Skipped: " + name);
          skipped++;
          break;
        default:
          System.out.println("Failed: " + name);
          failed++;
      }
    }
    if(failed == 0 && skipped == 0) {
      System.out.println(spec.getLuaCName() + ": All tests passed!");
    } else {
      System.out.println(spec.getLuaCName() + ": Failed " + failed + " of " + (failed + passed) + " tests, skipped "+skipped+" tests.");
    }
    return failed == 0;
  }
}
