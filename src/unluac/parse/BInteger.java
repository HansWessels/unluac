package unluac.parse;

import java.math.BigInteger;

public class BInteger extends BObject {
  
  private final BigInteger big;
  private final int n;
  
  private static BigInteger MAX_INT = null;
  private static BigInteger MIN_INT = null;
  
  public BInteger(BInteger b) {
    this.big = b.big;
    this.n = b.n;
  }
  
  public BInteger(int n) {
    this.big = null;
    this.n = n;
  }
  
  public BInteger(BigInteger big) {
    this.big = big;
    this.n = 0;
    if(MAX_INT == null) {
      MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);
      MIN_INT = BigInteger.valueOf(Integer.MIN_VALUE);
    }
  }

  public int asInt() {
    if(big == null) {
      return n;
    } else if(big.compareTo(MAX_INT) > 0 || big.compareTo(MIN_INT) < 0) {
      throw new IllegalStateException("The size of an integer is outside the range that unluac can handle.");
    } else {
      return big.intValue();
    }
  }
  
  public void iterate(Runnable thunk) {
    if(big == null) {
      int i = n;
      while(i-- != 0) {
        thunk.run();
      }
    } else {
      BigInteger i = big;
      while(i.signum() > 0) {
        thunk.run();
        i = i.subtract(BigInteger.ONE);
      }
    }
  }

}
