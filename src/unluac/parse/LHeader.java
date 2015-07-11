package unluac.parse;

import unluac.decompile.CodeExtract;

public class LHeader extends BObject {

  public final int format;
  public final BIntegerType integer;
  public final BSizeTType sizeT;
  public final LBooleanType bool;
  public final LNumberType number;
  public final LNumberType linteger;
  public final LNumberType lfloat;
  public final LStringType string;
  public final LConstantType constant;
  public final LLocalType local;
  public final LUpvalueType upvalue;
  public final LFunctionType function;
  public final CodeExtract extractor;
  
  public LHeader(int format, BIntegerType integer, BSizeTType sizeT, LBooleanType bool, LNumberType number, LNumberType linteger, LNumberType lfloat, LStringType string, LConstantType constant, LLocalType local, LUpvalueType upvalue, LFunctionType function, CodeExtract extractor) {
    this.format = format;
    this.integer = integer;
    this.sizeT = sizeT;
    this.bool = bool;
    this.number = number;
    this.linteger = linteger;
    this.lfloat = lfloat;
    this.string = string;
    this.constant = constant;
    this.local = local;
    this.upvalue = upvalue;
    this.function = function;
    this.extractor = extractor;
  }
  
}
