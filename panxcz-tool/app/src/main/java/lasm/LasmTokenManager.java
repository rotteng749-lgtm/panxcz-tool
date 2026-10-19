package lasm;

import android.ext.AddressItem;
import android.ext.Message;
import java.io.IOException;
import java.io.PrintStream;
import luaj.LuaError;

/* JADX INFO: loaded from: classes.dex */
public class LasmTokenManager implements LasmConstants {
    public static final int[] jjnewLexState;
    public static final String[] jjstrLiteralImages;
    static final long[] jjtoSkip;
    static final long[] jjtoSpecial;
    static final long[] jjtoToken;
    public static final String[] lexStateNames;
    protected char curChar;
    int curLexState;
    public PrintStream debugStream;
    int defaultLexState;
    protected SimpleCharStream input_stream;
    int jjmatchedKind;
    int jjmatchedPos;
    int jjnewStateCnt;
    int jjround;
    private final int[] jjrounds;
    private final int[] jjstateSet;
    static final long[] jjbitVec0 = {-2, -1, -1, -1};
    static final long[] jjbitVec2 = {0, 0, -1, -1};
    static final int[] jjnextStates = {3, 0, 5, 49, 33, 34, 39, 40, 44, 45, 1, 2, 1, 2, 0, 5, 46, 3, 0, 5, 27, 32, 22, 24, 25, 17, 19, 20, 33, 34, 39, 40, 30, 31, 37, 38, 41, 42, 50, 31, 32, 37, 38, 42, 43, 1, 2, 44, 3, 0, 5, 25, 30, 20, 22, 23, 15, 17, 18, 31, 32, 37, 38, 47, 48, 28, 29, 35, 36, 39, 40, 37, 50, 39, 40, 45, 46, 32, 33, 1, 2, 37, 26, 38, 34, 3, 0, 5, 39, 40, 45, 46, 29, 30, 43, 44, 51, 35, 36, 41, 42, 46, 47, 3, 4, 3, 4, 2, 7, 48, 5, 2, 7, 29, 34, 24, 26, 27, 19, 21, 22, 5, 2, 7, 35, 36, 41, 42, 32, 33};

    public void setDebugStream(PrintStream ds) {
        this.debugStream = ds;
    }

    private final int jjStopStringLiteralDfa_3(int pos, long active0, long active1) {
        switch (pos) {
            case 0:
                if (((-4611686018427387904L) & active0) == 0 && (3071 & active1) == 0) {
                    if ((14 & active0) != 0) {
                    }
                }
                break;
        }
        return -1;
    }

    private final int jjStartNfa_3(int pos, long active0, long active1) {
        return jjMoveNfa_3(jjStopStringLiteralDfa_3(pos, active0, active1), pos + 1);
    }

    private int jjStopAtPos(int pos, int kind) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        return pos + 1;
    }

    private int jjMoveStringLiteralDfa0_3() {
        switch (this.curChar) {
            case '\t':
                return jjStartNfaWithStates_3(0, 2, 50);
            case '\f':
                return jjStartNfaWithStates_3(0, 3, 50);
            case ' ':
                return jjStartNfaWithStates_3(0, 1, 50);
            case '.':
                return jjMoveStringLiteralDfa1_3(-4611686018427387904L, 3071L);
            case LasmConstants.NUMPARAMS /* 65 */:
                return jjMoveStringLiteralDfa1_3(131072L, 0L);
            case LasmConstants.IS_VARARG /* 66 */:
                return jjMoveStringLiteralDfa1_3(263882790666240L, 0L);
            case LasmConstants.MAXSTACKSIZE /* 67 */:
                return jjMoveStringLiteralDfa1_3(1128107587141632L, 0L);
            case LasmConstants.UPVAL /* 68 */:
                return jjMoveStringLiteralDfa1_3(1048576L, 0L);
            case LasmConstants.LINE /* 69 */:
                return jjMoveStringLiteralDfa1_3(268435456L, 0L);
            case LasmConstants.LOCAL /* 70 */:
                return jjMoveStringLiteralDfa1_3(2252005972115456L, 0L);
            case LasmConstants.END_LOCAL /* 71 */:
                return jjMoveStringLiteralDfa1_3(4503599627374080L, 0L);
            case LasmConstants.END /* 73 */:
                return jjMoveStringLiteralDfa1_3(8796093022208L, 0L);
            case LasmConstants.EOL /* 74 */:
                return jjMoveStringLiteralDfa1_3(134217728L, 0L);
            case LasmConstants.LABEL /* 76 */:
                return jjMoveStringLiteralDfa1_3(1644167616L, 0L);
            case LasmConstants.V /* 77 */:
                return jjMoveStringLiteralDfa1_3(2621472L, 0L);
            case LasmConstants.U /* 78 */:
                return jjMoveStringLiteralDfa1_3(16809984L, 0L);
            case LasmConstants.INT /* 79 */:
                return jjMoveStringLiteralDfa1_3(9007199254740992L, 0L);
            case LasmConstants.HEX /* 80 */:
                return jjMoveStringLiteralDfa1_3(4194304L, 0L);
            case 'R':
                return jjMoveStringLiteralDfa1_3(34359738368L, 0L);
            case LasmConstants.NL /* 83 */:
                return jjMoveStringLiteralDfa1_3(54888719970562048L, 0L);
            case LasmConstants.FLOAT /* 84 */:
                return jjMoveStringLiteralDfa1_3(848256040960L, 0L);
            case LasmConstants.DIGIT /* 85 */:
                return jjMoveStringLiteralDfa1_3(8388608L, 0L);
            case LasmConstants.EXP /* 86 */:
                return jjMoveStringLiteralDfa1_3(4398046511104L, 0L);
            case '[':
                return jjStopAtPos(0, 88);
            case ']':
                return jjStopAtPos(0, 89);
            case 'f':
                return jjMoveStringLiteralDfa1_3(288230376151711744L, 0L);
            case 'n':
                return jjMoveStringLiteralDfa1_3(72057594037927936L, 0L);
            case 't':
                return jjMoveStringLiteralDfa1_3(144115188075855872L, 0L);
            default:
                return jjMoveNfa_3(6, 0);
        }
    }

    private int jjMoveStringLiteralDfa1_3(long active0, long active1) {
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case '.':
                    if ((2048 & active1) != 0) {
                        return jjStopAtPos(1, 75);
                    }
                    break;
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa2_3(active0, 39608188403712L, active1, 0L);
                case LasmConstants.UPVAL /* 68 */:
                    return jjMoveStringLiteralDfa2_3(active0, 8796093153280L, active1, 0L);
                case LasmConstants.LINE /* 69 */:
                    if ((1073741824 & active0) != 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 1;
                    }
                    return jjMoveStringLiteralDfa2_3(active0, 18015538856984064L, active1, 0L);
                case LasmConstants.LOCAL /* 70 */:
                    return jjMoveStringLiteralDfa2_3(active0, 824633720832L, active1, 0L);
                case LasmConstants.FUNC /* 72 */:
                    return jjMoveStringLiteralDfa2_3(active0, 844424930131968L, active1, 0L);
                case LasmConstants.END /* 73 */:
                    return jjMoveStringLiteralDfa2_3(active0, 1048576L, active1, 0L);
                case LasmConstants.RANGE /* 75 */:
                    return jjMoveStringLiteralDfa2_3(active0, 36028797018963968L, active1, 0L);
                case LasmConstants.LABEL /* 76 */:
                    return jjMoveStringLiteralDfa2_3(active0, 2199023255552L, active1, 0L);
                case LasmConstants.V /* 77 */:
                    return jjMoveStringLiteralDfa2_3(active0, 134217728L, active1, 0L);
                case LasmConstants.U /* 78 */:
                    return jjMoveStringLiteralDfa2_3(active0, 17592194433024L, active1, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa2_3(active0, 5700074526999008L, active1, 0L);
                case LasmConstants.HEX /* 80 */:
                    if ((9007199254740992L & active0) != 0) {
                        return jjStopAtPos(1, 53);
                    }
                    break;
                case LasmConstants.STRING /* 81 */:
                    if ((268435456 & active0) != 0) {
                        return jjStopAtPos(1, 28);
                    }
                    break;
                case LasmConstants.FLOAT /* 84 */:
                    if ((536870912 & active0) != 0) {
                        return jjStopAtPos(1, 29);
                    }
                    break;
                case LasmConstants.DIGIT /* 85 */:
                    return jjMoveStringLiteralDfa2_3(active0, 2251799814471680L, active1, 0L);
                case 'X':
                    return jjMoveStringLiteralDfa2_3(active0, 140737488355328L, active1, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa2_3(active0, 288230376151711744L, active1, 0L);
                case 'e':
                    return jjMoveStringLiteralDfa2_3(active0, 0L, active1, 640L);
                case 'f':
                    return jjMoveStringLiteralDfa2_3(active0, 0L, active1, 256L);
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa2_3(active0, 72057594037927936L, active1, 4L);
                case Message.ERROR_GROUP_FAIL /* 108 */:
                    return jjMoveStringLiteralDfa2_3(active0, Long.MIN_VALUE, active1, 97L);
                case 'm':
                    return jjMoveStringLiteralDfa2_3(active0, 0L, active1, 8L);
                case 'n':
                    return jjMoveStringLiteralDfa2_3(active0, 0L, active1, 2L);
                case 'r':
                    return jjMoveStringLiteralDfa2_3(active0, 144115188075855872L, active1, 0L);
                case 's':
                    return jjMoveStringLiteralDfa2_3(active0, 4611686018427387904L, active1, 0L);
                case 'u':
                    return jjMoveStringLiteralDfa2_3(active0, 0L, active1, 16L);
            }
            return jjStartNfa_3(0, active0, active1);
        } catch (IOException e) {
            jjStopStringLiteralDfa_3(0, active0, active1);
            return 1;
        }
    }

    private int jjMoveStringLiteralDfa2_3(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_3(0, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa3_3(active02, 448L, active12, 0L);
                case LasmConstants.IS_VARARG /* 66 */:
                    if ((262144 & active02) != 0) {
                        return jjStopAtPos(2, 18);
                    }
                    break;
                case LasmConstants.UPVAL /* 68 */:
                    if ((131072 & active02) != 0) {
                        return jjStopAtPos(2, 17);
                    }
                    if ((2097152 & active02) != 0) {
                        return jjStopAtPos(2, 21);
                    }
                    break;
                case LasmConstants.END /* 73 */:
                    return jjMoveStringLiteralDfa3_3(active02, 36037610291855360L, active12, 0L);
                case LasmConstants.LABEL /* 76 */:
                    if ((524288 & active02) != 0) {
                        return jjStopAtPos(2, 19);
                    }
                    if ((281474976710656L & active02) != 0) {
                        return jjStopAtPos(2, 48);
                    }
                    return jjMoveStringLiteralDfa3_3(active02, 8590000128L, active12, 0L);
                case LasmConstants.V /* 77 */:
                    if ((8388608 & active02) != 0) {
                        return jjStopAtPos(2, 23);
                    }
                    break;
                case LasmConstants.U /* 78 */:
                    if ((33554432 & active02) != 0) {
                        return jjStopAtPos(2, 25);
                    }
                    return jjMoveStringLiteralDfa3_3(active02, 3412884159725568L, active12, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa3_3(active02, 161353331376128L, active12, 0L);
                case LasmConstants.HEX /* 80 */:
                    if ((134217728 & active02) != 0) {
                        return jjStopAtPos(2, 27);
                    }
                    break;
                case 'R':
                    if ((70368744177664L & active02) != 0) {
                        return jjStopAtPos(2, 46);
                    }
                    if ((562949953421312L & active02) != 0) {
                        return jjStopAtPos(2, 49);
                    }
                    return jjMoveStringLiteralDfa3_3(active02, 4604204941312L, active12, 0L);
                case LasmConstants.NL /* 83 */:
                    return jjMoveStringLiteralDfa3_3(active02, 6442450944L, active12, 0L);
                case LasmConstants.FLOAT /* 84 */:
                    if ((16777216 & active02) != 0) {
                        return jjStopAtPos(2, 24);
                    }
                    return jjMoveStringLiteralDfa3_3(active02, 22519132008250880L, active12, 0L);
                case LasmConstants.EXP /* 86 */:
                    if ((1048576 & active02) != 0) {
                        return jjStopAtPos(2, 20);
                    }
                    return jjMoveStringLiteralDfa3_3(active02, 32L, active12, 0L);
                case LasmConstants.HEXDIGIT /* 87 */:
                    if ((4194304 & active02) != 0) {
                        return jjStopAtPos(2, 22);
                    }
                    return jjMoveStringLiteralDfa3_3(active02, 32768L, active12, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa3_3(active02, 0L, active12, 9L);
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa3_3(active02, Long.MIN_VALUE, active12, 32L);
                case Message.ERROR_GROUP_FAIL /* 108 */:
                    if ((72057594037927936L & active02) != 0) {
                        return jjStopAtPos(2, 56);
                    }
                    return jjMoveStringLiteralDfa3_3(active02, 288230376151711744L, active12, 0L);
                case 'n':
                    return jjMoveStringLiteralDfa3_3(active02, 0L, active12, 640L);
                case 'o':
                    return jjMoveStringLiteralDfa3_3(active02, 4611686018427387904L, active12, 64L);
                case 'p':
                    return jjMoveStringLiteralDfa3_3(active02, 0L, active12, 16L);
                case 's':
                    return jjMoveStringLiteralDfa3_3(active02, 0L, active12, 4L);
                case 'u':
                    return jjMoveStringLiteralDfa3_3(active02, 144115188075855872L, active12, 258L);
            }
            return jjStartNfa_3(1, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_3(1, active02, active12);
            return 2;
        }
    }

    private int jjMoveStringLiteralDfa3_3(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_3(1, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa4_3(active02, 4398046511104L, active12, 0L);
                case LasmConstants.MAXSTACKSIZE /* 67 */:
                    return jjMoveStringLiteralDfa4_3(active02, 2251799880794112L, active12, 0L);
                case LasmConstants.UPVAL /* 68 */:
                    if ((35184372088832L & active02) != 0) {
                        return jjStopAtPos(3, 45);
                    }
                    return jjMoveStringLiteralDfa4_3(active02, 448L, active12, 0L);
                case LasmConstants.LINE /* 69 */:
                    if ((32 & active02) != 0) {
                        return jjStopAtPos(3, 5);
                    }
                    break;
                case LasmConstants.LOCAL /* 70 */:
                    if ((65536 & active02) != 0) {
                        return jjStopAtPos(3, 16);
                    }
                    break;
                case LasmConstants.LABEL /* 76 */:
                    if ((8589934592L & active02) != 0) {
                        return jjStopAtPos(3, 33);
                    }
                    return jjMoveStringLiteralDfa4_3(active02, 1185410973696L, active12, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa4_3(active02, 4503599627370496L, active12, 0L);
                case LasmConstants.HEX /* 80 */:
                    return jjMoveStringLiteralDfa4_3(active02, 36028934457917440L, active12, 0L);
                case 'R':
                    if ((140737488355328L & active02) != 0) {
                        return jjStopAtPos(3, 47);
                    }
                    return jjMoveStringLiteralDfa4_3(active02, 824633720832L, active12, 0L);
                case LasmConstants.NL /* 83 */:
                    return jjMoveStringLiteralDfa4_3(active02, 1128098930098176L, active12, 0L);
                case LasmConstants.FLOAT /* 84 */:
                    if ((2147483648L & active02) != 0) {
                        this.jjmatchedKind = 31;
                        this.jjmatchedPos = 3;
                    } else if ((17592186044416L & active02) != 0) {
                        return jjStopAtPos(3, 44);
                    }
                    return jjMoveStringLiteralDfa4_3(active02, 4295023616L, active12, 0L);
                case LasmConstants.DIGIT /* 85 */:
                    return jjMoveStringLiteralDfa4_3(active02, 34359747072L, active12, 0L);
                case LasmConstants.EXP /* 86 */:
                    if ((8796093022208L & active02) != 0) {
                        return jjStopAtPos(3, 43);
                    }
                    break;
                case '_':
                    return jjMoveStringLiteralDfa4_3(active02, 18014398509481984L, active12, 4L);
                case 'c':
                    return jjMoveStringLiteralDfa4_3(active02, 0L, active12, 64L);
                case Message.ERROR_FATAL /* 100 */:
                    if ((512 & active12) != 0) {
                        this.jjmatchedKind = 73;
                        this.jjmatchedPos = 3;
                    }
                    return jjMoveStringLiteralDfa4_3(active02, 0L, active12, 128L);
                case 'e':
                    if ((144115188075855872L & active02) != 0) {
                        return jjStopAtPos(3, 57);
                    }
                    break;
                case 'm':
                    return jjMoveStringLiteralDfa4_3(active02, 0L, active12, 2L);
                case 'n':
                    return jjMoveStringLiteralDfa4_3(active02, Long.MIN_VALUE, active12, 288L);
                case 's':
                    return jjMoveStringLiteralDfa4_3(active02, 288230376151711744L, active12, 1L);
                case 'u':
                    return jjMoveStringLiteralDfa4_3(active02, 4611686018427387904L, active12, 0L);
                case 'v':
                    return jjMoveStringLiteralDfa4_3(active02, 0L, active12, 16L);
                case 'x':
                    return jjMoveStringLiteralDfa4_3(active02, 0L, active12, 8L);
            }
            return jjStartNfa_3(2, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_3(2, active02, active12);
            return 3;
        }
    }

    private int jjMoveStringLiteralDfa4_3(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_3(2, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case ' ':
                    return jjMoveStringLiteralDfa5_3(active02, 0L, active12, 128L);
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa5_3(active02, 67165184L, active12, 0L);
                case LasmConstants.IS_VARARG /* 66 */:
                    return jjMoveStringLiteralDfa5_3(active02, 128L, active12, 0L);
                case LasmConstants.MAXSTACKSIZE /* 67 */:
                    return jjMoveStringLiteralDfa5_3(active02, 292057776128L, active12, 0L);
                case LasmConstants.END /* 73 */:
                    return jjMoveStringLiteralDfa5_3(active02, 1099511627776L, active12, 0L);
                case LasmConstants.RANGE /* 75 */:
                    if ((64 & active02) != 0) {
                        return jjStopAtPos(4, 6);
                    }
                    break;
                case LasmConstants.LABEL /* 76 */:
                    return jjMoveStringLiteralDfa5_3(active02, 549755813888L, active12, 0L);
                case LasmConstants.U /* 78 */:
                    return jjMoveStringLiteralDfa5_3(active02, 256L, active12, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa5_3(active02, 68719476736L, active12, 0L);
                case LasmConstants.HEX /* 80 */:
                    return jjMoveStringLiteralDfa5_3(active02, 8704L, active12, 0L);
                case 'R':
                    return jjMoveStringLiteralDfa5_3(active02, 4569845202944L, active12, 0L);
                case LasmConstants.NL /* 83 */:
                    return jjMoveStringLiteralDfa5_3(active02, 4294967296L, active12, 0L);
                case LasmConstants.FLOAT /* 84 */:
                    if ((1125899906842624L & active02) != 0) {
                        return jjStopAtPos(4, 50);
                    }
                    return jjMoveStringLiteralDfa5_3(active02, 18014398509481984L, active12, 0L);
                case LasmConstants.DIGIT /* 85 */:
                    return jjMoveStringLiteralDfa5_3(active02, 2199023255552L, active12, 0L);
                case '[':
                    if ((2251799813685248L & active02) != 0) {
                        return jjStopAtPos(4, 51);
                    }
                    if ((4503599627370496L & active02) != 0) {
                        return jjStopAtPos(4, 52);
                    }
                    break;
                case '_':
                    return jjMoveStringLiteralDfa5_3(active02, 36028797018963968L, active12, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa5_3(active02, 0L, active12, 80L);
                case 'c':
                    if ((256 & active12) != 0) {
                        return jjStopAtPos(4, 72);
                    }
                    break;
                case 'e':
                    if ((288230376151711744L & active02) != 0) {
                        return jjStopAtPos(4, 58);
                    }
                    if ((32 & active12) != 0) {
                        this.jjmatchedKind = 69;
                        this.jjmatchedPos = 4;
                    }
                    return jjMoveStringLiteralDfa5_3(active02, Long.MIN_VALUE, active12, 0L);
                case 'p':
                    return jjMoveStringLiteralDfa5_3(active02, 0L, active12, 2L);
                case 'r':
                    return jjMoveStringLiteralDfa5_3(active02, 4611686018427387904L, active12, 0L);
                case 's':
                    return jjMoveStringLiteralDfa5_3(active02, 0L, active12, 8L);
                case 't':
                    return jjMoveStringLiteralDfa5_3(active02, 0L, active12, 1L);
                case 'v':
                    return jjMoveStringLiteralDfa5_3(active02, 0L, active12, 4L);
            }
            return jjStartNfa_3(3, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_3(3, active02, active12);
            return 4;
        }
    }

    private int jjMoveStringLiteralDfa5_3(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_3(3, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa6_3(active02, 292057776128L, active12, 0L);
                case LasmConstants.IS_VARARG /* 66 */:
                    return jjMoveStringLiteralDfa6_3(active02, 56320L, active12, 0L);
                case LasmConstants.LINE /* 69 */:
                    return jjMoveStringLiteralDfa6_3(active02, 141733920768L, active12, 0L);
                case LasmConstants.END_LOCAL /* 71 */:
                    if ((4398046511104L & active02) != 0) {
                        return jjStopAtPos(5, 42);
                    }
                    break;
                case LasmConstants.END /* 73 */:
                    return jjMoveStringLiteralDfa6_3(active02, 256L, active12, 0L);
                case LasmConstants.U /* 78 */:
                    if ((34359738368L & active02) != 0) {
                        return jjStopAtPos(5, 35);
                    }
                    return jjMoveStringLiteralDfa6_3(active02, 36028797018963968L, active12, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa6_3(active02, 18015016984772736L, active12, 0L);
                case 'R':
                    return jjMoveStringLiteralDfa6_3(active02, 2199023255552L, active12, 0L);
                case LasmConstants.NL /* 83 */:
                    return jjMoveStringLiteralDfa6_3(active02, 1099511627776L, active12, 0L);
                case LasmConstants.FLOAT /* 84 */:
                    if ((67108864 & active02) != 0) {
                        return jjStopAtPos(5, 26);
                    }
                    break;
                case LasmConstants.EXP /* 86 */:
                    return jjMoveStringLiteralDfa6_3(active02, 8704L, active12, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa6_3(active02, 0L, active12, 6L);
                case 'c':
                    return jjMoveStringLiteralDfa6_3(active02, 4611686018427387904L, active12, 0L);
                case Message.ERROR_FATAL /* 100 */:
                    return jjMoveStringLiteralDfa6_3(active02, Long.MIN_VALUE, active12, 0L);
                case Message.ERROR_GROUP_FAIL /* 108 */:
                    if ((16 & active12) != 0) {
                        return jjStopAtPos(5, 68);
                    }
                    if ((64 & active12) != 0) {
                        return jjStopAtPos(5, 70);
                    }
                    return jjMoveStringLiteralDfa6_3(active02, 0L, active12, 129L);
                case 't':
                    return jjMoveStringLiteralDfa6_3(active02, 0L, active12, 8L);
            }
            return jjStartNfa_3(4, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_3(4, active02, active12);
            return 5;
        }
    }

    private int jjMoveStringLiteralDfa6_3(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_3(4, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa7_3(active02, 8704L, active12, 0L);
                case LasmConstants.LINE /* 69 */:
                    if ((2199023255552L & active02) != 0) {
                        return jjStopAtPos(6, 41);
                    }
                    return jjMoveStringLiteralDfa7_3(active02, 36028797018963968L, active12, 0L);
                case LasmConstants.LABEL /* 76 */:
                    if ((256 & active02) != 0) {
                        return jjStopAtPos(6, 8);
                    }
                    return jjMoveStringLiteralDfa7_3(active02, 292057827328L, active12, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa7_3(active02, 549755814016L, active12, 0L);
                case LasmConstants.HEX /* 80 */:
                    if ((68719476736L & active02) != 0) {
                        return jjStopAtPos(6, 36);
                    }
                    if ((137438953472L & active02) != 0) {
                        return jjStopAtPos(6, 37);
                    }
                    if ((18014398509481984L & active02) != 0) {
                        return jjStopAtPos(6, 54);
                    }
                    break;
                case LasmConstants.FLOAT /* 84 */:
                    if ((4294967296L & active02) != 0) {
                        return jjStopAtPos(6, 32);
                    }
                    if ((1099511627776L & active02) != 0) {
                        return jjStopAtPos(6, 40);
                    }
                    break;
                case LasmConstants.DIGIT /* 85 */:
                    return jjMoveStringLiteralDfa7_3(active02, 5120L, active12, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa7_3(active02, 0L, active12, 8L);
                case 'e':
                    if ((4611686018427387904L & active02) != 0) {
                        return jjStopAtPos(6, 62);
                    }
                    return jjMoveStringLiteralDfa7_3(active02, Long.MIN_VALUE, active12, 0L);
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa7_3(active02, 0L, active12, 1L);
                case 'o':
                    return jjMoveStringLiteralDfa7_3(active02, 0L, active12, 128L);
                case 'r':
                    return jjMoveStringLiteralDfa7_3(active02, 0L, active12, 6L);
            }
            return jjStartNfa_3(5, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_3(5, active02, active12);
            return 6;
        }
    }

    private int jjMoveStringLiteralDfa7_3(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_3(5, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.LINE /* 69 */:
                    if ((2048 & active02) != 0) {
                        return jjStopAtPos(7, 11);
                    }
                    if ((16384 & active02) != 0) {
                        return jjStopAtPos(7, 14);
                    }
                    if ((32768 & active02) != 0) {
                        return jjStopAtPos(7, 15);
                    }
                    break;
                case LasmConstants.LABEL /* 76 */:
                    if ((128 & active02) != 0) {
                        return jjStopAtPos(7, 7);
                    }
                    if ((512 & active02) != 0) {
                        return jjStopAtPos(7, 9);
                    }
                    if ((8192 & active02) != 0) {
                        return jjStopAtPos(7, 13);
                    }
                    if ((17179869184L & active02) != 0) {
                        return jjStopAtPos(7, 34);
                    }
                    if ((274877906944L & active02) != 0) {
                        return jjStopAtPos(7, 38);
                    }
                    break;
                case LasmConstants.HEX /* 80 */:
                    if ((1024 & active02) != 0) {
                        return jjStopAtPos(7, 10);
                    }
                    if ((4096 & active02) != 0) {
                        return jjStopAtPos(7, 12);
                    }
                    if ((549755813888L & active02) != 0) {
                        return jjStopAtPos(7, 39);
                    }
                    break;
                case 'X':
                    return jjMoveStringLiteralDfa8_3(active02, 36028797018963968L, active12, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa8_3(active02, 0L, active12, 6L);
                case 'c':
                    return jjMoveStringLiteralDfa8_3(active02, 0L, active12, 136L);
                case 'f':
                    return jjMoveStringLiteralDfa8_3(active02, Long.MIN_VALUE, active12, 0L);
                case 'n':
                    return jjMoveStringLiteralDfa8_3(active02, 0L, active12, 1L);
            }
            return jjStartNfa_3(6, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_3(6, active02, active12);
            return 7;
        }
    }

    private int jjMoveStringLiteralDfa8_3(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_3(6, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.FLOAT /* 84 */:
                    if ((36028797018963968L & active02) != 0) {
                        return jjStopAtPos(8, 55);
                    }
                    break;
                case 'a':
                    return jjMoveStringLiteralDfa9_3(active02, 0L, active12, 128L);
                case 'e':
                    return jjMoveStringLiteralDfa9_3(active02, 0L, active12, 1L);
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa9_3(active02, Long.MIN_VALUE, active12, 0L);
                case Message.ERROR_SPEEDHACK_FAIL /* 107 */:
                    return jjMoveStringLiteralDfa9_3(active02, 0L, active12, 8L);
                case 'm':
                    return jjMoveStringLiteralDfa9_3(active02, 0L, active12, 2L);
                case 'r':
                    return jjMoveStringLiteralDfa9_3(active02, 0L, active12, 4L);
            }
            return jjStartNfa_3(7, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_3(7, active02, active12);
            return 8;
        }
    }

    private int jjMoveStringLiteralDfa9_3(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_3(7, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Message.ERROR_FATAL /* 100 */:
                    return jjMoveStringLiteralDfa10_3(active02, 0L, active12, 1L);
                case 'g':
                    if ((4 & active12) != 0) {
                        return jjStopAtPos(9, 66);
                    }
                    break;
                case Message.ERROR_GROUP_FAIL /* 108 */:
                    if ((128 & active12) != 0) {
                        return jjStopAtPos(9, 71);
                    }
                    break;
                case 'n':
                    return jjMoveStringLiteralDfa10_3(active02, Long.MIN_VALUE, active12, 0L);
                case 's':
                    if ((2 & active12) != 0) {
                        return jjStopAtPos(9, 65);
                    }
                    return jjMoveStringLiteralDfa10_3(active02, 0L, active12, 8L);
            }
            return jjStartNfa_3(8, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_3(8, active02, active12);
            return 9;
        }
    }

    private int jjMoveStringLiteralDfa10_3(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_3(8, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'e':
                    return jjMoveStringLiteralDfa11_3(active02, Long.MIN_VALUE, active12, 1L);
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa11_3(active02, 0L, active12, 8L);
                default:
                    return jjStartNfa_3(9, active02, active12);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_3(9, active02, active12);
            return 10;
        }
    }

    private int jjMoveStringLiteralDfa11_3(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_3(9, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Message.ERROR_FATAL /* 100 */:
                    if ((Long.MIN_VALUE & active02) != 0) {
                        return jjStopAtPos(11, 63);
                    }
                    break;
                case 'f':
                    return jjMoveStringLiteralDfa12_3(active02, 0L, active12, 1L);
                case 'z':
                    return jjMoveStringLiteralDfa12_3(active02, 0L, active12, 8L);
            }
            return jjStartNfa_3(10, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_3(10, active02, active12);
            return 11;
        }
    }

    private int jjMoveStringLiteralDfa12_3(long old0, long active0, long old1, long active1) {
        long active12 = active1 & old1;
        if (((active0 & old0) | active12) == 0) {
            return jjStartNfa_3(10, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'e':
                    if ((8 & active12) != 0) {
                        return jjStopAtPos(12, 67);
                    }
                    break;
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa13_3(active12, 1L);
            }
            return jjStartNfa_3(11, 0L, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_3(11, 0L, active12);
            return 12;
        }
    }

    private int jjMoveStringLiteralDfa13_3(long old1, long active1) {
        long active12 = active1 & old1;
        if (active12 == 0) {
            return jjStartNfa_3(11, 0L, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'n':
                    return jjMoveStringLiteralDfa14_3(active12, 1L);
                default:
                    return jjStartNfa_3(12, 0L, active12);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_3(12, 0L, active12);
            return 13;
        }
    }

    private int jjMoveStringLiteralDfa14_3(long old1, long active1) {
        long active12 = active1 & old1;
        if (active12 == 0) {
            return jjStartNfa_3(12, 0L, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'e':
                    return jjMoveStringLiteralDfa15_3(active12, 1L);
                default:
                    return jjStartNfa_3(13, 0L, active12);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_3(13, 0L, active12);
            return 14;
        }
    }

    private int jjMoveStringLiteralDfa15_3(long old1, long active1) {
        long active12 = active1 & old1;
        if (active12 == 0) {
            return jjStartNfa_3(13, 0L, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Message.ERROR_FATAL /* 100 */:
                    if ((1 & active12) != 0) {
                        return jjStopAtPos(15, 64);
                    }
                    break;
            }
            return jjStartNfa_3(14, 0L, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_3(14, 0L, active12);
            return 15;
        }
    }

    private int jjStartNfaWithStates_3(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
            return jjMoveNfa_3(state, pos + 1);
        } catch (IOException e) {
            return pos + 1;
        }
    }

    static {
        String[] strArr = new String[90];
        strArr[0] = "";
        strArr[5] = "MOVE";
        strArr[6] = "LOADK";
        strArr[7] = "LOADBOOL";
        strArr[8] = "LOADNIL";
        strArr[9] = "GETUPVAL";
        strArr[10] = "GETTABUP";
        strArr[11] = "GETTABLE";
        strArr[12] = "SETTABUP";
        strArr[13] = "SETUPVAL";
        strArr[14] = "SETTABLE";
        strArr[15] = "NEWTABLE";
        strArr[16] = "SELF";
        strArr[17] = "ADD";
        strArr[18] = "SUB";
        strArr[19] = "MUL";
        strArr[20] = "DIV";
        strArr[21] = "MOD";
        strArr[22] = "POW";
        strArr[23] = "UNM";
        strArr[24] = "NOT";
        strArr[25] = "LEN";
        strArr[26] = "CONCAT";
        strArr[27] = "JMP";
        strArr[28] = "EQ";
        strArr[29] = "LT";
        strArr[30] = "LE";
        strArr[31] = "TEST";
        strArr[32] = "TESTSET";
        strArr[33] = "CALL";
        strArr[34] = "TAILCALL";
        strArr[35] = "RETURN";
        strArr[36] = "FORLOOP";
        strArr[37] = "FORPREP";
        strArr[38] = "TFORCALL";
        strArr[39] = "TFORLOOP";
        strArr[40] = "SETLIST";
        strArr[41] = "CLOSURE";
        strArr[42] = "VARARG";
        strArr[43] = "IDIV";
        strArr[44] = "BNOT";
        strArr[45] = "BAND";
        strArr[46] = "BOR";
        strArr[47] = "BXOR";
        strArr[48] = "SHL";
        strArr[49] = "SHR";
        strArr[50] = "CONST";
        strArr[51] = "FUNC[";
        strArr[52] = "GOTO[";
        strArr[53] = "OP";
        strArr[54] = "SET_TOP";
        strArr[55] = "SKIP_NEXT";
        strArr[56] = "nil";
        strArr[57] = "true";
        strArr[58] = "false";
        strArr[62] = ".source";
        strArr[63] = ".linedefined";
        strArr[64] = ".lastlinedefined";
        strArr[65] = ".numparams";
        strArr[66] = ".is_vararg";
        strArr[67] = ".maxstacksize";
        strArr[68] = ".upval";
        strArr[69] = ".line";
        strArr[70] = ".local";
        strArr[71] = ".end local";
        strArr[72] = ".func";
        strArr[73] = ".end";
        strArr[75] = "..";
        strArr[88] = "[";
        strArr[89] = "]";
        jjstrLiteralImages = strArr;
        lexStateNames = new String[]{"STATE_NAME", "STATE_SINT", "STATE_V_NAME", "DEFAULT"};
        jjnewLexState = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1, -1, -1, -1, -1, 0, 3, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, 3, -1, -1, -1, -1, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        jjtoToken = new long[]{-31, 51642367};
        jjtoSkip = new long[]{30, 0};
        jjtoSpecial = new long[]{16, 0};
    }

    private int jjMoveNfa_3(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 50;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            int i2 = this.jjround + 1;
            this.jjround = i2;
            if (i2 == Integer.MAX_VALUE) {
                ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1 << this.curChar;
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 0:
                            if ((9216 & l) != 0) {
                                if (kind > 74) {
                                    kind = 74;
                                }
                                jjCheckNAddStates(12, 15);
                            }
                            break;
                        case 1:
                            if ((4294971904L & l) != 0) {
                                jjCheckNAddTwoStates(1, 2);
                            }
                            break;
                        case 2:
                            if (this.curChar == ';') {
                                jjCheckNAddStates(0, 2);
                            }
                            break;
                        case 3:
                            if (((-9217) & l) != 0) {
                                jjCheckNAddStates(0, 2);
                            }
                            break;
                        case 4:
                            if (this.curChar == '\n') {
                                if (kind > 74) {
                                    kind = 74;
                                }
                                jjCheckNAddStates(12, 15);
                            }
                            break;
                        case 5:
                            if (this.curChar == '\r') {
                                int[] iArr = this.jjstateSet;
                                int i3 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i3 + 1;
                                iArr[i3] = 4;
                            }
                            break;
                        case 6:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 79) {
                                    kind = 79;
                                }
                                jjCheckNAddStates(3, 7);
                            } else if ((4294971904L & l) != 0) {
                                jjCheckNAddStates(8, 11);
                            } else if ((9216 & l) != 0) {
                                if (kind > 74) {
                                    kind = 74;
                                }
                                jjCheckNAddStates(12, 15);
                            } else if (this.curChar == ';') {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                jjCheckNAddStates(16, 19);
                            } else if (this.curChar == '.') {
                                jjCheckNAdd(28);
                            } else if (this.curChar == '-') {
                                jjAddStates(20, 21);
                            } else if (this.curChar == '\'') {
                                jjCheckNAddStates(22, 24);
                            } else if (this.curChar == '\"') {
                                jjCheckNAddStates(25, 27);
                            } else if (this.curChar == ':') {
                                int[] iArr2 = this.jjstateSet;
                                int i4 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i4 + 1;
                                iArr2[i4] = 7;
                            }
                            if (this.curChar == '0') {
                                int[] iArr3 = this.jjstateSet;
                                int i5 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i5 + 1;
                                iArr3[i5] = 14;
                            } else if (this.curChar == '\r') {
                                int[] iArr4 = this.jjstateSet;
                                int i6 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i6 + 1;
                                iArr4[i6] = 4;
                            }
                            break;
                        case 8:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 76) {
                                    kind = 76;
                                }
                                int[] iArr5 = this.jjstateSet;
                                int i7 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i7 + 1;
                                iArr5[i7] = 8;
                            }
                            break;
                        case 10:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 77) {
                                    kind = 77;
                                }
                                int[] iArr6 = this.jjstateSet;
                                int i8 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i8 + 1;
                                iArr6[i8] = 10;
                            }
                            break;
                        case 12:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 78) {
                                    kind = 78;
                                }
                                int[] iArr7 = this.jjstateSet;
                                int i9 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i9 + 1;
                                iArr7[i9] = 12;
                            }
                            break;
                        case 13:
                            if (this.curChar == '0') {
                                int[] iArr8 = this.jjstateSet;
                                int i10 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i10 + 1;
                                iArr8[i10] = 14;
                            }
                            break;
                        case 15:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 80) {
                                    kind = 80;
                                }
                                int[] iArr9 = this.jjstateSet;
                                int i11 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i11 + 1;
                                iArr9[i11] = 15;
                            }
                            break;
                        case 16:
                            if (this.curChar == '\"') {
                                jjCheckNAddStates(25, 27);
                            }
                            break;
                        case 18:
                            jjCheckNAddStates(25, 27);
                            break;
                        case 19:
                            if (((-17179869185L) & l) != 0) {
                                jjCheckNAddStates(25, 27);
                            }
                            break;
                        case 20:
                            if (this.curChar == '\"' && kind > 81) {
                                kind = 81;
                            }
                            break;
                        case 21:
                            if (this.curChar == '\'') {
                                jjCheckNAddStates(22, 24);
                            }
                            break;
                        case 23:
                            jjCheckNAddStates(22, 24);
                            break;
                        case 24:
                            if (((-549755813889L) & l) != 0) {
                                jjCheckNAddStates(22, 24);
                            }
                            break;
                        case 25:
                            if (this.curChar == '\'' && kind > 81) {
                                kind = 81;
                            }
                            break;
                        case 26:
                            if (this.curChar == '-') {
                                jjAddStates(20, 21);
                            }
                            break;
                        case 27:
                            if (this.curChar == '.') {
                                jjCheckNAdd(28);
                            }
                            break;
                        case 28:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAddTwoStates(28, 29);
                            }
                            break;
                        case 30:
                            if ((43980465111040L & l) != 0) {
                                jjCheckNAdd(31);
                            }
                            break;
                        case 31:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAdd(31);
                            }
                            break;
                        case 32:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAddStates(28, 31);
                            }
                            break;
                        case 33:
                            if ((287948901175001088L & l) != 0) {
                                jjCheckNAddTwoStates(33, 34);
                            }
                            break;
                        case 34:
                            if (this.curChar == '.') {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAddTwoStates(35, 36);
                            }
                            break;
                        case 35:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAddTwoStates(35, 36);
                            }
                            break;
                        case 37:
                            if ((43980465111040L & l) != 0) {
                                jjCheckNAdd(38);
                            }
                            break;
                        case 38:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAdd(38);
                            }
                            break;
                        case 39:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAddTwoStates(39, 40);
                            }
                            break;
                        case 41:
                            if ((43980465111040L & l) != 0) {
                                jjCheckNAdd(42);
                            }
                            break;
                        case 42:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAdd(42);
                            }
                            break;
                        case 43:
                            if ((4294971904L & l) != 0) {
                                jjCheckNAddStates(8, 11);
                            }
                            break;
                        case 44:
                            if ((4294971904L & l) != 0) {
                                jjCheckNAddTwoStates(44, 45);
                            }
                            break;
                        case 45:
                            if (this.curChar == ';') {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                jjCheckNAdd(46);
                            }
                            break;
                        case 46:
                            if (((-9217) & l) != 0) {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                jjCheckNAdd(46);
                            }
                            break;
                        case 47:
                            if (this.curChar == ';') {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                jjCheckNAddStates(16, 19);
                            }
                            break;
                        case LasmConstants.SHL /* 48 */:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 79) {
                                    kind = 79;
                                }
                                jjCheckNAddStates(3, 7);
                            }
                            break;
                        case LasmConstants.SHR /* 49 */:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 79) {
                                    kind = 79;
                                }
                                jjCheckNAdd(49);
                            }
                            break;
                        case 50:
                            if ((4294971904L & l) != 0) {
                                jjCheckNAddTwoStates(1, 2);
                            } else if (this.curChar == ';') {
                                jjCheckNAddStates(0, 2);
                            }
                            if ((4294971904L & l) != 0) {
                                jjCheckNAddTwoStates(44, 45);
                            } else if (this.curChar == ';') {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                jjCheckNAdd(46);
                            }
                            break;
                    }
                } while (i != startsAt);
            } else if (this.curChar < 128) {
                long l2 = 1 << (this.curChar & '?');
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 3:
                            jjAddStates(0, 2);
                            break;
                        case 6:
                            if (this.curChar == 'u') {
                                int[] iArr10 = this.jjstateSet;
                                int i12 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i12 + 1;
                                iArr10[i12] = 12;
                            } else if (this.curChar == 'v') {
                                int[] iArr11 = this.jjstateSet;
                                int i13 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i13 + 1;
                                iArr11[i13] = 10;
                            }
                            break;
                        case 7:
                        case 8:
                            if ((576460745995190270L & l2) != 0) {
                                if (kind > 76) {
                                    kind = 76;
                                }
                                jjCheckNAdd(8);
                            }
                            break;
                        case 9:
                            if (this.curChar == 'v') {
                                int[] iArr12 = this.jjstateSet;
                                int i14 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i14 + 1;
                                iArr12[i14] = 10;
                            }
                            break;
                        case 11:
                            if (this.curChar == 'u') {
                                int[] iArr13 = this.jjstateSet;
                                int i15 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i15 + 1;
                                iArr13[i15] = 12;
                            }
                            break;
                        case 14:
                            if ((72057594054705152L & l2) != 0) {
                                jjCheckNAdd(15);
                            }
                            break;
                        case 15:
                            if ((541165879422L & l2) != 0) {
                                if (kind > 80) {
                                    kind = 80;
                                }
                                jjCheckNAdd(15);
                            }
                            break;
                        case 17:
                            if (this.curChar == '\\') {
                                int[] iArr14 = this.jjstateSet;
                                int i16 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i16 + 1;
                                iArr14[i16] = 18;
                            }
                            break;
                        case 18:
                            jjCheckNAddStates(25, 27);
                            break;
                        case 19:
                            if (((-268435457) & l2) != 0) {
                                jjCheckNAddStates(25, 27);
                            }
                            break;
                        case 22:
                            if (this.curChar == '\\') {
                                int[] iArr15 = this.jjstateSet;
                                int i17 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i17 + 1;
                                iArr15[i17] = 23;
                            }
                            break;
                        case 23:
                            jjCheckNAddStates(22, 24);
                            break;
                        case 24:
                            if (((-268435457) & l2) != 0) {
                                jjCheckNAddStates(22, 24);
                            }
                            break;
                        case 29:
                            if ((137438953504L & l2) != 0) {
                                jjAddStates(32, 33);
                            }
                            break;
                        case 36:
                            if ((137438953504L & l2) != 0) {
                                jjAddStates(34, 35);
                            }
                            break;
                        case 40:
                            if ((137438953504L & l2) != 0) {
                                jjAddStates(36, 37);
                            }
                            break;
                        case 46:
                            if (kind > 4) {
                                kind = 4;
                            }
                            int[] iArr16 = this.jjstateSet;
                            int i18 = this.jjnewStateCnt;
                            this.jjnewStateCnt = i18 + 1;
                            iArr16[i18] = 46;
                            break;
                    }
                } while (i != startsAt);
            } else {
                int hiByte = this.curChar >> '\b';
                int i1 = hiByte >> 6;
                long l1 = 1 << (hiByte & 63);
                int i22 = (this.curChar & 255) >> 6;
                long l22 = 1 << (this.curChar & '?');
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 3:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjAddStates(0, 2);
                            }
                            break;
                        case 18:
                        case 19:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjCheckNAddStates(25, 27);
                            }
                            break;
                        case 23:
                        case 24:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjCheckNAddStates(22, 24);
                            }
                            break;
                        case 46:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                int[] iArr17 = this.jjstateSet;
                                int i19 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i19 + 1;
                                iArr17[i19] = 46;
                            }
                            break;
                    }
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            curPos++;
            i = this.jjnewStateCnt;
            this.jjnewStateCnt = startsAt;
            startsAt = 50 - startsAt;
            if (i != startsAt) {
                try {
                    this.curChar = this.input_stream.readChar();
                } catch (IOException e) {
                }
            }
            return curPos;
        }
    }

    private final int jjStopStringLiteralDfa_2(int pos, long active0, long active1) {
        switch (pos) {
            case 0:
                if (((-4611686018427387904L) & active0) == 0 && (3071 & active1) == 0) {
                    if ((14 & active0) != 0) {
                    }
                }
                break;
        }
        return -1;
    }

    private final int jjStartNfa_2(int pos, long active0, long active1) {
        return jjMoveNfa_2(jjStopStringLiteralDfa_2(pos, active0, active1), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_2() {
        switch (this.curChar) {
            case '\t':
                return jjStartNfaWithStates_2(0, 2, 51);
            case '\f':
                return jjStartNfaWithStates_2(0, 3, 51);
            case ' ':
                return jjStartNfaWithStates_2(0, 1, 51);
            case '.':
                return jjMoveStringLiteralDfa1_2(-4611686018427387904L, 3071L);
            case LasmConstants.NUMPARAMS /* 65 */:
                return jjMoveStringLiteralDfa1_2(131072L, 0L);
            case LasmConstants.IS_VARARG /* 66 */:
                return jjMoveStringLiteralDfa1_2(263882790666240L, 0L);
            case LasmConstants.MAXSTACKSIZE /* 67 */:
                return jjMoveStringLiteralDfa1_2(1128107587141632L, 0L);
            case LasmConstants.UPVAL /* 68 */:
                return jjMoveStringLiteralDfa1_2(1048576L, 0L);
            case LasmConstants.LINE /* 69 */:
                return jjMoveStringLiteralDfa1_2(268435456L, 0L);
            case LasmConstants.LOCAL /* 70 */:
                return jjMoveStringLiteralDfa1_2(2252005972115456L, 0L);
            case LasmConstants.END_LOCAL /* 71 */:
                return jjMoveStringLiteralDfa1_2(4503599627374080L, 0L);
            case LasmConstants.END /* 73 */:
                return jjMoveStringLiteralDfa1_2(8796093022208L, 0L);
            case LasmConstants.EOL /* 74 */:
                return jjMoveStringLiteralDfa1_2(134217728L, 0L);
            case LasmConstants.LABEL /* 76 */:
                return jjMoveStringLiteralDfa1_2(1644167616L, 0L);
            case LasmConstants.V /* 77 */:
                return jjMoveStringLiteralDfa1_2(2621472L, 0L);
            case LasmConstants.U /* 78 */:
                return jjMoveStringLiteralDfa1_2(16809984L, 0L);
            case LasmConstants.INT /* 79 */:
                return jjMoveStringLiteralDfa1_2(9007199254740992L, 0L);
            case LasmConstants.HEX /* 80 */:
                return jjMoveStringLiteralDfa1_2(4194304L, 0L);
            case 'R':
                return jjMoveStringLiteralDfa1_2(34359738368L, 0L);
            case LasmConstants.NL /* 83 */:
                return jjMoveStringLiteralDfa1_2(54888719970562048L, 0L);
            case LasmConstants.FLOAT /* 84 */:
                return jjMoveStringLiteralDfa1_2(848256040960L, 0L);
            case LasmConstants.DIGIT /* 85 */:
                return jjMoveStringLiteralDfa1_2(8388608L, 0L);
            case LasmConstants.EXP /* 86 */:
                return jjMoveStringLiteralDfa1_2(4398046511104L, 0L);
            case 'f':
                return jjMoveStringLiteralDfa1_2(288230376151711744L, 0L);
            case 'n':
                return jjMoveStringLiteralDfa1_2(72057594037927936L, 0L);
            case 't':
                return jjMoveStringLiteralDfa1_2(144115188075855872L, 0L);
            default:
                return jjMoveNfa_2(6, 0);
        }
    }

    private int jjMoveStringLiteralDfa1_2(long active0, long active1) {
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case '.':
                    if ((2048 & active1) != 0) {
                        return jjStopAtPos(1, 75);
                    }
                    break;
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa2_2(active0, 39608188403712L, active1, 0L);
                case LasmConstants.UPVAL /* 68 */:
                    return jjMoveStringLiteralDfa2_2(active0, 8796093153280L, active1, 0L);
                case LasmConstants.LINE /* 69 */:
                    if ((1073741824 & active0) != 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 1;
                    }
                    return jjMoveStringLiteralDfa2_2(active0, 18015538856984064L, active1, 0L);
                case LasmConstants.LOCAL /* 70 */:
                    return jjMoveStringLiteralDfa2_2(active0, 824633720832L, active1, 0L);
                case LasmConstants.FUNC /* 72 */:
                    return jjMoveStringLiteralDfa2_2(active0, 844424930131968L, active1, 0L);
                case LasmConstants.END /* 73 */:
                    return jjMoveStringLiteralDfa2_2(active0, 1048576L, active1, 0L);
                case LasmConstants.RANGE /* 75 */:
                    return jjMoveStringLiteralDfa2_2(active0, 36028797018963968L, active1, 0L);
                case LasmConstants.LABEL /* 76 */:
                    return jjMoveStringLiteralDfa2_2(active0, 2199023255552L, active1, 0L);
                case LasmConstants.V /* 77 */:
                    return jjMoveStringLiteralDfa2_2(active0, 134217728L, active1, 0L);
                case LasmConstants.U /* 78 */:
                    return jjMoveStringLiteralDfa2_2(active0, 17592194433024L, active1, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa2_2(active0, 5700074526999008L, active1, 0L);
                case LasmConstants.HEX /* 80 */:
                    if ((9007199254740992L & active0) != 0) {
                        return jjStopAtPos(1, 53);
                    }
                    break;
                case LasmConstants.STRING /* 81 */:
                    if ((268435456 & active0) != 0) {
                        return jjStopAtPos(1, 28);
                    }
                    break;
                case LasmConstants.FLOAT /* 84 */:
                    if ((536870912 & active0) != 0) {
                        return jjStopAtPos(1, 29);
                    }
                    break;
                case LasmConstants.DIGIT /* 85 */:
                    return jjMoveStringLiteralDfa2_2(active0, 2251799814471680L, active1, 0L);
                case 'X':
                    return jjMoveStringLiteralDfa2_2(active0, 140737488355328L, active1, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa2_2(active0, 288230376151711744L, active1, 0L);
                case 'e':
                    return jjMoveStringLiteralDfa2_2(active0, 0L, active1, 640L);
                case 'f':
                    return jjMoveStringLiteralDfa2_2(active0, 0L, active1, 256L);
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa2_2(active0, 72057594037927936L, active1, 4L);
                case Message.ERROR_GROUP_FAIL /* 108 */:
                    return jjMoveStringLiteralDfa2_2(active0, Long.MIN_VALUE, active1, 97L);
                case 'm':
                    return jjMoveStringLiteralDfa2_2(active0, 0L, active1, 8L);
                case 'n':
                    return jjMoveStringLiteralDfa2_2(active0, 0L, active1, 2L);
                case 'r':
                    return jjMoveStringLiteralDfa2_2(active0, 144115188075855872L, active1, 0L);
                case 's':
                    return jjMoveStringLiteralDfa2_2(active0, 4611686018427387904L, active1, 0L);
                case 'u':
                    return jjMoveStringLiteralDfa2_2(active0, 0L, active1, 16L);
            }
            return jjStartNfa_2(0, active0, active1);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(0, active0, active1);
            return 1;
        }
    }

    private int jjMoveStringLiteralDfa2_2(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_2(0, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa3_2(active02, 448L, active12, 0L);
                case LasmConstants.IS_VARARG /* 66 */:
                    if ((262144 & active02) != 0) {
                        return jjStopAtPos(2, 18);
                    }
                    break;
                case LasmConstants.UPVAL /* 68 */:
                    if ((131072 & active02) != 0) {
                        return jjStopAtPos(2, 17);
                    }
                    if ((2097152 & active02) != 0) {
                        return jjStopAtPos(2, 21);
                    }
                    break;
                case LasmConstants.END /* 73 */:
                    return jjMoveStringLiteralDfa3_2(active02, 36037610291855360L, active12, 0L);
                case LasmConstants.LABEL /* 76 */:
                    if ((524288 & active02) != 0) {
                        return jjStopAtPos(2, 19);
                    }
                    if ((281474976710656L & active02) != 0) {
                        return jjStopAtPos(2, 48);
                    }
                    return jjMoveStringLiteralDfa3_2(active02, 8590000128L, active12, 0L);
                case LasmConstants.V /* 77 */:
                    if ((8388608 & active02) != 0) {
                        return jjStopAtPos(2, 23);
                    }
                    break;
                case LasmConstants.U /* 78 */:
                    if ((33554432 & active02) != 0) {
                        return jjStopAtPos(2, 25);
                    }
                    return jjMoveStringLiteralDfa3_2(active02, 3412884159725568L, active12, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa3_2(active02, 161353331376128L, active12, 0L);
                case LasmConstants.HEX /* 80 */:
                    if ((134217728 & active02) != 0) {
                        return jjStopAtPos(2, 27);
                    }
                    break;
                case 'R':
                    if ((70368744177664L & active02) != 0) {
                        return jjStopAtPos(2, 46);
                    }
                    if ((562949953421312L & active02) != 0) {
                        return jjStopAtPos(2, 49);
                    }
                    return jjMoveStringLiteralDfa3_2(active02, 4604204941312L, active12, 0L);
                case LasmConstants.NL /* 83 */:
                    return jjMoveStringLiteralDfa3_2(active02, 6442450944L, active12, 0L);
                case LasmConstants.FLOAT /* 84 */:
                    if ((16777216 & active02) != 0) {
                        return jjStopAtPos(2, 24);
                    }
                    return jjMoveStringLiteralDfa3_2(active02, 22519132008250880L, active12, 0L);
                case LasmConstants.EXP /* 86 */:
                    if ((1048576 & active02) != 0) {
                        return jjStopAtPos(2, 20);
                    }
                    return jjMoveStringLiteralDfa3_2(active02, 32L, active12, 0L);
                case LasmConstants.HEXDIGIT /* 87 */:
                    if ((4194304 & active02) != 0) {
                        return jjStopAtPos(2, 22);
                    }
                    return jjMoveStringLiteralDfa3_2(active02, 32768L, active12, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa3_2(active02, 0L, active12, 9L);
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa3_2(active02, Long.MIN_VALUE, active12, 32L);
                case Message.ERROR_GROUP_FAIL /* 108 */:
                    if ((72057594037927936L & active02) != 0) {
                        return jjStopAtPos(2, 56);
                    }
                    return jjMoveStringLiteralDfa3_2(active02, 288230376151711744L, active12, 0L);
                case 'n':
                    return jjMoveStringLiteralDfa3_2(active02, 0L, active12, 640L);
                case 'o':
                    return jjMoveStringLiteralDfa3_2(active02, 4611686018427387904L, active12, 64L);
                case 'p':
                    return jjMoveStringLiteralDfa3_2(active02, 0L, active12, 16L);
                case 's':
                    return jjMoveStringLiteralDfa3_2(active02, 0L, active12, 4L);
                case 'u':
                    return jjMoveStringLiteralDfa3_2(active02, 144115188075855872L, active12, 258L);
            }
            return jjStartNfa_2(1, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(1, active02, active12);
            return 2;
        }
    }

    private int jjMoveStringLiteralDfa3_2(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_2(1, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa4_2(active02, 4398046511104L, active12, 0L);
                case LasmConstants.MAXSTACKSIZE /* 67 */:
                    return jjMoveStringLiteralDfa4_2(active02, 2251799880794112L, active12, 0L);
                case LasmConstants.UPVAL /* 68 */:
                    if ((35184372088832L & active02) != 0) {
                        return jjStopAtPos(3, 45);
                    }
                    return jjMoveStringLiteralDfa4_2(active02, 448L, active12, 0L);
                case LasmConstants.LINE /* 69 */:
                    if ((32 & active02) != 0) {
                        return jjStopAtPos(3, 5);
                    }
                    break;
                case LasmConstants.LOCAL /* 70 */:
                    if ((65536 & active02) != 0) {
                        return jjStopAtPos(3, 16);
                    }
                    break;
                case LasmConstants.LABEL /* 76 */:
                    if ((8589934592L & active02) != 0) {
                        return jjStopAtPos(3, 33);
                    }
                    return jjMoveStringLiteralDfa4_2(active02, 1185410973696L, active12, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa4_2(active02, 4503599627370496L, active12, 0L);
                case LasmConstants.HEX /* 80 */:
                    return jjMoveStringLiteralDfa4_2(active02, 36028934457917440L, active12, 0L);
                case 'R':
                    if ((140737488355328L & active02) != 0) {
                        return jjStopAtPos(3, 47);
                    }
                    return jjMoveStringLiteralDfa4_2(active02, 824633720832L, active12, 0L);
                case LasmConstants.NL /* 83 */:
                    return jjMoveStringLiteralDfa4_2(active02, 1128098930098176L, active12, 0L);
                case LasmConstants.FLOAT /* 84 */:
                    if ((2147483648L & active02) != 0) {
                        this.jjmatchedKind = 31;
                        this.jjmatchedPos = 3;
                    } else if ((17592186044416L & active02) != 0) {
                        return jjStopAtPos(3, 44);
                    }
                    return jjMoveStringLiteralDfa4_2(active02, 4295023616L, active12, 0L);
                case LasmConstants.DIGIT /* 85 */:
                    return jjMoveStringLiteralDfa4_2(active02, 34359747072L, active12, 0L);
                case LasmConstants.EXP /* 86 */:
                    if ((8796093022208L & active02) != 0) {
                        return jjStopAtPos(3, 43);
                    }
                    break;
                case '_':
                    return jjMoveStringLiteralDfa4_2(active02, 18014398509481984L, active12, 4L);
                case 'c':
                    return jjMoveStringLiteralDfa4_2(active02, 0L, active12, 64L);
                case Message.ERROR_FATAL /* 100 */:
                    if ((512 & active12) != 0) {
                        this.jjmatchedKind = 73;
                        this.jjmatchedPos = 3;
                    }
                    return jjMoveStringLiteralDfa4_2(active02, 0L, active12, 128L);
                case 'e':
                    if ((144115188075855872L & active02) != 0) {
                        return jjStopAtPos(3, 57);
                    }
                    break;
                case 'm':
                    return jjMoveStringLiteralDfa4_2(active02, 0L, active12, 2L);
                case 'n':
                    return jjMoveStringLiteralDfa4_2(active02, Long.MIN_VALUE, active12, 288L);
                case 's':
                    return jjMoveStringLiteralDfa4_2(active02, 288230376151711744L, active12, 1L);
                case 'u':
                    return jjMoveStringLiteralDfa4_2(active02, 4611686018427387904L, active12, 0L);
                case 'v':
                    return jjMoveStringLiteralDfa4_2(active02, 0L, active12, 16L);
                case 'x':
                    return jjMoveStringLiteralDfa4_2(active02, 0L, active12, 8L);
            }
            return jjStartNfa_2(2, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(2, active02, active12);
            return 3;
        }
    }

    private int jjMoveStringLiteralDfa4_2(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_2(2, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case ' ':
                    return jjMoveStringLiteralDfa5_2(active02, 0L, active12, 128L);
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa5_2(active02, 67165184L, active12, 0L);
                case LasmConstants.IS_VARARG /* 66 */:
                    return jjMoveStringLiteralDfa5_2(active02, 128L, active12, 0L);
                case LasmConstants.MAXSTACKSIZE /* 67 */:
                    return jjMoveStringLiteralDfa5_2(active02, 292057776128L, active12, 0L);
                case LasmConstants.END /* 73 */:
                    return jjMoveStringLiteralDfa5_2(active02, 1099511627776L, active12, 0L);
                case LasmConstants.RANGE /* 75 */:
                    if ((64 & active02) != 0) {
                        return jjStopAtPos(4, 6);
                    }
                    break;
                case LasmConstants.LABEL /* 76 */:
                    return jjMoveStringLiteralDfa5_2(active02, 549755813888L, active12, 0L);
                case LasmConstants.U /* 78 */:
                    return jjMoveStringLiteralDfa5_2(active02, 256L, active12, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa5_2(active02, 68719476736L, active12, 0L);
                case LasmConstants.HEX /* 80 */:
                    return jjMoveStringLiteralDfa5_2(active02, 8704L, active12, 0L);
                case 'R':
                    return jjMoveStringLiteralDfa5_2(active02, 4569845202944L, active12, 0L);
                case LasmConstants.NL /* 83 */:
                    return jjMoveStringLiteralDfa5_2(active02, 4294967296L, active12, 0L);
                case LasmConstants.FLOAT /* 84 */:
                    if ((1125899906842624L & active02) != 0) {
                        return jjStopAtPos(4, 50);
                    }
                    return jjMoveStringLiteralDfa5_2(active02, 18014398509481984L, active12, 0L);
                case LasmConstants.DIGIT /* 85 */:
                    return jjMoveStringLiteralDfa5_2(active02, 2199023255552L, active12, 0L);
                case '[':
                    if ((2251799813685248L & active02) != 0) {
                        return jjStopAtPos(4, 51);
                    }
                    if ((4503599627370496L & active02) != 0) {
                        return jjStopAtPos(4, 52);
                    }
                    break;
                case '_':
                    return jjMoveStringLiteralDfa5_2(active02, 36028797018963968L, active12, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa5_2(active02, 0L, active12, 80L);
                case 'c':
                    if ((256 & active12) != 0) {
                        return jjStopAtPos(4, 72);
                    }
                    break;
                case 'e':
                    if ((288230376151711744L & active02) != 0) {
                        return jjStopAtPos(4, 58);
                    }
                    if ((32 & active12) != 0) {
                        this.jjmatchedKind = 69;
                        this.jjmatchedPos = 4;
                    }
                    return jjMoveStringLiteralDfa5_2(active02, Long.MIN_VALUE, active12, 0L);
                case 'p':
                    return jjMoveStringLiteralDfa5_2(active02, 0L, active12, 2L);
                case 'r':
                    return jjMoveStringLiteralDfa5_2(active02, 4611686018427387904L, active12, 0L);
                case 's':
                    return jjMoveStringLiteralDfa5_2(active02, 0L, active12, 8L);
                case 't':
                    return jjMoveStringLiteralDfa5_2(active02, 0L, active12, 1L);
                case 'v':
                    return jjMoveStringLiteralDfa5_2(active02, 0L, active12, 4L);
            }
            return jjStartNfa_2(3, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(3, active02, active12);
            return 4;
        }
    }

    private int jjMoveStringLiteralDfa5_2(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_2(3, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa6_2(active02, 292057776128L, active12, 0L);
                case LasmConstants.IS_VARARG /* 66 */:
                    return jjMoveStringLiteralDfa6_2(active02, 56320L, active12, 0L);
                case LasmConstants.LINE /* 69 */:
                    return jjMoveStringLiteralDfa6_2(active02, 141733920768L, active12, 0L);
                case LasmConstants.END_LOCAL /* 71 */:
                    if ((4398046511104L & active02) != 0) {
                        return jjStopAtPos(5, 42);
                    }
                    break;
                case LasmConstants.END /* 73 */:
                    return jjMoveStringLiteralDfa6_2(active02, 256L, active12, 0L);
                case LasmConstants.U /* 78 */:
                    if ((34359738368L & active02) != 0) {
                        return jjStopAtPos(5, 35);
                    }
                    return jjMoveStringLiteralDfa6_2(active02, 36028797018963968L, active12, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa6_2(active02, 18015016984772736L, active12, 0L);
                case 'R':
                    return jjMoveStringLiteralDfa6_2(active02, 2199023255552L, active12, 0L);
                case LasmConstants.NL /* 83 */:
                    return jjMoveStringLiteralDfa6_2(active02, 1099511627776L, active12, 0L);
                case LasmConstants.FLOAT /* 84 */:
                    if ((67108864 & active02) != 0) {
                        return jjStopAtPos(5, 26);
                    }
                    break;
                case LasmConstants.EXP /* 86 */:
                    return jjMoveStringLiteralDfa6_2(active02, 8704L, active12, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa6_2(active02, 0L, active12, 6L);
                case 'c':
                    return jjMoveStringLiteralDfa6_2(active02, 4611686018427387904L, active12, 0L);
                case Message.ERROR_FATAL /* 100 */:
                    return jjMoveStringLiteralDfa6_2(active02, Long.MIN_VALUE, active12, 0L);
                case Message.ERROR_GROUP_FAIL /* 108 */:
                    if ((16 & active12) != 0) {
                        return jjStopAtPos(5, 68);
                    }
                    if ((64 & active12) != 0) {
                        return jjStopAtPos(5, 70);
                    }
                    return jjMoveStringLiteralDfa6_2(active02, 0L, active12, 129L);
                case 't':
                    return jjMoveStringLiteralDfa6_2(active02, 0L, active12, 8L);
            }
            return jjStartNfa_2(4, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(4, active02, active12);
            return 5;
        }
    }

    private int jjMoveStringLiteralDfa6_2(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_2(4, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa7_2(active02, 8704L, active12, 0L);
                case LasmConstants.LINE /* 69 */:
                    if ((2199023255552L & active02) != 0) {
                        return jjStopAtPos(6, 41);
                    }
                    return jjMoveStringLiteralDfa7_2(active02, 36028797018963968L, active12, 0L);
                case LasmConstants.LABEL /* 76 */:
                    if ((256 & active02) != 0) {
                        return jjStopAtPos(6, 8);
                    }
                    return jjMoveStringLiteralDfa7_2(active02, 292057827328L, active12, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa7_2(active02, 549755814016L, active12, 0L);
                case LasmConstants.HEX /* 80 */:
                    if ((68719476736L & active02) != 0) {
                        return jjStopAtPos(6, 36);
                    }
                    if ((137438953472L & active02) != 0) {
                        return jjStopAtPos(6, 37);
                    }
                    if ((18014398509481984L & active02) != 0) {
                        return jjStopAtPos(6, 54);
                    }
                    break;
                case LasmConstants.FLOAT /* 84 */:
                    if ((4294967296L & active02) != 0) {
                        return jjStopAtPos(6, 32);
                    }
                    if ((1099511627776L & active02) != 0) {
                        return jjStopAtPos(6, 40);
                    }
                    break;
                case LasmConstants.DIGIT /* 85 */:
                    return jjMoveStringLiteralDfa7_2(active02, 5120L, active12, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa7_2(active02, 0L, active12, 8L);
                case 'e':
                    if ((4611686018427387904L & active02) != 0) {
                        return jjStopAtPos(6, 62);
                    }
                    return jjMoveStringLiteralDfa7_2(active02, Long.MIN_VALUE, active12, 0L);
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa7_2(active02, 0L, active12, 1L);
                case 'o':
                    return jjMoveStringLiteralDfa7_2(active02, 0L, active12, 128L);
                case 'r':
                    return jjMoveStringLiteralDfa7_2(active02, 0L, active12, 6L);
            }
            return jjStartNfa_2(5, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(5, active02, active12);
            return 6;
        }
    }

    private int jjMoveStringLiteralDfa7_2(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_2(5, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.LINE /* 69 */:
                    if ((2048 & active02) != 0) {
                        return jjStopAtPos(7, 11);
                    }
                    if ((16384 & active02) != 0) {
                        return jjStopAtPos(7, 14);
                    }
                    if ((32768 & active02) != 0) {
                        return jjStopAtPos(7, 15);
                    }
                    break;
                case LasmConstants.LABEL /* 76 */:
                    if ((128 & active02) != 0) {
                        return jjStopAtPos(7, 7);
                    }
                    if ((512 & active02) != 0) {
                        return jjStopAtPos(7, 9);
                    }
                    if ((8192 & active02) != 0) {
                        return jjStopAtPos(7, 13);
                    }
                    if ((17179869184L & active02) != 0) {
                        return jjStopAtPos(7, 34);
                    }
                    if ((274877906944L & active02) != 0) {
                        return jjStopAtPos(7, 38);
                    }
                    break;
                case LasmConstants.HEX /* 80 */:
                    if ((1024 & active02) != 0) {
                        return jjStopAtPos(7, 10);
                    }
                    if ((4096 & active02) != 0) {
                        return jjStopAtPos(7, 12);
                    }
                    if ((549755813888L & active02) != 0) {
                        return jjStopAtPos(7, 39);
                    }
                    break;
                case 'X':
                    return jjMoveStringLiteralDfa8_2(active02, 36028797018963968L, active12, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa8_2(active02, 0L, active12, 6L);
                case 'c':
                    return jjMoveStringLiteralDfa8_2(active02, 0L, active12, 136L);
                case 'f':
                    return jjMoveStringLiteralDfa8_2(active02, Long.MIN_VALUE, active12, 0L);
                case 'n':
                    return jjMoveStringLiteralDfa8_2(active02, 0L, active12, 1L);
            }
            return jjStartNfa_2(6, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(6, active02, active12);
            return 7;
        }
    }

    private int jjMoveStringLiteralDfa8_2(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_2(6, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.FLOAT /* 84 */:
                    if ((36028797018963968L & active02) != 0) {
                        return jjStopAtPos(8, 55);
                    }
                    break;
                case 'a':
                    return jjMoveStringLiteralDfa9_2(active02, 0L, active12, 128L);
                case 'e':
                    return jjMoveStringLiteralDfa9_2(active02, 0L, active12, 1L);
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa9_2(active02, Long.MIN_VALUE, active12, 0L);
                case Message.ERROR_SPEEDHACK_FAIL /* 107 */:
                    return jjMoveStringLiteralDfa9_2(active02, 0L, active12, 8L);
                case 'm':
                    return jjMoveStringLiteralDfa9_2(active02, 0L, active12, 2L);
                case 'r':
                    return jjMoveStringLiteralDfa9_2(active02, 0L, active12, 4L);
            }
            return jjStartNfa_2(7, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(7, active02, active12);
            return 8;
        }
    }

    private int jjMoveStringLiteralDfa9_2(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_2(7, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Message.ERROR_FATAL /* 100 */:
                    return jjMoveStringLiteralDfa10_2(active02, 0L, active12, 1L);
                case 'g':
                    if ((4 & active12) != 0) {
                        return jjStopAtPos(9, 66);
                    }
                    break;
                case Message.ERROR_GROUP_FAIL /* 108 */:
                    if ((128 & active12) != 0) {
                        return jjStopAtPos(9, 71);
                    }
                    break;
                case 'n':
                    return jjMoveStringLiteralDfa10_2(active02, Long.MIN_VALUE, active12, 0L);
                case 's':
                    if ((2 & active12) != 0) {
                        return jjStopAtPos(9, 65);
                    }
                    return jjMoveStringLiteralDfa10_2(active02, 0L, active12, 8L);
            }
            return jjStartNfa_2(8, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(8, active02, active12);
            return 9;
        }
    }

    private int jjMoveStringLiteralDfa10_2(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_2(8, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'e':
                    return jjMoveStringLiteralDfa11_2(active02, Long.MIN_VALUE, active12, 1L);
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa11_2(active02, 0L, active12, 8L);
                default:
                    return jjStartNfa_2(9, active02, active12);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(9, active02, active12);
            return 10;
        }
    }

    private int jjMoveStringLiteralDfa11_2(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_2(9, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Message.ERROR_FATAL /* 100 */:
                    if ((Long.MIN_VALUE & active02) != 0) {
                        return jjStopAtPos(11, 63);
                    }
                    break;
                case 'f':
                    return jjMoveStringLiteralDfa12_2(active02, 0L, active12, 1L);
                case 'z':
                    return jjMoveStringLiteralDfa12_2(active02, 0L, active12, 8L);
            }
            return jjStartNfa_2(10, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(10, active02, active12);
            return 11;
        }
    }

    private int jjMoveStringLiteralDfa12_2(long old0, long active0, long old1, long active1) {
        long active12 = active1 & old1;
        if (((active0 & old0) | active12) == 0) {
            return jjStartNfa_2(10, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'e':
                    if ((8 & active12) != 0) {
                        return jjStopAtPos(12, 67);
                    }
                    break;
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa13_2(active12, 1L);
            }
            return jjStartNfa_2(11, 0L, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(11, 0L, active12);
            return 12;
        }
    }

    private int jjMoveStringLiteralDfa13_2(long old1, long active1) {
        long active12 = active1 & old1;
        if (active12 == 0) {
            return jjStartNfa_2(11, 0L, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'n':
                    return jjMoveStringLiteralDfa14_2(active12, 1L);
                default:
                    return jjStartNfa_2(12, 0L, active12);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(12, 0L, active12);
            return 13;
        }
    }

    private int jjMoveStringLiteralDfa14_2(long old1, long active1) {
        long active12 = active1 & old1;
        if (active12 == 0) {
            return jjStartNfa_2(12, 0L, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'e':
                    return jjMoveStringLiteralDfa15_2(active12, 1L);
                default:
                    return jjStartNfa_2(13, 0L, active12);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(13, 0L, active12);
            return 14;
        }
    }

    private int jjMoveStringLiteralDfa15_2(long old1, long active1) {
        long active12 = active1 & old1;
        if (active12 == 0) {
            return jjStartNfa_2(13, 0L, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Message.ERROR_FATAL /* 100 */:
                    if ((1 & active12) != 0) {
                        return jjStopAtPos(15, 64);
                    }
                    break;
            }
            return jjStartNfa_2(14, 0L, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_2(14, 0L, active12);
            return 15;
        }
    }

    private int jjStartNfaWithStates_2(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
            return jjMoveNfa_2(state, pos + 1);
        } catch (IOException e) {
            return pos + 1;
        }
    }

    private int jjMoveNfa_2(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 51;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            int i2 = this.jjround + 1;
            this.jjround = i2;
            if (i2 == Integer.MAX_VALUE) {
                ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1 << this.curChar;
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 0:
                            if ((9216 & l) != 0) {
                                if (kind > 74) {
                                    kind = 74;
                                }
                                jjCheckNAddStates(12, 15);
                            }
                            break;
                        case 1:
                            if ((4294971904L & l) != 0) {
                                jjCheckNAddTwoStates(1, 2);
                            }
                            break;
                        case 2:
                            if (this.curChar == ';') {
                                jjCheckNAddStates(0, 2);
                            }
                            break;
                        case 3:
                            if (((-9217) & l) != 0) {
                                jjCheckNAddStates(0, 2);
                            }
                            break;
                        case 4:
                            if (this.curChar == '\n') {
                                if (kind > 74) {
                                    kind = 74;
                                }
                                jjCheckNAddStates(12, 15);
                            }
                            break;
                        case 5:
                            if (this.curChar == '\r') {
                                int[] iArr = this.jjstateSet;
                                int i3 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i3 + 1;
                                iArr[i3] = 4;
                            }
                            break;
                        case 6:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 79) {
                                    kind = 79;
                                }
                                jjCheckNAddStates(38, 42);
                            } else if ((4294971904L & l) != 0) {
                                jjCheckNAddStates(43, 46);
                            } else if ((9216 & l) != 0) {
                                if (kind > 74) {
                                    kind = 74;
                                }
                                jjCheckNAddStates(12, 15);
                            } else if (this.curChar == ';') {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                jjCheckNAddStates(47, 50);
                            } else if (this.curChar == '.') {
                                jjCheckNAdd(26);
                            } else if (this.curChar == '-') {
                                jjAddStates(51, 52);
                            } else if (this.curChar == '\'') {
                                jjCheckNAddStates(53, 55);
                            } else if (this.curChar == '\"') {
                                jjCheckNAddStates(56, 58);
                            } else if (this.curChar == ':') {
                                int[] iArr2 = this.jjstateSet;
                                int i4 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i4 + 1;
                                iArr2[i4] = 7;
                            }
                            if (this.curChar == '0') {
                                int[] iArr3 = this.jjstateSet;
                                int i5 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i5 + 1;
                                iArr3[i5] = 12;
                            } else if (this.curChar == '\r') {
                                int[] iArr4 = this.jjstateSet;
                                int i6 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i6 + 1;
                                iArr4[i6] = 4;
                            }
                            break;
                        case 8:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 76) {
                                    kind = 76;
                                }
                                int[] iArr5 = this.jjstateSet;
                                int i7 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i7 + 1;
                                iArr5[i7] = 8;
                            }
                            break;
                        case 10:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 78) {
                                    kind = 78;
                                }
                                int[] iArr6 = this.jjstateSet;
                                int i8 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i8 + 1;
                                iArr6[i8] = 10;
                            }
                            break;
                        case 11:
                            if (this.curChar == '0') {
                                int[] iArr7 = this.jjstateSet;
                                int i9 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i9 + 1;
                                iArr7[i9] = 12;
                            }
                            break;
                        case 13:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 80) {
                                    kind = 80;
                                }
                                int[] iArr8 = this.jjstateSet;
                                int i10 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i10 + 1;
                                iArr8[i10] = 13;
                            }
                            break;
                        case 14:
                            if (this.curChar == '\"') {
                                jjCheckNAddStates(56, 58);
                            }
                            break;
                        case 16:
                            jjCheckNAddStates(56, 58);
                            break;
                        case 17:
                            if (((-17179869185L) & l) != 0) {
                                jjCheckNAddStates(56, 58);
                            }
                            break;
                        case 18:
                            if (this.curChar == '\"' && kind > 81) {
                                kind = 81;
                            }
                            break;
                        case 19:
                            if (this.curChar == '\'') {
                                jjCheckNAddStates(53, 55);
                            }
                            break;
                        case 21:
                            jjCheckNAddStates(53, 55);
                            break;
                        case 22:
                            if (((-549755813889L) & l) != 0) {
                                jjCheckNAddStates(53, 55);
                            }
                            break;
                        case 23:
                            if (this.curChar == '\'' && kind > 81) {
                                kind = 81;
                            }
                            break;
                        case 24:
                            if (this.curChar == '-') {
                                jjAddStates(51, 52);
                            }
                            break;
                        case 25:
                            if (this.curChar == '.') {
                                jjCheckNAdd(26);
                            }
                            break;
                        case 26:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAddTwoStates(26, 27);
                            }
                            break;
                        case 28:
                            if ((43980465111040L & l) != 0) {
                                jjCheckNAdd(29);
                            }
                            break;
                        case 29:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAdd(29);
                            }
                            break;
                        case 30:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAddStates(59, 62);
                            }
                            break;
                        case 31:
                            if ((287948901175001088L & l) != 0) {
                                jjCheckNAddTwoStates(31, 32);
                            }
                            break;
                        case 32:
                            if (this.curChar == '.') {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAddTwoStates(33, 34);
                            }
                            break;
                        case 33:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAddTwoStates(33, 34);
                            }
                            break;
                        case 35:
                            if ((43980465111040L & l) != 0) {
                                jjCheckNAdd(36);
                            }
                            break;
                        case 36:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAdd(36);
                            }
                            break;
                        case 37:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAddTwoStates(37, 38);
                            }
                            break;
                        case 39:
                            if ((43980465111040L & l) != 0) {
                                jjCheckNAdd(40);
                            }
                            break;
                        case 40:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAdd(40);
                            }
                            break;
                        case 41:
                            if ((4294971904L & l) != 0) {
                                jjCheckNAddStates(43, 46);
                            }
                            break;
                        case 42:
                            if ((4294971904L & l) != 0) {
                                jjCheckNAddTwoStates(42, 43);
                            }
                            break;
                        case 43:
                            if (this.curChar == ';') {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                jjCheckNAdd(44);
                            }
                            break;
                        case 44:
                            if (((-9217) & l) != 0) {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                jjCheckNAdd(44);
                            }
                            break;
                        case 45:
                            if (this.curChar == ';') {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                jjCheckNAddStates(47, 50);
                            }
                            break;
                        case 47:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 59) {
                                    kind = 59;
                                }
                                int[] iArr9 = this.jjstateSet;
                                int i11 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i11 + 1;
                                iArr9[i11] = 47;
                            }
                            break;
                        case LasmConstants.SHL /* 48 */:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 77) {
                                    kind = 77;
                                }
                                int[] iArr10 = this.jjstateSet;
                                int i12 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i12 + 1;
                                iArr10[i12] = 48;
                            }
                            break;
                        case LasmConstants.SHR /* 49 */:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 79) {
                                    kind = 79;
                                }
                                jjCheckNAddStates(38, 42);
                            }
                            break;
                        case 50:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 79) {
                                    kind = 79;
                                }
                                jjCheckNAdd(50);
                            }
                            break;
                        case LasmConstants.PROTO /* 51 */:
                            if ((4294971904L & l) != 0) {
                                jjCheckNAddTwoStates(1, 2);
                            } else if (this.curChar == ';') {
                                jjCheckNAddStates(0, 2);
                            }
                            if ((4294971904L & l) != 0) {
                                jjCheckNAddTwoStates(42, 43);
                            } else if (this.curChar == ';') {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                jjCheckNAdd(44);
                            }
                            break;
                    }
                } while (i != startsAt);
            } else if (this.curChar < 128) {
                long l2 = 1 << (this.curChar & '?');
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 3:
                            jjAddStates(0, 2);
                            break;
                        case 6:
                            if (this.curChar == 'v') {
                                jjAddStates(63, 64);
                            } else if (this.curChar == 'u') {
                                int[] iArr11 = this.jjstateSet;
                                int i13 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i13 + 1;
                                iArr11[i13] = 10;
                            }
                            break;
                        case 7:
                        case 8:
                            if ((576460745995190270L & l2) != 0) {
                                if (kind > 76) {
                                    kind = 76;
                                }
                                jjCheckNAdd(8);
                            }
                            break;
                        case 9:
                            if (this.curChar == 'u') {
                                int[] iArr12 = this.jjstateSet;
                                int i14 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i14 + 1;
                                iArr12[i14] = 10;
                            }
                            break;
                        case 12:
                            if ((72057594054705152L & l2) != 0) {
                                jjCheckNAdd(13);
                            }
                            break;
                        case 13:
                            if ((541165879422L & l2) != 0) {
                                if (kind > 80) {
                                    kind = 80;
                                }
                                jjCheckNAdd(13);
                            }
                            break;
                        case 15:
                            if (this.curChar == '\\') {
                                int[] iArr13 = this.jjstateSet;
                                int i15 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i15 + 1;
                                iArr13[i15] = 16;
                            }
                            break;
                        case 16:
                            jjCheckNAddStates(56, 58);
                            break;
                        case 17:
                            if (((-268435457) & l2) != 0) {
                                jjCheckNAddStates(56, 58);
                            }
                            break;
                        case 20:
                            if (this.curChar == '\\') {
                                int[] iArr14 = this.jjstateSet;
                                int i16 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i16 + 1;
                                iArr14[i16] = 21;
                            }
                            break;
                        case 21:
                            jjCheckNAddStates(53, 55);
                            break;
                        case 22:
                            if (((-268435457) & l2) != 0) {
                                jjCheckNAddStates(53, 55);
                            }
                            break;
                        case 27:
                            if ((137438953504L & l2) != 0) {
                                jjAddStates(65, 66);
                            }
                            break;
                        case 34:
                            if ((137438953504L & l2) != 0) {
                                jjAddStates(67, 68);
                            }
                            break;
                        case 38:
                            if ((137438953504L & l2) != 0) {
                                jjAddStates(69, 70);
                            }
                            break;
                        case 44:
                            if (kind > 4) {
                                kind = 4;
                            }
                            int[] iArr15 = this.jjstateSet;
                            int i17 = this.jjnewStateCnt;
                            this.jjnewStateCnt = i17 + 1;
                            iArr15[i17] = 44;
                            break;
                        case 46:
                            if (this.curChar == 'v') {
                                jjAddStates(63, 64);
                            }
                            break;
                    }
                } while (i != startsAt);
            } else {
                int hiByte = this.curChar >> '\b';
                int i1 = hiByte >> 6;
                long l1 = 1 << (hiByte & 63);
                int i22 = (this.curChar & 255) >> 6;
                long l22 = 1 << (this.curChar & '?');
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 3:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjAddStates(0, 2);
                            }
                            break;
                        case 16:
                        case 17:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjCheckNAddStates(56, 58);
                            }
                            break;
                        case 21:
                        case 22:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjCheckNAddStates(53, 55);
                            }
                            break;
                        case 44:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                int[] iArr16 = this.jjstateSet;
                                int i18 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i18 + 1;
                                iArr16[i18] = 44;
                            }
                            break;
                    }
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            curPos++;
            i = this.jjnewStateCnt;
            this.jjnewStateCnt = startsAt;
            startsAt = 51 - startsAt;
            if (i != startsAt) {
                try {
                    this.curChar = this.input_stream.readChar();
                } catch (IOException e) {
                }
            }
            return curPos;
        }
    }

    private final int jjStopStringLiteralDfa_1(int pos, long active0, long active1) {
        switch (pos) {
            case 0:
                if ((14 & active0) == 0) {
                    if (((-4611686018427387904L) & active0) != 0 || (3071 & active1) != 0) {
                    }
                }
                break;
        }
        return -1;
    }

    private final int jjStartNfa_1(int pos, long active0, long active1) {
        return jjMoveNfa_1(jjStopStringLiteralDfa_1(pos, active0, active1), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_1() {
        switch (this.curChar) {
            case '\t':
                return jjStartNfaWithStates_1(0, 2, 51);
            case '\f':
                return jjStartNfaWithStates_1(0, 3, 51);
            case ' ':
                return jjStartNfaWithStates_1(0, 1, 51);
            case '.':
                return jjMoveStringLiteralDfa1_1(-4611686018427387904L, 3071L);
            case LasmConstants.NUMPARAMS /* 65 */:
                return jjMoveStringLiteralDfa1_1(131072L, 0L);
            case LasmConstants.IS_VARARG /* 66 */:
                return jjMoveStringLiteralDfa1_1(263882790666240L, 0L);
            case LasmConstants.MAXSTACKSIZE /* 67 */:
                return jjMoveStringLiteralDfa1_1(1128107587141632L, 0L);
            case LasmConstants.UPVAL /* 68 */:
                return jjMoveStringLiteralDfa1_1(1048576L, 0L);
            case LasmConstants.LINE /* 69 */:
                return jjMoveStringLiteralDfa1_1(268435456L, 0L);
            case LasmConstants.LOCAL /* 70 */:
                return jjMoveStringLiteralDfa1_1(2252005972115456L, 0L);
            case LasmConstants.END_LOCAL /* 71 */:
                return jjMoveStringLiteralDfa1_1(4503599627374080L, 0L);
            case LasmConstants.END /* 73 */:
                return jjMoveStringLiteralDfa1_1(8796093022208L, 0L);
            case LasmConstants.EOL /* 74 */:
                return jjMoveStringLiteralDfa1_1(134217728L, 0L);
            case LasmConstants.LABEL /* 76 */:
                return jjMoveStringLiteralDfa1_1(1644167616L, 0L);
            case LasmConstants.V /* 77 */:
                return jjMoveStringLiteralDfa1_1(2621472L, 0L);
            case LasmConstants.U /* 78 */:
                return jjMoveStringLiteralDfa1_1(16809984L, 0L);
            case LasmConstants.INT /* 79 */:
                return jjMoveStringLiteralDfa1_1(9007199254740992L, 0L);
            case LasmConstants.HEX /* 80 */:
                return jjMoveStringLiteralDfa1_1(4194304L, 0L);
            case 'R':
                return jjMoveStringLiteralDfa1_1(34359738368L, 0L);
            case LasmConstants.NL /* 83 */:
                return jjMoveStringLiteralDfa1_1(54888719970562048L, 0L);
            case LasmConstants.FLOAT /* 84 */:
                return jjMoveStringLiteralDfa1_1(848256040960L, 0L);
            case LasmConstants.DIGIT /* 85 */:
                return jjMoveStringLiteralDfa1_1(8388608L, 0L);
            case LasmConstants.EXP /* 86 */:
                return jjMoveStringLiteralDfa1_1(4398046511104L, 0L);
            case 'f':
                return jjMoveStringLiteralDfa1_1(288230376151711744L, 0L);
            case 'n':
                return jjMoveStringLiteralDfa1_1(72057594037927936L, 0L);
            case 't':
                return jjMoveStringLiteralDfa1_1(144115188075855872L, 0L);
            default:
                return jjMoveNfa_1(6, 0);
        }
    }

    private int jjMoveStringLiteralDfa1_1(long active0, long active1) {
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case '.':
                    if ((2048 & active1) != 0) {
                        return jjStopAtPos(1, 75);
                    }
                    break;
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa2_1(active0, 39608188403712L, active1, 0L);
                case LasmConstants.UPVAL /* 68 */:
                    return jjMoveStringLiteralDfa2_1(active0, 8796093153280L, active1, 0L);
                case LasmConstants.LINE /* 69 */:
                    if ((1073741824 & active0) != 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 1;
                    }
                    return jjMoveStringLiteralDfa2_1(active0, 18015538856984064L, active1, 0L);
                case LasmConstants.LOCAL /* 70 */:
                    return jjMoveStringLiteralDfa2_1(active0, 824633720832L, active1, 0L);
                case LasmConstants.FUNC /* 72 */:
                    return jjMoveStringLiteralDfa2_1(active0, 844424930131968L, active1, 0L);
                case LasmConstants.END /* 73 */:
                    return jjMoveStringLiteralDfa2_1(active0, 1048576L, active1, 0L);
                case LasmConstants.RANGE /* 75 */:
                    return jjMoveStringLiteralDfa2_1(active0, 36028797018963968L, active1, 0L);
                case LasmConstants.LABEL /* 76 */:
                    return jjMoveStringLiteralDfa2_1(active0, 2199023255552L, active1, 0L);
                case LasmConstants.V /* 77 */:
                    return jjMoveStringLiteralDfa2_1(active0, 134217728L, active1, 0L);
                case LasmConstants.U /* 78 */:
                    return jjMoveStringLiteralDfa2_1(active0, 17592194433024L, active1, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa2_1(active0, 5700074526999008L, active1, 0L);
                case LasmConstants.HEX /* 80 */:
                    if ((9007199254740992L & active0) != 0) {
                        return jjStopAtPos(1, 53);
                    }
                    break;
                case LasmConstants.STRING /* 81 */:
                    if ((268435456 & active0) != 0) {
                        return jjStopAtPos(1, 28);
                    }
                    break;
                case LasmConstants.FLOAT /* 84 */:
                    if ((536870912 & active0) != 0) {
                        return jjStopAtPos(1, 29);
                    }
                    break;
                case LasmConstants.DIGIT /* 85 */:
                    return jjMoveStringLiteralDfa2_1(active0, 2251799814471680L, active1, 0L);
                case 'X':
                    return jjMoveStringLiteralDfa2_1(active0, 140737488355328L, active1, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa2_1(active0, 288230376151711744L, active1, 0L);
                case 'e':
                    return jjMoveStringLiteralDfa2_1(active0, 0L, active1, 640L);
                case 'f':
                    return jjMoveStringLiteralDfa2_1(active0, 0L, active1, 256L);
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa2_1(active0, 72057594037927936L, active1, 4L);
                case Message.ERROR_GROUP_FAIL /* 108 */:
                    return jjMoveStringLiteralDfa2_1(active0, Long.MIN_VALUE, active1, 97L);
                case 'm':
                    return jjMoveStringLiteralDfa2_1(active0, 0L, active1, 8L);
                case 'n':
                    return jjMoveStringLiteralDfa2_1(active0, 0L, active1, 2L);
                case 'r':
                    return jjMoveStringLiteralDfa2_1(active0, 144115188075855872L, active1, 0L);
                case 's':
                    return jjMoveStringLiteralDfa2_1(active0, 4611686018427387904L, active1, 0L);
                case 'u':
                    return jjMoveStringLiteralDfa2_1(active0, 0L, active1, 16L);
            }
            return jjStartNfa_1(0, active0, active1);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(0, active0, active1);
            return 1;
        }
    }

    private int jjMoveStringLiteralDfa2_1(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_1(0, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa3_1(active02, 448L, active12, 0L);
                case LasmConstants.IS_VARARG /* 66 */:
                    if ((262144 & active02) != 0) {
                        return jjStopAtPos(2, 18);
                    }
                    break;
                case LasmConstants.UPVAL /* 68 */:
                    if ((131072 & active02) != 0) {
                        return jjStopAtPos(2, 17);
                    }
                    if ((2097152 & active02) != 0) {
                        return jjStopAtPos(2, 21);
                    }
                    break;
                case LasmConstants.END /* 73 */:
                    return jjMoveStringLiteralDfa3_1(active02, 36037610291855360L, active12, 0L);
                case LasmConstants.LABEL /* 76 */:
                    if ((524288 & active02) != 0) {
                        return jjStopAtPos(2, 19);
                    }
                    if ((281474976710656L & active02) != 0) {
                        return jjStopAtPos(2, 48);
                    }
                    return jjMoveStringLiteralDfa3_1(active02, 8590000128L, active12, 0L);
                case LasmConstants.V /* 77 */:
                    if ((8388608 & active02) != 0) {
                        return jjStopAtPos(2, 23);
                    }
                    break;
                case LasmConstants.U /* 78 */:
                    if ((33554432 & active02) != 0) {
                        return jjStopAtPos(2, 25);
                    }
                    return jjMoveStringLiteralDfa3_1(active02, 3412884159725568L, active12, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa3_1(active02, 161353331376128L, active12, 0L);
                case LasmConstants.HEX /* 80 */:
                    if ((134217728 & active02) != 0) {
                        return jjStopAtPos(2, 27);
                    }
                    break;
                case 'R':
                    if ((70368744177664L & active02) != 0) {
                        return jjStopAtPos(2, 46);
                    }
                    if ((562949953421312L & active02) != 0) {
                        return jjStopAtPos(2, 49);
                    }
                    return jjMoveStringLiteralDfa3_1(active02, 4604204941312L, active12, 0L);
                case LasmConstants.NL /* 83 */:
                    return jjMoveStringLiteralDfa3_1(active02, 6442450944L, active12, 0L);
                case LasmConstants.FLOAT /* 84 */:
                    if ((16777216 & active02) != 0) {
                        return jjStopAtPos(2, 24);
                    }
                    return jjMoveStringLiteralDfa3_1(active02, 22519132008250880L, active12, 0L);
                case LasmConstants.EXP /* 86 */:
                    if ((1048576 & active02) != 0) {
                        return jjStopAtPos(2, 20);
                    }
                    return jjMoveStringLiteralDfa3_1(active02, 32L, active12, 0L);
                case LasmConstants.HEXDIGIT /* 87 */:
                    if ((4194304 & active02) != 0) {
                        return jjStopAtPos(2, 22);
                    }
                    return jjMoveStringLiteralDfa3_1(active02, 32768L, active12, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa3_1(active02, 0L, active12, 9L);
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa3_1(active02, Long.MIN_VALUE, active12, 32L);
                case Message.ERROR_GROUP_FAIL /* 108 */:
                    if ((72057594037927936L & active02) != 0) {
                        return jjStopAtPos(2, 56);
                    }
                    return jjMoveStringLiteralDfa3_1(active02, 288230376151711744L, active12, 0L);
                case 'n':
                    return jjMoveStringLiteralDfa3_1(active02, 0L, active12, 640L);
                case 'o':
                    return jjMoveStringLiteralDfa3_1(active02, 4611686018427387904L, active12, 64L);
                case 'p':
                    return jjMoveStringLiteralDfa3_1(active02, 0L, active12, 16L);
                case 's':
                    return jjMoveStringLiteralDfa3_1(active02, 0L, active12, 4L);
                case 'u':
                    return jjMoveStringLiteralDfa3_1(active02, 144115188075855872L, active12, 258L);
            }
            return jjStartNfa_1(1, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(1, active02, active12);
            return 2;
        }
    }

    private int jjMoveStringLiteralDfa3_1(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_1(1, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa4_1(active02, 4398046511104L, active12, 0L);
                case LasmConstants.MAXSTACKSIZE /* 67 */:
                    return jjMoveStringLiteralDfa4_1(active02, 2251799880794112L, active12, 0L);
                case LasmConstants.UPVAL /* 68 */:
                    if ((35184372088832L & active02) != 0) {
                        return jjStopAtPos(3, 45);
                    }
                    return jjMoveStringLiteralDfa4_1(active02, 448L, active12, 0L);
                case LasmConstants.LINE /* 69 */:
                    if ((32 & active02) != 0) {
                        return jjStopAtPos(3, 5);
                    }
                    break;
                case LasmConstants.LOCAL /* 70 */:
                    if ((65536 & active02) != 0) {
                        return jjStopAtPos(3, 16);
                    }
                    break;
                case LasmConstants.LABEL /* 76 */:
                    if ((8589934592L & active02) != 0) {
                        return jjStopAtPos(3, 33);
                    }
                    return jjMoveStringLiteralDfa4_1(active02, 1185410973696L, active12, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa4_1(active02, 4503599627370496L, active12, 0L);
                case LasmConstants.HEX /* 80 */:
                    return jjMoveStringLiteralDfa4_1(active02, 36028934457917440L, active12, 0L);
                case 'R':
                    if ((140737488355328L & active02) != 0) {
                        return jjStopAtPos(3, 47);
                    }
                    return jjMoveStringLiteralDfa4_1(active02, 824633720832L, active12, 0L);
                case LasmConstants.NL /* 83 */:
                    return jjMoveStringLiteralDfa4_1(active02, 1128098930098176L, active12, 0L);
                case LasmConstants.FLOAT /* 84 */:
                    if ((2147483648L & active02) != 0) {
                        this.jjmatchedKind = 31;
                        this.jjmatchedPos = 3;
                    } else if ((17592186044416L & active02) != 0) {
                        return jjStopAtPos(3, 44);
                    }
                    return jjMoveStringLiteralDfa4_1(active02, 4295023616L, active12, 0L);
                case LasmConstants.DIGIT /* 85 */:
                    return jjMoveStringLiteralDfa4_1(active02, 34359747072L, active12, 0L);
                case LasmConstants.EXP /* 86 */:
                    if ((8796093022208L & active02) != 0) {
                        return jjStopAtPos(3, 43);
                    }
                    break;
                case '_':
                    return jjMoveStringLiteralDfa4_1(active02, 18014398509481984L, active12, 4L);
                case 'c':
                    return jjMoveStringLiteralDfa4_1(active02, 0L, active12, 64L);
                case Message.ERROR_FATAL /* 100 */:
                    if ((512 & active12) != 0) {
                        this.jjmatchedKind = 73;
                        this.jjmatchedPos = 3;
                    }
                    return jjMoveStringLiteralDfa4_1(active02, 0L, active12, 128L);
                case 'e':
                    if ((144115188075855872L & active02) != 0) {
                        return jjStopAtPos(3, 57);
                    }
                    break;
                case 'm':
                    return jjMoveStringLiteralDfa4_1(active02, 0L, active12, 2L);
                case 'n':
                    return jjMoveStringLiteralDfa4_1(active02, Long.MIN_VALUE, active12, 288L);
                case 's':
                    return jjMoveStringLiteralDfa4_1(active02, 288230376151711744L, active12, 1L);
                case 'u':
                    return jjMoveStringLiteralDfa4_1(active02, 4611686018427387904L, active12, 0L);
                case 'v':
                    return jjMoveStringLiteralDfa4_1(active02, 0L, active12, 16L);
                case 'x':
                    return jjMoveStringLiteralDfa4_1(active02, 0L, active12, 8L);
            }
            return jjStartNfa_1(2, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(2, active02, active12);
            return 3;
        }
    }

    private int jjMoveStringLiteralDfa4_1(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_1(2, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case ' ':
                    return jjMoveStringLiteralDfa5_1(active02, 0L, active12, 128L);
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa5_1(active02, 67165184L, active12, 0L);
                case LasmConstants.IS_VARARG /* 66 */:
                    return jjMoveStringLiteralDfa5_1(active02, 128L, active12, 0L);
                case LasmConstants.MAXSTACKSIZE /* 67 */:
                    return jjMoveStringLiteralDfa5_1(active02, 292057776128L, active12, 0L);
                case LasmConstants.END /* 73 */:
                    return jjMoveStringLiteralDfa5_1(active02, 1099511627776L, active12, 0L);
                case LasmConstants.RANGE /* 75 */:
                    if ((64 & active02) != 0) {
                        return jjStopAtPos(4, 6);
                    }
                    break;
                case LasmConstants.LABEL /* 76 */:
                    return jjMoveStringLiteralDfa5_1(active02, 549755813888L, active12, 0L);
                case LasmConstants.U /* 78 */:
                    return jjMoveStringLiteralDfa5_1(active02, 256L, active12, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa5_1(active02, 68719476736L, active12, 0L);
                case LasmConstants.HEX /* 80 */:
                    return jjMoveStringLiteralDfa5_1(active02, 8704L, active12, 0L);
                case 'R':
                    return jjMoveStringLiteralDfa5_1(active02, 4569845202944L, active12, 0L);
                case LasmConstants.NL /* 83 */:
                    return jjMoveStringLiteralDfa5_1(active02, 4294967296L, active12, 0L);
                case LasmConstants.FLOAT /* 84 */:
                    if ((1125899906842624L & active02) != 0) {
                        return jjStopAtPos(4, 50);
                    }
                    return jjMoveStringLiteralDfa5_1(active02, 18014398509481984L, active12, 0L);
                case LasmConstants.DIGIT /* 85 */:
                    return jjMoveStringLiteralDfa5_1(active02, 2199023255552L, active12, 0L);
                case '[':
                    if ((2251799813685248L & active02) != 0) {
                        return jjStopAtPos(4, 51);
                    }
                    if ((4503599627370496L & active02) != 0) {
                        return jjStopAtPos(4, 52);
                    }
                    break;
                case '_':
                    return jjMoveStringLiteralDfa5_1(active02, 36028797018963968L, active12, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa5_1(active02, 0L, active12, 80L);
                case 'c':
                    if ((256 & active12) != 0) {
                        return jjStopAtPos(4, 72);
                    }
                    break;
                case 'e':
                    if ((288230376151711744L & active02) != 0) {
                        return jjStopAtPos(4, 58);
                    }
                    if ((32 & active12) != 0) {
                        this.jjmatchedKind = 69;
                        this.jjmatchedPos = 4;
                    }
                    return jjMoveStringLiteralDfa5_1(active02, Long.MIN_VALUE, active12, 0L);
                case 'p':
                    return jjMoveStringLiteralDfa5_1(active02, 0L, active12, 2L);
                case 'r':
                    return jjMoveStringLiteralDfa5_1(active02, 4611686018427387904L, active12, 0L);
                case 's':
                    return jjMoveStringLiteralDfa5_1(active02, 0L, active12, 8L);
                case 't':
                    return jjMoveStringLiteralDfa5_1(active02, 0L, active12, 1L);
                case 'v':
                    return jjMoveStringLiteralDfa5_1(active02, 0L, active12, 4L);
            }
            return jjStartNfa_1(3, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(3, active02, active12);
            return 4;
        }
    }

    private int jjMoveStringLiteralDfa5_1(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_1(3, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa6_1(active02, 292057776128L, active12, 0L);
                case LasmConstants.IS_VARARG /* 66 */:
                    return jjMoveStringLiteralDfa6_1(active02, 56320L, active12, 0L);
                case LasmConstants.LINE /* 69 */:
                    return jjMoveStringLiteralDfa6_1(active02, 141733920768L, active12, 0L);
                case LasmConstants.END_LOCAL /* 71 */:
                    if ((4398046511104L & active02) != 0) {
                        return jjStopAtPos(5, 42);
                    }
                    break;
                case LasmConstants.END /* 73 */:
                    return jjMoveStringLiteralDfa6_1(active02, 256L, active12, 0L);
                case LasmConstants.U /* 78 */:
                    if ((34359738368L & active02) != 0) {
                        return jjStopAtPos(5, 35);
                    }
                    return jjMoveStringLiteralDfa6_1(active02, 36028797018963968L, active12, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa6_1(active02, 18015016984772736L, active12, 0L);
                case 'R':
                    return jjMoveStringLiteralDfa6_1(active02, 2199023255552L, active12, 0L);
                case LasmConstants.NL /* 83 */:
                    return jjMoveStringLiteralDfa6_1(active02, 1099511627776L, active12, 0L);
                case LasmConstants.FLOAT /* 84 */:
                    if ((67108864 & active02) != 0) {
                        return jjStopAtPos(5, 26);
                    }
                    break;
                case LasmConstants.EXP /* 86 */:
                    return jjMoveStringLiteralDfa6_1(active02, 8704L, active12, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa6_1(active02, 0L, active12, 6L);
                case 'c':
                    return jjMoveStringLiteralDfa6_1(active02, 4611686018427387904L, active12, 0L);
                case Message.ERROR_FATAL /* 100 */:
                    return jjMoveStringLiteralDfa6_1(active02, Long.MIN_VALUE, active12, 0L);
                case Message.ERROR_GROUP_FAIL /* 108 */:
                    if ((16 & active12) != 0) {
                        return jjStopAtPos(5, 68);
                    }
                    if ((64 & active12) != 0) {
                        return jjStopAtPos(5, 70);
                    }
                    return jjMoveStringLiteralDfa6_1(active02, 0L, active12, 129L);
                case 't':
                    return jjMoveStringLiteralDfa6_1(active02, 0L, active12, 8L);
            }
            return jjStartNfa_1(4, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(4, active02, active12);
            return 5;
        }
    }

    private int jjMoveStringLiteralDfa6_1(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_1(4, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa7_1(active02, 8704L, active12, 0L);
                case LasmConstants.LINE /* 69 */:
                    if ((2199023255552L & active02) != 0) {
                        return jjStopAtPos(6, 41);
                    }
                    return jjMoveStringLiteralDfa7_1(active02, 36028797018963968L, active12, 0L);
                case LasmConstants.LABEL /* 76 */:
                    if ((256 & active02) != 0) {
                        return jjStopAtPos(6, 8);
                    }
                    return jjMoveStringLiteralDfa7_1(active02, 292057827328L, active12, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa7_1(active02, 549755814016L, active12, 0L);
                case LasmConstants.HEX /* 80 */:
                    if ((68719476736L & active02) != 0) {
                        return jjStopAtPos(6, 36);
                    }
                    if ((137438953472L & active02) != 0) {
                        return jjStopAtPos(6, 37);
                    }
                    if ((18014398509481984L & active02) != 0) {
                        return jjStopAtPos(6, 54);
                    }
                    break;
                case LasmConstants.FLOAT /* 84 */:
                    if ((4294967296L & active02) != 0) {
                        return jjStopAtPos(6, 32);
                    }
                    if ((1099511627776L & active02) != 0) {
                        return jjStopAtPos(6, 40);
                    }
                    break;
                case LasmConstants.DIGIT /* 85 */:
                    return jjMoveStringLiteralDfa7_1(active02, 5120L, active12, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa7_1(active02, 0L, active12, 8L);
                case 'e':
                    if ((4611686018427387904L & active02) != 0) {
                        return jjStopAtPos(6, 62);
                    }
                    return jjMoveStringLiteralDfa7_1(active02, Long.MIN_VALUE, active12, 0L);
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa7_1(active02, 0L, active12, 1L);
                case 'o':
                    return jjMoveStringLiteralDfa7_1(active02, 0L, active12, 128L);
                case 'r':
                    return jjMoveStringLiteralDfa7_1(active02, 0L, active12, 6L);
            }
            return jjStartNfa_1(5, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(5, active02, active12);
            return 6;
        }
    }

    private int jjMoveStringLiteralDfa7_1(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_1(5, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.LINE /* 69 */:
                    if ((2048 & active02) != 0) {
                        return jjStopAtPos(7, 11);
                    }
                    if ((16384 & active02) != 0) {
                        return jjStopAtPos(7, 14);
                    }
                    if ((32768 & active02) != 0) {
                        return jjStopAtPos(7, 15);
                    }
                    break;
                case LasmConstants.LABEL /* 76 */:
                    if ((128 & active02) != 0) {
                        return jjStopAtPos(7, 7);
                    }
                    if ((512 & active02) != 0) {
                        return jjStopAtPos(7, 9);
                    }
                    if ((8192 & active02) != 0) {
                        return jjStopAtPos(7, 13);
                    }
                    if ((17179869184L & active02) != 0) {
                        return jjStopAtPos(7, 34);
                    }
                    if ((274877906944L & active02) != 0) {
                        return jjStopAtPos(7, 38);
                    }
                    break;
                case LasmConstants.HEX /* 80 */:
                    if ((1024 & active02) != 0) {
                        return jjStopAtPos(7, 10);
                    }
                    if ((4096 & active02) != 0) {
                        return jjStopAtPos(7, 12);
                    }
                    if ((549755813888L & active02) != 0) {
                        return jjStopAtPos(7, 39);
                    }
                    break;
                case 'X':
                    return jjMoveStringLiteralDfa8_1(active02, 36028797018963968L, active12, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa8_1(active02, 0L, active12, 6L);
                case 'c':
                    return jjMoveStringLiteralDfa8_1(active02, 0L, active12, 136L);
                case 'f':
                    return jjMoveStringLiteralDfa8_1(active02, Long.MIN_VALUE, active12, 0L);
                case 'n':
                    return jjMoveStringLiteralDfa8_1(active02, 0L, active12, 1L);
            }
            return jjStartNfa_1(6, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(6, active02, active12);
            return 7;
        }
    }

    private int jjMoveStringLiteralDfa8_1(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_1(6, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.FLOAT /* 84 */:
                    if ((36028797018963968L & active02) != 0) {
                        return jjStopAtPos(8, 55);
                    }
                    break;
                case 'a':
                    return jjMoveStringLiteralDfa9_1(active02, 0L, active12, 128L);
                case 'e':
                    return jjMoveStringLiteralDfa9_1(active02, 0L, active12, 1L);
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa9_1(active02, Long.MIN_VALUE, active12, 0L);
                case Message.ERROR_SPEEDHACK_FAIL /* 107 */:
                    return jjMoveStringLiteralDfa9_1(active02, 0L, active12, 8L);
                case 'm':
                    return jjMoveStringLiteralDfa9_1(active02, 0L, active12, 2L);
                case 'r':
                    return jjMoveStringLiteralDfa9_1(active02, 0L, active12, 4L);
            }
            return jjStartNfa_1(7, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(7, active02, active12);
            return 8;
        }
    }

    private int jjMoveStringLiteralDfa9_1(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_1(7, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Message.ERROR_FATAL /* 100 */:
                    return jjMoveStringLiteralDfa10_1(active02, 0L, active12, 1L);
                case 'g':
                    if ((4 & active12) != 0) {
                        return jjStopAtPos(9, 66);
                    }
                    break;
                case Message.ERROR_GROUP_FAIL /* 108 */:
                    if ((128 & active12) != 0) {
                        return jjStopAtPos(9, 71);
                    }
                    break;
                case 'n':
                    return jjMoveStringLiteralDfa10_1(active02, Long.MIN_VALUE, active12, 0L);
                case 's':
                    if ((2 & active12) != 0) {
                        return jjStopAtPos(9, 65);
                    }
                    return jjMoveStringLiteralDfa10_1(active02, 0L, active12, 8L);
            }
            return jjStartNfa_1(8, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(8, active02, active12);
            return 9;
        }
    }

    private int jjMoveStringLiteralDfa10_1(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_1(8, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'e':
                    return jjMoveStringLiteralDfa11_1(active02, Long.MIN_VALUE, active12, 1L);
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa11_1(active02, 0L, active12, 8L);
                default:
                    return jjStartNfa_1(9, active02, active12);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(9, active02, active12);
            return 10;
        }
    }

    private int jjMoveStringLiteralDfa11_1(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_1(9, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Message.ERROR_FATAL /* 100 */:
                    if ((Long.MIN_VALUE & active02) != 0) {
                        return jjStopAtPos(11, 63);
                    }
                    break;
                case 'f':
                    return jjMoveStringLiteralDfa12_1(active02, 0L, active12, 1L);
                case 'z':
                    return jjMoveStringLiteralDfa12_1(active02, 0L, active12, 8L);
            }
            return jjStartNfa_1(10, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(10, active02, active12);
            return 11;
        }
    }

    private int jjMoveStringLiteralDfa12_1(long old0, long active0, long old1, long active1) {
        long active12 = active1 & old1;
        if (((active0 & old0) | active12) == 0) {
            return jjStartNfa_1(10, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'e':
                    if ((8 & active12) != 0) {
                        return jjStopAtPos(12, 67);
                    }
                    break;
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa13_1(active12, 1L);
            }
            return jjStartNfa_1(11, 0L, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(11, 0L, active12);
            return 12;
        }
    }

    private int jjMoveStringLiteralDfa13_1(long old1, long active1) {
        long active12 = active1 & old1;
        if (active12 == 0) {
            return jjStartNfa_1(11, 0L, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'n':
                    return jjMoveStringLiteralDfa14_1(active12, 1L);
                default:
                    return jjStartNfa_1(12, 0L, active12);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(12, 0L, active12);
            return 13;
        }
    }

    private int jjMoveStringLiteralDfa14_1(long old1, long active1) {
        long active12 = active1 & old1;
        if (active12 == 0) {
            return jjStartNfa_1(12, 0L, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'e':
                    return jjMoveStringLiteralDfa15_1(active12, 1L);
                default:
                    return jjStartNfa_1(13, 0L, active12);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(13, 0L, active12);
            return 14;
        }
    }

    private int jjMoveStringLiteralDfa15_1(long old1, long active1) {
        long active12 = active1 & old1;
        if (active12 == 0) {
            return jjStartNfa_1(13, 0L, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Message.ERROR_FATAL /* 100 */:
                    if ((1 & active12) != 0) {
                        return jjStopAtPos(15, 64);
                    }
                    break;
            }
            return jjStartNfa_1(14, 0L, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_1(14, 0L, active12);
            return 15;
        }
    }

    private int jjStartNfaWithStates_1(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
            return jjMoveNfa_1(state, pos + 1);
        } catch (IOException e) {
            return pos + 1;
        }
    }

    private int jjMoveNfa_1(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 51;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            int i2 = this.jjround + 1;
            this.jjround = i2;
            if (i2 == Integer.MAX_VALUE) {
                ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1 << this.curChar;
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 0:
                            if ((9216 & l) != 0) {
                                if (kind > 74) {
                                    kind = 74;
                                }
                                jjCheckNAddStates(12, 15);
                            }
                            break;
                        case 1:
                            if ((4294971904L & l) != 0) {
                                jjCheckNAddTwoStates(1, 2);
                            }
                            break;
                        case 2:
                            if (this.curChar == ';') {
                                jjCheckNAddStates(0, 2);
                            }
                            break;
                        case 3:
                            if (((-9217) & l) != 0) {
                                jjCheckNAddStates(0, 2);
                            }
                            break;
                        case 4:
                            if (this.curChar == '\n') {
                                if (kind > 74) {
                                    kind = 74;
                                }
                                jjCheckNAddStates(12, 15);
                            }
                            break;
                        case 5:
                            if (this.curChar == '\r') {
                                int[] iArr = this.jjstateSet;
                                int i3 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i3 + 1;
                                iArr[i3] = 4;
                            }
                            break;
                        case 6:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 60) {
                                    kind = 60;
                                }
                                jjCheckNAddStates(71, 76);
                            } else if ((4294971904L & l) != 0) {
                                jjCheckNAddStates(77, 80);
                            } else if ((9216 & l) != 0) {
                                if (kind > 74) {
                                    kind = 74;
                                }
                                jjCheckNAddStates(12, 15);
                            } else if (this.curChar == '-') {
                                jjCheckNAddStates(81, 83);
                            } else if (this.curChar == ';') {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                jjCheckNAddStates(84, 87);
                            } else if (this.curChar == '.') {
                                jjCheckNAdd(27);
                            } else if (this.curChar == '\'') {
                                jjCheckNAddStates(22, 24);
                            } else if (this.curChar == '\"') {
                                jjCheckNAddStates(25, 27);
                            } else if (this.curChar == ':') {
                                int[] iArr2 = this.jjstateSet;
                                int i4 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i4 + 1;
                                iArr2[i4] = 7;
                            }
                            if (this.curChar == '0') {
                                int[] iArr3 = this.jjstateSet;
                                int i5 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i5 + 1;
                                iArr3[i5] = 14;
                            } else if (this.curChar == '\r') {
                                int[] iArr4 = this.jjstateSet;
                                int i6 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i6 + 1;
                                iArr4[i6] = 4;
                            }
                            break;
                        case 8:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 76) {
                                    kind = 76;
                                }
                                int[] iArr5 = this.jjstateSet;
                                int i7 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i7 + 1;
                                iArr5[i7] = 8;
                            }
                            break;
                        case 10:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 77) {
                                    kind = 77;
                                }
                                int[] iArr6 = this.jjstateSet;
                                int i8 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i8 + 1;
                                iArr6[i8] = 10;
                            }
                            break;
                        case 12:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 78) {
                                    kind = 78;
                                }
                                int[] iArr7 = this.jjstateSet;
                                int i9 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i9 + 1;
                                iArr7[i9] = 12;
                            }
                            break;
                        case 13:
                            if (this.curChar == '0') {
                                int[] iArr8 = this.jjstateSet;
                                int i10 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i10 + 1;
                                iArr8[i10] = 14;
                            }
                            break;
                        case 15:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 80) {
                                    kind = 80;
                                }
                                int[] iArr9 = this.jjstateSet;
                                int i11 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i11 + 1;
                                iArr9[i11] = 15;
                            }
                            break;
                        case 16:
                            if (this.curChar == '\"') {
                                jjCheckNAddStates(25, 27);
                            }
                            break;
                        case 18:
                            jjCheckNAddStates(25, 27);
                            break;
                        case 19:
                            if (((-17179869185L) & l) != 0) {
                                jjCheckNAddStates(25, 27);
                            }
                            break;
                        case 20:
                            if (this.curChar == '\"' && kind > 81) {
                                kind = 81;
                            }
                            break;
                        case 21:
                            if (this.curChar == '\'') {
                                jjCheckNAddStates(22, 24);
                            }
                            break;
                        case 23:
                            jjCheckNAddStates(22, 24);
                            break;
                        case 24:
                            if (((-549755813889L) & l) != 0) {
                                jjCheckNAddStates(22, 24);
                            }
                            break;
                        case 25:
                            if (this.curChar == '\'' && kind > 81) {
                                kind = 81;
                            }
                            break;
                        case 26:
                            if (this.curChar == '.') {
                                jjCheckNAdd(27);
                            }
                            break;
                        case 27:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAddTwoStates(27, 28);
                            }
                            break;
                        case 29:
                            if ((43980465111040L & l) != 0) {
                                jjCheckNAdd(30);
                            }
                            break;
                        case 30:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAdd(30);
                            }
                            break;
                        case 31:
                            if ((4294971904L & l) != 0) {
                                jjCheckNAddStates(77, 80);
                            }
                            break;
                        case 32:
                            if ((4294971904L & l) != 0) {
                                jjCheckNAddTwoStates(32, 33);
                            }
                            break;
                        case 33:
                            if (this.curChar == ';') {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                jjCheckNAdd(34);
                            }
                            break;
                        case 34:
                            if (((-9217) & l) != 0) {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                jjCheckNAdd(34);
                            }
                            break;
                        case 35:
                            if (this.curChar == ';') {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                jjCheckNAddStates(84, 87);
                            }
                            break;
                        case 36:
                            if (this.curChar == '-') {
                                jjCheckNAddStates(81, 83);
                            }
                            break;
                        case 37:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 60) {
                                    kind = 60;
                                }
                                jjCheckNAdd(37);
                            }
                            break;
                        case 38:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAddStates(88, 91);
                            }
                            break;
                        case 39:
                            if ((287948901175001088L & l) != 0) {
                                jjCheckNAddTwoStates(39, 40);
                            }
                            break;
                        case 40:
                            if (this.curChar == '.') {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAddTwoStates(41, 42);
                            }
                            break;
                        case 41:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAddTwoStates(41, 42);
                            }
                            break;
                        case 43:
                            if ((43980465111040L & l) != 0) {
                                jjCheckNAdd(44);
                            }
                            break;
                        case 44:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAdd(44);
                            }
                            break;
                        case 45:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAddTwoStates(45, 46);
                            }
                            break;
                        case 47:
                            if ((43980465111040L & l) != 0) {
                                jjCheckNAdd(48);
                            }
                            break;
                        case LasmConstants.SHL /* 48 */:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAdd(48);
                            }
                            break;
                        case LasmConstants.SHR /* 49 */:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 60) {
                                    kind = 60;
                                }
                                jjCheckNAddStates(71, 76);
                            }
                            break;
                        case 50:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 79) {
                                    kind = 79;
                                }
                                jjCheckNAdd(50);
                            }
                            break;
                        case LasmConstants.PROTO /* 51 */:
                            if ((4294971904L & l) != 0) {
                                jjCheckNAddTwoStates(1, 2);
                            } else if (this.curChar == ';') {
                                jjCheckNAddStates(0, 2);
                            }
                            if ((4294971904L & l) != 0) {
                                jjCheckNAddTwoStates(32, 33);
                            } else if (this.curChar == ';') {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                jjCheckNAdd(34);
                            }
                            break;
                    }
                } while (i != startsAt);
            } else if (this.curChar < 128) {
                long l2 = 1 << (this.curChar & '?');
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 3:
                            jjAddStates(0, 2);
                            break;
                        case 6:
                            if (this.curChar == 'u') {
                                int[] iArr10 = this.jjstateSet;
                                int i12 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i12 + 1;
                                iArr10[i12] = 12;
                            } else if (this.curChar == 'v') {
                                int[] iArr11 = this.jjstateSet;
                                int i13 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i13 + 1;
                                iArr11[i13] = 10;
                            }
                            break;
                        case 7:
                        case 8:
                            if ((576460745995190270L & l2) != 0) {
                                if (kind > 76) {
                                    kind = 76;
                                }
                                jjCheckNAdd(8);
                            }
                            break;
                        case 9:
                            if (this.curChar == 'v') {
                                int[] iArr12 = this.jjstateSet;
                                int i14 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i14 + 1;
                                iArr12[i14] = 10;
                            }
                            break;
                        case 11:
                            if (this.curChar == 'u') {
                                int[] iArr13 = this.jjstateSet;
                                int i15 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i15 + 1;
                                iArr13[i15] = 12;
                            }
                            break;
                        case 14:
                            if ((72057594054705152L & l2) != 0) {
                                jjCheckNAdd(15);
                            }
                            break;
                        case 15:
                            if ((541165879422L & l2) != 0) {
                                if (kind > 80) {
                                    kind = 80;
                                }
                                jjCheckNAdd(15);
                            }
                            break;
                        case 17:
                            if (this.curChar == '\\') {
                                int[] iArr14 = this.jjstateSet;
                                int i16 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i16 + 1;
                                iArr14[i16] = 18;
                            }
                            break;
                        case 18:
                            jjCheckNAddStates(25, 27);
                            break;
                        case 19:
                            if (((-268435457) & l2) != 0) {
                                jjCheckNAddStates(25, 27);
                            }
                            break;
                        case 22:
                            if (this.curChar == '\\') {
                                int[] iArr15 = this.jjstateSet;
                                int i17 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i17 + 1;
                                iArr15[i17] = 23;
                            }
                            break;
                        case 23:
                            jjCheckNAddStates(22, 24);
                            break;
                        case 24:
                            if (((-268435457) & l2) != 0) {
                                jjCheckNAddStates(22, 24);
                            }
                            break;
                        case 28:
                            if ((137438953504L & l2) != 0) {
                                jjAddStates(92, 93);
                            }
                            break;
                        case 34:
                            if (kind > 4) {
                                kind = 4;
                            }
                            int[] iArr16 = this.jjstateSet;
                            int i18 = this.jjnewStateCnt;
                            this.jjnewStateCnt = i18 + 1;
                            iArr16[i18] = 34;
                            break;
                        case 42:
                            if ((137438953504L & l2) != 0) {
                                jjAddStates(94, 95);
                            }
                            break;
                        case 46:
                            if ((137438953504L & l2) != 0) {
                                jjAddStates(63, 64);
                            }
                            break;
                    }
                } while (i != startsAt);
            } else {
                int hiByte = this.curChar >> '\b';
                int i1 = hiByte >> 6;
                long l1 = 1 << (hiByte & 63);
                int i22 = (this.curChar & 255) >> 6;
                long l22 = 1 << (this.curChar & '?');
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 3:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjAddStates(0, 2);
                            }
                            break;
                        case 18:
                        case 19:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjCheckNAddStates(25, 27);
                            }
                            break;
                        case 23:
                        case 24:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjCheckNAddStates(22, 24);
                            }
                            break;
                        case 34:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                int[] iArr17 = this.jjstateSet;
                                int i19 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i19 + 1;
                                iArr17[i19] = 34;
                            }
                            break;
                    }
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            curPos++;
            i = this.jjnewStateCnt;
            this.jjnewStateCnt = startsAt;
            startsAt = 51 - startsAt;
            if (i != startsAt) {
                try {
                    this.curChar = this.input_stream.readChar();
                } catch (IOException e) {
                }
            }
            return curPos;
        }
    }

    private final int jjStopStringLiteralDfa_0(int pos, long active0, long active1) {
        switch (pos) {
            case 0:
                if ((576460752303423456L & active0) != 0) {
                    this.jjmatchedKind = 61;
                    return 1;
                }
                if ((14 & active0) != 0) {
                    return 52;
                }
                return (((-4611686018427387904L) & active0) == 0 && (3071 & active1) == 0) ? -1 : 30;
            case 1:
                if ((9007201167343616L & active0) != 0) {
                    return 1;
                }
                if ((567453551136079840L & active0) == 0) {
                    return -1;
                }
                if (this.jjmatchedPos == 1) {
                    return 1;
                }
                this.jjmatchedKind = 61;
                this.jjmatchedPos = 1;
                return 1;
            case 2:
                if ((72972387913433088L & active0) != 0) {
                    return 1;
                }
                if ((494481163256201184L & active0) == 0) {
                    return -1;
                }
                this.jjmatchedKind = 61;
                this.jjmatchedPos = 2;
                return 1;
            case 3:
                if ((144317513247817760L & active0) != 0) {
                    return 1;
                }
                if ((350163650008383424L & active0) == 0) {
                    return -1;
                }
                if (this.jjmatchedPos == 3) {
                    return 1;
                }
                this.jjmatchedKind = 61;
                this.jjmatchedPos = 3;
                return 1;
            case 4:
                if ((289356276058554432L & active0) != 0) {
                    return 1;
                }
                if ((54051978803740544L & active0) == 0) {
                    return -1;
                }
                if (this.jjmatchedPos == 4) {
                    return 1;
                }
                this.jjmatchedKind = 61;
                this.jjmatchedPos = 4;
                return 1;
            case 5:
                if ((4432473358336L & active0) != 0) {
                    return 1;
                }
                if ((54047546330382208L & active0) == 0) {
                    return -1;
                }
                this.jjmatchedKind = 61;
                this.jjmatchedPos = 5;
                return 1;
            case 6:
                if ((36029638832619136L & active0) == 0) {
                    return (18017907497763072L & active0) == 0 ? -1 : 1;
                }
                this.jjmatchedKind = 61;
                this.jjmatchedPos = 6;
                return 1;
            case 7:
                if ((36028797018963968L & active0) == 0) {
                    return (841813655168L & active0) == 0 ? -1 : 1;
                }
                this.jjmatchedKind = 61;
                this.jjmatchedPos = 7;
                return 1;
            case 8:
                return (36028797018963968L & active0) == 0 ? -1 : 1;
            default:
                return -1;
        }
    }

    private final int jjStartNfa_0(int pos, long active0, long active1) {
        return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0, active1), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case '\t':
                return jjStartNfaWithStates_0(0, 2, 52);
            case '\f':
                return jjStartNfaWithStates_0(0, 3, 52);
            case ' ':
                return jjStartNfaWithStates_0(0, 1, 52);
            case '.':
                return jjMoveStringLiteralDfa1_0(-4611686018427387904L, 3071L);
            case LasmConstants.NUMPARAMS /* 65 */:
                return jjMoveStringLiteralDfa1_0(131072L, 0L);
            case LasmConstants.IS_VARARG /* 66 */:
                return jjMoveStringLiteralDfa1_0(263882790666240L, 0L);
            case LasmConstants.MAXSTACKSIZE /* 67 */:
                return jjMoveStringLiteralDfa1_0(1128107587141632L, 0L);
            case LasmConstants.UPVAL /* 68 */:
                return jjMoveStringLiteralDfa1_0(1048576L, 0L);
            case LasmConstants.LINE /* 69 */:
                return jjMoveStringLiteralDfa1_0(268435456L, 0L);
            case LasmConstants.LOCAL /* 70 */:
                return jjMoveStringLiteralDfa1_0(2252005972115456L, 0L);
            case LasmConstants.END_LOCAL /* 71 */:
                return jjMoveStringLiteralDfa1_0(4503599627374080L, 0L);
            case LasmConstants.END /* 73 */:
                return jjMoveStringLiteralDfa1_0(8796093022208L, 0L);
            case LasmConstants.EOL /* 74 */:
                return jjMoveStringLiteralDfa1_0(134217728L, 0L);
            case LasmConstants.LABEL /* 76 */:
                return jjMoveStringLiteralDfa1_0(1644167616L, 0L);
            case LasmConstants.V /* 77 */:
                return jjMoveStringLiteralDfa1_0(2621472L, 0L);
            case LasmConstants.U /* 78 */:
                return jjMoveStringLiteralDfa1_0(16809984L, 0L);
            case LasmConstants.INT /* 79 */:
                return jjMoveStringLiteralDfa1_0(9007199254740992L, 0L);
            case LasmConstants.HEX /* 80 */:
                return jjMoveStringLiteralDfa1_0(4194304L, 0L);
            case 'R':
                return jjMoveStringLiteralDfa1_0(34359738368L, 0L);
            case LasmConstants.NL /* 83 */:
                return jjMoveStringLiteralDfa1_0(54888719970562048L, 0L);
            case LasmConstants.FLOAT /* 84 */:
                return jjMoveStringLiteralDfa1_0(848256040960L, 0L);
            case LasmConstants.DIGIT /* 85 */:
                return jjMoveStringLiteralDfa1_0(8388608L, 0L);
            case LasmConstants.EXP /* 86 */:
                return jjMoveStringLiteralDfa1_0(4398046511104L, 0L);
            case 'f':
                return jjMoveStringLiteralDfa1_0(288230376151711744L, 0L);
            case 'n':
                return jjMoveStringLiteralDfa1_0(72057594037927936L, 0L);
            case 't':
                return jjMoveStringLiteralDfa1_0(144115188075855872L, 0L);
            default:
                return jjMoveNfa_0(0, 0);
        }
    }

    private int jjMoveStringLiteralDfa1_0(long active0, long active1) {
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case '.':
                    if ((2048 & active1) != 0) {
                        return jjStopAtPos(1, 75);
                    }
                    break;
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa2_0(active0, 39608188403712L, active1, 0L);
                case LasmConstants.UPVAL /* 68 */:
                    return jjMoveStringLiteralDfa2_0(active0, 8796093153280L, active1, 0L);
                case LasmConstants.LINE /* 69 */:
                    if ((1073741824 & active0) != 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 1;
                    }
                    return jjMoveStringLiteralDfa2_0(active0, 18015538856984064L, active1, 0L);
                case LasmConstants.LOCAL /* 70 */:
                    return jjMoveStringLiteralDfa2_0(active0, 824633720832L, active1, 0L);
                case LasmConstants.FUNC /* 72 */:
                    return jjMoveStringLiteralDfa2_0(active0, 844424930131968L, active1, 0L);
                case LasmConstants.END /* 73 */:
                    return jjMoveStringLiteralDfa2_0(active0, 1048576L, active1, 0L);
                case LasmConstants.RANGE /* 75 */:
                    return jjMoveStringLiteralDfa2_0(active0, 36028797018963968L, active1, 0L);
                case LasmConstants.LABEL /* 76 */:
                    return jjMoveStringLiteralDfa2_0(active0, 2199023255552L, active1, 0L);
                case LasmConstants.V /* 77 */:
                    return jjMoveStringLiteralDfa2_0(active0, 134217728L, active1, 0L);
                case LasmConstants.U /* 78 */:
                    return jjMoveStringLiteralDfa2_0(active0, 17592194433024L, active1, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa2_0(active0, 5700074526999008L, active1, 0L);
                case LasmConstants.HEX /* 80 */:
                    if ((9007199254740992L & active0) != 0) {
                        return jjStartNfaWithStates_0(1, 53, 1);
                    }
                    break;
                case LasmConstants.STRING /* 81 */:
                    if ((268435456 & active0) != 0) {
                        return jjStartNfaWithStates_0(1, 28, 1);
                    }
                    break;
                case LasmConstants.FLOAT /* 84 */:
                    if ((536870912 & active0) != 0) {
                        return jjStartNfaWithStates_0(1, 29, 1);
                    }
                    break;
                case LasmConstants.DIGIT /* 85 */:
                    return jjMoveStringLiteralDfa2_0(active0, 2251799814471680L, active1, 0L);
                case 'X':
                    return jjMoveStringLiteralDfa2_0(active0, 140737488355328L, active1, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa2_0(active0, 288230376151711744L, active1, 0L);
                case 'e':
                    return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 640L);
                case 'f':
                    return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 256L);
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa2_0(active0, 72057594037927936L, active1, 4L);
                case Message.ERROR_GROUP_FAIL /* 108 */:
                    return jjMoveStringLiteralDfa2_0(active0, Long.MIN_VALUE, active1, 97L);
                case 'm':
                    return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 8L);
                case 'n':
                    return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 2L);
                case 'r':
                    return jjMoveStringLiteralDfa2_0(active0, 144115188075855872L, active1, 0L);
                case 's':
                    return jjMoveStringLiteralDfa2_0(active0, 4611686018427387904L, active1, 0L);
                case 'u':
                    return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 16L);
            }
            return jjStartNfa_0(0, active0, active1);
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(0, active0, active1);
            return 1;
        }
    }

    private int jjMoveStringLiteralDfa2_0(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_0(0, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa3_0(active02, 448L, active12, 0L);
                case LasmConstants.IS_VARARG /* 66 */:
                    if ((262144 & active02) != 0) {
                        return jjStartNfaWithStates_0(2, 18, 1);
                    }
                    break;
                case LasmConstants.UPVAL /* 68 */:
                    if ((131072 & active02) != 0) {
                        return jjStartNfaWithStates_0(2, 17, 1);
                    }
                    if ((2097152 & active02) != 0) {
                        return jjStartNfaWithStates_0(2, 21, 1);
                    }
                    break;
                case LasmConstants.END /* 73 */:
                    return jjMoveStringLiteralDfa3_0(active02, 36037610291855360L, active12, 0L);
                case LasmConstants.LABEL /* 76 */:
                    if ((524288 & active02) != 0) {
                        return jjStartNfaWithStates_0(2, 19, 1);
                    }
                    if ((281474976710656L & active02) != 0) {
                        return jjStartNfaWithStates_0(2, 48, 1);
                    }
                    return jjMoveStringLiteralDfa3_0(active02, 8590000128L, active12, 0L);
                case LasmConstants.V /* 77 */:
                    if ((8388608 & active02) != 0) {
                        return jjStartNfaWithStates_0(2, 23, 1);
                    }
                    break;
                case LasmConstants.U /* 78 */:
                    if ((33554432 & active02) != 0) {
                        return jjStartNfaWithStates_0(2, 25, 1);
                    }
                    return jjMoveStringLiteralDfa3_0(active02, 3412884159725568L, active12, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa3_0(active02, 161353331376128L, active12, 0L);
                case LasmConstants.HEX /* 80 */:
                    if ((134217728 & active02) != 0) {
                        return jjStartNfaWithStates_0(2, 27, 1);
                    }
                    break;
                case 'R':
                    if ((70368744177664L & active02) != 0) {
                        return jjStartNfaWithStates_0(2, 46, 1);
                    }
                    if ((562949953421312L & active02) != 0) {
                        return jjStartNfaWithStates_0(2, 49, 1);
                    }
                    return jjMoveStringLiteralDfa3_0(active02, 4604204941312L, active12, 0L);
                case LasmConstants.NL /* 83 */:
                    return jjMoveStringLiteralDfa3_0(active02, 6442450944L, active12, 0L);
                case LasmConstants.FLOAT /* 84 */:
                    if ((16777216 & active02) != 0) {
                        return jjStartNfaWithStates_0(2, 24, 1);
                    }
                    return jjMoveStringLiteralDfa3_0(active02, 22519132008250880L, active12, 0L);
                case LasmConstants.EXP /* 86 */:
                    if ((1048576 & active02) != 0) {
                        return jjStartNfaWithStates_0(2, 20, 1);
                    }
                    return jjMoveStringLiteralDfa3_0(active02, 32L, active12, 0L);
                case LasmConstants.HEXDIGIT /* 87 */:
                    if ((4194304 & active02) != 0) {
                        return jjStartNfaWithStates_0(2, 22, 1);
                    }
                    return jjMoveStringLiteralDfa3_0(active02, 32768L, active12, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa3_0(active02, 0L, active12, 9L);
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa3_0(active02, Long.MIN_VALUE, active12, 32L);
                case Message.ERROR_GROUP_FAIL /* 108 */:
                    if ((72057594037927936L & active02) != 0) {
                        return jjStartNfaWithStates_0(2, 56, 1);
                    }
                    return jjMoveStringLiteralDfa3_0(active02, 288230376151711744L, active12, 0L);
                case 'n':
                    return jjMoveStringLiteralDfa3_0(active02, 0L, active12, 640L);
                case 'o':
                    return jjMoveStringLiteralDfa3_0(active02, 4611686018427387904L, active12, 64L);
                case 'p':
                    return jjMoveStringLiteralDfa3_0(active02, 0L, active12, 16L);
                case 's':
                    return jjMoveStringLiteralDfa3_0(active02, 0L, active12, 4L);
                case 'u':
                    return jjMoveStringLiteralDfa3_0(active02, 144115188075855872L, active12, 258L);
            }
            return jjStartNfa_0(1, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(1, active02, active12);
            return 2;
        }
    }

    private int jjMoveStringLiteralDfa3_0(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_0(1, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa4_0(active02, 4398046511104L, active12, 0L);
                case LasmConstants.MAXSTACKSIZE /* 67 */:
                    return jjMoveStringLiteralDfa4_0(active02, 2251799880794112L, active12, 0L);
                case LasmConstants.UPVAL /* 68 */:
                    if ((35184372088832L & active02) != 0) {
                        return jjStartNfaWithStates_0(3, 45, 1);
                    }
                    return jjMoveStringLiteralDfa4_0(active02, 448L, active12, 0L);
                case LasmConstants.LINE /* 69 */:
                    if ((32 & active02) != 0) {
                        return jjStartNfaWithStates_0(3, 5, 1);
                    }
                    break;
                case LasmConstants.LOCAL /* 70 */:
                    if ((65536 & active02) != 0) {
                        return jjStartNfaWithStates_0(3, 16, 1);
                    }
                    break;
                case LasmConstants.LABEL /* 76 */:
                    if ((8589934592L & active02) != 0) {
                        return jjStartNfaWithStates_0(3, 33, 1);
                    }
                    return jjMoveStringLiteralDfa4_0(active02, 1185410973696L, active12, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa4_0(active02, 4503599627370496L, active12, 0L);
                case LasmConstants.HEX /* 80 */:
                    return jjMoveStringLiteralDfa4_0(active02, 36028934457917440L, active12, 0L);
                case 'R':
                    if ((140737488355328L & active02) != 0) {
                        return jjStartNfaWithStates_0(3, 47, 1);
                    }
                    return jjMoveStringLiteralDfa4_0(active02, 824633720832L, active12, 0L);
                case LasmConstants.NL /* 83 */:
                    return jjMoveStringLiteralDfa4_0(active02, 1128098930098176L, active12, 0L);
                case LasmConstants.FLOAT /* 84 */:
                    if ((2147483648L & active02) != 0) {
                        this.jjmatchedKind = 31;
                        this.jjmatchedPos = 3;
                    } else if ((17592186044416L & active02) != 0) {
                        return jjStartNfaWithStates_0(3, 44, 1);
                    }
                    return jjMoveStringLiteralDfa4_0(active02, 4295023616L, active12, 0L);
                case LasmConstants.DIGIT /* 85 */:
                    return jjMoveStringLiteralDfa4_0(active02, 34359747072L, active12, 0L);
                case LasmConstants.EXP /* 86 */:
                    if ((8796093022208L & active02) != 0) {
                        return jjStartNfaWithStates_0(3, 43, 1);
                    }
                    break;
                case '_':
                    return jjMoveStringLiteralDfa4_0(active02, 18014398509481984L, active12, 4L);
                case 'c':
                    return jjMoveStringLiteralDfa4_0(active02, 0L, active12, 64L);
                case Message.ERROR_FATAL /* 100 */:
                    if ((512 & active12) != 0) {
                        this.jjmatchedKind = 73;
                        this.jjmatchedPos = 3;
                    }
                    return jjMoveStringLiteralDfa4_0(active02, 0L, active12, 128L);
                case 'e':
                    if ((144115188075855872L & active02) != 0) {
                        return jjStartNfaWithStates_0(3, 57, 1);
                    }
                    break;
                case 'm':
                    return jjMoveStringLiteralDfa4_0(active02, 0L, active12, 2L);
                case 'n':
                    return jjMoveStringLiteralDfa4_0(active02, Long.MIN_VALUE, active12, 288L);
                case 's':
                    return jjMoveStringLiteralDfa4_0(active02, 288230376151711744L, active12, 1L);
                case 'u':
                    return jjMoveStringLiteralDfa4_0(active02, 4611686018427387904L, active12, 0L);
                case 'v':
                    return jjMoveStringLiteralDfa4_0(active02, 0L, active12, 16L);
                case 'x':
                    return jjMoveStringLiteralDfa4_0(active02, 0L, active12, 8L);
            }
            return jjStartNfa_0(2, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(2, active02, active12);
            return 3;
        }
    }

    private int jjMoveStringLiteralDfa4_0(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_0(2, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case ' ':
                    return jjMoveStringLiteralDfa5_0(active02, 0L, active12, 128L);
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa5_0(active02, 67165184L, active12, 0L);
                case LasmConstants.IS_VARARG /* 66 */:
                    return jjMoveStringLiteralDfa5_0(active02, 128L, active12, 0L);
                case LasmConstants.MAXSTACKSIZE /* 67 */:
                    return jjMoveStringLiteralDfa5_0(active02, 292057776128L, active12, 0L);
                case LasmConstants.END /* 73 */:
                    return jjMoveStringLiteralDfa5_0(active02, 1099511627776L, active12, 0L);
                case LasmConstants.RANGE /* 75 */:
                    if ((64 & active02) != 0) {
                        return jjStartNfaWithStates_0(4, 6, 1);
                    }
                    break;
                case LasmConstants.LABEL /* 76 */:
                    return jjMoveStringLiteralDfa5_0(active02, 549755813888L, active12, 0L);
                case LasmConstants.U /* 78 */:
                    return jjMoveStringLiteralDfa5_0(active02, 256L, active12, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa5_0(active02, 68719476736L, active12, 0L);
                case LasmConstants.HEX /* 80 */:
                    return jjMoveStringLiteralDfa5_0(active02, 8704L, active12, 0L);
                case 'R':
                    return jjMoveStringLiteralDfa5_0(active02, 4569845202944L, active12, 0L);
                case LasmConstants.NL /* 83 */:
                    return jjMoveStringLiteralDfa5_0(active02, 4294967296L, active12, 0L);
                case LasmConstants.FLOAT /* 84 */:
                    if ((1125899906842624L & active02) != 0) {
                        return jjStartNfaWithStates_0(4, 50, 1);
                    }
                    return jjMoveStringLiteralDfa5_0(active02, 18014398509481984L, active12, 0L);
                case LasmConstants.DIGIT /* 85 */:
                    return jjMoveStringLiteralDfa5_0(active02, 2199023255552L, active12, 0L);
                case '[':
                    if ((2251799813685248L & active02) != 0) {
                        return jjStopAtPos(4, 51);
                    }
                    if ((4503599627370496L & active02) != 0) {
                        return jjStopAtPos(4, 52);
                    }
                    break;
                case '_':
                    return jjMoveStringLiteralDfa5_0(active02, 36028797018963968L, active12, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa5_0(active02, 0L, active12, 80L);
                case 'c':
                    if ((256 & active12) != 0) {
                        return jjStopAtPos(4, 72);
                    }
                    break;
                case 'e':
                    if ((288230376151711744L & active02) != 0) {
                        return jjStartNfaWithStates_0(4, 58, 1);
                    }
                    if ((32 & active12) != 0) {
                        this.jjmatchedKind = 69;
                        this.jjmatchedPos = 4;
                    }
                    return jjMoveStringLiteralDfa5_0(active02, Long.MIN_VALUE, active12, 0L);
                case 'p':
                    return jjMoveStringLiteralDfa5_0(active02, 0L, active12, 2L);
                case 'r':
                    return jjMoveStringLiteralDfa5_0(active02, 4611686018427387904L, active12, 0L);
                case 's':
                    return jjMoveStringLiteralDfa5_0(active02, 0L, active12, 8L);
                case 't':
                    return jjMoveStringLiteralDfa5_0(active02, 0L, active12, 1L);
                case 'v':
                    return jjMoveStringLiteralDfa5_0(active02, 0L, active12, 4L);
            }
            return jjStartNfa_0(3, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(3, active02, active12);
            return 4;
        }
    }

    private int jjMoveStringLiteralDfa5_0(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_0(3, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa6_0(active02, 292057776128L, active12, 0L);
                case LasmConstants.IS_VARARG /* 66 */:
                    return jjMoveStringLiteralDfa6_0(active02, 56320L, active12, 0L);
                case LasmConstants.LINE /* 69 */:
                    return jjMoveStringLiteralDfa6_0(active02, 141733920768L, active12, 0L);
                case LasmConstants.END_LOCAL /* 71 */:
                    if ((4398046511104L & active02) != 0) {
                        return jjStartNfaWithStates_0(5, 42, 1);
                    }
                    break;
                case LasmConstants.END /* 73 */:
                    return jjMoveStringLiteralDfa6_0(active02, 256L, active12, 0L);
                case LasmConstants.U /* 78 */:
                    if ((34359738368L & active02) != 0) {
                        return jjStartNfaWithStates_0(5, 35, 1);
                    }
                    return jjMoveStringLiteralDfa6_0(active02, 36028797018963968L, active12, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa6_0(active02, 18015016984772736L, active12, 0L);
                case 'R':
                    return jjMoveStringLiteralDfa6_0(active02, 2199023255552L, active12, 0L);
                case LasmConstants.NL /* 83 */:
                    return jjMoveStringLiteralDfa6_0(active02, 1099511627776L, active12, 0L);
                case LasmConstants.FLOAT /* 84 */:
                    if ((67108864 & active02) != 0) {
                        return jjStartNfaWithStates_0(5, 26, 1);
                    }
                    break;
                case LasmConstants.EXP /* 86 */:
                    return jjMoveStringLiteralDfa6_0(active02, 8704L, active12, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa6_0(active02, 0L, active12, 6L);
                case 'c':
                    return jjMoveStringLiteralDfa6_0(active02, 4611686018427387904L, active12, 0L);
                case Message.ERROR_FATAL /* 100 */:
                    return jjMoveStringLiteralDfa6_0(active02, Long.MIN_VALUE, active12, 0L);
                case Message.ERROR_GROUP_FAIL /* 108 */:
                    if ((16 & active12) != 0) {
                        return jjStopAtPos(5, 68);
                    }
                    if ((64 & active12) != 0) {
                        return jjStopAtPos(5, 70);
                    }
                    return jjMoveStringLiteralDfa6_0(active02, 0L, active12, 129L);
                case 't':
                    return jjMoveStringLiteralDfa6_0(active02, 0L, active12, 8L);
            }
            return jjStartNfa_0(4, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(4, active02, active12);
            return 5;
        }
    }

    private int jjMoveStringLiteralDfa6_0(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_0(4, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.NUMPARAMS /* 65 */:
                    return jjMoveStringLiteralDfa7_0(active02, 8704L, active12, 0L);
                case LasmConstants.LINE /* 69 */:
                    if ((2199023255552L & active02) != 0) {
                        return jjStartNfaWithStates_0(6, 41, 1);
                    }
                    return jjMoveStringLiteralDfa7_0(active02, 36028797018963968L, active12, 0L);
                case LasmConstants.LABEL /* 76 */:
                    if ((256 & active02) != 0) {
                        return jjStartNfaWithStates_0(6, 8, 1);
                    }
                    return jjMoveStringLiteralDfa7_0(active02, 292057827328L, active12, 0L);
                case LasmConstants.INT /* 79 */:
                    return jjMoveStringLiteralDfa7_0(active02, 549755814016L, active12, 0L);
                case LasmConstants.HEX /* 80 */:
                    if ((68719476736L & active02) != 0) {
                        return jjStartNfaWithStates_0(6, 36, 1);
                    }
                    if ((137438953472L & active02) != 0) {
                        return jjStartNfaWithStates_0(6, 37, 1);
                    }
                    if ((18014398509481984L & active02) != 0) {
                        return jjStartNfaWithStates_0(6, 54, 1);
                    }
                    break;
                case LasmConstants.FLOAT /* 84 */:
                    if ((4294967296L & active02) != 0) {
                        return jjStartNfaWithStates_0(6, 32, 1);
                    }
                    if ((1099511627776L & active02) != 0) {
                        return jjStartNfaWithStates_0(6, 40, 1);
                    }
                    break;
                case LasmConstants.DIGIT /* 85 */:
                    return jjMoveStringLiteralDfa7_0(active02, 5120L, active12, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa7_0(active02, 0L, active12, 8L);
                case 'e':
                    if ((4611686018427387904L & active02) != 0) {
                        return jjStopAtPos(6, 62);
                    }
                    return jjMoveStringLiteralDfa7_0(active02, Long.MIN_VALUE, active12, 0L);
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa7_0(active02, 0L, active12, 1L);
                case 'o':
                    return jjMoveStringLiteralDfa7_0(active02, 0L, active12, 128L);
                case 'r':
                    return jjMoveStringLiteralDfa7_0(active02, 0L, active12, 6L);
            }
            return jjStartNfa_0(5, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(5, active02, active12);
            return 6;
        }
    }

    private int jjMoveStringLiteralDfa7_0(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_0(5, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.LINE /* 69 */:
                    if ((2048 & active02) != 0) {
                        return jjStartNfaWithStates_0(7, 11, 1);
                    }
                    if ((16384 & active02) != 0) {
                        return jjStartNfaWithStates_0(7, 14, 1);
                    }
                    if ((32768 & active02) != 0) {
                        return jjStartNfaWithStates_0(7, 15, 1);
                    }
                    break;
                case LasmConstants.LABEL /* 76 */:
                    if ((128 & active02) != 0) {
                        return jjStartNfaWithStates_0(7, 7, 1);
                    }
                    if ((512 & active02) != 0) {
                        return jjStartNfaWithStates_0(7, 9, 1);
                    }
                    if ((8192 & active02) != 0) {
                        return jjStartNfaWithStates_0(7, 13, 1);
                    }
                    if ((17179869184L & active02) != 0) {
                        return jjStartNfaWithStates_0(7, 34, 1);
                    }
                    if ((274877906944L & active02) != 0) {
                        return jjStartNfaWithStates_0(7, 38, 1);
                    }
                    break;
                case LasmConstants.HEX /* 80 */:
                    if ((1024 & active02) != 0) {
                        return jjStartNfaWithStates_0(7, 10, 1);
                    }
                    if ((4096 & active02) != 0) {
                        return jjStartNfaWithStates_0(7, 12, 1);
                    }
                    if ((549755813888L & active02) != 0) {
                        return jjStartNfaWithStates_0(7, 39, 1);
                    }
                    break;
                case 'X':
                    return jjMoveStringLiteralDfa8_0(active02, 36028797018963968L, active12, 0L);
                case 'a':
                    return jjMoveStringLiteralDfa8_0(active02, 0L, active12, 6L);
                case 'c':
                    return jjMoveStringLiteralDfa8_0(active02, 0L, active12, 136L);
                case 'f':
                    return jjMoveStringLiteralDfa8_0(active02, Long.MIN_VALUE, active12, 0L);
                case 'n':
                    return jjMoveStringLiteralDfa8_0(active02, 0L, active12, 1L);
            }
            return jjStartNfa_0(6, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(6, active02, active12);
            return 7;
        }
    }

    private int jjMoveStringLiteralDfa8_0(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_0(6, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case LasmConstants.FLOAT /* 84 */:
                    if ((36028797018963968L & active02) != 0) {
                        return jjStartNfaWithStates_0(8, 55, 1);
                    }
                    break;
                case 'a':
                    return jjMoveStringLiteralDfa9_0(active02, 0L, active12, 128L);
                case 'e':
                    return jjMoveStringLiteralDfa9_0(active02, 0L, active12, 1L);
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa9_0(active02, Long.MIN_VALUE, active12, 0L);
                case Message.ERROR_SPEEDHACK_FAIL /* 107 */:
                    return jjMoveStringLiteralDfa9_0(active02, 0L, active12, 8L);
                case 'm':
                    return jjMoveStringLiteralDfa9_0(active02, 0L, active12, 2L);
                case 'r':
                    return jjMoveStringLiteralDfa9_0(active02, 0L, active12, 4L);
            }
            return jjStartNfa_0(7, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(7, active02, active12);
            return 8;
        }
    }

    private int jjMoveStringLiteralDfa9_0(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_0(7, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Message.ERROR_FATAL /* 100 */:
                    return jjMoveStringLiteralDfa10_0(active02, 0L, active12, 1L);
                case 'g':
                    if ((4 & active12) != 0) {
                        return jjStopAtPos(9, 66);
                    }
                    break;
                case Message.ERROR_GROUP_FAIL /* 108 */:
                    if ((128 & active12) != 0) {
                        return jjStopAtPos(9, 71);
                    }
                    break;
                case 'n':
                    return jjMoveStringLiteralDfa10_0(active02, Long.MIN_VALUE, active12, 0L);
                case 's':
                    if ((2 & active12) != 0) {
                        return jjStopAtPos(9, 65);
                    }
                    return jjMoveStringLiteralDfa10_0(active02, 0L, active12, 8L);
            }
            return jjStartNfa_0(8, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(8, active02, active12);
            return 9;
        }
    }

    private int jjMoveStringLiteralDfa10_0(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_0(8, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'e':
                    return jjMoveStringLiteralDfa11_0(active02, Long.MIN_VALUE, active12, 1L);
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa11_0(active02, 0L, active12, 8L);
                default:
                    return jjStartNfa_0(9, active02, active12);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(9, active02, active12);
            return 10;
        }
    }

    private int jjMoveStringLiteralDfa11_0(long old0, long active0, long old1, long active1) {
        long active02 = active0 & old0;
        long active12 = active1 & old1;
        if ((active02 | active12) == 0) {
            return jjStartNfa_0(9, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Message.ERROR_FATAL /* 100 */:
                    if ((Long.MIN_VALUE & active02) != 0) {
                        return jjStopAtPos(11, 63);
                    }
                    break;
                case 'f':
                    return jjMoveStringLiteralDfa12_0(active02, 0L, active12, 1L);
                case 'z':
                    return jjMoveStringLiteralDfa12_0(active02, 0L, active12, 8L);
            }
            return jjStartNfa_0(10, active02, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(10, active02, active12);
            return 11;
        }
    }

    private int jjMoveStringLiteralDfa12_0(long old0, long active0, long old1, long active1) {
        long active12 = active1 & old1;
        if (((active0 & old0) | active12) == 0) {
            return jjStartNfa_0(10, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'e':
                    if ((8 & active12) != 0) {
                        return jjStopAtPos(12, 67);
                    }
                    break;
                case Message.ERROR_EPERM_ON_ATTACH /* 105 */:
                    return jjMoveStringLiteralDfa13_0(active12, 1L);
            }
            return jjStartNfa_0(11, 0L, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(11, 0L, active12);
            return 12;
        }
    }

    private int jjMoveStringLiteralDfa13_0(long old1, long active1) {
        long active12 = active1 & old1;
        if (active12 == 0) {
            return jjStartNfa_0(11, 0L, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'n':
                    return jjMoveStringLiteralDfa14_0(active12, 1L);
                default:
                    return jjStartNfa_0(12, 0L, active12);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(12, 0L, active12);
            return 13;
        }
    }

    private int jjMoveStringLiteralDfa14_0(long old1, long active1) {
        long active12 = active1 & old1;
        if (active12 == 0) {
            return jjStartNfa_0(12, 0L, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 'e':
                    return jjMoveStringLiteralDfa15_0(active12, 1L);
                default:
                    return jjStartNfa_0(13, 0L, active12);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(13, 0L, active12);
            return 14;
        }
    }

    private int jjMoveStringLiteralDfa15_0(long old1, long active1) {
        long active12 = active1 & old1;
        if (active12 == 0) {
            return jjStartNfa_0(13, 0L, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case Message.ERROR_FATAL /* 100 */:
                    if ((1 & active12) != 0) {
                        return jjStopAtPos(15, 64);
                    }
                    break;
            }
            return jjStartNfa_0(14, 0L, active12);
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(14, 0L, active12);
            return 15;
        }
    }

    private int jjStartNfaWithStates_0(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
            return jjMoveNfa_0(state, pos + 1);
        } catch (IOException e) {
            return pos + 1;
        }
    }

    private int jjMoveNfa_0(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 52;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            int i2 = this.jjround + 1;
            this.jjround = i2;
            if (i2 == Integer.MAX_VALUE) {
                ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1 << this.curChar;
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 0:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 79) {
                                    kind = 79;
                                }
                                jjCheckNAddStates(96, 100);
                            } else if ((4294971904L & l) != 0) {
                                jjCheckNAddStates(101, 104);
                            } else if ((9216 & l) != 0) {
                                if (kind > 74) {
                                    kind = 74;
                                }
                                jjCheckNAddStates(Message.ERROR_EPERM_ON_ATTACH, Message.ERROR_GROUP_FAIL);
                            } else if (this.curChar == ';') {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                jjCheckNAddStates(109, 112);
                            } else if (this.curChar == '.') {
                                jjCheckNAdd(30);
                            } else if (this.curChar == '-') {
                                jjAddStates(113, 114);
                            } else if (this.curChar == '\'') {
                                jjCheckNAddStates(115, 117);
                            } else if (this.curChar == '\"') {
                                jjCheckNAddStates(118, 120);
                            } else if (this.curChar == ':') {
                                int[] iArr = this.jjstateSet;
                                int i3 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i3 + 1;
                                iArr[i3] = 9;
                            }
                            if (this.curChar == '0') {
                                int[] iArr2 = this.jjstateSet;
                                int i4 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i4 + 1;
                                iArr2[i4] = 16;
                            } else if (this.curChar == '\r') {
                                int[] iArr3 = this.jjstateSet;
                                int i5 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i5 + 1;
                                iArr3[i5] = 6;
                            }
                            break;
                        case 1:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 61) {
                                    kind = 61;
                                }
                                int[] iArr4 = this.jjstateSet;
                                int i6 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i6 + 1;
                                iArr4[i6] = 1;
                            }
                            break;
                        case 2:
                            if ((9216 & l) != 0) {
                                if (kind > 74) {
                                    kind = 74;
                                }
                                jjCheckNAddStates(Message.ERROR_EPERM_ON_ATTACH, Message.ERROR_GROUP_FAIL);
                            }
                            break;
                        case 3:
                            if ((4294971904L & l) != 0) {
                                jjCheckNAddTwoStates(3, 4);
                            }
                            break;
                        case 4:
                            if (this.curChar == ';') {
                                jjCheckNAddStates(121, 123);
                            }
                            break;
                        case 5:
                            if (((-9217) & l) != 0) {
                                jjCheckNAddStates(121, 123);
                            }
                            break;
                        case 6:
                            if (this.curChar == '\n') {
                                if (kind > 74) {
                                    kind = 74;
                                }
                                jjCheckNAddStates(Message.ERROR_EPERM_ON_ATTACH, Message.ERROR_GROUP_FAIL);
                            }
                            break;
                        case 7:
                            if (this.curChar == '\r') {
                                int[] iArr5 = this.jjstateSet;
                                int i7 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i7 + 1;
                                iArr5[i7] = 6;
                            }
                            break;
                        case 8:
                            if (this.curChar == ':') {
                                int[] iArr6 = this.jjstateSet;
                                int i8 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i8 + 1;
                                iArr6[i8] = 9;
                            }
                            break;
                        case 10:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 76) {
                                    kind = 76;
                                }
                                int[] iArr7 = this.jjstateSet;
                                int i9 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i9 + 1;
                                iArr7[i9] = 10;
                            }
                            break;
                        case 12:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 77) {
                                    kind = 77;
                                }
                                int[] iArr8 = this.jjstateSet;
                                int i10 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i10 + 1;
                                iArr8[i10] = 12;
                            }
                            break;
                        case 14:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 78) {
                                    kind = 78;
                                }
                                int[] iArr9 = this.jjstateSet;
                                int i11 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i11 + 1;
                                iArr9[i11] = 14;
                            }
                            break;
                        case 15:
                            if (this.curChar == '0') {
                                int[] iArr10 = this.jjstateSet;
                                int i12 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i12 + 1;
                                iArr10[i12] = 16;
                            }
                            break;
                        case 17:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 80) {
                                    kind = 80;
                                }
                                int[] iArr11 = this.jjstateSet;
                                int i13 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i13 + 1;
                                iArr11[i13] = 17;
                            }
                            break;
                        case 18:
                            if (this.curChar == '\"') {
                                jjCheckNAddStates(118, 120);
                            }
                            break;
                        case 20:
                            jjCheckNAddStates(118, 120);
                            break;
                        case 21:
                            if (((-17179869185L) & l) != 0) {
                                jjCheckNAddStates(118, 120);
                            }
                            break;
                        case 22:
                            if (this.curChar == '\"' && kind > 81) {
                                kind = 81;
                            }
                            break;
                        case 23:
                            if (this.curChar == '\'') {
                                jjCheckNAddStates(115, 117);
                            }
                            break;
                        case 25:
                            jjCheckNAddStates(115, 117);
                            break;
                        case 26:
                            if (((-549755813889L) & l) != 0) {
                                jjCheckNAddStates(115, 117);
                            }
                            break;
                        case 27:
                            if (this.curChar == '\'' && kind > 81) {
                                kind = 81;
                            }
                            break;
                        case 28:
                            if (this.curChar == '-') {
                                jjAddStates(113, 114);
                            }
                            break;
                        case 29:
                            if (this.curChar == '.') {
                                jjCheckNAdd(30);
                            }
                            break;
                        case 30:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAddTwoStates(30, 31);
                            }
                            break;
                        case 32:
                            if ((43980465111040L & l) != 0) {
                                jjCheckNAdd(33);
                            }
                            break;
                        case 33:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAdd(33);
                            }
                            break;
                        case 34:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAddStates(LuaError.MAX_STRING_LEN, AddressItem.FLAG_AUTO);
                            }
                            break;
                        case 35:
                            if ((287948901175001088L & l) != 0) {
                                jjCheckNAddTwoStates(35, 36);
                            }
                            break;
                        case 36:
                            if (this.curChar == '.') {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAddTwoStates(37, 38);
                            }
                            break;
                        case 37:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAddTwoStates(37, 38);
                            }
                            break;
                        case 39:
                            if ((43980465111040L & l) != 0) {
                                jjCheckNAdd(40);
                            }
                            break;
                        case 40:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAdd(40);
                            }
                            break;
                        case 41:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAddTwoStates(41, 42);
                            }
                            break;
                        case 43:
                            if ((43980465111040L & l) != 0) {
                                jjCheckNAdd(44);
                            }
                            break;
                        case 44:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                jjCheckNAdd(44);
                            }
                            break;
                        case 45:
                            if ((4294971904L & l) != 0) {
                                jjCheckNAddStates(101, 104);
                            }
                            break;
                        case 46:
                            if ((4294971904L & l) != 0) {
                                jjCheckNAddTwoStates(46, 47);
                            }
                            break;
                        case 47:
                            if (this.curChar == ';') {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                jjCheckNAdd(48);
                            }
                            break;
                        case LasmConstants.SHL /* 48 */:
                            if (((-9217) & l) != 0) {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                jjCheckNAdd(48);
                            }
                            break;
                        case LasmConstants.SHR /* 49 */:
                            if (this.curChar == ';') {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                jjCheckNAddStates(109, 112);
                            }
                            break;
                        case 50:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 79) {
                                    kind = 79;
                                }
                                jjCheckNAddStates(96, 100);
                            }
                            break;
                        case LasmConstants.PROTO /* 51 */:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 79) {
                                    kind = 79;
                                }
                                jjCheckNAdd(51);
                            }
                            break;
                        case LasmConstants.GOTO /* 52 */:
                            if ((4294971904L & l) != 0) {
                                jjCheckNAddTwoStates(3, 4);
                            } else if (this.curChar == ';') {
                                jjCheckNAddStates(121, 123);
                            }
                            if ((4294971904L & l) != 0) {
                                jjCheckNAddTwoStates(46, 47);
                            } else if (this.curChar == ';') {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                jjCheckNAdd(48);
                            }
                            break;
                    }
                } while (i != startsAt);
            } else if (this.curChar < 128) {
                long l2 = 1 << (this.curChar & '?');
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 0:
                            if ((576460745995190270L & l2) != 0) {
                                if (kind > 61) {
                                    kind = 61;
                                }
                                jjCheckNAdd(1);
                            }
                            if (this.curChar == 'u') {
                                int[] iArr12 = this.jjstateSet;
                                int i14 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i14 + 1;
                                iArr12[i14] = 14;
                            } else if (this.curChar == 'v') {
                                int[] iArr13 = this.jjstateSet;
                                int i15 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i15 + 1;
                                iArr13[i15] = 12;
                            }
                            break;
                        case 1:
                            if ((576460745995190270L & l2) != 0) {
                                if (kind > 61) {
                                    kind = 61;
                                }
                                jjCheckNAdd(1);
                            }
                            break;
                        case 5:
                            jjAddStates(121, 123);
                            break;
                        case 9:
                        case 10:
                            if ((576460745995190270L & l2) != 0) {
                                if (kind > 76) {
                                    kind = 76;
                                }
                                jjCheckNAdd(10);
                            }
                            break;
                        case 11:
                            if (this.curChar == 'v') {
                                int[] iArr14 = this.jjstateSet;
                                int i16 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i16 + 1;
                                iArr14[i16] = 12;
                            }
                            break;
                        case 13:
                            if (this.curChar == 'u') {
                                int[] iArr15 = this.jjstateSet;
                                int i17 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i17 + 1;
                                iArr15[i17] = 14;
                            }
                            break;
                        case 16:
                            if ((72057594054705152L & l2) != 0) {
                                jjCheckNAdd(17);
                            }
                            break;
                        case 17:
                            if ((541165879422L & l2) != 0) {
                                if (kind > 80) {
                                    kind = 80;
                                }
                                jjCheckNAdd(17);
                            }
                            break;
                        case 19:
                            if (this.curChar == '\\') {
                                int[] iArr16 = this.jjstateSet;
                                int i18 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i18 + 1;
                                iArr16[i18] = 20;
                            }
                            break;
                        case 20:
                            jjCheckNAddStates(118, 120);
                            break;
                        case 21:
                            if (((-268435457) & l2) != 0) {
                                jjCheckNAddStates(118, 120);
                            }
                            break;
                        case 24:
                            if (this.curChar == '\\') {
                                int[] iArr17 = this.jjstateSet;
                                int i19 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i19 + 1;
                                iArr17[i19] = 25;
                            }
                            break;
                        case 25:
                            jjCheckNAddStates(115, 117);
                            break;
                        case 26:
                            if (((-268435457) & l2) != 0) {
                                jjCheckNAddStates(115, 117);
                            }
                            break;
                        case 31:
                            if ((137438953504L & l2) != 0) {
                                jjAddStates(128, 129);
                            }
                            break;
                        case 38:
                            if ((137438953504L & l2) != 0) {
                                jjAddStates(69, 70);
                            }
                            break;
                        case 42:
                            if ((137438953504L & l2) != 0) {
                                jjAddStates(94, 95);
                            }
                            break;
                        case LasmConstants.SHL /* 48 */:
                            if (kind > 4) {
                                kind = 4;
                            }
                            int[] iArr18 = this.jjstateSet;
                            int i20 = this.jjnewStateCnt;
                            this.jjnewStateCnt = i20 + 1;
                            iArr18[i20] = 48;
                            break;
                    }
                } while (i != startsAt);
            } else {
                int hiByte = this.curChar >> '\b';
                int i1 = hiByte >> 6;
                long l1 = 1 << (hiByte & 63);
                int i22 = (this.curChar & 255) >> 6;
                long l22 = 1 << (this.curChar & '?');
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 5:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjAddStates(121, 123);
                            }
                            break;
                        case 20:
                        case 21:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjCheckNAddStates(118, 120);
                            }
                            break;
                        case 25:
                        case 26:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjCheckNAddStates(115, 117);
                            }
                            break;
                        case LasmConstants.SHL /* 48 */:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                int[] iArr19 = this.jjstateSet;
                                int i21 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i21 + 1;
                                iArr19[i21] = 48;
                            }
                            break;
                    }
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            curPos++;
            i = this.jjnewStateCnt;
            this.jjnewStateCnt = startsAt;
            startsAt = 52 - startsAt;
            if (i != startsAt) {
                try {
                    this.curChar = this.input_stream.readChar();
                } catch (IOException e) {
                }
            }
            return curPos;
        }
    }

    private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0:
                if ((jjbitVec2[i2] & l2) == 0) {
                }
                break;
            default:
                if ((jjbitVec0[i1] & l1) == 0) {
                    break;
                }
                break;
        }
        return true;
    }

    public LasmTokenManager(SimpleCharStream stream) {
        this.debugStream = System.out;
        this.jjrounds = new int[52];
        this.jjstateSet = new int[104];
        this.curLexState = 3;
        this.defaultLexState = 3;
        this.input_stream = stream;
    }

    public LasmTokenManager(SimpleCharStream stream, int lexState) {
        this(stream);
        SwitchTo(lexState);
    }

    public void ReInit(SimpleCharStream stream) {
        this.jjnewStateCnt = 0;
        this.jjmatchedPos = 0;
        this.curLexState = this.defaultLexState;
        this.input_stream = stream;
        ReInitRounds();
    }

    private void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 52;
        while (true) {
            int i2 = i;
            i = i2 - 1;
            if (i2 > 0) {
                this.jjrounds[i] = Integer.MIN_VALUE;
            } else {
                return;
            }
        }
    }

    public void ReInit(SimpleCharStream stream, int lexState) {
        ReInit(stream);
        SwitchTo(lexState);
    }

    public void SwitchTo(int lexState) {
        if (lexState >= 4 || lexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
        this.curLexState = lexState;
    }

    protected Token jjFillToken() {
        String im = jjstrLiteralImages[this.jjmatchedKind];
        String curTokenImage = im == null ? this.input_stream.GetImage() : im;
        int beginLine = this.input_stream.getBeginLine();
        int beginColumn = this.input_stream.getBeginColumn();
        int endLine = this.input_stream.getEndLine();
        int endColumn = this.input_stream.getEndColumn();
        Token t = Token.newToken(this.jjmatchedKind, curTokenImage);
        t.beginLine = beginLine;
        t.endLine = endLine;
        t.beginColumn = beginColumn;
        t.endColumn = endColumn;
        return t;
    }

    public Token getNextToken() {
        Token specialToken = null;
        int curPos = 0;
        while (true) {
            try {
                this.curChar = this.input_stream.BeginToken();
                switch (this.curLexState) {
                    case 0:
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = jjMoveStringLiteralDfa0_0();
                        break;
                    case 1:
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = jjMoveStringLiteralDfa0_1();
                        break;
                    case 2:
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = jjMoveStringLiteralDfa0_2();
                        break;
                    case 3:
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = jjMoveStringLiteralDfa0_3();
                        break;
                }
                if (this.jjmatchedKind != Integer.MAX_VALUE) {
                    if (this.jjmatchedPos + 1 < curPos) {
                        this.input_stream.backup((curPos - this.jjmatchedPos) - 1);
                    }
                    if ((jjtoToken[this.jjmatchedKind >> 6] & (1 << (this.jjmatchedKind & 63))) != 0) {
                        Token matchedToken = jjFillToken();
                        matchedToken.specialToken = specialToken;
                        if (jjnewLexState[this.jjmatchedKind] != -1) {
                            this.curLexState = jjnewLexState[this.jjmatchedKind];
                        }
                        return matchedToken;
                    }
                    if ((jjtoSpecial[this.jjmatchedKind >> 6] & (1 << (this.jjmatchedKind & 63))) != 0) {
                        Token matchedToken2 = jjFillToken();
                        if (specialToken == null) {
                            specialToken = matchedToken2;
                        } else {
                            matchedToken2.specialToken = specialToken;
                            specialToken.next = matchedToken2;
                            specialToken = matchedToken2;
                        }
                    }
                    if (jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = jjnewLexState[this.jjmatchedKind];
                    }
                } else {
                    int error_line = this.input_stream.getEndLine();
                    int error_column = this.input_stream.getEndColumn();
                    String error_after = null;
                    boolean EOFSeen = false;
                    try {
                        this.input_stream.readChar();
                        this.input_stream.backup(1);
                    } catch (IOException e) {
                        EOFSeen = true;
                        error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
                        if (this.curChar == '\n' || this.curChar == '\r') {
                            error_line++;
                            error_column = 0;
                        } else {
                            error_column++;
                        }
                    }
                    if (!EOFSeen) {
                        this.input_stream.backup(1);
                        error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
                    }
                    throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
                }
            } catch (IOException e2) {
                this.jjmatchedKind = 0;
                Token matchedToken3 = jjFillToken();
                matchedToken3.specialToken = specialToken;
                return matchedToken3;
            }
        }
    }

    private void jjCheckNAdd(int state) {
        if (this.jjrounds[state] != this.jjround) {
            int[] iArr = this.jjstateSet;
            int i = this.jjnewStateCnt;
            this.jjnewStateCnt = i + 1;
            iArr[i] = state;
            this.jjrounds[state] = this.jjround;
        }
    }

    private void jjAddStates(int start, int end) {
        while (true) {
            int[] iArr = this.jjstateSet;
            int i = this.jjnewStateCnt;
            this.jjnewStateCnt = i + 1;
            iArr[i] = jjnextStates[start];
            int start2 = start + 1;
            if (start == end) {
                return;
            } else {
                start = start2;
            }
        }
    }

    private void jjCheckNAddTwoStates(int state1, int state2) {
        jjCheckNAdd(state1);
        jjCheckNAdd(state2);
    }

    private void jjCheckNAddStates(int start, int end) {
        while (true) {
            jjCheckNAdd(jjnextStates[start]);
            int start2 = start + 1;
            if (start == end) {
                return;
            } else {
                start = start2;
            }
        }
    }
}
