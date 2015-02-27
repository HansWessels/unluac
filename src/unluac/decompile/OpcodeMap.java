package unluac.decompile;

public class OpcodeMap {

  private Op[] map;
  
  public OpcodeMap(int version) {
    if(version == 0x50) {
      map = new Op[35];
      map[0] = Op.MOVE;
      map[1] = Op.LOADK;
      map[2] = Op.LOADBOOL;
      map[3] = Op.LOADNIL;
      map[4] = Op.GETUPVAL;
      map[5] = Op.GETGLOBAL;
      map[6] = Op.GETTABLE;
      map[7] = Op.SETGLOBAL;
      map[8] = Op.SETUPVAL;
      map[9] = Op.SETTABLE;
      map[10] = Op.NEWTABLE50;
      map[11] = Op.SELF;
      map[12] = Op.ADD;
      map[13] = Op.SUB;
      map[14] = Op.MUL;
      map[15] = Op.DIV;
      map[16] = Op.POW;
      map[17] = Op.UNM;
      map[18] = Op.NOT;
      map[19] = Op.CONCAT;
      map[20] = Op.JMP;
      map[21] = Op.EQ;
      map[22] = Op.LT;
      map[23] = Op.LE;
      map[24] = Op.TEST50;
      map[25] = Op.CALL;
      map[26] = Op.TAILCALL;
      map[27] = Op.RETURN;
      map[28] = Op.FORLOOP;
      map[29] = Op.TFORLOOP;
      map[30] = Op.TFORPREP;
      map[31] = Op.SETLIST50;
      map[32] = Op.SETLISTO;
      map[33] = Op.CLOSE;
      map[34] = Op.CLOSURE;
    } else if(version == 0x51) {
      map = new Op[38];
      map[0] = Op.MOVE;
      map[1] = Op.LOADK;
      map[2] = Op.LOADBOOL;
      map[3] = Op.LOADNIL;
      map[4] = Op.GETUPVAL;
      map[5] = Op.GETGLOBAL;
      map[6] = Op.GETTABLE;
      map[7] = Op.SETGLOBAL;
      map[8] = Op.SETUPVAL;
      map[9] = Op.SETTABLE;
      map[10] = Op.NEWTABLE;
      map[11] = Op.SELF;
      map[12] = Op.ADD;
      map[13] = Op.SUB;
      map[14] = Op.MUL;
      map[15] = Op.DIV;
      map[16] = Op.MOD;
      map[17] = Op.POW;
      map[18] = Op.UNM;
      map[19] = Op.NOT;
      map[20] = Op.LEN;
      map[21] = Op.CONCAT;
      map[22] = Op.JMP;
      map[23] = Op.EQ;
      map[24] = Op.LT;
      map[25] = Op.LE;
      map[26] = Op.TEST;
      map[27] = Op.TESTSET;
      map[28] = Op.CALL;
      map[29] = Op.TAILCALL;
      map[30] = Op.RETURN;
      map[31] = Op.FORLOOP;
      map[32] = Op.FORPREP;
      map[33] = Op.TFORLOOP;
      map[34] = Op.SETLIST;
      map[35] = Op.CLOSE;
      map[36] = Op.CLOSURE;
      map[37] = Op.VARARG;
    } else if(version == 0x52) {
      map = new Op[40];
      map[0] = Op.MOVE;
      map[1] = Op.LOADK;
      map[2] = Op.LOADKX;
      map[3] = Op.LOADBOOL;
      map[4] = Op.LOADNIL;
      map[5] = Op.GETUPVAL;
      map[6] = Op.GETTABUP;
      map[7] = Op.GETTABLE;
      map[8] = Op.SETTABUP;
      map[9] = Op.SETUPVAL;
      map[10] = Op.SETTABLE;
      map[11] = Op.NEWTABLE;
      map[12] = Op.SELF;
      map[13] = Op.ADD;
      map[14] = Op.SUB;
      map[15] = Op.MUL;
      map[16] = Op.DIV;
      map[17] = Op.MOD;
      map[18] = Op.POW;
      map[19] = Op.UNM;
      map[20] = Op.NOT;
      map[21] = Op.LEN;
      map[22] = Op.CONCAT;
      map[23] = Op.JMP;
      map[24] = Op.EQ;
      map[25] = Op.LT;
      map[26] = Op.LE;
      map[27] = Op.TEST;
      map[28] = Op.TESTSET;
      map[29] = Op.CALL;
      map[30] = Op.TAILCALL;
      map[31] = Op.RETURN;
      map[32] = Op.FORLOOP;
      map[33] = Op.FORPREP;
      map[34] = Op.TFORCALL;
      map[35] = Op.TFORLOOP;
      map[36] = Op.SETLIST;
      map[37] = Op.CLOSURE;
      map[38] = Op.VARARG;
      map[39] = Op.EXTRAARG;
    } else {
      map = new Op[47];
      map[0] = Op.MOVE;
      map[1] = Op.LOADK;
      map[2] = Op.LOADKX;
      map[3] = Op.LOADBOOL;
      map[4] = Op.LOADNIL;
      map[5] = Op.GETUPVAL;
      map[6] = Op.GETTABUP;
      map[7] = Op.GETTABLE;
      map[8] = Op.SETTABUP;
      map[9] = Op.SETUPVAL;
      map[10] = Op.SETTABLE;
      map[11] = Op.NEWTABLE;
      map[12] = Op.SELF;
      map[13] = Op.ADD;
      map[14] = Op.SUB;
      map[15] = Op.MUL;
      map[16] = Op.MOD;
      map[17] = Op.POW;
      map[18] = Op.DIV;
      map[19] = Op.IDIV;
      map[20] = Op.BAND;
      map[21] = Op.BOR;
      map[22] = Op.BXOR;
      map[23] = Op.SHL;
      map[24] = Op.SHR;
      map[25] = Op.UNM;
      map[26] = Op.BNOT;
      map[27] = Op.NOT;
      map[28] = Op.LEN;
      map[29] = Op.CONCAT;
      map[30] = Op.JMP;
      map[31] = Op.EQ;
      map[32] = Op.LT;
      map[33] = Op.LE;
      map[34] = Op.TEST;
      map[35] = Op.TESTSET;
      map[36] = Op.CALL;
      map[37] = Op.TAILCALL;
      map[38] = Op.RETURN;
      map[39] = Op.FORLOOP;
      map[40] = Op.FORPREP;
      map[41] = Op.TFORCALL;
      map[42] = Op.TFORLOOP;
      map[43] = Op.SETLIST;
      map[44] = Op.CLOSURE;
      map[45] = Op.VARARG;
      map[46] = Op.EXTRAARG;
    }
  }
  
  public Op get(int opNumber) {
    if(opNumber >= 0 && opNumber < map.length) {
      return map[opNumber];
    } else {
      return null;
    }
  }
  
}

