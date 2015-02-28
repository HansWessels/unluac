package unluac.test;

public class LuaSpec {

  public enum NumberFormat {
    DEFAULT,
    FLOAT,
  }
  
  public LuaSpec() {
    this.isDefault = true;
    this.version = 0;
    this.numberFormat = NumberFormat.DEFAULT;
  }
  
  public LuaSpec(int version) {
    this.isDefault = false;
    this.version = version;
    this.numberFormat = NumberFormat.DEFAULT;
  }
  
  public void setNumberFormat(NumberFormat format) {
    this.numberFormat = format;
  }
  
  public String getLuaCName() {
    return "luac" + getVersionString() + getNumberFormatString();
  }
  
  private String getVersionString() {
    if(isDefault) {
      return "";
    } else {
      return Integer.toHexString(version);
    }
  }
  
  private String getNumberFormatString() {
    switch(numberFormat) {
      case DEFAULT:
        return "";
      case FLOAT:
        return "_float";
      default:
        throw new IllegalStateException();
    }
  }
  
  private boolean isDefault;
  private int version;
  private NumberFormat numberFormat;
  
}
