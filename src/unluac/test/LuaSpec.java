package unluac.test;

public class LuaSpec {

  public LuaSpec() {
    this.isDefault = true;
    this.version = 0;
  }
  
  public LuaSpec(int version) {
    this.isDefault = false;
    this.version = version;
  }
  
  public String getLuaCName() {
    if(isDefault) {
      return "luac";
    } else {
      return "luac" + Integer.toHexString(version);
    }
  }
  
  private final boolean isDefault;
  private final int version;
  
}
