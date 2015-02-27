package unluac;

import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import unluac.decompile.Decompiler;
import unluac.decompile.Output;
import unluac.decompile.OutputProvider;
import unluac.parse.BHeader;
import unluac.parse.LFunction;
import unluac.parse.LHeader;

public class Main {

  public static String version = "1.2.2";
  
  public static void main(String[] args) {
    if(args.length == 0 || args.length > 1) {
      System.err.println("unluac v" + version);
      if(args.length == 0) {
        System.err.println("  error: no input file provided");
      } else {
        System.err.println("  error: too many arguments");
      }
      System.err.println("  usage: java -jar unluac.jar <file>");
      System.exit(1);
    } else {
      String fn = args[0];
      LFunction lmain = null;
      try {
        lmain = file_to_function(fn);
      } catch(IOException e) {
        System.err.println("unluac v" + version);
        System.err.print("  error: ");
        System.err.println(e.getMessage());
        System.exit(1);
      }
      Decompiler d = new Decompiler(lmain);
      d.decompile();
      d.print();
      System.exit(0);
    }
  }
  
  private static LFunction file_to_function(String fn) throws IOException {
    RandomAccessFile file = new RandomAccessFile(fn, "r");
    ByteBuffer buffer = ByteBuffer.allocate((int) file.length());
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    int len = (int) file.length();
    FileChannel in = file.getChannel();
    while(len > 0) len -= in.read(buffer);
    buffer.rewind();
    BHeader header = new BHeader(buffer);
    if(header.version == Version.LUA53) {
      int upvalues = 0xFF & buffer.get();
      if(header.debug) {
        System.out.println("-- main chunk upvalue count: " + upvalues);
      }
      // TODO: check this value
    }
    return header.function.parse(buffer, header);
  }
  
  public static void decompile(String in, String out) throws IOException {
    LFunction lmain = file_to_function(in);
    Decompiler d = new Decompiler(lmain);
    d.decompile();
    final PrintStream pout = new PrintStream(out);
    d.print(new Output(new OutputProvider() {

      @Override
      public void print(String s) {
        pout.print(s);
      }

      @Override
      public void println() {
        pout.println();
      }
      
    }));
    pout.flush();
    pout.close();
  }
  
}
