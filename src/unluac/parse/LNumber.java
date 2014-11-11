package unluac.parse;

public abstract class LNumber extends LObject {

  public static LNumber makeInteger(int number) {
    return new LIntNumber(number);
  }
  
  @Override
  public abstract String toString();
    
  //TODO: problem solution for this issue
  public abstract double value();
}

class LFloatNumber extends LNumber {
  
  public final float number;
  
  public LFloatNumber(float number) {
    this.number = number;
  }
  
  @Override
  public String toString() {
    if(number == (float) Math.round(number)) {
      return Integer.toString((int) number);
    } else {
      return Float.toString(number);
    }
  }
  
  @Override
  public boolean equals(Object o) {
    if(o instanceof LFloatNumber) {
      return number == ((LFloatNumber) o).number;
    } else if(o instanceof LNumber) {
      return value() == ((LNumber) o).value();
    }
    return false;
  }
  
  @Override
  public double value() {
    return number;
  }
  
}

class LDoubleNumber extends LNumber {
  
  public final double number;
  
  public LDoubleNumber(double number) {
    this.number = number;
  }
  
  @Override
  public String toString() {
    if(number == (double) Math.round(number)) {
      return Long.toString((long) number);
    } else {
      return Double.toString(number);
    }
  }
  
  @Override
  public boolean equals(Object o) {
    if(o instanceof LDoubleNumber) {
      return number == ((LDoubleNumber) o).number;
    } else if(o instanceof LNumber) {
      return value() == ((LNumber) o).value();
    }
    return false;
  }
  
  @Override
  public double value() {
    return number;
  }
  
}

class LIntNumber extends LNumber {
  
  public final int number;
  
  public LIntNumber(int number) {
    this.number = number;
  }
  
  @Override
  public String toString() {    
    return Integer.toString(number);
  }
  
  @Override
  public boolean equals(Object o) {
    if(o instanceof LIntNumber) {
      return number == ((LIntNumber) o).number;
    } else if(o instanceof LNumber) {
      return value() == ((LNumber) o).value();
    }
    return false;
  }
  
  @Override
  public double value() {
    return number;
  }
  
}

class LLongNumber extends LNumber {
  
  public final long number;
  
  public LLongNumber(long number) {
    this.number = number;
  }
  
  @Override
  public String toString() {    
    return Long.toString(number);
  }
  
  @Override
  public boolean equals(Object o) {
    if(o instanceof LLongNumber) {
      return number == ((LLongNumber) o).number;
    } else if(o instanceof LNumber) {
      return value() == ((LNumber) o).value();
    }
    return false;
  }
  
  @Override
  public double value() {
    return number;
  }
  
}