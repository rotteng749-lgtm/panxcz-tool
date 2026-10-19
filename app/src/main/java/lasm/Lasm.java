package lasm;

import android.ext.AddressItem;
import android.ext.Config;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import lasm.LasmBase;
import luaj.LuaLong;
import luaj.LuaValue;
import luaj.Prototype;

/* JADX INFO: loaded from: classes.dex */
public class Lasm extends LasmBase implements LasmConstants {
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private static int[] jj_la1_2;
    private List<int[]> jj_expentries;
    private int[] jj_expentry;
    private int jj_gen;
    SimpleCharStream jj_input_stream;
    private int jj_kind;
    private final int[] jj_la1;
    public Token jj_nt;
    private int jj_ntk;
    public Token token;
    public LasmTokenManager token_source;

    public final Prototype assemble() throws ParseException {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
            case LasmConstants.EOL /* 74 */:
                jj_consume_token(74);
                break;
            default:
                this.jj_la1[0] = this.jj_gen;
                break;
        }
        LasmPrototype p = Code();
        jj_consume_token(0);
        return p;
    }

    public final LasmPrototype Code() throws ParseException {
        LasmPrototype p = new LasmPrototype();
        jj_consume_token(62);
        jj_consume_token(81);
        p.source = parseLuaString(this.token);
        jj_consume_token(74);
        jj_consume_token(63);
        jj_consume_token(79);
        p.linedefined = parseInt(this.token);
        jj_consume_token(74);
        jj_consume_token(64);
        jj_consume_token(79);
        p.lastlinedefined = parseInt(this.token);
        jj_consume_token(74);
        jj_consume_token(65);
        jj_consume_token(79);
        p.numparams = parseIntMax(this.token, 255);
        jj_consume_token(74);
        jj_consume_token(66);
        jj_consume_token(79);
        p.is_vararg = parseIntMax(this.token, 255);
        jj_consume_token(74);
        jj_consume_token(67);
        jj_consume_token(79);
        p.init(this.token);
        jj_consume_token(74);
        while (true) {
            switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                case LasmConstants.UPVAL /* 68 */:
                    UpVal(p);
                    break;
                default:
                    this.jj_la1[1] = this.jj_gen;
                    while (true) {
                        Op(p);
                        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                            case 11:
                            case 12:
                            case 13:
                            case 14:
                            case 15:
                            case 16:
                            case 17:
                            case 18:
                            case 19:
                            case 20:
                            case 21:
                            case 22:
                            case 23:
                            case 24:
                            case 25:
                            case 26:
                            case 27:
                            case 28:
                            case 29:
                            case 30:
                            case 31:
                            case 32:
                            case 33:
                            case 34:
                            case 35:
                            case 36:
                            case 37:
                            case 38:
                            case 39:
                            case 40:
                            case 41:
                            case 42:
                            case 43:
                            case 44:
                            case 45:
                            case 46:
                            case 47:
                            case LasmConstants.SHL /* 48 */:
                            case LasmConstants.SHR /* 49 */:
                            case LasmConstants.OP /* 53 */:
                            case LasmConstants.LINE /* 69 */:
                            case LasmConstants.LOCAL /* 70 */:
                            case LasmConstants.LABEL /* 76 */:
                                break;
                            case 50:
                            case LasmConstants.PROTO /* 51 */:
                            case LasmConstants.GOTO /* 52 */:
                            case LasmConstants.SET_TOP /* 54 */:
                            case LasmConstants.SKIP_NEXT /* 55 */:
                            case LasmConstants.NIL /* 56 */:
                            case LasmConstants.TRUE /* 57 */:
                            case LasmConstants.FALSE /* 58 */:
                            case 59:
                            case 60:
                            case 61:
                            case 62:
                            case 63:
                            case 64:
                            case LasmConstants.NUMPARAMS /* 65 */:
                            case LasmConstants.IS_VARARG /* 66 */:
                            case LasmConstants.MAXSTACKSIZE /* 67 */:
                            case LasmConstants.UPVAL /* 68 */:
                            case LasmConstants.END_LOCAL /* 71 */:
                            case LasmConstants.FUNC /* 72 */:
                            case LasmConstants.END /* 73 */:
                            case LasmConstants.EOL /* 74 */:
                            case LasmConstants.RANGE /* 75 */:
                            default:
                                this.jj_la1[2] = this.jj_gen;
                                while (true) {
                                    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
                                        case LasmConstants.FUNC /* 72 */:
                                            Func(p);
                                            break;
                                        default:
                                            this.jj_la1[3] = this.jj_gen;
                                            return p.build();
                                    }
                                }
                        }
                    }
            }
        }
    }

    public final void Func(LasmPrototype p) throws ParseException {
        jj_consume_token(72);
        Token name = jj_consume_token(61);
        jj_consume_token(74);
        LasmPrototype f = Code();
        p.addFunc(name, f);
        jj_consume_token(73);
        jj_consume_token(74);
    }

    public final LuaValue RK() throws ParseException {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
            case 50:
            case LasmConstants.NIL /* 56 */:
            case LasmConstants.TRUE /* 57 */:
            case LasmConstants.FALSE /* 58 */:
            case LasmConstants.INT /* 79 */:
            case LasmConstants.HEX /* 80 */:
            case LasmConstants.STRING /* 81 */:
            case LasmConstants.FLOAT /* 84 */:
                LuaValue v = K();
                return v;
            case 59:
            case LasmConstants.V /* 77 */:
                LuaValue v2 = R();
                return v2;
            default:
                this.jj_la1[4] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
    }

    public final LuaValue K() throws ParseException {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
            case 50:
                jj_consume_token(50);
                jj_consume_token(88);
                jj_consume_token(79);
                LuaValue k = new LasmBase.Const(this.token);
                jj_consume_token(89);
                return k;
            case LasmConstants.NIL /* 56 */:
                jj_consume_token(56);
                LuaValue k2 = LuaValue.NIL;
                return k2;
            case LasmConstants.TRUE /* 57 */:
                jj_consume_token(57);
                LuaValue k3 = LuaValue.TRUE;
                return k3;
            case LasmConstants.FALSE /* 58 */:
                jj_consume_token(58);
                LuaValue k4 = LuaValue.FALSE;
                return k4;
            case LasmConstants.INT /* 79 */:
                jj_consume_token(79);
                LuaValue k5 = LuaLong.valueOf(parseLong(this.token));
                return k5;
            case LasmConstants.HEX /* 80 */:
                jj_consume_token(80);
                LuaValue k6 = parseLuaNumber(this.token);
                return k6;
            case LasmConstants.STRING /* 81 */:
                jj_consume_token(81);
                LuaValue k7 = parseLuaString(this.token);
                return k7;
            case LasmConstants.FLOAT /* 84 */:
                jj_consume_token(84);
                LuaValue k8 = parseLuaNumber(this.token);
                return k8;
            default:
                this.jj_la1[5] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
    }

    public final LasmBase.Internal RU() throws ParseException {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
            case 59:
            case LasmConstants.V /* 77 */:
                LasmBase.Internal v = R();
                return v;
            case LasmConstants.U /* 78 */:
                LasmBase.Internal v2 = U();
                return v2;
            default:
                this.jj_la1[6] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
    }

    public final LasmBase.U U() throws ParseException {
        jj_consume_token(78);
        return new LasmBase.U(this.token);
    }

    public final LasmBase.V R() throws ParseException {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
            case 59:
                jj_consume_token(59);
                break;
            case LasmConstants.V /* 77 */:
                jj_consume_token(77);
                break;
            default:
                this.jj_la1[7] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        return new LasmBase.V(this.token);
    }

    public final Token Dest() throws ParseException {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
            case LasmConstants.GOTO /* 52 */:
                jj_consume_token(52);
                Token ret = jj_consume_token(60);
                jj_consume_token(89);
                return ret;
            case LasmConstants.LABEL /* 76 */:
                Token ret2 = jj_consume_token(76);
                return ret2;
            default:
                this.jj_la1[8] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
    }

    public final void UpVal(LasmPrototype p) throws ParseException {
        Token name;
        jj_consume_token(68);
        LasmBase.Internal vu = RU();
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
            case LasmConstants.NIL /* 56 */:
                name = jj_consume_token(56);
                break;
            case LasmConstants.STRING /* 81 */:
                name = jj_consume_token(81);
                break;
            default:
                this.jj_la1[9] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        jj_consume_token(74);
        p.addUpval(vu, name);
    }

    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Failed to find switch 'out' block (already processed)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.calcSwitchOut(SwitchRegionMaker.java:217)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.process(SwitchRegionMaker.java:68)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:112)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.makeEndlessLoop(LoopRegionMaker.java:282)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:65)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:89)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeMthRegion(RegionMaker.java:48)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:25)
        */
    public final void Op(lasm.LasmPrototype r12) throws lasm.ParseException {
        /*
            Method dump skipped, instruction units count: 1962
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: lasm.Lasm.Op(lasm.LasmPrototype):void");
    }

    static {
        jj_la1_init_0();
        jj_la1_init_1();
        jj_la1_init_2();
    }

    private static void jj_la1_init_0() {
        int[] iArr = new int[34];
        iArr[2] = -32;
        iArr[16] = 256;
        iArr[17] = 67584;
        iArr[19] = 8273920;
        iArr[20] = 58720288;
        iArr[23] = 1879048192;
        iArr[32] = -32;
        jj_la1_0 = iArr;
    }

    private static void jj_la1_init_1() {
        jj_la1_1 = new int[]{0, 0, 2359295, 0, 251920384, 117702656, 134217728, 134217728, Config.STATE_VIDEO, AddressItem.FLAG_REVERT, 0, 0, 0, 0, 0, AddressItem.FLAG_RANGE, 64, 0, 0, 256000, 4096, 134217728, 176, 0, 0, 138412032, 138412032, 1028, 0, 0, 134217728, 537395200, 2359295, 0};
    }

    private static void jj_la1_init_2() {
        jj_la1_2 = new int[]{1024, 16, 4192, 256, 1286144, 1277952, 24576, 8192, 4096, 131072, 4192, 128, 128, 128, 4192, 0, 0, 0, 2048, 0, 0, 8192, 0, 0, 2048, 8192, 8192, 0, 2048, 2048, 8192, 0, 0, 128};
    }

    public Lasm(InputStream stream) {
        this(stream, null);
    }

    public Lasm(InputStream stream, String encoding) {
        this.jj_la1 = new int[34];
        this.jj_expentries = new ArrayList();
        this.jj_kind = -1;
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
            this.token_source = new LasmTokenManager(this.jj_input_stream);
            this.token = new Token();
            this.jj_ntk = -1;
            this.jj_gen = 0;
            for (int i = 0; i < 34; i++) {
                this.jj_la1[i] = -1;
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void ReInit(InputStream stream) {
        ReInit(stream, null);
    }

    public void ReInit(InputStream stream, String encoding) {
        try {
            this.jj_input_stream.ReInit(stream, encoding, 1, 1);
            this.token_source.ReInit(this.jj_input_stream);
            this.token = new Token();
            this.jj_ntk = -1;
            this.jj_gen = 0;
            for (int i = 0; i < 34; i++) {
                this.jj_la1[i] = -1;
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public Lasm(Reader stream) {
        this.jj_la1 = new int[34];
        this.jj_expentries = new ArrayList();
        this.jj_kind = -1;
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new LasmTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 34; i++) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(Reader stream) {
        this.jj_input_stream.ReInit(stream, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 34; i++) {
            this.jj_la1[i] = -1;
        }
    }

    public Lasm(LasmTokenManager tm) {
        this.jj_la1 = new int[34];
        this.jj_expentries = new ArrayList();
        this.jj_kind = -1;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 34; i++) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(LasmTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 34; i++) {
            this.jj_la1[i] = -1;
        }
    }

    private Token jj_consume_token(int kind) throws ParseException {
        Token oldToken = this.token;
        if (oldToken.next != null) {
            this.token = this.token.next;
        } else {
            Token token = this.token;
            Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        if (this.token.kind == kind) {
            this.jj_gen++;
            return this.token;
        }
        this.token = oldToken;
        this.jj_kind = kind;
        throw generateParseException();
    }

    public final Token getNextToken() {
        if (this.token.next != null) {
            this.token = this.token.next;
        } else {
            Token token = this.token;
            Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        this.jj_gen++;
        return this.token;
    }

    public final Token getToken(int index) {
        Token t;
        Token t2 = this.token;
        int i = 0;
        Token t3 = t2;
        while (i < index) {
            if (t3.next != null) {
                t = t3.next;
            } else {
                t = this.token_source.getNextToken();
                t3.next = t;
            }
            i++;
            t3 = t;
        }
        return t3;
    }

    private int jj_ntk() {
        Token token = this.token.next;
        this.jj_nt = token;
        if (token == null) {
            Token token2 = this.token;
            Token nextToken = this.token_source.getNextToken();
            token2.next = nextToken;
            int i = nextToken.kind;
            this.jj_ntk = i;
            return i;
        }
        int i2 = this.jj_nt.kind;
        this.jj_ntk = i2;
        return i2;
    }

    public ParseException generateParseException() {
        this.jj_expentries.clear();
        boolean[] la1tokens = new boolean[90];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (int i = 0; i < 34; i++) {
            if (this.jj_la1[i] == this.jj_gen) {
                for (int j = 0; j < 32; j++) {
                    if ((jj_la1_0[i] & (1 << j)) != 0) {
                        la1tokens[j] = true;
                    }
                    if ((jj_la1_1[i] & (1 << j)) != 0) {
                        la1tokens[j + 32] = true;
                    }
                    if ((jj_la1_2[i] & (1 << j)) != 0) {
                        la1tokens[j + 64] = true;
                    }
                }
            }
        }
        for (int i2 = 0; i2 < 90; i2++) {
            if (la1tokens[i2]) {
                this.jj_expentry = new int[1];
                this.jj_expentry[0] = i2;
                this.jj_expentries.add(this.jj_expentry);
            }
        }
        int[][] exptokseq = new int[this.jj_expentries.size()][];
        for (int i3 = 0; i3 < this.jj_expentries.size(); i3++) {
            exptokseq[i3] = this.jj_expentries.get(i3);
        }
        return new ParseException(this.token, exptokseq, tokenImage);
    }

    public final void enable_tracing() {
    }

    public final void disable_tracing() {
    }
}
