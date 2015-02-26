package unluac.decompile;

import java.util.HashSet;
import java.util.Set;

import unluac.parse.LBoolean;
import unluac.parse.LNil;
import unluac.parse.LNumber;
import unluac.parse.LObject;
import unluac.parse.LString;

public class Constant {

  private static final Set<String> reservedWords = new HashSet<String>();
  
  static {
    reservedWords.add("and");
    reservedWords.add("break");
    reservedWords.add("do");
    reservedWords.add("else");
    reservedWords.add("elseif");
    reservedWords.add("end");
    reservedWords.add("false");
    reservedWords.add("for");
    reservedWords.add("function");
    reservedWords.add("if");
    reservedWords.add("in");
    reservedWords.add("local");
    reservedWords.add("nil");
    reservedWords.add("not");
    reservedWords.add("or");
    reservedWords.add("repeat");
    reservedWords.add("return");
    reservedWords.add("then");
    reservedWords.add("true");
    reservedWords.add("until");
    reservedWords.add("while");
  }
  
  private final int type;
  
  private final boolean bool;
  private final LNumber number;
  private final String string;
  
  public Constant(int constant) {
    type = 2;
    bool = false;
    number = LNumber.makeInteger(constant);
    string = null;
  }
  
  public Constant(LObject constant) {
    if(constant instanceof LNil) {
      type = 0;
      bool = false;
      number = null;
      string = null;
    } else if(constant instanceof LBoolean) {
      type = 1;
      bool = constant == LBoolean.LTRUE;
      number = null;
      string = null;
    } else if(constant instanceof LNumber) {
      type = 2;
      bool = false;
      number = (LNumber) constant;
      string = null;
    } else if(constant instanceof LString) {
      type = 3;
      bool = false;
      number = null;
      string = ((LString) constant).deref();
    } else {
      throw new IllegalArgumentException("Illegal constant type: " + constant.toString());
    }
  }
  
  public void print(Output out, boolean braced) {
    switch(type) {
      case 0:
        out.print("nil");
        break;
      case 1:
        out.print(bool ? "true" : "false");
        break;
      case 2:
        out.print(number.toString());
        break;
      case 3:
        int newlines = 0;
        int unprintable = 0;
        for(int i = 0; i < string.length(); i++) {
          char c = string.charAt(i);
          if(c == '\n') {
            newlines++;
          } else if((c <= 31 && c != '\t') || c >= 127) {
            unprintable++;
          }
        }
        if(unprintable == 0 && !string.contains("[[") && (newlines > 1 || (newlines == 1 && string.indexOf('\n') != string.length() - 1))) {
          int pipe = 0;
          String pipeString = "]]";
          while(string.indexOf(pipeString) >= 0) {
            pipe++;
            pipeString = "]";
            int i = pipe;
            while(i-- > 0) pipeString += "=";
            pipeString += "]";
          }
          if(braced) out.print("(");
          out.print("[");
          while(pipe-- > 0) out.print("=");
          out.print("[");
          int indent = out.getIndentationLevel();
          out.setIndentationLevel(0);
          out.println();
          out.print(string);
          out.print(pipeString);
          if(braced) out.print(")");
          out.setIndentationLevel(indent);
        } else {
          out.print("\"");
          for(int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if(c <= 31 || c >= 127) {
              if(c == 7) {
                out.print("\\a");
              } else if(c == 8) {
                out.print("\\b");
              } else if(c == 12) {
                out.print("\\f");
              } else if(c == 10) {
                out.print("\\n");
              } else if(c == 13) {
                out.print("\\r");
              } else if(c == 9) {
                out.print("\\t");
              } else if(c == 11) {
                out.print("\\v");
              } else {
                String dec = Integer.toString(c);
                int len = dec.length();
                out.print("\\");
                while(len++ < 3) {
                  out.print("0");
                }
                out.print(dec);
              }
            } else if(c == 34) {
              out.print("\\\"");
            } else if(c == 92) {
              out.print("\\\\");
            } else {
              out.print(Character.toString(c));
            }
          }
          out.print("\"");
        }
        break;
      default:
        throw new IllegalStateException();
    }
  }
  
  public boolean isNil() {
    return type == 0;
  }
  
  public boolean isBoolean() {
    return type == 1;
  }
  
  public boolean isNumber() {
    return type == 2;
  }
  
  public boolean isInteger() {
    return number.value() == Math.round(number.value());
  }
  
  public int asInteger() {
    if(!isInteger()) {
      throw new IllegalStateException();
    }
    return (int) number.value();
  }
  
  public boolean isString() {
    return type == 3;
  }
  
  public boolean isIdentifier() {
    if(!isString()) {
      return false;
    }
    if(reservedWords.contains(string)) {
      return false;
    }
    if(string.length() == 0) {
      return false;
    }
    char start = string.charAt(0);
    if(start != '_' && !Character.isLetter(start)) {
      return false;
    }
    for(int i = 1; i < string.length(); i++) {
      char next = string.charAt(i);
      if(Character.isLetter(next)) {
        continue;
      }
      if(Character.isDigit(next)) {
        continue;
      }
      if(next == '_') {
        continue;
      }
      return false;
    }
    return true;
  }
  
  public String asName() {
    if(type != 3) {
      throw new IllegalStateException();
    }
    return string;
  }
  
}
