package android.ext;

import catch_.me_.if_.you_.can_.R;
import java.util.Locale;
import lasm.LasmConstants;
import luaj.LuaError;
import luaj.LuaTable;

/* JADX INFO: loaded from: classes.dex */
public class ArmDis {
    static final int I_BIT = 22;
    static final int P_BIT = 24;
    static final String UNDEFINED_INSTRUCTION = "\t \t ; <UNDEFINED> instruction: %0-31x";
    static final String UNPREDICTABLE_INSTRUCTION = "\t ; <UNPREDICTABLE>";
    static final int U_BIT = 23;
    static final int W_BIT = 21;
    Input input;
    private int valuep;
    private int widthp;
    static final String[] COND = {"EQ", "NE", "CS", "CC", "MI", "PL", "VS", "VC", "HI", "LS", "GE", "LT", "GT", "LE", "AL", "<UND>", ""};
    static final String[] REG = {"R0", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8", "R9", "R10", "R11", "R12", "SP", "LR", "PC"};
    static final String[] FP = {"0.0", "1.0", "2.0", "3.0", "4.0", "5.0", "0.5", "10.0"};
    static final String[] SHIFT = {"LSL", "LSR", "ASR", "ROR"};
    static final NextAsm nextAsm = new NextAsm();

    static void printAddr(StringBuilder out, long addr, long offset) {
        if (addr == 0) {
            if (offset < 0) {
                out.append('-');
                offset = -offset;
            } else {
                out.append('+');
            }
        }
        out.append("0x");
        long addr2 = addr + offset;
        if (addr2 != 0) {
            ItemContextMenu.setLastArmAddr(addr2);
        }
        ToolsLight.prefixLongHex(out, 8, addr2);
        out.append(ListManager.TEXT_SEPARATOR).append(offset <= 0 ? '-' : '+').append("0x").append(Long.toHexString(offset > 0 ? offset : -offset).toUpperCase(Locale.US)).append(' ').append(offset > 0 ? (char) 8595 : (char) 8593);
    }

    static final char charAt(String str, int sj, int j) {
        if (j < sj) {
            return str.charAt(j);
        }
        return (char) 0;
    }

    static final void append(StringBuilder out, char c) {
        if (c != 0) {
            out.append(c);
        }
    }

    static int decodeBitfield(String asm, int sj, int j, int data, ArmDis ret) {
        char c;
        int end;
        int value = 0;
        int width = 0;
        do {
            c = charAt(asm, sj, j);
            int start = 0;
            while (c >= '0' && c <= '9') {
                start = ((start * 10) + c) - 48;
                j++;
                c = charAt(asm, sj, j);
            }
            if (c == '-') {
                j++;
                c = charAt(asm, sj, j);
                end = 0;
                while (c >= '0' && c <= '9') {
                    end = ((end * 10) + c) - 48;
                    j++;
                    c = charAt(asm, sj, j);
                }
            } else {
                end = start;
            }
            int bits = end - start;
            if (bits < 0) {
                throw new RuntimeException("140 Bits is negative: " + bits + ListManager.TEXT_SEPARATOR + start + ListManager.TEXT_SEPARATOR + end + "; '" + asm + "'; " + j + ListManager.TEXT_SEPARATOR + sj + "; 0x" + Integer.toHexString(data));
            }
            value |= ((data >> start) & ((2 << bits) - 1)) << width;
            width += bits + 1;
            j++;
        } while (c == ',');
        ret.valuep = value;
        ret.widthp = width;
        return j - 1;
    }

    static int decodeBitfield(String asm, int sj, int j, ArmDis ret, int value) {
        char c;
        int end;
        int width = 0;
        int out = ret.valuep;
        do {
            c = charAt(asm, sj, j);
            int start = 0;
            while (c >= '0' && c <= '9') {
                start = ((start * 10) + c) - 48;
                j++;
                c = charAt(asm, sj, j);
            }
            if (c == '-') {
                j++;
                c = charAt(asm, sj, j);
                end = 0;
                while (c >= '0' && c <= '9') {
                    end = ((end * 10) + c) - 48;
                    j++;
                    c = charAt(asm, sj, j);
                }
            } else {
                end = start;
            }
            int bits = end - start;
            if (bits < 0) {
                throw new RuntimeException("141 Bits is negative: " + bits + ListManager.TEXT_SEPARATOR + start + ListManager.TEXT_SEPARATOR + end + "; '" + asm + "'; " + j + ListManager.TEXT_SEPARATOR + sj + "; 0x" + Integer.toHexString(value));
            }
            int mask = (2 << bits) - 1;
            int val = ((value >> width) & mask) << start;
            out |= val;
            if ((out & ((((-1) >> width) & mask) << start)) != val) {
                throw nextAsm;
            }
            width += bits + 1;
            j++;
        } while (c == ',');
        ret.valuep = out;
        ret.widthp = width;
        return j - 1;
    }

    static void decodeShift(StringBuilder out, int data, boolean printShift) {
        out.append(REG[data & 15]);
        if ((data & 4080) != 0) {
            if ((data & 16) == 0) {
                int amount = (data & 3968) >> 7;
                int shift = (data & 96) >> 5;
                if (amount == 0) {
                    if (shift == 3) {
                        out.append(", RRX");
                        return;
                    }
                    amount = 32;
                }
                out.append(", ");
                if (printShift) {
                    out.append(SHIFT[shift]).append(' ');
                }
                out.append('#').append(amount);
                return;
            }
            if ((data & 128) == 128) {
                out.append(" <illegal shifter operand>");
                return;
            }
            out.append(", ");
            if (printShift) {
                out.append(SHIFT[(data & 96) >> 5]).append(' ');
            }
            out.append(REG[(data & 3840) >> 8]);
        }
    }

    static int decodeShift(int out, Input in, boolean printShift) {
        int reg = in.index(REG);
        if (reg == -1) {
            throw nextAsm;
        }
        int out2 = out | reg;
        if (in.check(", ")) {
            if (in.check("RRX")) {
                return out2 | 96;
            }
            if (printShift) {
                int shift = in.index(SHIFT);
                if (shift == -1) {
                    throw nextAsm;
                }
                out2 |= shift << 5;
                if (!in.check(' ')) {
                    throw nextAsm;
                }
            }
            if (in.check('#')) {
                int amount = in.getInt();
                if (amount < 0 || amount > 32) {
                    throw nextAsm;
                }
                if (amount == 32) {
                    amount = 0;
                }
                return out2 | (amount << 7);
            }
            int out3 = out2 | 16;
            int reg2 = in.index(REG);
            if (reg2 == -1) {
                throw nextAsm;
            }
            return out3 | (reg2 << 8);
        }
        return out2;
    }

    static void bankedRegname(StringBuilder out, int reg) {
        String name;
        switch (reg) {
            case 15:
                name = "CPSR";
                break;
            case 32:
                name = "R8_usr";
                break;
            case 33:
                name = "R9_usr";
                break;
            case 34:
                name = "R10_usr";
                break;
            case 35:
                name = "R11_usr";
                break;
            case 36:
                name = "R12_usr";
                break;
            case 37:
                name = "SP_usr";
                break;
            case 38:
                name = "LR_usr";
                break;
            case 40:
                name = "R8_fiq";
                break;
            case 41:
                name = "R9_fiq";
                break;
            case 42:
                name = "R10_fiq";
                break;
            case 43:
                name = "R11_fiq";
                break;
            case 44:
                name = "R12_fiq";
                break;
            case 45:
                name = "SP_fiq";
                break;
            case 46:
                name = "LR_fiq";
                break;
            case LasmConstants.SHL /* 48 */:
                name = "LR_irq";
                break;
            case LasmConstants.SHR /* 49 */:
                name = "SP_irq";
                break;
            case 50:
                name = "LR_svc";
                break;
            case LasmConstants.PROTO /* 51 */:
                name = "SP_svc";
                break;
            case LasmConstants.GOTO /* 52 */:
                name = "LR_abt";
                break;
            case LasmConstants.OP /* 53 */:
                name = "SP_abt";
                break;
            case LasmConstants.SET_TOP /* 54 */:
                name = "LR_und";
                break;
            case LasmConstants.SKIP_NEXT /* 55 */:
                name = "SP_und";
                break;
            case 60:
                name = "LR_mon";
                break;
            case 61:
                name = "SP_mon";
                break;
            case 62:
                name = "ELR_hyp";
                break;
            case 63:
                name = "SP_hyp";
                break;
            case LasmConstants.INT /* 79 */:
                name = "SPSR";
                break;
            case 110:
                name = "SPSR_fiq";
                break;
            case 112:
                name = "SPSR_irq";
                break;
            case 114:
                name = "SPSR_svc";
                break;
            case 116:
                name = "SPSR_abt";
                break;
            case 118:
                name = "SPSR_und";
                break;
            case LuaError.MAX_STRING_LEN /* 124 */:
                name = "SPSR_mon";
                break;
            case 126:
                name = "SPSR_hyp";
                break;
            default:
                out.append("(UNDEF: ").append(reg).append(')');
                return;
        }
        out.append(name);
    }

    static int bankedRegname(Input in) {
        if (in.check("CPSR")) {
            return 15;
        }
        if (in.check("R8_USR")) {
            return 32;
        }
        if (in.check("R9_USR")) {
            return 33;
        }
        if (in.check("R10_USR")) {
            return 34;
        }
        if (in.check("R11_USR")) {
            return 35;
        }
        if (in.check("R12_USR")) {
            return 36;
        }
        if (in.check("SP_USR")) {
            return 37;
        }
        if (in.check("LR_USR")) {
            return 38;
        }
        if (in.check("R8_FIQ")) {
            return 40;
        }
        if (in.check("R9_FIQ")) {
            return 41;
        }
        if (in.check("R10_FIQ")) {
            return 42;
        }
        if (in.check("R11_FIQ")) {
            return 43;
        }
        if (in.check("R12_FIQ")) {
            return 44;
        }
        if (in.check("SP_FIQ")) {
            return 45;
        }
        if (in.check("LR_FIQ")) {
            return 46;
        }
        if (in.check("LR_IRQ")) {
            return 48;
        }
        if (in.check("SP_IRQ")) {
            return 49;
        }
        if (in.check("LR_SVC")) {
            return 50;
        }
        if (in.check("SP_SVC")) {
            return 51;
        }
        if (in.check("LR_ABT")) {
            return 52;
        }
        if (in.check("SP_ABT")) {
            return 53;
        }
        if (in.check("LR_UND")) {
            return 54;
        }
        if (in.check("SP_UND")) {
            return 55;
        }
        if (in.check("LR_MON")) {
            return 60;
        }
        if (in.check("SP_MON")) {
            return 61;
        }
        if (in.check("ELR_HYP")) {
            return 62;
        }
        if (in.check("SP_HYP")) {
            return 63;
        }
        if (in.check("SPSR_FIQ")) {
            return 110;
        }
        if (in.check("SPSR_IRQ")) {
            return 112;
        }
        if (in.check("SPSR_SVC")) {
            return 114;
        }
        if (in.check("SPSR_ABT")) {
            return 116;
        }
        if (in.check("SPSR_UND")) {
            return 118;
        }
        if (in.check("SPSR_MON")) {
            return LuaError.MAX_STRING_LEN;
        }
        if (in.check("SPSR_HYP")) {
            return 126;
        }
        if (in.check("SPSR")) {
            return 79;
        }
        if (!in.check("(UNDEF: ")) {
            return -1;
        }
        int reg = in.getInt();
        if ((reg & AddressItem.FLAG_ALL_OTHERS) != 0) {
            throw nextAsm;
        }
        if (in.check(')')) {
            return reg;
        }
        throw nextAsm;
    }

    static void dataBarrierOption(StringBuilder out, int option) {
        String name;
        int option2 = option & 15;
        switch (option2) {
            case 1:
                name = "oshld";
                break;
            case 2:
                name = "oshst";
                break;
            case 3:
                name = "osh";
                break;
            case 4:
            case 8:
            case 12:
            default:
                out.append('#').append(option2);
                return;
            case 5:
                name = "nshld";
                break;
            case 6:
                name = "unst";
                break;
            case 7:
                name = "un";
                break;
            case 9:
                name = "ishld";
                break;
            case 10:
                name = "ishst";
                break;
            case 11:
                name = "ish";
                break;
            case 13:
                name = "ld";
                break;
            case 14:
                name = "st";
                break;
            case 15:
                name = "sy";
                break;
        }
        out.append(name);
    }

    static int dataBarrierOption(Input in) {
        if (in.check("OSHLD")) {
            return 1;
        }
        if (in.check("OSHST")) {
            return 2;
        }
        if (in.check("OSH")) {
            return 3;
        }
        if (in.check("NSHLD")) {
            return 5;
        }
        if (in.check("UNST")) {
            return 6;
        }
        if (in.check("UN")) {
            return 7;
        }
        if (in.check("ISHLD")) {
            return 9;
        }
        if (in.check("ISHST")) {
            return 10;
        }
        if (in.check("ISH")) {
            return 11;
        }
        if (in.check("LD")) {
            return 13;
        }
        if (in.check("ST")) {
            return 14;
        }
        if (in.check("SY")) {
            return 15;
        }
        if (!in.check('#')) {
            return -1;
        }
        int option = in.getInt();
        if ((option & (-16)) != 0) {
            throw nextAsm;
        }
        return option;
    }

    static class NextAsm extends RuntimeException {
        NextAsm() {
        }
    }

    static class Coproc {
        static final int COND_UNCOND = 16;
        static final int[] NUM;
        static final String[] STR;

        Coproc() {
        }

        static {
            int[] num = {234881280, 267423504, 235929856, 267423504, 236978432, 267423504, 238027008, 267423504, 239075584, 267423504, 240124160, 267423504, 241172736, 267423504, 242221312, 267423504, 243269888, 267423504, 244318464, 267423504, 245367040, 267423504, 246415616, 267423504, 247464192, 267423504, 234914048, 267423504, 235962624, 267423504, 237011200, 267423504, 238059776, 267423504, 239108352, 267423504, 240156928, 267423504, 241205504, 267423504, 242254080, 267423504, 243302656, 267423504, 244351232, 267423504, 245399808, 267423504, 246448384, 267423504, 247496960, 267423504, 248545536, 267423504, 249594112, 267423504, 250642688, 267423504, 234881296, 267390751, 235929872, 268373912, 236978448, 268374015, 238027024, 268374015, 239075600, 268374015, 240124176, 268374015, 244379920, 267976688, 246477072, 267976688, 248574224, 267976688, 250671376, 267976688, 201326848, 235933440, 202375424, 235933440, 201327104, 235933440, 202375680, 235933440, 221055744, 264179457, 220203776, 263196417, 221252352, 263196417, 209718016, 261099265, 213715712, 264179457, 210766592, 261099265, 218106624, 254807808, 219155200, 254807808, 221055488, 264179456, 220203520, 263196416, 221252096, 263196416, 209717760, 261099264, 213715456, 264179456, 210766336, 261099264, 218106368, 254807808, 219154944, 254807808, 220203777, 263196417, 221252353, 263196417, 209718017, 261099265, 210766593, 261099265, 243272464, 267390832, 243272496, 267390832, 245369616, 267390832, 245369648, 267390832, 247466768, 267390832, 249563920, 267390832, 205523728, 267390928, 206572304, 267390928, 234883856, 265293680, 235932432, 256905072, 234883888, 265293616, 235932464, 256905008, 239078160, 265293584, 240126736, 256904976, 246549312, 264179536, 246614848, 264179536, 246549056, 264179536, 246614592, 264179536, 249563664, 268374015, 249629200, 268374015, 249956880, 268374015, 250022416, 268374015, 250087952, 268374015, 250153488, 268374015, 250219024, 268374015, 250612240, 268374015, 250739216, 268435455, 250677776, 268374015, 251005456, 268374015, 251070992, 268374015, 251136528, 268374015, 251202064, 268374015, 251267600, 268374015, 234883856, 265293823, 235932432, 265293823, 249563664, 267390975, 250612240, 267390975, 234883600, 267390847, 235932176, 267390847, 246745664, 264179568, 246745920, 264179568, 246417984, 264179664, 246418112, 264179664, 246418240, 264179664, 246418368, 264179664, 246483520, 264179664, 246483648, 264179664, 246483776, 264179664, 246483904, 264179664, 246876864, 264179664, 246877120, 264179664, 246942272, 264179536, 246942528, 264179536, 246680128, 264179536, 246680384, 264179536, 247073344, 264114000, 247073600, 264114000, 247204416, 264114000, 247204672, 264114000, 247335488, 264114000, 247335744, 264114000, 206572304, 263196656, 246417920, 263196656, 246418176, 263196656, 205523472, 267390928, 205523728, 267390928, 206572048, 267390928, 234883584, 263196496, 234883648, 263196496, 234883840, 263196496, 234883904, 263196496, 235932160, 263196496, 235932224, 263196496, 235932416, 263196496, 235932480, 263196496, 236980736, 263196496, 236980800, 263196496, 236980992, 263196496, 236981056, 263196496, 238029312, 263196496, 238029376, 263196496, 238029568, 263196496, 238029632, 263196496, 243272192, 263196496, 243272448, 263196496, 245369344, 263196496, 245369600, 263196496, 245369408, 263196496, 245369664, 263196496, 244320832, 263196496, 244321088, 263196496, 244320768, 263196496, 244321024, 263196496, -33551872, -8384768, -33551616, -8384768, -25163264, -5238976, -25163008, -5238976, -25163200, -5238976, -25162944, -5238976, -21231040, -4452528, -21230784, -4452528, 246811200, 264114000, 246811456, 264114000, -21493184, -4452528, -21492928, -4452528, 205520896, 267386880, 206569472, 267386880, 234881024, 251658256, 235991056, 252768272, 235929616, 252706832, 234881040, 252706832, 201326592, 235929600, 202375168, 235929600, -61865984, -1048576, -62914560, -1048576, -66060288, -32505856, -67108864, -32505856, -33554432, -16777200, -33554416, -15728624, -32505840, -15728624};
            String[] str = {"ADF%c%P%R\t %12-14f, %16-18f, %0-3f", "MUF%c%P%R\t %12-14f, %16-18f, %0-3f", "SUF%c%P%R\t %12-14f, %16-18f, %0-3f", "RSF%c%P%R\t %12-14f, %16-18f, %0-3f", "DVF%c%P%R\t %12-14f, %16-18f, %0-3f", "RDF%c%P%R\t %12-14f, %16-18f, %0-3f", "POW%c%P%R\t %12-14f, %16-18f, %0-3f", "RPW%c%P%R\t %12-14f, %16-18f, %0-3f", "RMF%c%P%R\t %12-14f, %16-18f, %0-3f", "FML%c%P%R\t %12-14f, %16-18f, %0-3f", "FDV%c%P%R\t %12-14f, %16-18f, %0-3f", "FRD%c%P%R\t %12-14f, %16-18f, %0-3f", "POL%c%P%R\t %12-14f, %16-18f, %0-3f", "MVF%c%P%R\t %12-14f, %0-3f", "MNF%c%P%R\t %12-14f, %0-3f", "ABS%c%P%R\t %12-14f, %0-3f", "RND%c%P%R\t %12-14f, %0-3f", "SQT%c%P%R\t %12-14f, %0-3f", "LOG%c%P%R\t %12-14f, %0-3f", "LGN%c%P%R\t %12-14f, %0-3f", "EXP%c%P%R\t %12-14f, %0-3f", "SIN%c%P%R\t %12-14f, %0-3f", "COS%c%P%R\t %12-14f, %0-3f", "TAN%c%P%R\t %12-14f, %0-3f", "ASN%c%P%R\t %12-14f, %0-3f", "ACS%c%P%R\t %12-14f, %0-3f", "ATN%c%P%R\t %12-14f, %0-3f", "URD%c%P%R\t %12-14f, %0-3f", "NRM%c%P%R\t %12-14f, %0-3f", "FLT%c%P%R\t %16-18f, %12-15r", "FIX%c%R\t %12-15r, %0-2f", "WFS%c\t %12-15r", "RFS%c\t %12-15r", "WFC%c\t %12-15r", "RFC%c\t %12-15r", "CMF%c\t %16-18f, %0-3f", "CNF%c\t %16-18f, %0-3f", "CMFE%c\t %16-18f, %0-3f", "CNFE%c\t %16-18f, %0-3f", "STF%c%Q\t %12-14f, %A", "LDF%c%Q\t %12-14f, %A", "SFM%c\t %12-14f, %F, %A", "LFM%c\t %12-14f, %F, %A", "VPUSH%c\t %B", "VSTMDB%c\t %16-19r!, %B", "VLDMDB%c\t %16-19r!, %B", "VSTMIA%c\t %16-19r%21'!, %B", "VPOP%c\t %B", "VLDMIA%c\t %16-19r%21'!, %B", "VSTR%c\t %12-15,22D, %A", "VLDR%c\t %12-15,22D, %A", "VPUSH%c\t %y3", "VSTMDB%c\t %16-19r!, %y3", "VLDMDB%c\t %16-19r!, %y3", "VSTMIA%c\t %16-19r%21'!, %y3", "VPOP%c\t %y3", "VLDMIA%c\t %16-19r%21'!, %y3", "VSTR%c\t %y1, %A", "VLDR%c\t %y1, %A", "FSTMDBX%c\t %16-19r!, %z3\t ;@ Deprecated", "FLDMDBX%c\t %16-19r!, %z3\t ;@ Deprecated", "FSTMIAX%c\t %16-19r%21'!, %z3\t ;@ Deprecated", "FLDMIAX%c\t %16-19r%21'!, %z3\t ;@ Deprecated", "VDUP%c.32\t %16-19,7D, %12-15r", "VDUP%c.16\t %16-19,7D, %12-15r", "VDUP%c.32\t %16-19,7Q, %12-15r", "VDUP%c.16\t %16-19,7Q, %12-15r", "VDUP%c.8\t %16-19,7D, %12-15r", "VDUP%c.8\t %16-19,7Q, %12-15r", "VMOV%c\t %0-3,5D, %12-15r, %16-19r", "VMOV%c\t %12-15r, %16-19r, %0-3,5D", "VMOV%c.32\t %16-19,7D[%21d], %12-15r", "VMOV%c.32\t %12-15r, %16-19,7D[%21d]", "VMOV%c.16\t %16-19,7D[%6,21d], %12-15r", "VMOV%c.%23?US16\t %12-15r, %16-19,7D[%6,21d]", "VMOV%c.8\t %16-19,7D[%5,6,21d], %12-15r", "VMOV%c.%23?US8\t %12-15r, %16-19,7D[%5,6,21d]", "VCVT%7?TB%c.F64.F16\t %z1, %y0", "VCVT%7?TB%c.F16.F64\t %y1, %z0", "VCVT%7?TB%c.F32.F16\t %y1, %y0", "VCVT%7?TB%c.F16.F32\t %y1, %y0", "VMSR%c\t fpsid, %12-15r", "VMSR%c\t fpscr, %12-15r", "VMSR%c\t mvfr1, %12-15r", "VMSR%c\t mvfR0, %12-15r", "VMSR%c\t fpexc, %12-15r", "VMSR%c\t fpinst, %12-15r\t @ Impl def", "VMSR%c\t fpinst2, %12-15r\t @ Impl def", "VMRS%c\t %12-15r, fpsid", "VMRS%c\t APSR_nzcv, fpscr", "VMRS%c\t %12-15r, fpscr", "VMRS%c\t %12-15r, mvfr1", "VMRS%c\t %12-15r, mvfR0", "VMRS%c\t %12-15r, fpexc", "VMRS%c\t %12-15r, fpinst\t @ Impl def", "VMRS%c\t %12-15r, fpinst2\t @ Impl def", "VMOV%c.32\t %z2[%21d], %12-15r", "VMOV%c.32\t %12-15r, %z2[%21d]", "VMSR%c\t <impl def %16-19x>, %12-15r", "VMRS%c\t %12-15r, <impl def %16-19x>", "VMOV%c\t %y2, %12-15r", "VMOV%c\t %12-15r, %y2", "VCMP%7'E%c.F32\t %y1, #0.0", "VCMP%7'E%c.F64\t %z1, #0.0", "VMOV%c.F32\t %y1, %y0", "VABS%c.F32\t %y1, %y0", "VMOV%c.F64\t %z1, %z0", "VABS%c.F64\t %z1, %z0", "VNEG%c.F32\t %y1, %y0", "VSQRT%c.F32\t %y1, %y0", "VNEG%c.F64\t %z1, %z0", "VSQRT%c.F64\t %z1, %z0", "VCVT%c.F64.F32\t %z1, %y0", "VCVT%c.F32.F64\t %y1, %z0", "VCVT%c.F32.%7?SU32\t %y1, %y0", "VCVT%c.F64.%7?SU32\t %z1, %y0", "VCMP%7'E%c.F32\t %y1, %y0", "VCMP%7'E%c.F64\t %z1, %z0", "VCVT%c.F32.%16?US%7?31%7?26\t %y1, %y1, #%5,0-3k", "VCVT%c.F64.%16?US%7?31%7?26\t %z1, %z1, #%5,0-3k", "VCVT%7`R%c.%16?SU32.F32\t %y1, %y0", "VCVT%7`R%c.%16?SU32.F64\t %y1, %z0", "VCVT%c.%16?US%7?31%7?26.F32\t %y1, %y1, #%5,0-3k", "VCVT%c.%16?US%7?31%7?26.F64\t %z1, %z1, #%5,0-3k", "VMOV%c\t %12-15r, %16-19r, %z0", "VMOV%c.F32\t %y1, #%0-3,16-19d", "VMOV%c.F64\t %z1, #%0-3,16-19d", "VMOV%c\t %y4, %12-15r, %16-19r", "VMOV%c\t %z0, %12-15r, %16-19r", "VMOV%c\t %12-15r, %16-19r, %y4", "VMLA%c.F32\t %y1, %y2, %y0", "VMLS%c.F32\t %y1, %y2, %y0", "VMLA%c.F64\t %z1, %z2, %z0", "VMLS%c.F64\t %z1, %z2, %z0", "VNMLS%c.F32\t %y1, %y2, %y0", "VNMLA%c.F32\t %y1, %y2, %y0", "VNMLS%c.F64\t %z1, %z2, %z0", "VNMLA%c.F64\t %z1, %z2, %z0", "VMUL%c.F32\t %y1, %y2, %y0", "VNMUL%c.F32\t %y1, %y2, %y0", "VMUL%c.F64\t %z1, %z2, %z0", "VNMUL%c.F64\t %z1, %z2, %z0", "VADD%c.F32\t %y1, %y2, %y0", "VSUB%c.F32\t %y1, %y2, %y0", "VADD%c.F64\t %z1, %z2, %z0", "VSUB%c.F64\t %z1, %z2, %z0", "VDIV%c.F32\t %y1, %y2, %y0", "VDIV%c.F64\t %z1, %z2, %z0", "VFMA%c.F32\t %y1, %y2, %y0", "VFMA%c.F64\t %z1, %z2, %z0", "VFMS%c.F32\t %y1, %y2, %y0", "VFMS%c.F64\t %z1, %z2, %z0", "VFNMA%c.F32\t %y1, %y2, %y0", "VFNMA%c.F64\t %z1, %z2, %z0", "VFNMS%c.F32\t %y1, %y2, %y0", "VFNMS%c.F64\t %z1, %z2, %z0", "VSEL%20-21c%u.F32\t %y1, %y2, %y0", "VSEL%20-21c%u.F64\t %z1, %z2, %z0", "VMAXNM%u.F32\t %y1, %y2, %y0", "VMAXNM%u.F64\t %z1, %z2, %z0", "VMINNM%u.F32\t %y1, %y2, %y0", "VMINNM%u.F64\t %z1, %z2, %z0", "VCVT%16-17?MPNA%u.%7?SU32.F32\t %y1, %y0", "VCVT%16-17?MPNA%u.%7?SU32.F64\t %y1, %z0", "VRINT%7,16??XZR%c.F32\t %y1, %y0", "VRINT%7,16??XZR%c.F64\t %z1, %z0", "VRINT%16-17?MPNA%u.F32\t %y1, %y0", "VRINT%16-17?MPNA%u.F64\t %z1, %z0", "MCRR%c\t %8-11d, %4-7d, %12-15R, %16-19r, CR%0-3d", "MRRC%c\t %8-11d, %4-7d, %12-15Ru, %16-19Ru, CR%0-3d", "CDP%c\t %8-11d, %20-23d, CR%12-15d, CR%16-19d, CR%0-3d, {%5-7d}", "MRC%c\t %8-11d, %21-23d, APSR_nzcv, CR%16-19d, CR%0-3d, {%5-7d}", "MRC%c\t %8-11d, %21-23d, %12-15r, CR%16-19d, CR%0-3d, {%5-7d}", "MCR%c\t %8-11d, %21-23d, %12-15R, CR%16-19d, CR%0-3d, {%5-7d}", "STC%22'L%c\t %8-11d, CR%12-15d, %A", "LDC%22'L%c\t %8-11d, CR%12-15d, %A", "MRRC2%c\t %8-11d, %4-7d, %12-15Ru, %16-19Ru, CR%0-3d", "MCRR2%c\t %8-11d, %4-7d, %12-15R, %16-19R, CR%0-3d", "LDC2%22'L%c\t %8-11d, CR%12-15d, %A", "STC2%22'L%c\t %8-11d, CR%12-15d, %A", "CDP2%c\t %8-11d, %20-23d, CR%12-15d, CR%16-19d, CR%0-3d, {%5-7d}", "MCR2%c\t %8-11d, %21-23d, %12-15R, CR%16-19d, CR%0-3d, {%5-7d}", "MRC2%c\t %8-11d, %21-23d, %12-15r, CR%16-19d, CR%0-3d, {%5-7d}"};
            NUM = num;
            STR = str;
        }

        static String getOpcode(ArmDis ret, long addr, int data, boolean thumb) {
            int cond;
            int regno;
            boolean WRITEBACK_BIT_SET = (2097152 & data) != 0;
            boolean NEGATIVE_BIT_SET = (8388608 & data) == 0;
            boolean PRE_BIT_SET = (16777216 & data) != 0;
            int si = STR.length;
            for (int i = 0; i < si; i++) {
                int uReg = 16;
                boolean isUnpredictable = false;
                int commentValue = 0;
                int mask = NUM[(i * 2) + 1];
                int value = NUM[i * 2];
                if (thumb) {
                    mask |= -268435456;
                    value |= -536870912;
                    cond = 16;
                } else if (((-268435456) & data) == -268435456) {
                    mask |= -268435456;
                    cond = 16;
                } else {
                    cond = (data >> 28) & 15;
                    if (cond == 14) {
                        cond = 16;
                    }
                }
                if ((data & mask) == value) {
                    StringBuilder out = new StringBuilder();
                    String asm = STR[i];
                    int j = 0;
                    int sj = asm.length();
                    while (j < sj) {
                        char c = ArmDis.charAt(asm, sj, j);
                        if (c != '%') {
                            ArmDis.append(out, c);
                        } else {
                            j++;
                            char c2 = ArmDis.charAt(asm, sj, j);
                            switch (c2) {
                                case '%':
                                    out.append('%');
                                    continue;
                                case LasmConstants.SHL /* 48 */:
                                case LasmConstants.SHR /* 49 */:
                                case '2':
                                case LasmConstants.PROTO /* 51 */:
                                case LasmConstants.GOTO /* 52 */:
                                case LasmConstants.OP /* 53 */:
                                case LasmConstants.SET_TOP /* 54 */:
                                case LasmConstants.SKIP_NEXT /* 55 */:
                                case LasmConstants.NIL /* 56 */:
                                case LasmConstants.TRUE /* 57 */:
                                    j = ArmDis.decodeBitfield(asm, sj, j, data, ret);
                                    int value2 = ret.valuep;
                                    int width = ret.widthp;
                                    char c3 = ArmDis.charAt(asm, sj, j);
                                    switch (c3) {
                                        case '\'':
                                            j++;
                                            char c4 = ArmDis.charAt(asm, sj, j);
                                            if (value2 == (1 << width) - 1) {
                                                ArmDis.append(out, c4);
                                            } else {
                                                continue;
                                            }
                                            break;
                                        case '?':
                                            ArmDis.append(out, ArmDis.charAt(asm, sj, ((1 << width) + j) - value2));
                                            j += 1 << width;
                                            ArmDis.charAt(asm, sj, j);
                                            continue;
                                        case LasmConstants.UPVAL /* 68 */:
                                            out.append('D').append(value2);
                                            continue;
                                        case LasmConstants.STRING /* 81 */:
                                            if ((value2 & 1) != 0) {
                                                out.append("<illegal reg Q").append(value2 >> 1).append(".5>");
                                                continue;
                                            } else {
                                                out.append(SearchMenuItem.HEX_UTF).append(value2 >> 1);
                                            }
                                            break;
                                        case 'R':
                                            if (value2 == 15) {
                                                isUnpredictable = true;
                                            }
                                            break;
                                        case '`':
                                            j++;
                                            char c5 = ArmDis.charAt(asm, sj, j);
                                            if (value2 == 0) {
                                                ArmDis.append(out, c5);
                                            } else {
                                                continue;
                                            }
                                            break;
                                        case 'c':
                                            switch (value2) {
                                                case 0:
                                                    out.append("EQ");
                                                    break;
                                                case 1:
                                                    out.append("VS");
                                                    break;
                                                case 2:
                                                    out.append("GE");
                                                    break;
                                                case 3:
                                                    out.append("GT");
                                                    break;
                                                default:
                                                    out.append("??");
                                                    continue;
                                            }
                                            break;
                                        case Message.ERROR_FATAL /* 100 */:
                                            out.append(value2);
                                            commentValue = value2;
                                            continue;
                                        case 'f':
                                            if (value2 > 7) {
                                                out.append('#').append(ArmDis.FP[value2 & 7]);
                                                continue;
                                            } else {
                                                out.append('F').append(value2);
                                            }
                                            break;
                                        case Message.ERROR_SPEEDHACK_FAIL /* 107 */:
                                            int from = (data & 128) == 0 ? 32 : 16;
                                            out.append(from - value2);
                                            continue;
                                        case 'r':
                                            break;
                                        case 'x':
                                            out.append("0x").append(Integer.toHexString(value2).toUpperCase(Locale.US));
                                            continue;
                                        default:
                                            throw new RuntimeException("10 Invalid format char '" + c3 + "'; " + i + ListManager.TEXT_SEPARATOR + j + "; '" + asm + "'");
                                    }
                                    if (ArmDis.charAt(asm, sj, j + 1) == 'u') {
                                        j++;
                                        ArmDis.charAt(asm, sj, j);
                                        if (uReg == value2) {
                                            isUnpredictable = true;
                                        }
                                        uReg = value2;
                                    }
                                    out.append(ArmDis.REG[value2]);
                                    continue;
                                case LasmConstants.NUMPARAMS /* 65 */:
                                    int rn = (data >> 16) & 15;
                                    int offset = data & 255;
                                    out.append('[');
                                    out.append(ArmDis.REG[rn]);
                                    if (PRE_BIT_SET || WRITEBACK_BIT_SET) {
                                        offset *= 4;
                                        if (NEGATIVE_BIT_SET) {
                                            offset = -offset;
                                        }
                                        if (rn != 15) {
                                            commentValue = offset;
                                        }
                                    }
                                    if (PRE_BIT_SET) {
                                        if (offset != 0) {
                                            out.append(",#");
                                            out.append(offset);
                                            out.append(']');
                                            if (WRITEBACK_BIT_SET) {
                                                out.append('!');
                                            }
                                        } else if (NEGATIVE_BIT_SET) {
                                            out.append(",#-0]");
                                        } else {
                                            out.append(']');
                                        }
                                    } else {
                                        out.append(']');
                                        if (WRITEBACK_BIT_SET) {
                                            if (offset != 0) {
                                                out.append(", #");
                                                out.append(offset);
                                            } else if (NEGATIVE_BIT_SET) {
                                                out.append(", #-0");
                                            }
                                        } else {
                                            out.append(", {");
                                            if (NEGATIVE_BIT_SET && offset == 0) {
                                                out.append('-');
                                            }
                                            out.append(offset);
                                            out.append('}');
                                            commentValue = offset;
                                        }
                                    }
                                    if (rn != 15) {
                                        continue;
                                    } else if (PRE_BIT_SET || WRITEBACK_BIT_SET) {
                                        out.append("\t ; ");
                                        ArmDis.printAddr(out, addr, ((long) (((thumb ? 2 : 4) * 2) + offset)) - (3 & addr));
                                    }
                                    break;
                                case LasmConstants.IS_VARARG /* 66 */:
                                    int regno2 = ((data >> 12) & 15) | ((data >> 18) & 16);
                                    int offset2 = (data >> 1) & 63;
                                    if (offset2 == 0) {
                                        out.append('D').append(regno2);
                                        continue;
                                    } else if (offset2 == 1) {
                                        out.append("{D").append(regno2).append('}');
                                    } else if (regno2 + offset2 > 32) {
                                        out.append("{D").append(regno2).append("-<overflow reg D").append((regno2 + offset2) - 1).append(">}");
                                    } else {
                                        out.append("{D").append(regno2).append("-D").append((regno2 + offset2) - 1).append('}');
                                    }
                                    break;
                                case LasmConstants.LOCAL /* 70 */:
                                    switch (4227072 & data) {
                                        case 0:
                                            out.append('4');
                                            break;
                                        case 32768:
                                            out.append('1');
                                            break;
                                        case AddressItem.FLAG_ORDERED /* 4194304 */:
                                            out.append('2');
                                            break;
                                        default:
                                            out.append('3');
                                            continue;
                                    }
                                    break;
                                case LasmConstants.HEX /* 80 */:
                                    switch (524416 & data) {
                                        case 0:
                                            out.append('S');
                                            break;
                                        case 128:
                                            out.append('D');
                                            break;
                                        case Config.STATE_ASHMEM /* 524288 */:
                                            out.append('E');
                                            break;
                                        default:
                                            out.append("<illegal precision>");
                                            continue;
                                    }
                                    break;
                                case LasmConstants.STRING /* 81 */:
                                    switch (4227072 & data) {
                                        case 0:
                                            out.append('S');
                                            break;
                                        case 32768:
                                            out.append('D');
                                            break;
                                        case AddressItem.FLAG_ORDERED /* 4194304 */:
                                            out.append('E');
                                            break;
                                        default:
                                            out.append('P');
                                            continue;
                                    }
                                    break;
                                case 'R':
                                    switch (data & 96) {
                                        case 0:
                                            continue;
                                        case 32:
                                            out.append('P');
                                            break;
                                        case 64:
                                            out.append('M');
                                            break;
                                        default:
                                            out.append('Z');
                                            break;
                                    }
                                    break;
                                case 'c':
                                    break;
                                case 'u':
                                    if (cond != 16) {
                                        isUnpredictable = true;
                                    }
                                    break;
                                case 'y':
                                case 'z':
                                    boolean single = c2 == 'y';
                                    j++;
                                    char c6 = ArmDis.charAt(asm, sj, j);
                                    switch (c6) {
                                        case LasmConstants.SHL /* 48 */:
                                        case LasmConstants.GOTO /* 52 */:
                                            int regno3 = data & 15;
                                            if (single) {
                                                regno = (regno3 << 1) + ((data >> 5) & 1);
                                            } else {
                                                regno = regno3 + (((data >> 5) & 1) << 4);
                                            }
                                            break;
                                        case LasmConstants.SHR /* 49 */:
                                        case LasmConstants.PROTO /* 51 */:
                                            if (c6 == '3') {
                                                out.append('{');
                                            }
                                            int regno4 = (data >> 12) & 15;
                                            if (single) {
                                                regno = (regno4 << 1) + ((data >> 22) & 1);
                                            } else {
                                                regno = regno4 + (((data >> 22) & 1) << 4);
                                            }
                                            break;
                                        case '2':
                                            int regno5 = (data >> 16) & 15;
                                            if (single) {
                                                regno = (regno5 << 1) + ((data >> 7) & 1);
                                            } else {
                                                regno = regno5 + (((data >> 7) & 1) << 4);
                                            }
                                            break;
                                        default:
                                            throw new RuntimeException("20 Invalid format char '" + c6 + "'; " + i + ListManager.TEXT_SEPARATOR + j + "; '" + asm + "'");
                                    }
                                    out.append(single ? 'S' : 'D').append(regno);
                                    if (c6 == '3') {
                                        int count = data & 255;
                                        if (!single) {
                                            count >>= 1;
                                        }
                                        int count2 = count - 1;
                                        if (count2 != 0) {
                                            out.append('-').append(single ? 'S' : 'D').append(regno + count2);
                                        }
                                        out.append('}');
                                        continue;
                                    } else if (c6 == '4') {
                                        out.append(", ").append(single ? 'S' : 'D').append(regno + 1);
                                    }
                                    break;
                                default:
                                    throw new RuntimeException("30 Invalid format char '" + c2 + "'; " + i + ListManager.TEXT_SEPARATOR + j + "; '" + asm + "'");
                            }
                            out.append(ArmDis.COND[cond]);
                        }
                        j++;
                    }
                    if (commentValue > 32 || commentValue < -16) {
                        out.append("\t ; 0x").append(Integer.toHexString(commentValue).toUpperCase(Locale.US));
                    }
                    if (isUnpredictable) {
                        out.append(ArmDis.UNPREDICTABLE_INSTRUCTION);
                    }
                    return out.toString();
                }
            }
            return null;
        }

        /* JADX WARN: Removed duplicated region for block: B:21:0x0061  */
        /* JADX WARN: Removed duplicated region for block: B:255:0x067a A[Catch: NextAsm -> 0x0118, TryCatch #0 {NextAsm -> 0x0118, blocks: (B:8:0x001c, B:10:0x0026, B:12:0x0030, B:13:0x0033, B:16:0x0040, B:337:0x0873, B:341:0x0883, B:343:0x088f, B:345:0x089b, B:350:0x08ad, B:352:0x08bf, B:354:0x08cb, B:355:0x08ce, B:22:0x0074, B:27:0x008a, B:41:0x00c5, B:37:0x00b4, B:38:0x00b9, B:40:0x00c1, B:42:0x00c8, B:43:0x00d2, B:44:0x00d5, B:45:0x0117, B:48:0x011b, B:51:0x0125, B:53:0x0131, B:55:0x0143, B:57:0x0154, B:59:0x0164, B:60:0x0168, B:63:0x0172, B:66:0x018a, B:68:0x0196, B:69:0x019a, B:76:0x01ac, B:78:0x01be, B:80:0x01ca, B:122:0x02a6, B:124:0x02b2, B:82:0x01cf, B:84:0x01db, B:86:0x01e7, B:88:0x01f3, B:89:0x01f7, B:92:0x0205, B:95:0x021a, B:97:0x0226, B:100:0x0234, B:103:0x023c, B:104:0x0240, B:107:0x024e, B:109:0x025a, B:113:0x0269, B:115:0x027b, B:117:0x0287, B:118:0x028a, B:120:0x0296, B:121:0x029b, B:125:0x02b8, B:127:0x02c4, B:129:0x02d4, B:130:0x02dc, B:132:0x02f0, B:134:0x02fc, B:136:0x0300, B:140:0x0308, B:144:0x0317, B:146:0x0323, B:147:0x032a, B:152:0x0346, B:153:0x034c, B:154:0x0350, B:157:0x0358, B:161:0x0365, B:162:0x0369, B:165:0x0370, B:168:0x0379, B:169:0x037d, B:172:0x0385, B:176:0x0392, B:177:0x0396, B:178:0x0399, B:179:0x03a8, B:183:0x03b5, B:184:0x03d8, B:185:0x03db, B:186:0x041d, B:187:0x041e, B:189:0x0430, B:191:0x0442, B:192:0x0444, B:193:0x045c, B:195:0x0468, B:198:0x0492, B:200:0x049e, B:203:0x04ca, B:206:0x04f4, B:209:0x04fe, B:213:0x052d, B:215:0x0539, B:217:0x054b, B:218:0x0565, B:220:0x0571, B:223:0x0595, B:225:0x05a1, B:228:0x05d0, B:231:0x05de, B:232:0x05f6, B:235:0x0605, B:238:0x0614, B:244:0x062b, B:246:0x063b, B:248:0x0647, B:255:0x067a, B:257:0x0686, B:252:0x0660, B:254:0x066c, B:258:0x068c, B:260:0x0698, B:262:0x06a4, B:264:0x06b0, B:265:0x06c0, B:270:0x06d8, B:271:0x06f6, B:274:0x0709, B:278:0x0714, B:280:0x0724, B:284:0x0734, B:286:0x073e, B:287:0x0742, B:288:0x0745, B:289:0x0787, B:293:0x0790, B:296:0x07a2, B:300:0x07b2, B:302:0x07bc, B:305:0x07ca, B:308:0x07d2, B:311:0x07e4, B:314:0x07f8, B:318:0x0808, B:320:0x0812, B:323:0x081c, B:325:0x0820, B:326:0x0822, B:328:0x0834, B:331:0x083d, B:335:0x0853, B:336:0x0863), top: B:359:0x001c }] */
        /* JADX WARN: Removed duplicated region for block: B:257:0x0686 A[Catch: NextAsm -> 0x0118, TryCatch #0 {NextAsm -> 0x0118, blocks: (B:8:0x001c, B:10:0x0026, B:12:0x0030, B:13:0x0033, B:16:0x0040, B:337:0x0873, B:341:0x0883, B:343:0x088f, B:345:0x089b, B:350:0x08ad, B:352:0x08bf, B:354:0x08cb, B:355:0x08ce, B:22:0x0074, B:27:0x008a, B:41:0x00c5, B:37:0x00b4, B:38:0x00b9, B:40:0x00c1, B:42:0x00c8, B:43:0x00d2, B:44:0x00d5, B:45:0x0117, B:48:0x011b, B:51:0x0125, B:53:0x0131, B:55:0x0143, B:57:0x0154, B:59:0x0164, B:60:0x0168, B:63:0x0172, B:66:0x018a, B:68:0x0196, B:69:0x019a, B:76:0x01ac, B:78:0x01be, B:80:0x01ca, B:122:0x02a6, B:124:0x02b2, B:82:0x01cf, B:84:0x01db, B:86:0x01e7, B:88:0x01f3, B:89:0x01f7, B:92:0x0205, B:95:0x021a, B:97:0x0226, B:100:0x0234, B:103:0x023c, B:104:0x0240, B:107:0x024e, B:109:0x025a, B:113:0x0269, B:115:0x027b, B:117:0x0287, B:118:0x028a, B:120:0x0296, B:121:0x029b, B:125:0x02b8, B:127:0x02c4, B:129:0x02d4, B:130:0x02dc, B:132:0x02f0, B:134:0x02fc, B:136:0x0300, B:140:0x0308, B:144:0x0317, B:146:0x0323, B:147:0x032a, B:152:0x0346, B:153:0x034c, B:154:0x0350, B:157:0x0358, B:161:0x0365, B:162:0x0369, B:165:0x0370, B:168:0x0379, B:169:0x037d, B:172:0x0385, B:176:0x0392, B:177:0x0396, B:178:0x0399, B:179:0x03a8, B:183:0x03b5, B:184:0x03d8, B:185:0x03db, B:186:0x041d, B:187:0x041e, B:189:0x0430, B:191:0x0442, B:192:0x0444, B:193:0x045c, B:195:0x0468, B:198:0x0492, B:200:0x049e, B:203:0x04ca, B:206:0x04f4, B:209:0x04fe, B:213:0x052d, B:215:0x0539, B:217:0x054b, B:218:0x0565, B:220:0x0571, B:223:0x0595, B:225:0x05a1, B:228:0x05d0, B:231:0x05de, B:232:0x05f6, B:235:0x0605, B:238:0x0614, B:244:0x062b, B:246:0x063b, B:248:0x0647, B:255:0x067a, B:257:0x0686, B:252:0x0660, B:254:0x066c, B:258:0x068c, B:260:0x0698, B:262:0x06a4, B:264:0x06b0, B:265:0x06c0, B:270:0x06d8, B:271:0x06f6, B:274:0x0709, B:278:0x0714, B:280:0x0724, B:284:0x0734, B:286:0x073e, B:287:0x0742, B:288:0x0745, B:289:0x0787, B:293:0x0790, B:296:0x07a2, B:300:0x07b2, B:302:0x07bc, B:305:0x07ca, B:308:0x07d2, B:311:0x07e4, B:314:0x07f8, B:318:0x0808, B:320:0x0812, B:323:0x081c, B:325:0x0820, B:326:0x0822, B:328:0x0834, B:331:0x083d, B:335:0x0853, B:336:0x0863), top: B:359:0x001c }] */
        /* JADX WARN: Removed duplicated region for block: B:307:0x07ce  */
        /* JADX WARN: Removed duplicated region for block: B:350:0x08ad A[Catch: NextAsm -> 0x0118, TryCatch #0 {NextAsm -> 0x0118, blocks: (B:8:0x001c, B:10:0x0026, B:12:0x0030, B:13:0x0033, B:16:0x0040, B:337:0x0873, B:341:0x0883, B:343:0x088f, B:345:0x089b, B:350:0x08ad, B:352:0x08bf, B:354:0x08cb, B:355:0x08ce, B:22:0x0074, B:27:0x008a, B:41:0x00c5, B:37:0x00b4, B:38:0x00b9, B:40:0x00c1, B:42:0x00c8, B:43:0x00d2, B:44:0x00d5, B:45:0x0117, B:48:0x011b, B:51:0x0125, B:53:0x0131, B:55:0x0143, B:57:0x0154, B:59:0x0164, B:60:0x0168, B:63:0x0172, B:66:0x018a, B:68:0x0196, B:69:0x019a, B:76:0x01ac, B:78:0x01be, B:80:0x01ca, B:122:0x02a6, B:124:0x02b2, B:82:0x01cf, B:84:0x01db, B:86:0x01e7, B:88:0x01f3, B:89:0x01f7, B:92:0x0205, B:95:0x021a, B:97:0x0226, B:100:0x0234, B:103:0x023c, B:104:0x0240, B:107:0x024e, B:109:0x025a, B:113:0x0269, B:115:0x027b, B:117:0x0287, B:118:0x028a, B:120:0x0296, B:121:0x029b, B:125:0x02b8, B:127:0x02c4, B:129:0x02d4, B:130:0x02dc, B:132:0x02f0, B:134:0x02fc, B:136:0x0300, B:140:0x0308, B:144:0x0317, B:146:0x0323, B:147:0x032a, B:152:0x0346, B:153:0x034c, B:154:0x0350, B:157:0x0358, B:161:0x0365, B:162:0x0369, B:165:0x0370, B:168:0x0379, B:169:0x037d, B:172:0x0385, B:176:0x0392, B:177:0x0396, B:178:0x0399, B:179:0x03a8, B:183:0x03b5, B:184:0x03d8, B:185:0x03db, B:186:0x041d, B:187:0x041e, B:189:0x0430, B:191:0x0442, B:192:0x0444, B:193:0x045c, B:195:0x0468, B:198:0x0492, B:200:0x049e, B:203:0x04ca, B:206:0x04f4, B:209:0x04fe, B:213:0x052d, B:215:0x0539, B:217:0x054b, B:218:0x0565, B:220:0x0571, B:223:0x0595, B:225:0x05a1, B:228:0x05d0, B:231:0x05de, B:232:0x05f6, B:235:0x0605, B:238:0x0614, B:244:0x062b, B:246:0x063b, B:248:0x0647, B:255:0x067a, B:257:0x0686, B:252:0x0660, B:254:0x066c, B:258:0x068c, B:260:0x0698, B:262:0x06a4, B:264:0x06b0, B:265:0x06c0, B:270:0x06d8, B:271:0x06f6, B:274:0x0709, B:278:0x0714, B:280:0x0724, B:284:0x0734, B:286:0x073e, B:287:0x0742, B:288:0x0745, B:289:0x0787, B:293:0x0790, B:296:0x07a2, B:300:0x07b2, B:302:0x07bc, B:305:0x07ca, B:308:0x07d2, B:311:0x07e4, B:314:0x07f8, B:318:0x0808, B:320:0x0812, B:323:0x081c, B:325:0x0820, B:326:0x0822, B:328:0x0834, B:331:0x083d, B:335:0x0853, B:336:0x0863), top: B:359:0x001c }] */
        /* JADX WARN: Removed duplicated region for block: B:366:0x0030 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:399:0x00c1 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:426:0x00c1 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:430:0x00c1 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:73:0x01a6  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        static int getOpcode(android.ext.ArmDis r37, long r38, java.lang.String r40, boolean r41, int r42) {
            /*
                Method dump skipped, instruction units count: 2476
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: android.ext.ArmDis.Coproc.getOpcode(android.ext.ArmDis, long, java.lang.String, boolean, int):int");
        }
    }

    static class Neon {
        static final byte[] ENC = {4, Message.CMD_SC_MEMORY_ITEM, 4, 4, 3, Message.CMD_SC_REPORT_ERROR, 3, 1, 2, Message.CMD_SC_TARGET_DEAD, 2, 0, 0, 0, 0, 0};
        static final int[] NUM;
        static final String[] STR;

        Neon() {
        }

        static {
            int[] num = {-223344576, -5240752, -223346688, -5240816, -206304256, -4780144, -206435328, -5042288, -206500864, -5173360, -206567424, -5239728, -206567360, -5239728, -206174720, -4255792, -206174464, -4255792, -234877936, -6287600, -232780784, -6287600, -205913088, -4256752, -205848576, -4256752, -206568704, -4255792, -206568640, -4255792, -206568576, -4255792, -206568512, -4255792, -205978944, -4255792, -205913216, -4255792, -205913152, -4255792, -225965552, -21033008, -225441264, -21033008, -224392688, -21033008, -206568192, -4255856, -206568064, -4255856, -206438400, -4255856, -206437888, -5042224, -206437824, -5042224, -206437760, -5042224, -206437696, -5042224, -206437632, -5042224, -205847552, -4256112, -205847424, -4256112, -206569472, -5042288, -206569344, -5042288, -206569216, -5042288, -206568448, -5042288, -206568320, -5042288, -206567680, -5042288, -206567552, -5042288, -206438272, -5042288, -206438144, -5042288, -206438016, -5042288, -206503936, -5043312, -206503808, -5043312, -206503680, -5043312, -206503552, -5043312, -206503424, -5043312, -206503168, -5043312, -206503040, -5043312, -206568960, -5042416, -206567936, -5042416, -206371328, -5042672, -234877888, -5238960, -233829312, -5238960, -232780736, -5238960, -231732160, -5238960, -218100672, -5238960, -217052096, -5238960, -216003520, -5238960, -218099952, -6287600, -216002800, -6287600, -234880752, -5239024, -233832176, -5239024, -232783600, -5239024, -231735024, -5239024, -218103536, -5239024, -217054960, -5239024, -216006384, -5239024, -214957808, -5239024, -234877696, -6287600, -234877680, -6287600, -234877440, -6287600, -234877184, -6287600, -234877168, -6287600, -232780544, -6287600, -232780528, -6287600, -232780032, -6287600, -232780016, -6287600, -218100480, -6287600, -218100464, -6287600, -218100224, -6287600, -218100208, -6287600, -218099968, -6287600, -216003328, -6287600, -216003072, -6287600, -216003056, -6287600, -216002816, -6287600, -234878976, -8384752, -234878960, -8384752, -234878720, -8384752, -234878208, -8384752, -234878192, -8384752, -218101760, -8384752, -218101744, -8384752, -218101504, -8384752, -218100992, -8384752, -234881024, -25161968, -234881008, -25161968, -234880768, -25161968, -234880512, -25161968, -234880496, -25161968, -234880256, -25161968, -234880240, -25161968, -234880000, -25161968, -234879984, -25161968, -234879744, -25161968, -234879728, -25161968, -234879488, -25161968, -234879472, -25161968, -234879232, -25161968, -234879216, -25161968, -234878704, -25161968, -234878464, -25161968, -234878448, -25161968, -226488816, -21491792, -226488784, -21491792, -226488560, -21491792, -226490352, -21492304, -226490320, -21492304, -226490096, -21492304, -226490064, -21492304, -226489328, -21492048, -226489296, -21492048, -226492144, -21493328, -226492112, -21493328, -226492400, -21493584, -226492368, -21493584, -225966064, -4714544, -225966000, -4714544, -209188848, -4714544, -209188784, -4714544, -225965808, -21491760, -225965744, -21491760, -225965552, -21491760, -225441776, -5238832, -225441712, -5238832, -225966832, -4714608, -209189872, -4714608, -209189616, -4714608, -209189360, -4714608, -208664560, -5238832, -208664496, -5238832, -225441520, -22016048, -225441456, -22016048, -225441264, -22016048, -225968112, -21491824, -225967856, -21491824, -225967600, -21491824, -225967344, -21491824, -225966320, -21491824, -224393200, -6287408, -224393136, -6287408, -225442544, -5238896, -208665584, -5238896, -208665328, -5238896, -208665072, -5238896, -224392688, -23064624, -225443824, -22016112, -225443568, -22016112, -225443312, -22016112, -225443056, -22016112, -225442032, -22016112, -207615984, -6287408, -207615920, -6287408, -224392944, -23064624, -224392880, -23064624, -224393968, -6287472, -207617008, -6287472, -207616752, -6287472, -207616496, -6287472, -224395248, -23064688, -224394992, -23064688, -224394736, -23064688, -224394480, -23064688, -224393456, -23064688, -226490992, -8384624, -209714032, -8384624, -209713776, -8384624, -209713520, -8384624, -226492272, -25161840, -226492016, -25161840, -226491760, -25161840, -226491504, -25161840, -226490480, -25161840, -224391664, -23064944, -224391680, -22016176, -226488832, -23064752, -226491392, -8384688, -226490880, -8384688, -226490112, -8384688, -226489600, -8384688, -226489088, -8384688, -209714176, -8384688, -209713664, -8384688, -226492416, -25161904, -226492160, -25161904, -226491904, -25161904, -226491648, -25161904, -226491136, -25161904, -226490624, -25161904, -226490368, -25161904, -226489856, -25161904, -226489344, -25161904, -226492352, -8384688, -226492096, -8384688, -226491584, -8384688, -226491328, -8384688, -226491072, -8384688, -226490560, -8384688, -226490304, -8384688, -226490048, -8384688, -226489536, -8384688, -226489280, -8384688, -226489024, -8384688, -209715136, -8384688, -209714880, -8384688, -209714112, -8384688, -209713856, -8384688, -209713088, -8384688, -209712832, -8384688, -209712064, -8384688, -209711808, -8384688, -226491840, -25161904, -226490816, -25161904, -226489792, -25161904, -190836800, -5238848, -190837760, -5239040, -190837504, -5239040, -190837248, -5239040, -190836992, -5239040, -201326080, -7336192, -201325824, -7336192, -201325568, -7336192, -201325312, -7336192, -201325056, -7336192, -201324800, -7336192, -201324544, -7336192, -201324288, -7336192, -201324032, -7336192, -201326592, -7336448, -192937984, -7339264, -192937728, -7339264, -192937472, -7339264, -192937216, -7339264};
            String[] str = {"VEXT%c.8\t %12-15,22R, %16-19,7R, %0-3,5R, #%8-11d", "VEXT%c.8\t %12-15,22R, %16-19,7R, %0-3,5R, #%8-11d", "VDUP%c.32\t %12-15,22R, %0-3,5D[%19d]", "VDUP%c.16\t %12-15,22R, %0-3,5D[%18-19d]", "VDUP%c.8\t %12-15,22R, %0-3,5D[%17-19d]", "VTBL%c.8\t %12-15,22D, %F, %0-3,5D", "VTBX%c.8\t %12-15,22D, %F, %0-3,5D", "VCVT%c.F16.F32\t %12-15,22D, %0-3,5Q", "VCVT%c.F32.F16\t %12-15,22Q, %0-3,5D", "VFMA%c.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VFMS%c.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VRINT%7-9?P?M?ZAXN%u.F32\t %12-15,22R, %0-3,5R", "VCVT%8-9?MPNA%u.%7?US32.F32\t %12-15,22R, %0-3,5R", "AESE%u.8\t %12-15,22Q, %0-3,5Q", "AESD%u.8\t %12-15,22Q, %0-3,5Q", "AESMC%u.8\t %12-15,22Q, %0-3,5Q", "AESIMC%u.8\t %12-15,22Q, %0-3,5Q", "SHA1H%u.32\t %12-15,22Q, %0-3,5Q", "SHA1SU1%u.32\t %12-15,22Q, %0-3,5Q", "SHA256SU0%u.32\t %12-15,22Q, %0-3,5Q", "VMOVL%c.%24?US8\t %12-15,22Q, %0-3,5D", "VMOVL%c.%24?US16\t %12-15,22Q, %0-3,5D", "VMOVL%c.%24?US32\t %12-15,22Q, %0-3,5D", "VCNT%c.8\t %12-15,22R, %0-3,5R", "VMVN%c\t %12-15,22R, %0-3,5R", "VSWP%c\t %12-15,22R, %0-3,5R", "VMOVN%c.i%18-19T2\t %12-15,22D, %0-3,5Q", "VQMOVUN%c.s%18-19T2\t %12-15,22D, %0-3,5Q", "VQMOVN%c.s%18-19T2\t %12-15,22D, %0-3,5Q", "VQMOVN%c.u%18-19T2\t %12-15,22D, %0-3,5Q", "VSHLL%c.i%18-19S2\t %12-15,22Q, %0-3,5D, #%18-19S2", "VRECPE%c.%8?FU%18-19S2\t %12-15,22R, %0-3,5R", "VRSQRTE%c.%8?FU%18-19S2\t %12-15,22R, %0-3,5R", "VREV64%c.%18-19S2\t %12-15,22R, %0-3,5R", "VREV32%c.%18-19S2\t %12-15,22R, %0-3,5R", "VREV16%c.%18-19S2\t %12-15,22R, %0-3,5R", "VCLS%c.s%18-19S2\t %12-15,22R, %0-3,5R", "VCLZ%c.i%18-19S2\t %12-15,22R, %0-3,5R", "VQABS%c.s%18-19S2\t %12-15,22R, %0-3,5R", "VQNEG%c.s%18-19S2\t %12-15,22R, %0-3,5R", "VTRN%c.%18-19S2\t %12-15,22R, %0-3,5R", "VUZP%c.%18-19S2\t %12-15,22R, %0-3,5R", "VZIP%c.%18-19S2\t %12-15,22R, %0-3,5R", "VCGT%c.%10?FS%18-19S2\t %12-15,22R, %0-3,5R, #0", "VCGE%c.%10?FS%18-19S2\t %12-15,22R, %0-3,5R, #0", "VCEQ%c.%10?FI%18-19S2\t %12-15,22R, %0-3,5R, #0", "VCLE%c.%10?FS%18-19S2\t %12-15,22R, %0-3,5R, #0", "VCLT%c.%10?FS%18-19S2\t %12-15,22R, %0-3,5R, #0", "VABS%c.%10?FS%18-19S2\t %12-15,22R, %0-3,5R", "VNEG%c.%10?FS%18-19S2\t %12-15,22R, %0-3,5R", "VPADDL%c.%7?US%18-19S2\t %12-15,22R, %0-3,5R", "VPADAL%c.%7?US%18-19S2\t %12-15,22R, %0-3,5R", "VCVT%c.%7-8?USFF%18-19Sa.%7-8?FFUS%18-19Sa\t %12-15,22R, %0-3,5R", "SHA1C%u.32\t %12-15,22Q, %16-19,7Q, %0-3,5Q", "SHA1P%u.32\t %12-15,22Q, %16-19,7Q, %0-3,5Q", "SHA1M%u.32\t %12-15,22Q, %16-19,7Q, %0-3,5Q", "SHA1SU0%u.32\t %12-15,22Q, %16-19,7Q, %0-3,5Q", "SHA256H%u.32\t %12-15,22Q, %16-19,7Q, %0-3,5Q", "SHA256H2%u.32\t %12-15,22Q, %16-19,7Q, %0-3,5Q", "SHA256SU1%u.32\t %12-15,22Q, %16-19,7Q, %0-3,5Q", "VMAXNM%u.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VMINNM%u.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VAND%c\t %12-15,22R, %16-19,7R, %0-3,5R", "VBIC%c\t %12-15,22R, %16-19,7R, %0-3,5R", "VORR%c\t %12-15,22R, %16-19,7R, %0-3,5R", "VORN%c\t %12-15,22R, %16-19,7R, %0-3,5R", "VEOR%c\t %12-15,22R, %16-19,7R, %0-3,5R", "VBSL%c\t %12-15,22R, %16-19,7R, %0-3,5R", "VBIT%c\t %12-15,22R, %16-19,7R, %0-3,5R", "VBIF%c\t %12-15,22R, %16-19,7R, %0-3,5R", "VADD%c.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VMLA%c.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VCEQ%c.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VMAX%c.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VRECPS%c.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VSUB%c.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VMLS%c.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VMIN%c.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VRSQRTS%c.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VPADD%c.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VMUL%c.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VCGE%c.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VACGE%c.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VPMAX%c.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VABD%c.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VCGT%c.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VACGT%c.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VPMIN%c.F%20U0\t %12-15,22R, %16-19,7R, %0-3,5R", "VADD%c.i%20-21S3\t %12-15,22R, %16-19,7R, %0-3,5R", "VTST%c.%20-21S2\t %12-15,22R, %16-19,7R, %0-3,5R", "VMLA%c.i%20-21S2\t %12-15,22R, %16-19,7R, %0-3,5R", "VQDMULH%c.s%20-21S6\t %12-15,22R, %16-19,7R, %0-3,5R", "VPADD%c.i%20-21S2\t %12-15,22R, %16-19,7R, %0-3,5R", "VSUB%c.i%20-21S3\t %12-15,22R, %16-19,7R, %0-3,5R", "VCEQ%c.i%20-21S2\t %12-15,22R, %16-19,7R, %0-3,5R", "VMLS%c.i%20-21S2\t %12-15,22R, %16-19,7R, %0-3,5R", "VQRDMULH%c.s%20-21S6\t %12-15,22R, %16-19,7R, %0-3,5R", "VHADD%c.%24?US%20-21S2\t %12-15,22R, %16-19,7R, %0-3,5R", "VQADD%c.%24?US%20-21S3\t %12-15,22R, %16-19,7R, %0-3,5R", "VRHADD%c.%24?US%20-21S2\t %12-15,22R, %16-19,7R, %0-3,5R", "VHSUB%c.%24?US%20-21S2\t %12-15,22R, %16-19,7R, %0-3,5R", "VQSUB%c.%24?US%20-21S3\t %12-15,22R, %16-19,7R, %0-3,5R", "VCGT%c.%24?US%20-21S2\t %12-15,22R, %16-19,7R, %0-3,5R", "VCGE%c.%24?US%20-21S2\t %12-15,22R, %16-19,7R, %0-3,5R", "VSHL%c.%24?US%20-21S3\t %12-15,22R, %0-3,5R, %16-19,7R", "VQSHL%c.%24?US%20-21S3\t %12-15,22R, %0-3,5R, %16-19,7R", "VRSHL%c.%24?US%20-21S3\t %12-15,22R, %0-3,5R, %16-19,7R", "VQRSHL%c.%24?US%20-21S3\t %12-15,22R, %0-3,5R, %16-19,7R", "VMAX%c.%24?US%20-21S2\t %12-15,22R, %16-19,7R, %0-3,5R", "VMIN%c.%24?US%20-21S2\t %12-15,22R, %16-19,7R, %0-3,5R", "VABD%c.%24?US%20-21S2\t %12-15,22R, %16-19,7R, %0-3,5R", "VABA%c.%24?US%20-21S2\t %12-15,22R, %16-19,7R, %0-3,5R", "VMUL%c.%24?PI%20-21S2\t %12-15,22R, %16-19,7R, %0-3,5R", "VPMAX%c.%24?US%20-21S2\t %12-15,22R, %16-19,7R, %0-3,5R", "VPMIN%c.%24?US%20-21S2\t %12-15,22R, %16-19,7R, %0-3,5R", "VMOV%c.i8\t %12-15,22R, %E", "VMOV%c.i64\t %12-15,22R, %E", "VMOV%c.F32\t %12-15,22R, %E", "VMOV%c.i16\t %12-15,22R, %E", "VMVN%c.i16\t %12-15,22R, %E", "VORR%c.i16\t %12-15,22R, %E", "VBIC%c.i16\t %12-15,22R, %E", "VMOV%c.i32\t %12-15,22R, %E", "VMVN%c.i32\t %12-15,22R, %E", "VORR%c.i32\t %12-15,22R, %E", "VBIC%c.i32\t %12-15,22R, %E", "VMOV%c.i32\t %12-15,22R, %E", "VMVN%c.i32\t %12-15,22R, %E", "VSHRN%c.i16\t %12-15,22D, %0-3,5Q, #%16-18e", "VRSHRN%c.i16\t %12-15,22D, %0-3,5Q, #%16-18e", "VQSHRUN%c.s16\t %12-15,22D, %0-3,5Q, #%16-18e", "VQRSHRUN%c.s16\t %12-15,22D, %0-3,5Q, #%16-18e", "VQSHRN%c.%24?US16\t %12-15,22D, %0-3,5Q, #%16-18e", "VQRSHRN%c.%24?US16\t %12-15,22D, %0-3,5Q, #%16-18e", "VSHLL%c.%24?US8\t %12-15,22Q, %0-3,5D, #%16-18d", "VSHRN%c.i32\t %12-15,22D, %0-3,5Q, #%16-19e", "VRSHRN%c.i32\t %12-15,22D, %0-3,5Q, #%16-19e", "VSHL%c.%24?US8\t %12-15,22R, %0-3,5R, #%16-18d", "VSRI%c.8\t %12-15,22R, %0-3,5R, #%16-18e", "VSLI%c.8\t %12-15,22R, %0-3,5R, #%16-18d", "VQSHLU%c.s8\t %12-15,22R, %0-3,5R, #%16-18d", "VQSHRUN%c.s32\t %12-15,22D, %0-3,5Q, #%16-19e", "VQRSHRUN%c.s32\t %12-15,22D, %0-3,5Q, #%16-19e", "VQSHRN%c.%24?US32\t %12-15,22D, %0-3,5Q, #%16-19e", "VQRSHRN%c.%24?US32\t %12-15,22D, %0-3,5Q, #%16-19e", "VSHLL%c.%24?US16\t %12-15,22Q, %0-3,5D, #%16-19d", "VSHR%c.%24?US8\t %12-15,22R, %0-3,5R, #%16-18e", "VSRA%c.%24?US8\t %12-15,22R, %0-3,5R, #%16-18e", "VRSHR%c.%24?US8\t %12-15,22R, %0-3,5R, #%16-18e", "VRSRA%c.%24?US8\t %12-15,22R, %0-3,5R, #%16-18e", "VQSHL%c.%24?US8\t %12-15,22R, %0-3,5R, #%16-18d", "VSHRN%c.i64\t %12-15,22D, %0-3,5Q, #%16-20e", "VRSHRN%c.i64\t %12-15,22D, %0-3,5Q, #%16-20e", "VSHL%c.%24?US16\t %12-15,22R, %0-3,5R, #%16-19d", "VSRI%c.16\t %12-15,22R, %0-3,5R, #%16-19e", "VSLI%c.16\t %12-15,22R, %0-3,5R, #%16-19d", "VQSHLU%c.s16\t %12-15,22R, %0-3,5R, #%16-19d", "VSHLL%c.%24?US32\t %12-15,22Q, %0-3,5D, #%16-20d", "VSHR%c.%24?US16\t %12-15,22R, %0-3,5R, #%16-19e", "VSRA%c.%24?US16\t %12-15,22R, %0-3,5R, #%16-19e", "VRSHR%c.%24?US16\t %12-15,22R, %0-3,5R, #%16-19e", "VRSRA%c.%24?US16\t %12-15,22R, %0-3,5R, #%16-19e", "VQSHL%c.%24?US16\t %12-15,22R, %0-3,5R, #%16-19d", "VQSHRUN%c.s64\t %12-15,22D, %0-3,5Q, #%16-20e", "VQRSHRUN%c.s64\t %12-15,22D, %0-3,5Q, #%16-20e", "VQSHRN%c.%24?US64\t %12-15,22D, %0-3,5Q, #%16-20e", "VQRSHRN%c.%24?US64\t %12-15,22D, %0-3,5Q, #%16-20e", "VSHL%c.%24?US32\t %12-15,22R, %0-3,5R, #%16-20d", "VSRI%c.32\t %12-15,22R, %0-3,5R, #%16-20e", "VSLI%c.32\t %12-15,22R, %0-3,5R, #%16-20d", "VQSHLU%c.s32\t %12-15,22R, %0-3,5R, #%16-20d", "VSHR%c.%24?US32\t %12-15,22R, %0-3,5R, #%16-20e", "VSRA%c.%24?US32\t %12-15,22R, %0-3,5R, #%16-20e", "VRSHR%c.%24?US32\t %12-15,22R, %0-3,5R, #%16-20e", "VRSRA%c.%24?US32\t %12-15,22R, %0-3,5R, #%16-20e", "VQSHL%c.%24?US32\t %12-15,22R, %0-3,5R, #%16-20d", "VSHL%c.%24?US64\t %12-15,22R, %0-3,5R, #%16-21d", "VSRI%c.64\t %12-15,22R, %0-3,5R, #%16-21e", "VSLI%c.64\t %12-15,22R, %0-3,5R, #%16-21d", "VQSHLU%c.s64\t %12-15,22R, %0-3,5R, #%16-21d", "VSHR%c.%24?US64\t %12-15,22R, %0-3,5R, #%16-21e", "VSRA%c.%24?US64\t %12-15,22R, %0-3,5R, #%16-21e", "VRSHR%c.%24?US64\t %12-15,22R, %0-3,5R, #%16-21e", "VRSRA%c.%24?US64\t %12-15,22R, %0-3,5R, #%16-21e", "VQSHL%c.%24?US64\t %12-15,22R, %0-3,5R, #%16-21d", "VCVT%c.%24,8?USFF32.%24,8?FFUS32\t %12-15,22R, %0-3,5R, #%16-20e", "VMULL%c.p64\t %12-15,22Q, %16-19,7D, %0-3,5D", "VMULL%c.p%20S0\t %12-15,22Q, %16-19,7D, %0-3,5D", "VADDHN%c.i%20-21T2\t %12-15,22D, %16-19,7Q, %0-3,5Q", "VSUBHN%c.i%20-21T2\t %12-15,22D, %16-19,7Q, %0-3,5Q", "VQDMLAL%c.s%20-21S6\t %12-15,22Q, %16-19,7D, %0-3,5D", "VQDMLSL%c.s%20-21S6\t %12-15,22Q, %16-19,7D, %0-3,5D", "VQDMULL%c.s%20-21S6\t %12-15,22Q, %16-19,7D, %0-3,5D", "VRADDHN%c.i%20-21T2\t %12-15,22D, %16-19,7Q, %0-3,5Q", "VRSUBHN%c.i%20-21T2\t %12-15,22D, %16-19,7Q, %0-3,5Q", "VADDL%c.%24?US%20-21S2\t %12-15,22Q, %16-19,7D, %0-3,5D", "VADDW%c.%24?US%20-21S2\t %12-15,22Q, %16-19,7Q, %0-3,5D", "VSUBL%c.%24?US%20-21S2\t %12-15,22Q, %16-19,7D, %0-3,5D", "VSUBW%c.%24?US%20-21S2\t %12-15,22Q, %16-19,7Q, %0-3,5D", "VABAL%c.%24?US%20-21S2\t %12-15,22Q, %16-19,7D, %0-3,5D", "VABDL%c.%24?US%20-21S2\t %12-15,22Q, %16-19,7D, %0-3,5D", "VMLAL%c.%24?US%20-21S2\t %12-15,22Q, %16-19,7D, %0-3,5D", "VMLSL%c.%24?US%20-21S2\t %12-15,22Q, %16-19,7D, %0-3,5D", "VMULL%c.%24?US%20-21S2\t %12-15,22Q, %16-19,7D, %0-3,5D", "VMLA%c.i%20-21S6\t %12-15,22D, %16-19,7D, %D", "VMLA%c.F%20-21Sa\t %12-15,22D, %16-19,7D, %D", "VQDMLAL%c.s%20-21S6\t %12-15,22Q, %16-19,7D, %D", "VMLS%c.i%20-21S6\t %12-15,22D, %16-19,7D, %D", "VMLS%c.F%20-21S6\t %12-15,22D, %16-19,7D, %D", "VQDMLSL%c.s%20-21S6\t %12-15,22Q, %16-19,7D, %D", "VMUL%c.i%20-21S6\t %12-15,22D, %16-19,7D, %D", "VMUL%c.F%20-21Sa\t %12-15,22D, %16-19,7D, %D", "VQDMULL%c.s%20-21S6\t %12-15,22Q, %16-19,7D, %D", "VQDMULH%c.s%20-21S6\t %12-15,22D, %16-19,7D, %D", "VQRDMULH%c.s%20-21S6\t %12-15,22D, %16-19,7D, %D", "VMLA%c.i%20-21S6\t %12-15,22Q, %16-19,7Q, %D", "VMLA%c.F%20-21Sa\t %12-15,22Q, %16-19,7Q, %D", "VMLS%c.i%20-21S6\t %12-15,22Q, %16-19,7Q, %D", "VMLS%c.F%20-21Sa\t %12-15,22Q, %16-19,7Q, %D", "VMUL%c.i%20-21S6\t %12-15,22Q, %16-19,7Q, %D", "VMUL%c.F%20-21Sa\t %12-15,22Q, %16-19,7Q, %D", "VQDMULH%c.s%20-21S6\t %12-15,22Q, %16-19,7Q, %D", "VQRDMULH%c.s%20-21S6\t %12-15,22Q, %16-19,7Q, %D", "VMLAL%c.%24?US%20-21S6\t %12-15,22Q, %16-19,7D, %D", "VMLSL%c.%24?US%20-21S6\t %12-15,22Q, %16-19,7D, %D", "VMULL%c.%24?US%20-21S6\t %12-15,22Q, %16-19,7D, %D", "VLD4%c.32\t %C", "VLD1%c.%6-7S2\t %C", "VLD2%c.%6-7S2\t %C", "VLD3%c.%6-7S2\t %C", "VLD4%c.%6-7S2\t %C", "V%21?LS%21?DT1%c.%6-7S3\t %A", "V%21?LS%21?DT2%c.%6-7S2\t %A", "V%21?LS%21?DT3%c.%6-7S2\t %A", "V%21?LS%21?DT3%c.%6-7S2\t %A", "V%21?LS%21?DT1%c.%6-7S3\t %A", "V%21?LS%21?DT1%c.%6-7S3\t %A", "V%21?LS%21?DT2%c.%6-7S2\t %A", "V%21?LS%21?DT2%c.%6-7S2\t %A", "V%21?LS%21?DT1%c.%6-7S3\t %A", "V%21?LS%21?DT4%c.%6-7S2\t %A", "V%21?LS%21?DT1%c.%10-11S2\t %B", "V%21?LS%21?DT2%c.%10-11S2\t %B", "V%21?LS%21?DT3%c.%10-11S2\t %B", "V%21?LS%21?DT4%c.%10-11S2\t %B"};
            NUM = num;
            STR = str;
        }

        static String getOpcode(ArmDis ret, long addr, int data, boolean thumb) {
            int limit;
            int value;
            int size;
            if (thumb) {
                if (((-285212672) & data) == -285212672) {
                    boolean bit28 = (268435456 & data) != 0;
                    int data2 = data & LuaTable.MAX_LEN;
                    if (bit28) {
                        data = data2 | (-218103808);
                    } else {
                        data = data2 | (-234881024);
                    }
                } else if (((-16777216) & data) == -117440512) {
                    data ^= 218103808;
                } else {
                    return null;
                }
            }
            int si = STR.length;
            for (int i = 0; i < si; i++) {
                if ((NUM[(i * 2) + 1] & data) == NUM[i * 2]) {
                    StringBuilder out = new StringBuilder();
                    int commentValue = 0;
                    String asm = STR[i];
                    int j = 0;
                    int sj = asm.length();
                    while (j < sj) {
                        char c = ArmDis.charAt(asm, sj, j);
                        if (c != '%') {
                            ArmDis.append(out, c);
                        } else {
                            j++;
                            char c2 = ArmDis.charAt(asm, sj, j);
                            switch (c2) {
                                case '%':
                                    out.append('%');
                                    break;
                                case LasmConstants.SHL /* 48 */:
                                case LasmConstants.SHR /* 49 */:
                                case '2':
                                case LasmConstants.PROTO /* 51 */:
                                case LasmConstants.GOTO /* 52 */:
                                case LasmConstants.OP /* 53 */:
                                case LasmConstants.SET_TOP /* 54 */:
                                case LasmConstants.SKIP_NEXT /* 55 */:
                                case LasmConstants.NIL /* 56 */:
                                case LasmConstants.TRUE /* 57 */:
                                    j = ArmDis.decodeBitfield(asm, sj, j, data, ret);
                                    int value2 = ret.valuep;
                                    int width = ret.widthp;
                                    char c3 = ArmDis.charAt(asm, sj, j);
                                    switch (c3) {
                                        case '\'':
                                            j++;
                                            char c4 = ArmDis.charAt(asm, sj, j);
                                            if (value2 == (1 << width) - 1) {
                                                ArmDis.append(out, c4);
                                            } else {
                                                continue;
                                            }
                                            break;
                                        case '?':
                                            ArmDis.append(out, ArmDis.charAt(asm, sj, ((1 << width) + j) - value2));
                                            j += 1 << width;
                                            ArmDis.charAt(asm, sj, j);
                                            continue;
                                        case LasmConstants.UPVAL /* 68 */:
                                            out.append('D').append(value2);
                                            continue;
                                        case LasmConstants.STRING /* 81 */:
                                            break;
                                        case 'R':
                                            if ((data & 64) == 0) {
                                                out.append('D').append(value2);
                                            }
                                            break;
                                        case LasmConstants.NL /* 83 */:
                                        case LasmConstants.FLOAT /* 84 */:
                                        case LasmConstants.DIGIT /* 85 */:
                                            int base = 8 << (c3 - 'S');
                                            j++;
                                            char c5 = ArmDis.charAt(asm, sj, j);
                                            if (c5 >= '0' && c5 <= '9') {
                                                limit = c5 - '0';
                                            } else if (c5 >= 'a' && c5 <= 'f') {
                                                limit = (c5 - 'a') + 10;
                                            } else {
                                                throw new RuntimeException("40 Invalid format char '" + c5 + "'; " + i + ListManager.TEXT_SEPARATOR + j + "; '" + asm + "'");
                                            }
                                            int low = limit >> 2;
                                            int high = limit & 3;
                                            if (value2 < low || value2 > high) {
                                                out.append("<illegal width ").append(base << value2).append('>');
                                                continue;
                                            } else {
                                                out.append(base << value2);
                                            }
                                            break;
                                        case '`':
                                            j++;
                                            char c6 = ArmDis.charAt(asm, sj, j);
                                            if (value2 == 0) {
                                                ArmDis.append(out, c6);
                                            } else {
                                                continue;
                                            }
                                            break;
                                        case Message.ERROR_FATAL /* 100 */:
                                            out.append(value2);
                                            commentValue = value2;
                                            continue;
                                        case 'e':
                                            out.append((1 << width) - value2);
                                            continue;
                                        case 'r':
                                            out.append(ArmDis.REG[value2]);
                                            continue;
                                        default:
                                            throw new RuntimeException("50 Invalid format char '" + c3 + "'; " + i + ListManager.TEXT_SEPARATOR + j + "; '" + asm + "'");
                                    }
                                    if ((value2 & 1) != 0) {
                                        out.append("<illegal reg Q").append(value2 >> 1).append(".5>");
                                    } else {
                                        out.append(SearchMenuItem.HEX_UTF).append(value2 >> 1);
                                    }
                                    break;
                                case LasmConstants.NUMPARAMS /* 65 */:
                                    int rd = ((data >> 12) & 15) | (((data >> 22) & 1) << 4);
                                    int rn = (data >> 16) & 15;
                                    int rm = (data >> 0) & 15;
                                    int align = (data >> 4) & 3;
                                    int type = (data >> 8) & 15;
                                    int n = ENC[type] & 15;
                                    int stride = (ENC[type] >> 4) + 1;
                                    out.append('{');
                                    if (stride > 1) {
                                        for (int ix = 0; ix != n; ix++) {
                                            if (ix != 0) {
                                                out.append(',');
                                            }
                                            out.append('D').append((ix * stride) + rd);
                                        }
                                    } else if (n == 1) {
                                        out.append('D').append(rd);
                                    } else {
                                        out.append('D').append(rd).append("-D").append((rd + n) - 1);
                                    }
                                    out.append("}, [").append(ArmDis.REG[rn]);
                                    if (align != 0) {
                                        out.append(" :").append(32 << align);
                                    }
                                    out.append(']');
                                    if (rm == 13) {
                                        out.append('!');
                                    } else if (rm != 15) {
                                        out.append(", ").append(ArmDis.REG[rm]);
                                    }
                                    break;
                                case LasmConstants.IS_VARARG /* 66 */:
                                    int rd2 = ((data >> 12) & 15) | (((data >> 22) & 1) << 4);
                                    int rn2 = (data >> 16) & 15;
                                    int rm2 = (data >> 0) & 15;
                                    int idx_align = (data >> 4) & 15;
                                    int align2 = 0;
                                    int size2 = (data >> 10) & 3;
                                    int idx = idx_align >> (size2 + 1);
                                    int length = ((data >> 8) & 3) + 1;
                                    int stride2 = 1;
                                    if (length > 1 && size2 > 0) {
                                        stride2 = ((1 << size2) & idx_align) != 0 ? 2 : 1;
                                    }
                                    switch (length) {
                                        case 1:
                                            if (((1 << size2) & idx_align) != 0) {
                                                return null;
                                            }
                                            if (size2 > 0) {
                                                int amask = (1 << size2) - 1;
                                                if ((idx_align & amask) == amask) {
                                                    align2 = 8 << size2;
                                                } else if ((idx_align & amask) != 0) {
                                                    return null;
                                                }
                                            }
                                            break;
                                        case 2:
                                            if (size2 == 2 && (idx_align & 2) != 0) {
                                                return null;
                                            }
                                            align2 = (idx_align & 1) != 0 ? 16 << size2 : 0;
                                            break;
                                        case 3:
                                            if ((size2 == 2 && (idx_align & 3) != 0) || (idx_align & 1) != 0) {
                                                return null;
                                            }
                                            break;
                                        case 4:
                                            if (size2 == 2) {
                                                if ((idx_align & 3) == 3) {
                                                    return null;
                                                }
                                                align2 = (idx_align & 3) * 64;
                                            } else if ((idx_align & 1) != 0) {
                                                align2 = 32 << size2;
                                            } else {
                                                align2 = 0;
                                            }
                                            break;
                                        default:
                                            throw new RuntimeException("150 Invalid length " + length + " for format char '" + c2 + "'; " + i + ListManager.TEXT_SEPARATOR + j + "; '" + asm + "'");
                                    }
                                    out.append('{');
                                    for (int ix2 = 0; ix2 < length; ix2++) {
                                        if (ix2 != 0) {
                                            out.append(',');
                                        }
                                        out.append('D').append((ix2 * stride2) + rd2).append('[').append(idx).append(']');
                                    }
                                    out.append("}, [").append(ArmDis.REG[rn2]);
                                    if (align2 != 0) {
                                        out.append(" :").append(align2);
                                    }
                                    out.append(']');
                                    if (rm2 == 13) {
                                        out.append('!');
                                    } else if (rm2 != 15) {
                                        out.append(", ").append(ArmDis.REG[rm2]);
                                    }
                                    break;
                                case LasmConstants.MAXSTACKSIZE /* 67 */:
                                    int rd3 = ((data >> 12) & 15) | (((data >> 22) & 1) << 4);
                                    int rn3 = (data >> 16) & 15;
                                    int rm3 = (data >> 0) & 15;
                                    int align3 = (data >> 4) & 1;
                                    int size3 = (data >> 6) & 3;
                                    int type2 = (data >> 8) & 3;
                                    int n2 = type2 + 1;
                                    int stride3 = (data >> 5) & 1;
                                    if (stride3 != 0 && n2 == 1) {
                                        n2++;
                                    } else {
                                        stride3++;
                                    }
                                    out.append('{');
                                    if (stride3 > 1) {
                                        for (int ix3 = 0; ix3 != n2; ix3++) {
                                            if (ix3 != 0) {
                                                out.append(',');
                                            }
                                            out.append('D').append((ix3 * stride3) + rd3).append("[]");
                                        }
                                    } else if (n2 == 1) {
                                        out.append('D').append(rd3).append("[]");
                                    } else {
                                        out.append('D').append(rd3).append("[]-D").append((rd3 + n2) - 1).append("[]");
                                    }
                                    out.append("}, [").append(ArmDis.REG[rn3]);
                                    if (align3 != 0) {
                                        int align4 = ((type2 + 1) * 8) << size3;
                                        if (type2 == 3 && size3 > 1) {
                                            align4 >>= 1;
                                        }
                                        if (type2 == 2 || (type2 == 0 && size3 == 0)) {
                                            out.append(" :<bad align ").append(align4).append('>');
                                        } else {
                                            out.append(" :").append(align4);
                                        }
                                    }
                                    out.append(']');
                                    if (rm3 == 13) {
                                        out.append('!');
                                    } else if (rm3 != 15) {
                                        out.append(", ").append(ArmDis.REG[rm3]);
                                    }
                                    break;
                                case LasmConstants.UPVAL /* 68 */:
                                    int raw_reg = (data & 15) | ((data >> 1) & 16);
                                    int size4 = (data >> 20) & 3;
                                    int reg = raw_reg & ((4 << size4) - 1);
                                    int ix4 = (raw_reg >> size4) >> 2;
                                    out.append('D').append(reg).append('[').append(ix4).append(']');
                                    break;
                                case LasmConstants.LINE /* 69 */:
                                    int cmode = (data >> 8) & 15;
                                    int op = (data >> 5) & 1;
                                    int hival = 0;
                                    boolean isfloat = false;
                                    int bits = 0 | (((data >> 24) & 1) << 7) | (((data >> 16) & 7) << 4) | (((data >> 0) & 15) << 0);
                                    if (cmode < 8) {
                                        value = bits << (((cmode >> 1) & 3) * 8);
                                        size = 32;
                                    } else if (cmode < 12) {
                                        value = bits << (((cmode >> 1) & 1) * 8);
                                        size = 16;
                                    } else if (cmode < 14) {
                                        int shift = (cmode & 1) + 1;
                                        value = (bits << (shift * 8)) | ((1 << (shift * 8)) - 1);
                                        size = 32;
                                    } else if (cmode != 14) {
                                        if (op == 0) {
                                            int value3 = ((bits & AddressItem.FLAG_AUTO) << 19) | ((bits & 128) << 24);
                                            int tmp = (bits & 64) != 0 ? 60 : 64;
                                            value = value3 | (tmp << 24);
                                            size = 32;
                                            isfloat = true;
                                        } else {
                                            out.append("<illegal constant ");
                                            ToolsLight.prefixIntegerHex(out, 8, bits);
                                            out.append(':').append(Integer.toHexString(cmode).toUpperCase(Locale.US)).append(':').append(Integer.toHexString(op).toUpperCase(Locale.US)).append('>');
                                        }
                                        break;
                                    } else if (op != 0) {
                                        value = 0;
                                        hival = 0;
                                        for (int ix5 = 7; ix5 >= 0; ix5--) {
                                            int mask = ((bits >> ix5) & 1) != 0 ? 255 : 0;
                                            if (ix5 <= 3) {
                                                value = (value << 8) | mask;
                                            } else {
                                                hival = (hival << 8) | mask;
                                            }
                                        }
                                        size = 64;
                                    } else {
                                        value = bits;
                                        size = 8;
                                    }
                                    out.append('#');
                                    switch (size) {
                                        case 8:
                                        case 16:
                                        case 32:
                                            if (isfloat) {
                                                out.append(Float.intBitsToFloat(value));
                                            } else {
                                                out.append(value);
                                            }
                                            out.append("\t ; 0x");
                                            ToolsLight.prefixIntegerHex(out, size >> 2, value);
                                            break;
                                        case 64:
                                            ToolsLight.prefixIntegerHex(out, 8, hival);
                                            ToolsLight.prefixIntegerHex(out, 8, value);
                                            break;
                                        default:
                                            throw new RuntimeException("160 Invalid size " + size + " for format char '" + c2 + "'; " + i + ListManager.TEXT_SEPARATOR + j + "; '" + asm + "'");
                                    }
                                    break;
                                case LasmConstants.LOCAL /* 70 */:
                                    int regno = ((data >> 16) & 15) | ((data >> 3) & 16);
                                    int num = (data >> 8) & 3;
                                    out.append("{D").append(regno);
                                    if (num != 0) {
                                        if (num + regno >= 32) {
                                            out.append("-<overflow reg D").append(regno + num);
                                        } else {
                                            out.append("-D").append(regno + num);
                                        }
                                    }
                                    out.append('}');
                                    break;
                                case 'c':
                                case 'u':
                                    break;
                                default:
                                    throw new RuntimeException("60 Invalid format char '" + c2 + "'; " + i + ListManager.TEXT_SEPARATOR + j + "; '" + asm + "'");
                            }
                        }
                        j++;
                    }
                    if (commentValue > 32 || commentValue < -16) {
                        out.append("\t ; 0x").append(Integer.toHexString(commentValue).toUpperCase(Locale.US));
                    }
                    if (0 != 0) {
                        out.append(ArmDis.UNPREDICTABLE_INSTRUCTION);
                    }
                    return out.toString();
                }
            }
            return null;
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Removed duplicated region for block: B:174:0x042f  */
        /* JADX WARN: Removed duplicated region for block: B:177:0x043d A[Catch: NextAsm -> 0x0113, TryCatch #0 {NextAsm -> 0x0113, blocks: (B:8:0x001f, B:10:0x0029, B:11:0x002c, B:14:0x0039, B:478:0x0c11, B:482:0x0c21, B:487:0x0c35, B:20:0x006d, B:25:0x0083, B:39:0x00be, B:35:0x00ad, B:36:0x00b2, B:38:0x00ba, B:40:0x00c1, B:41:0x00cb, B:42:0x00ce, B:43:0x0112, B:46:0x0116, B:49:0x0120, B:51:0x012c, B:53:0x014a, B:56:0x0162, B:60:0x0176, B:62:0x0182, B:66:0x01a0, B:68:0x01ac, B:70:0x01be, B:72:0x01ce, B:75:0x01e0, B:78:0x01f2, B:80:0x01fe, B:83:0x020c, B:101:0x0265, B:103:0x0271, B:87:0x021b, B:89:0x0227, B:92:0x0237, B:93:0x023b, B:95:0x0247, B:107:0x0288, B:109:0x0294, B:111:0x02a6, B:114:0x02be, B:116:0x02ca, B:119:0x02d4, B:121:0x02e0, B:129:0x0302, B:131:0x030e, B:133:0x0320, B:135:0x0330, B:136:0x0334, B:137:0x0337, B:138:0x0389, B:160:0x03ed, B:165:0x03fb, B:168:0x0409, B:171:0x0417, B:172:0x041f, B:175:0x0431, B:177:0x043d, B:180:0x044b, B:205:0x04b4, B:207:0x04c0, B:184:0x0456, B:187:0x0464, B:190:0x0472, B:193:0x047d, B:196:0x048b, B:198:0x0494, B:201:0x04a2, B:204:0x04b0, B:139:0x038a, B:141:0x0396, B:143:0x03a2, B:147:0x03b0, B:148:0x03b6, B:151:0x03c2, B:153:0x03ce, B:156:0x03da, B:158:0x03e6, B:211:0x04d8, B:213:0x04e4, B:215:0x04f6, B:218:0x050e, B:220:0x051a, B:222:0x0526, B:224:0x0530, B:228:0x053a, B:230:0x0546, B:232:0x0558, B:234:0x0568, B:241:0x0598, B:243:0x05a4, B:284:0x065c, B:249:0x05b4, B:252:0x05c6, B:255:0x05cc, B:257:0x05d8, B:260:0x05e6, B:285:0x0660, B:287:0x066c, B:263:0x05f2, B:265:0x05fe, B:268:0x060e, B:270:0x061a, B:271:0x061e, B:275:0x0632, B:276:0x0636, B:279:0x0646, B:291:0x0684, B:293:0x0690, B:295:0x06ae, B:297:0x06cc, B:300:0x06da, B:303:0x06e2, B:305:0x06ee, B:307:0x06f8, B:310:0x0714, B:313:0x0720, B:317:0x0743, B:319:0x0749, B:327:0x0774, B:332:0x078a, B:333:0x0790, B:335:0x0796, B:336:0x0799, B:340:0x07a7, B:343:0x07c5, B:346:0x07d2, B:349:0x07dc, B:355:0x0803, B:358:0x0813, B:363:0x0844, B:365:0x0850, B:367:0x0860, B:368:0x0866, B:371:0x0884, B:373:0x0888, B:376:0x088e, B:380:0x089f, B:381:0x08c2, B:382:0x08c5, B:383:0x0909, B:384:0x090a, B:386:0x091c, B:387:0x0934, B:390:0x095e, B:393:0x098e, B:397:0x09a8, B:398:0x09aa, B:406:0x09ea, B:413:0x0a06, B:414:0x0a0b, B:415:0x0a4f, B:416:0x0a50, B:418:0x0a5c, B:421:0x0a86, B:423:0x0a8a, B:425:0x0a96, B:428:0x0ac0, B:429:0x0ac2, B:431:0x0ace, B:437:0x0b02, B:439:0x0b12, B:441:0x0b1e, B:448:0x0b51, B:450:0x0b5d, B:445:0x0b37, B:447:0x0b43, B:451:0x0b63, B:453:0x0b6f, B:455:0x0b7b, B:457:0x0b87, B:458:0x0b97, B:470:0x0bc7, B:473:0x0bf3, B:472:0x0bdd, B:474:0x0bfb, B:477:0x0c0e), top: B:491:0x001f }] */
        /* JADX WARN: Removed duplicated region for block: B:19:0x005a  */
        /* JADX WARN: Removed duplicated region for block: B:228:0x053a A[Catch: NextAsm -> 0x0113, PHI: r15 r33
  0x053a: PHI (r15v31 'fail' int) = (r15v28 'fail' int), (r15v29 'fail' int), (r15v40 'fail' int) binds: [B:272:0x0628, B:280:0x0650, B:579:0x053a] A[DONT_GENERATE, DONT_INLINE]
  0x053a: PHI (r33v30 'out' int) = (r33v27 'out' int), (r33v28 'out' int), (r33v36 'out' int) binds: [B:272:0x0628, B:280:0x0650, B:579:0x053a] A[DONT_GENERATE, DONT_INLINE], TryCatch #0 {NextAsm -> 0x0113, blocks: (B:8:0x001f, B:10:0x0029, B:11:0x002c, B:14:0x0039, B:478:0x0c11, B:482:0x0c21, B:487:0x0c35, B:20:0x006d, B:25:0x0083, B:39:0x00be, B:35:0x00ad, B:36:0x00b2, B:38:0x00ba, B:40:0x00c1, B:41:0x00cb, B:42:0x00ce, B:43:0x0112, B:46:0x0116, B:49:0x0120, B:51:0x012c, B:53:0x014a, B:56:0x0162, B:60:0x0176, B:62:0x0182, B:66:0x01a0, B:68:0x01ac, B:70:0x01be, B:72:0x01ce, B:75:0x01e0, B:78:0x01f2, B:80:0x01fe, B:83:0x020c, B:101:0x0265, B:103:0x0271, B:87:0x021b, B:89:0x0227, B:92:0x0237, B:93:0x023b, B:95:0x0247, B:107:0x0288, B:109:0x0294, B:111:0x02a6, B:114:0x02be, B:116:0x02ca, B:119:0x02d4, B:121:0x02e0, B:129:0x0302, B:131:0x030e, B:133:0x0320, B:135:0x0330, B:136:0x0334, B:137:0x0337, B:138:0x0389, B:160:0x03ed, B:165:0x03fb, B:168:0x0409, B:171:0x0417, B:172:0x041f, B:175:0x0431, B:177:0x043d, B:180:0x044b, B:205:0x04b4, B:207:0x04c0, B:184:0x0456, B:187:0x0464, B:190:0x0472, B:193:0x047d, B:196:0x048b, B:198:0x0494, B:201:0x04a2, B:204:0x04b0, B:139:0x038a, B:141:0x0396, B:143:0x03a2, B:147:0x03b0, B:148:0x03b6, B:151:0x03c2, B:153:0x03ce, B:156:0x03da, B:158:0x03e6, B:211:0x04d8, B:213:0x04e4, B:215:0x04f6, B:218:0x050e, B:220:0x051a, B:222:0x0526, B:224:0x0530, B:228:0x053a, B:230:0x0546, B:232:0x0558, B:234:0x0568, B:241:0x0598, B:243:0x05a4, B:284:0x065c, B:249:0x05b4, B:252:0x05c6, B:255:0x05cc, B:257:0x05d8, B:260:0x05e6, B:285:0x0660, B:287:0x066c, B:263:0x05f2, B:265:0x05fe, B:268:0x060e, B:270:0x061a, B:271:0x061e, B:275:0x0632, B:276:0x0636, B:279:0x0646, B:291:0x0684, B:293:0x0690, B:295:0x06ae, B:297:0x06cc, B:300:0x06da, B:303:0x06e2, B:305:0x06ee, B:307:0x06f8, B:310:0x0714, B:313:0x0720, B:317:0x0743, B:319:0x0749, B:327:0x0774, B:332:0x078a, B:333:0x0790, B:335:0x0796, B:336:0x0799, B:340:0x07a7, B:343:0x07c5, B:346:0x07d2, B:349:0x07dc, B:355:0x0803, B:358:0x0813, B:363:0x0844, B:365:0x0850, B:367:0x0860, B:368:0x0866, B:371:0x0884, B:373:0x0888, B:376:0x088e, B:380:0x089f, B:381:0x08c2, B:382:0x08c5, B:383:0x0909, B:384:0x090a, B:386:0x091c, B:387:0x0934, B:390:0x095e, B:393:0x098e, B:397:0x09a8, B:398:0x09aa, B:406:0x09ea, B:413:0x0a06, B:414:0x0a0b, B:415:0x0a4f, B:416:0x0a50, B:418:0x0a5c, B:421:0x0a86, B:423:0x0a8a, B:425:0x0a96, B:428:0x0ac0, B:429:0x0ac2, B:431:0x0ace, B:437:0x0b02, B:439:0x0b12, B:441:0x0b1e, B:448:0x0b51, B:450:0x0b5d, B:445:0x0b37, B:447:0x0b43, B:451:0x0b63, B:453:0x0b6f, B:455:0x0b7b, B:457:0x0b87, B:458:0x0b97, B:470:0x0bc7, B:473:0x0bf3, B:472:0x0bdd, B:474:0x0bfb, B:477:0x0c0e), top: B:491:0x001f }] */
        /* JADX WARN: Removed duplicated region for block: B:230:0x0546 A[Catch: NextAsm -> 0x0113, TryCatch #0 {NextAsm -> 0x0113, blocks: (B:8:0x001f, B:10:0x0029, B:11:0x002c, B:14:0x0039, B:478:0x0c11, B:482:0x0c21, B:487:0x0c35, B:20:0x006d, B:25:0x0083, B:39:0x00be, B:35:0x00ad, B:36:0x00b2, B:38:0x00ba, B:40:0x00c1, B:41:0x00cb, B:42:0x00ce, B:43:0x0112, B:46:0x0116, B:49:0x0120, B:51:0x012c, B:53:0x014a, B:56:0x0162, B:60:0x0176, B:62:0x0182, B:66:0x01a0, B:68:0x01ac, B:70:0x01be, B:72:0x01ce, B:75:0x01e0, B:78:0x01f2, B:80:0x01fe, B:83:0x020c, B:101:0x0265, B:103:0x0271, B:87:0x021b, B:89:0x0227, B:92:0x0237, B:93:0x023b, B:95:0x0247, B:107:0x0288, B:109:0x0294, B:111:0x02a6, B:114:0x02be, B:116:0x02ca, B:119:0x02d4, B:121:0x02e0, B:129:0x0302, B:131:0x030e, B:133:0x0320, B:135:0x0330, B:136:0x0334, B:137:0x0337, B:138:0x0389, B:160:0x03ed, B:165:0x03fb, B:168:0x0409, B:171:0x0417, B:172:0x041f, B:175:0x0431, B:177:0x043d, B:180:0x044b, B:205:0x04b4, B:207:0x04c0, B:184:0x0456, B:187:0x0464, B:190:0x0472, B:193:0x047d, B:196:0x048b, B:198:0x0494, B:201:0x04a2, B:204:0x04b0, B:139:0x038a, B:141:0x0396, B:143:0x03a2, B:147:0x03b0, B:148:0x03b6, B:151:0x03c2, B:153:0x03ce, B:156:0x03da, B:158:0x03e6, B:211:0x04d8, B:213:0x04e4, B:215:0x04f6, B:218:0x050e, B:220:0x051a, B:222:0x0526, B:224:0x0530, B:228:0x053a, B:230:0x0546, B:232:0x0558, B:234:0x0568, B:241:0x0598, B:243:0x05a4, B:284:0x065c, B:249:0x05b4, B:252:0x05c6, B:255:0x05cc, B:257:0x05d8, B:260:0x05e6, B:285:0x0660, B:287:0x066c, B:263:0x05f2, B:265:0x05fe, B:268:0x060e, B:270:0x061a, B:271:0x061e, B:275:0x0632, B:276:0x0636, B:279:0x0646, B:291:0x0684, B:293:0x0690, B:295:0x06ae, B:297:0x06cc, B:300:0x06da, B:303:0x06e2, B:305:0x06ee, B:307:0x06f8, B:310:0x0714, B:313:0x0720, B:317:0x0743, B:319:0x0749, B:327:0x0774, B:332:0x078a, B:333:0x0790, B:335:0x0796, B:336:0x0799, B:340:0x07a7, B:343:0x07c5, B:346:0x07d2, B:349:0x07dc, B:355:0x0803, B:358:0x0813, B:363:0x0844, B:365:0x0850, B:367:0x0860, B:368:0x0866, B:371:0x0884, B:373:0x0888, B:376:0x088e, B:380:0x089f, B:381:0x08c2, B:382:0x08c5, B:383:0x0909, B:384:0x090a, B:386:0x091c, B:387:0x0934, B:390:0x095e, B:393:0x098e, B:397:0x09a8, B:398:0x09aa, B:406:0x09ea, B:413:0x0a06, B:414:0x0a0b, B:415:0x0a4f, B:416:0x0a50, B:418:0x0a5c, B:421:0x0a86, B:423:0x0a8a, B:425:0x0a96, B:428:0x0ac0, B:429:0x0ac2, B:431:0x0ace, B:437:0x0b02, B:439:0x0b12, B:441:0x0b1e, B:448:0x0b51, B:450:0x0b5d, B:445:0x0b37, B:447:0x0b43, B:451:0x0b63, B:453:0x0b6f, B:455:0x0b7b, B:457:0x0b87, B:458:0x0b97, B:470:0x0bc7, B:473:0x0bf3, B:472:0x0bdd, B:474:0x0bfb, B:477:0x0c0e), top: B:491:0x001f }] */
        /* JADX WARN: Removed duplicated region for block: B:448:0x0b51 A[Catch: NextAsm -> 0x0113, TryCatch #0 {NextAsm -> 0x0113, blocks: (B:8:0x001f, B:10:0x0029, B:11:0x002c, B:14:0x0039, B:478:0x0c11, B:482:0x0c21, B:487:0x0c35, B:20:0x006d, B:25:0x0083, B:39:0x00be, B:35:0x00ad, B:36:0x00b2, B:38:0x00ba, B:40:0x00c1, B:41:0x00cb, B:42:0x00ce, B:43:0x0112, B:46:0x0116, B:49:0x0120, B:51:0x012c, B:53:0x014a, B:56:0x0162, B:60:0x0176, B:62:0x0182, B:66:0x01a0, B:68:0x01ac, B:70:0x01be, B:72:0x01ce, B:75:0x01e0, B:78:0x01f2, B:80:0x01fe, B:83:0x020c, B:101:0x0265, B:103:0x0271, B:87:0x021b, B:89:0x0227, B:92:0x0237, B:93:0x023b, B:95:0x0247, B:107:0x0288, B:109:0x0294, B:111:0x02a6, B:114:0x02be, B:116:0x02ca, B:119:0x02d4, B:121:0x02e0, B:129:0x0302, B:131:0x030e, B:133:0x0320, B:135:0x0330, B:136:0x0334, B:137:0x0337, B:138:0x0389, B:160:0x03ed, B:165:0x03fb, B:168:0x0409, B:171:0x0417, B:172:0x041f, B:175:0x0431, B:177:0x043d, B:180:0x044b, B:205:0x04b4, B:207:0x04c0, B:184:0x0456, B:187:0x0464, B:190:0x0472, B:193:0x047d, B:196:0x048b, B:198:0x0494, B:201:0x04a2, B:204:0x04b0, B:139:0x038a, B:141:0x0396, B:143:0x03a2, B:147:0x03b0, B:148:0x03b6, B:151:0x03c2, B:153:0x03ce, B:156:0x03da, B:158:0x03e6, B:211:0x04d8, B:213:0x04e4, B:215:0x04f6, B:218:0x050e, B:220:0x051a, B:222:0x0526, B:224:0x0530, B:228:0x053a, B:230:0x0546, B:232:0x0558, B:234:0x0568, B:241:0x0598, B:243:0x05a4, B:284:0x065c, B:249:0x05b4, B:252:0x05c6, B:255:0x05cc, B:257:0x05d8, B:260:0x05e6, B:285:0x0660, B:287:0x066c, B:263:0x05f2, B:265:0x05fe, B:268:0x060e, B:270:0x061a, B:271:0x061e, B:275:0x0632, B:276:0x0636, B:279:0x0646, B:291:0x0684, B:293:0x0690, B:295:0x06ae, B:297:0x06cc, B:300:0x06da, B:303:0x06e2, B:305:0x06ee, B:307:0x06f8, B:310:0x0714, B:313:0x0720, B:317:0x0743, B:319:0x0749, B:327:0x0774, B:332:0x078a, B:333:0x0790, B:335:0x0796, B:336:0x0799, B:340:0x07a7, B:343:0x07c5, B:346:0x07d2, B:349:0x07dc, B:355:0x0803, B:358:0x0813, B:363:0x0844, B:365:0x0850, B:367:0x0860, B:368:0x0866, B:371:0x0884, B:373:0x0888, B:376:0x088e, B:380:0x089f, B:381:0x08c2, B:382:0x08c5, B:383:0x0909, B:384:0x090a, B:386:0x091c, B:387:0x0934, B:390:0x095e, B:393:0x098e, B:397:0x09a8, B:398:0x09aa, B:406:0x09ea, B:413:0x0a06, B:414:0x0a0b, B:415:0x0a4f, B:416:0x0a50, B:418:0x0a5c, B:421:0x0a86, B:423:0x0a8a, B:425:0x0a96, B:428:0x0ac0, B:429:0x0ac2, B:431:0x0ace, B:437:0x0b02, B:439:0x0b12, B:441:0x0b1e, B:448:0x0b51, B:450:0x0b5d, B:445:0x0b37, B:447:0x0b43, B:451:0x0b63, B:453:0x0b6f, B:455:0x0b7b, B:457:0x0b87, B:458:0x0b97, B:470:0x0bc7, B:473:0x0bf3, B:472:0x0bdd, B:474:0x0bfb, B:477:0x0c0e), top: B:491:0x001f }] */
        /* JADX WARN: Removed duplicated region for block: B:450:0x0b5d A[Catch: NextAsm -> 0x0113, TryCatch #0 {NextAsm -> 0x0113, blocks: (B:8:0x001f, B:10:0x0029, B:11:0x002c, B:14:0x0039, B:478:0x0c11, B:482:0x0c21, B:487:0x0c35, B:20:0x006d, B:25:0x0083, B:39:0x00be, B:35:0x00ad, B:36:0x00b2, B:38:0x00ba, B:40:0x00c1, B:41:0x00cb, B:42:0x00ce, B:43:0x0112, B:46:0x0116, B:49:0x0120, B:51:0x012c, B:53:0x014a, B:56:0x0162, B:60:0x0176, B:62:0x0182, B:66:0x01a0, B:68:0x01ac, B:70:0x01be, B:72:0x01ce, B:75:0x01e0, B:78:0x01f2, B:80:0x01fe, B:83:0x020c, B:101:0x0265, B:103:0x0271, B:87:0x021b, B:89:0x0227, B:92:0x0237, B:93:0x023b, B:95:0x0247, B:107:0x0288, B:109:0x0294, B:111:0x02a6, B:114:0x02be, B:116:0x02ca, B:119:0x02d4, B:121:0x02e0, B:129:0x0302, B:131:0x030e, B:133:0x0320, B:135:0x0330, B:136:0x0334, B:137:0x0337, B:138:0x0389, B:160:0x03ed, B:165:0x03fb, B:168:0x0409, B:171:0x0417, B:172:0x041f, B:175:0x0431, B:177:0x043d, B:180:0x044b, B:205:0x04b4, B:207:0x04c0, B:184:0x0456, B:187:0x0464, B:190:0x0472, B:193:0x047d, B:196:0x048b, B:198:0x0494, B:201:0x04a2, B:204:0x04b0, B:139:0x038a, B:141:0x0396, B:143:0x03a2, B:147:0x03b0, B:148:0x03b6, B:151:0x03c2, B:153:0x03ce, B:156:0x03da, B:158:0x03e6, B:211:0x04d8, B:213:0x04e4, B:215:0x04f6, B:218:0x050e, B:220:0x051a, B:222:0x0526, B:224:0x0530, B:228:0x053a, B:230:0x0546, B:232:0x0558, B:234:0x0568, B:241:0x0598, B:243:0x05a4, B:284:0x065c, B:249:0x05b4, B:252:0x05c6, B:255:0x05cc, B:257:0x05d8, B:260:0x05e6, B:285:0x0660, B:287:0x066c, B:263:0x05f2, B:265:0x05fe, B:268:0x060e, B:270:0x061a, B:271:0x061e, B:275:0x0632, B:276:0x0636, B:279:0x0646, B:291:0x0684, B:293:0x0690, B:295:0x06ae, B:297:0x06cc, B:300:0x06da, B:303:0x06e2, B:305:0x06ee, B:307:0x06f8, B:310:0x0714, B:313:0x0720, B:317:0x0743, B:319:0x0749, B:327:0x0774, B:332:0x078a, B:333:0x0790, B:335:0x0796, B:336:0x0799, B:340:0x07a7, B:343:0x07c5, B:346:0x07d2, B:349:0x07dc, B:355:0x0803, B:358:0x0813, B:363:0x0844, B:365:0x0850, B:367:0x0860, B:368:0x0866, B:371:0x0884, B:373:0x0888, B:376:0x088e, B:380:0x089f, B:381:0x08c2, B:382:0x08c5, B:383:0x0909, B:384:0x090a, B:386:0x091c, B:387:0x0934, B:390:0x095e, B:393:0x098e, B:397:0x09a8, B:398:0x09aa, B:406:0x09ea, B:413:0x0a06, B:414:0x0a0b, B:415:0x0a4f, B:416:0x0a50, B:418:0x0a5c, B:421:0x0a86, B:423:0x0a8a, B:425:0x0a96, B:428:0x0ac0, B:429:0x0ac2, B:431:0x0ace, B:437:0x0b02, B:439:0x0b12, B:441:0x0b1e, B:448:0x0b51, B:450:0x0b5d, B:445:0x0b37, B:447:0x0b43, B:451:0x0b63, B:453:0x0b6f, B:455:0x0b7b, B:457:0x0b87, B:458:0x0b97, B:470:0x0bc7, B:473:0x0bf3, B:472:0x0bdd, B:474:0x0bfb, B:477:0x0c0e), top: B:491:0x001f }] */
        /* JADX WARN: Removed duplicated region for block: B:487:0x0c35 A[Catch: NextAsm -> 0x0113, TRY_LEAVE, TryCatch #0 {NextAsm -> 0x0113, blocks: (B:8:0x001f, B:10:0x0029, B:11:0x002c, B:14:0x0039, B:478:0x0c11, B:482:0x0c21, B:487:0x0c35, B:20:0x006d, B:25:0x0083, B:39:0x00be, B:35:0x00ad, B:36:0x00b2, B:38:0x00ba, B:40:0x00c1, B:41:0x00cb, B:42:0x00ce, B:43:0x0112, B:46:0x0116, B:49:0x0120, B:51:0x012c, B:53:0x014a, B:56:0x0162, B:60:0x0176, B:62:0x0182, B:66:0x01a0, B:68:0x01ac, B:70:0x01be, B:72:0x01ce, B:75:0x01e0, B:78:0x01f2, B:80:0x01fe, B:83:0x020c, B:101:0x0265, B:103:0x0271, B:87:0x021b, B:89:0x0227, B:92:0x0237, B:93:0x023b, B:95:0x0247, B:107:0x0288, B:109:0x0294, B:111:0x02a6, B:114:0x02be, B:116:0x02ca, B:119:0x02d4, B:121:0x02e0, B:129:0x0302, B:131:0x030e, B:133:0x0320, B:135:0x0330, B:136:0x0334, B:137:0x0337, B:138:0x0389, B:160:0x03ed, B:165:0x03fb, B:168:0x0409, B:171:0x0417, B:172:0x041f, B:175:0x0431, B:177:0x043d, B:180:0x044b, B:205:0x04b4, B:207:0x04c0, B:184:0x0456, B:187:0x0464, B:190:0x0472, B:193:0x047d, B:196:0x048b, B:198:0x0494, B:201:0x04a2, B:204:0x04b0, B:139:0x038a, B:141:0x0396, B:143:0x03a2, B:147:0x03b0, B:148:0x03b6, B:151:0x03c2, B:153:0x03ce, B:156:0x03da, B:158:0x03e6, B:211:0x04d8, B:213:0x04e4, B:215:0x04f6, B:218:0x050e, B:220:0x051a, B:222:0x0526, B:224:0x0530, B:228:0x053a, B:230:0x0546, B:232:0x0558, B:234:0x0568, B:241:0x0598, B:243:0x05a4, B:284:0x065c, B:249:0x05b4, B:252:0x05c6, B:255:0x05cc, B:257:0x05d8, B:260:0x05e6, B:285:0x0660, B:287:0x066c, B:263:0x05f2, B:265:0x05fe, B:268:0x060e, B:270:0x061a, B:271:0x061e, B:275:0x0632, B:276:0x0636, B:279:0x0646, B:291:0x0684, B:293:0x0690, B:295:0x06ae, B:297:0x06cc, B:300:0x06da, B:303:0x06e2, B:305:0x06ee, B:307:0x06f8, B:310:0x0714, B:313:0x0720, B:317:0x0743, B:319:0x0749, B:327:0x0774, B:332:0x078a, B:333:0x0790, B:335:0x0796, B:336:0x0799, B:340:0x07a7, B:343:0x07c5, B:346:0x07d2, B:349:0x07dc, B:355:0x0803, B:358:0x0813, B:363:0x0844, B:365:0x0850, B:367:0x0860, B:368:0x0866, B:371:0x0884, B:373:0x0888, B:376:0x088e, B:380:0x089f, B:381:0x08c2, B:382:0x08c5, B:383:0x0909, B:384:0x090a, B:386:0x091c, B:387:0x0934, B:390:0x095e, B:393:0x098e, B:397:0x09a8, B:398:0x09aa, B:406:0x09ea, B:413:0x0a06, B:414:0x0a0b, B:415:0x0a4f, B:416:0x0a50, B:418:0x0a5c, B:421:0x0a86, B:423:0x0a8a, B:425:0x0a96, B:428:0x0ac0, B:429:0x0ac2, B:431:0x0ace, B:437:0x0b02, B:439:0x0b12, B:441:0x0b1e, B:448:0x0b51, B:450:0x0b5d, B:445:0x0b37, B:447:0x0b43, B:451:0x0b63, B:453:0x0b6f, B:455:0x0b7b, B:457:0x0b87, B:458:0x0b97, B:470:0x0bc7, B:473:0x0bf3, B:472:0x0bdd, B:474:0x0bfb, B:477:0x0c0e), top: B:491:0x001f }] */
        /* JADX WARN: Removed duplicated region for block: B:500:0x0029 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:522:0x0048 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:527:0x0048 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:568:0x00ba A[SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        static int getOpcode(android.ext.ArmDis r56, long r57, java.lang.String r59, boolean r60, int r61) {
            /*
                Method dump skipped, instruction units count: 3286
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: android.ext.ArmDis.Neon.getOpcode(android.ext.ArmDis, long, java.lang.String, boolean, int):int");
        }
    }

    static class Arm {
        static final int[] NUM;
        static final String[] STR;

        Arm() {
        }

        static {
            int[] num = {-509607936, -1, -403701520, -1048336, 19922704, 268435440, 144, 266338544, 2097296, 266338544, 16777360, 263196656, 8388752, 262144240, 10485904, 262144240, 52490245, 268435455, -520093584, -1048336, -520093632, -1044496, -517996480, -1044496, -515899328, -1044496, -520093120, -1044496, -517995968, -1044496, -515898816, -1044496, 23068782, 268435455, 20971632, 267387120, 118550544, 267448560, 120647696, 267448560, -200216576, -59707392, -196022272, -42930176, 52490480, 268435440, -176164783, -13, -176164799, -13, -176164784, -16, -176164800, -16, -176164768, -16, 130023455, 266338431, 130023440, 266338416, 6291600, 267387120, 2097328, 254804208, 3145872, 254804208, 3145872, 254804112, 50331648, 267386880, 54525952, 267386880, 117378864, 268374000, 127926352, 262144112, 23068784, 267387120, -176164833, -1, 30412703, 267390975, 28315551, 267390975, 32509855, 267390975, 29364112, 267390960, 27266960, 267390960, 31461264, 267390960, 52490241, 268435455, 52490242, 268435455, 52490243, 268435455, 52490244, 268435455, 52490240, 268435200, -251133952, -449, -251002880, -480, -250871808, -449, -250740736, -480, -251658240, -917984, 109051920, 267390960, 109051920, 267386992, 109051984, 267390960, 109051984, 267386992, 26218399, 267390975, 102764304, 267390960, 102764432, 267390960, 102764336, 267390960, 102764400, 267390960, 102764528, 267390960, 102764368, 267390960, 101715728, 267390960, 101715856, 267390960, 101715760, 267390960, 103812880, 267390960, 103813008, 267390960, 103812912, 267390960, 103812976, 267390960, 103813104, 267390960, 103812944, 267390960, 101715824, 267390960, 101715952, 267390960, 101715792, 267390960, 105910032, 267390960, 105910160, 267390960, 105910064, 267390960, 108007184, 267390960, 108007312, 267390960, 108007216, 267390960, 108007280, 267390960, 108007408, 267390960, 108007248, 267390960, 106958608, 267390960, 106958736, 267390960, 106958640, 267390960, 106958704, 267390960, 106958832, 267390960, 106958672, 267390960, 105910128, 267390960, 105910256, 267390960, 105910096, 267390960, 113184560, 268374000, 113184688, 268374000, 117378992, 268374000, -133166592, -28246017, 113180784, 268374000, 113181808, 268374000, 113182832, 268374000, 113183856, 268374000, 110035056, 268374000, 110036080, 268374000, 110037104, 268374000, 110038128, 268374000, 112132208, 268374000, 112133232, 268374000, 112134256, 268374000, 112135280, 268374000, 117375088, 268374000, 117376112, 268374000, 117377136, 268374000, 117378160, 268374000, 114229360, 268374000, 114230384, 268374000, 114231408, 268374000, 114232432, 268374000, 116326512, 268374000, 116327536, 268374000, 116328560, 268374000, 116329584, 268374000, 112197744, 267390960, 112198768, 267390960, 112199792, 267390960, 112200816, 267390960, 109052016, 267390960, 109053040, 267390960, 109054064, 267390960, 109055088, 267390960, 111149168, 267390960, 111150192, 267390960, 111151216, 267390960, 111152240, 267390960, 116392048, 267390960, 116393072, 267390960, 116394096, 267390960, 116395120, 267390960, 113246320, 267390960, 113247344, 267390960, 113248368, 267390960, 113249392, 267390960, 115343472, 267390960, 115344496, 267390960, 115345520, 267390960, 115346544, 267390960, 109055920, 267390960, -251592704, -1024, 117501968, 267448528, 117502032, 267448528, 117440528, 267387088, 121634832, 267387088, 117440592, 267387088, 121634896, 267387088, 122744848, 267448528, 122683408, 267387088, 122683600, 267387088, -129170176, -27263008, 111149072, 266342384, 111149072, 266338416, 111149136, 266338416, 111152944, 267390960, 25169808, 267390960, 4194448, 267387120, 125890576, 267448560, 125829136, 267387120, 115343376, 266342384, 115343376, 266338416, 115343440, 266338416, 115347248, 267390960, 19922720, 268435440, -517996432, -1048336, -100663296, -33554432, 19922736, 268435440, 24055568, 268374000, 208, 235929840, 240, 235929840, -179245056, -42930176, 16777344, 267387120, 16777376, 267387120, 16777408, 267387120, 16777440, 267387120, 18874496, 267387120, 18874560, 267387120, 20971648, 267387120, 20971680, 267387120, 20971712, 267387120, 20971744, 267387120, 23068800, 267448560, 23068832, 267448560, 23068864, 267448560, 23068896, 267448560, 18874528, 267448560, 18874592, 267448560, 16777296, 267390960, 20971600, 267390960, 18874448, 267390960, 23068752, 267390960, 86835204, 268374015, 71303168, 240123904, AddressItem.FLAG_VALUE_GREATER_OR_EQUAL, 240123904, 104857600, 240127984, 100663296, 240127984, 71303168, 206569488, AddressItem.FLAG_VALUE_GREATER_OR_EQUAL, 206569488, 71303168, 240123904, 104857600, 240123920, 4194480, 240124144, 176, 240127984, 5243024, 240124144, 5243024, 240124048, 1048720, 240127984, 1048720, 240127888, AddressItem.FLAG_MODE_HACKING, 266338304, 0, 266338320, 16, 266338448, 35651584, 266338304, AddressItem.FLAG_MODE_XOR, 266338320, 2097168, 266338448, 37748736, 266338304, AddressItem.FLAG_ORDERED, 266338320, 4194320, 266338448, 39845888, 266338304, 6291456, 266338320, 6291472, 266338448, 41943040, 266338304, AddressItem.FLAG_RANGE, 266338320, 8388624, 266338448, 44040192, 266338304, 10485760, 266338320, 10485776, 266338448, 46137344, 266338304, 12582912, 266338320, 12582928, 266338448, 48234496, 266338304, 14680064, 266338320, 14680080, 266338448, 18936320, 263254528, 18935808, 229699584, AddressItem.FLAG_REVERT, 263195903, 51380224, 267386880, 0, 267386896, 17825808, 267387024, 53477376, 267386880, 19922944, 267386896, 19922960, 267387024, 55574528, 267386880, 22020096, 267386896, 22020112, 267387024, 57671680, 267386880, 24117248, 267386896, 24117264, 267387024, 58720256, 266338304, 25165824, 266338320, 25165840, 266338448, 60817408, 267321344, 27262976, 233770992, 27262976, 233767008, 27263008, 233767008, 27263040, 233767008, 27263072, 233770992, 27263072, 233767008, 62914560, 266338304, 29360128, 266338320, 29360144, 266338448, 65011712, 266338304, 31457280, 266338320, 31457296, 266338448, 100663312, 234881040, 77398020, 268374015, 72351744, 206569472, 70254592, 225443840, 68157440, 206569472, 153944065, 268435455, 153944066, 268435455, 153944068, 268435455, 153944072, 268435455, 153944080, 268435455, 153944096, 268435455, 153944128, 268435455, 153944192, 268435455, 153944320, 268435455, 153944576, 268435455, 153945088, 268435455, 153946112, 268435455, 153948160, 268435455, 153952256, 268435455, 153960448, 268435455, 153976832, 268435455, 153944064, 268369920, 142606336, 267386880, 134217728, 235929600, 146604033, 268435455, 146604034, 268435455, 146604036, 268435455, 146604040, 268435455, 146604048, 268435455, 146604064, 268435455, 146604096, 268435455, 146604160, 268435455, 146604288, 268435455, 146604544, 268435455, 146605056, 268435455, 146606080, 268435455, 146608128, 268435455, 146612224, 268435455, 146620416, 268435455, 146636800, 268435455, 146604032, 268369920, 143654912, 261095424, 135266304, 235929600, 167772160, 234881024, 251658240, 251658240, 0, 0};
            String[] str = {"NOP\t ; (MOV R0, R0)", "UDF#%e", "BX%c\t %0-3r", "MUL%20'S%c\t %16-19R, %0-3R, %8-11R", "MLA%20'S%c\t %16-19R, %0-3R, %8-11R, %12-15R", "SWP%22'B%c\t %12-15RU, %0-3Ru, [%16-19RuU]", "%22?SUMULL%20'S%c\t %12-15Ru, %16-19Ru, %0-3R, %8-11R", "%22?SUMLAL%20'S%c\t %12-15Ru, %16-19Ru, %0-3R, %8-11R", "SEVL", "HLT0x%16-19X%12-15X%8-11X%0-3X", "CRC32B%12-15R, %16-19R, %0-3R", "CRC32H%12-15R, %16-19R, %0-3R", "CRC32W%12-15R, %16-19R, %0-3R", "CRC32CB%12-15R, %16-19R, %0-3R", "CRC32CH%12-15R, %16-19R, %0-3R", "CRC32CW%12-15R, %16-19R, %0-3R", "ERET%c", "HVC%c\t %e", "SDIV%c\t %16-19r, %0-3r, %8-11r", "UDIV%c\t %16-19r, %0-3r, %8-11r", "PLDW\t %a", "PLI\t %P", "DBG%c\t #%0-3d", "DMB\t %U", "DSB\t %U", "DMB\t %U", "DSB\t %U", "ISB\t %U", "BFC%c\t %12-15R, %E", "BFI%c\t %12-15R, %0-3r, %E", "MLS%c\t %16-19R, %0-3R, %8-11R, %12-15R", "STRHT%c\t %12-15R, %S", ArmDis.UNDEFINED_INSTRUCTION, "LDR%6'S%5?HBT%c\t %12-15R, %S", "MOVW%c\t %12-15R, %V", "MOVT%c\t %12-15R, %V", "RBIT%c\t %12-15R, %0-3R", "%22?USBFX%c\t %12-15r, %0-3r, #%7-11d, #%16-20W", "SMC%c\t %e", "CLREX", "LDREXB%c\t %12-15R, [%16-19R]", "LDREXD%c\t %12-15r, [%16-19R]", "LDREXH%c\t %12-15R, [%16-19R]", "STREXB%c\t %12-15R, %0-3R, [%16-19R]", "STREXD%c\t %12-15R, %0-3r, [%16-19R]", "STREXH%c\t %12-15R, %0-3R, [%16-19R]", "YIELD%c", "WFE%c", "WFI%c", "SEV%c", "NOP%c\t {%0-7d}", "CPSIE%8'A%7'I%6'F", "CPSIE%8'A%7'I%6'F,#%0-4d", "CPSID%8'A%7'I%6'F", "CPSID%8'A%7'I%6'F,#%0-4d", "CPS#%0-4d", "PKHBT%c\t %12-15R, %16-19R, %0-3R", "PKHBT%c\t %12-15R, %16-19R, %0-3R, LSL #%7-11d", "PKHTB%c\t %12-15R, %16-19R, %0-3R, ASR #32", "PKHTB%c\t %12-15R, %16-19R, %0-3R, ASR #%7-11d", "LDREX%c\t r%12-15d, [%16-19R]", "QADD16%c\t %12-15R, %16-19R, %0-3R", "QADD8%c\t %12-15R, %16-19R, %0-3R", "QASX%c\t %12-15R, %16-19R, %0-3R", "QSUB16%c\t %12-15R, %16-19R, %0-3R", "QSUB8%c\t %12-15R, %16-19R, %0-3R", "QSAX%c\t %12-15R, %16-19R, %0-3R", "SADD16%c\t %12-15R, %16-19R, %0-3R", "SADD8%c\t %12-15R, %16-19R, %0-3R", "SASX%c\t %12-15R, %16-19R, %0-3R", "SHADD16%c\t %12-15R, %16-19R, %0-3R", "SHADD8%c\t %12-15R, %16-19R, %0-3R", "SHASX%c\t %12-15R, %16-19R, %0-3R", "SHSUB16%c\t %12-15R, %16-19R, %0-3R", "SHSUB8%c\t %12-15R, %16-19R, %0-3R", "SHSAX%c\t %12-15R, %16-19R, %0-3R", "SSUB16%c\t %12-15R, %16-19R, %0-3R", "SSUB8%c\t %12-15R, %16-19R, %0-3R", "SSAX%c\t %12-15R, %16-19R, %0-3R", "UADD16%c\t %12-15R, %16-19R, %0-3R", "UADD8%c\t %12-15R, %16-19R, %0-3R", "UASX%c\t %12-15R, %16-19R, %0-3R", "UHADD16%c\t %12-15R, %16-19R, %0-3R", "UHADD8%c\t %12-15R, %16-19R, %0-3R", "UHASX%c\t %12-15R, %16-19R, %0-3R", "UHSUB16%c\t %12-15R, %16-19R, %0-3R", "UHSUB8%c\t %12-15R, %16-19R, %0-3R", "UHSAX%c\t %12-15R, %16-19R, %0-3R", "UQADD16%c\t %12-15R, %16-19R, %0-3R", "UQADD8%c\t %12-15R, %16-19R, %0-3R", "UQASX%c\t %12-15R, %16-19R, %0-3R", "UQSUB16%c\t %12-15R, %16-19R, %0-3R", "UQSUB8%c\t %12-15R, %16-19R, %0-3R", "UQSAX%c\t %12-15R, %16-19R, %0-3R", "USUB16%c\t %12-15R, %16-19R, %0-3R", "USUB8%c\t %12-15R, %16-19R, %0-3R", "USAX%c\t %12-15R, %16-19R, %0-3R", "REV%c\t %12-15R, %0-3R", "REV16%c\t %12-15R, %0-3R", "REVSH%c\t %12-15R, %0-3R", "RFE%23?ID%24?BA\t %16-19r%21'!", "SXTH%c\t %12-15R, %0-3R", "SXTH%c\t %12-15R, %0-3R, ROR #8", "SXTH%c\t %12-15R, %0-3R, ROR #16", "SXTH%c\t %12-15R, %0-3R, ROR #24", "SXTB16%c\t %12-15R, %0-3R", "SXTB16%c\t %12-15R, %0-3R, ROR #8", "SXTB16%c\t %12-15R, %0-3R, ROR #16", "SXTB16%c\t %12-15R, %0-3R, ROR #24", "SXTB%c\t %12-15R, %0-3R", "SXTB%c\t %12-15R, %0-3R, ROR #8", "SXTB%c\t %12-15R, %0-3R, ROR #16", "SXTB%c\t %12-15R, %0-3R, ROR #24", "UXTH%c\t %12-15R, %0-3R", "UXTH%c\t %12-15R, %0-3R, ROR #8", "UXTH%c\t %12-15R, %0-3R, ROR #16", "UXTH%c\t %12-15R, %0-3R, ROR #24", "UXTB16%c\t %12-15R, %0-3R", "UXTB16%c\t %12-15R, %0-3R, ROR #8", "UXTB16%c\t %12-15R, %0-3R, ROR #16", "UXTB16%c\t %12-15R, %0-3R, ROR #24", "UXTB%c\t %12-15R, %0-3R", "UXTB%c\t %12-15R, %0-3R, ROR #8", "UXTB%c\t %12-15R, %0-3R, ROR #16", "UXTB%c\t %12-15R, %0-3R, ROR #24", "SXTAH%c\t %12-15R, %16-19r, %0-3R", "SXTAH%c\t %12-15R, %16-19r, %0-3R, ROR #8", "SXTAH%c\t %12-15R, %16-19r, %0-3R, ROR #16", "SXTAH%c\t %12-15R, %16-19r, %0-3R, ROR #24", "SXTAB16%c\t %12-15R, %16-19r, %0-3R", "SXTAB16%c\t %12-15R, %16-19r, %0-3R, ROR #8", "SXTAB16%c\t %12-15R, %16-19r, %0-3R, ROR #16", "SXTAB16%c\t %12-15R, %16-19r, %0-3R, ROR #24", "SXTAB%c\t %12-15R, %16-19r, %0-3R", "SXTAB%c\t %12-15R, %16-19r, %0-3R, ROR #8", "SXTAB%c\t %12-15R, %16-19r, %0-3R, ROR #16", "SXTAB%c\t %12-15R, %16-19r, %0-3R, ROR #24", "UXTAH%c\t %12-15R, %16-19r, %0-3R", "UXTAH%c\t %12-15R, %16-19r, %0-3R, ROR #8", "UXTAH%c\t %12-15R, %16-19r, %0-3R, ROR #16", "UXTAH%c\t %12-15R, %16-19r, %0-3R, ROR #24", "UXTAB16%c\t %12-15R, %16-19r, %0-3R", "UXTAB16%c\t %12-15R, %16-19r, %0-3R, ROR #8", "UXTAB16%c\t %12-15R, %16-19r, %0-3R, ROR #16", "UXTAB16%c\t %12-15R, %16-19r, %0-3R, ROR #24", "UXTAB%c\t %12-15R, %16-19r, %0-3R", "UXTAB%c\t %12-15R, %16-19r, %0-3R, ROR #8", "UXTAB%c\t %12-15R, %16-19r, %0-3R, ROR #16", "UXTAB%c\t %12-15R, %16-19r, %0-3R, ROR #24", "SEL%c\t %12-15R, %16-19R, %0-3R", "SETEND%9?BLE", "SMUAD%5'X%c\t %16-19R, %0-3R, %8-11R", "SMUSD%5'X%c\t %16-19R, %0-3R, %8-11R", "SMLAD%5'X%c\t %16-19R, %0-3R, %8-11R, %12-15R", "SMLALD%5'X%c\t %12-15Ru, %16-19Ru, %0-3R, %8-11R", "SMLSD%5'X%c\t %16-19R, %0-3R, %8-11R, %12-15R", "SMLSLD%5'X%c\t %12-15Ru, %16-19Ru, %0-3R, %8-11R", "SMMUL%5'R%c\t %16-19R, %0-3R, %8-11R", "SMMLA%5'R%c\t %16-19R, %0-3R, %8-11R, %12-15R", "SMMLS%5'R%c\t %16-19R, %0-3R, %8-11R, %12-15R", "SRS%23?ID%24?BA\t %16-19r%21'!, #%0-4d", "SSAT%c\t %12-15R, #%16-20W, %0-3R", "SSAT%c\t %12-15R, #%16-20W, %0-3R, LSL #%7-11d", "SSAT%c\t %12-15R, #%16-20W, %0-3R, ASR #%7-11d", "SSAT16%c\t %12-15r, #%16-19W, %0-3r", "STREX%c\t %12-15R, %0-3R, [%16-19R]", "UMAAL%c\t %12-15R, %16-19R, %0-3R, %8-11R", "USAD8%c\t %16-19R, %0-3R, %8-11R", "USADA8%c\t %16-19R, %0-3R, %8-11R, %12-15R", "USAT%c\t %12-15R, #%16-20d, %0-3R", "USAT%c\t %12-15R, #%16-20d, %0-3R, LSL #%7-11d", "USAT%c\t %12-15R, #%16-20d, %0-3R, ASR #%7-11d", "USAT16%c\t %12-15R, #%16-19d, %0-3R", "BXJ%c\t %0-3R", "BKPT\t 0x%16-19X%12-15X%8-11X%0-3X", "BLX\t %B", "BLX%c\t %0-3R", "CLZ%c\t %12-15R, %0-3R", "LDRD%c\t %12-15r, %s", "STRD%c\t %12-15r, %s", "PLD%a", "SMLABB%c\t %16-19R, %0-3R, %8-11R, %12-15R", "SMLATB%c\t %16-19R, %0-3R, %8-11R, %12-15R", "SMLABT%c\t %16-19R, %0-3R, %8-11R, %12-15R", "SMLATT%c\t %16-19r, %0-3r, %8-11R, %12-15R", "SMLAWB%c\t %16-19R, %0-3R, %8-11R, %12-15R", "SMLAWT%c\t %16-19R, %0-3r, %8-11R, %12-15R", "SMLALBB%c\t %12-15Ru, %16-19Ru, %0-3R, %8-11R", "SMLALTB%c\t %12-15Ru, %16-19Ru, %0-3R, %8-11R", "SMLALBT%c\t %12-15Ru, %16-19Ru, %0-3R, %8-11R", "SMLALTT%c\t %12-15Ru, %16-19Ru, %0-3R, %8-11R", "SMULBB%c\t %16-19R, %0-3R, %8-11R", "SMULTB%c\t %16-19R, %0-3R, %8-11R", "SMULBT%c\t %16-19R, %0-3R, %8-11R", "SMULTT%c\t %16-19R, %0-3R, %8-11R", "SMULWB%c\t %16-19R, %0-3R, %8-11R", "SMULWT%c\t %16-19R, %0-3R, %8-11R", "QADD%c\t %12-15R, %0-3R, %16-19R", "QDADD%c\t %12-15R, %0-3R, %16-19R", "QSUB%c\t %12-15R, %0-3R, %16-19R", "QDSUB%c\t %12-15R, %0-3R, %16-19R", "PUSH%c\t {%12-15r}\t \t ; (STR%c %12-15r, %a)", "STRB%t%c\t %12-15R, %a", "STR%t%c\t %12-15r, %a", "STRB%t%c\t %12-15R, %a", "STR%t%c\t %12-15r, %a", "STRB%t%c\t %12-15R, %a", "STR%t%c\t %12-15r, %a", "STRB%c\t %12-15R, %a", "STRB%c\t %12-15R, %a", "STRH%c\t %12-15R, %s", "STRH%c\t %12-15R, %s", ArmDis.UNDEFINED_INSTRUCTION, "LDR%6'S%5?HB%c\t %12-15R, %s", ArmDis.UNDEFINED_INSTRUCTION, "LDR%6'S%5?HB%c\t %12-15R, %s", "AND%20'S%c\t %12-15r, %16-19r, %o", "AND%20'S%c\t %12-15r, %16-19r, %o", "AND%20'S%c\t %12-15R, %16-19R, %o", "EOR%20'S%c\t %12-15r, %16-19r, %o", "EOR%20'S%c\t %12-15r, %16-19r, %o", "EOR%20'S%c\t %12-15R, %16-19R, %o", "SUB%20'S%c\t %12-15r, %16-19r, %o", "SUB%20'S%c\t %12-15r, %16-19r, %o", "SUB%20'S%c\t %12-15R, %16-19R, %o", "RSB%20'S%c\t %12-15r, %16-19r, %o", "RSB%20'S%c\t %12-15r, %16-19r, %o", "RSB%20'S%c\t %12-15R, %16-19R, %o", "ADD%20'S%c\t %12-15r, %16-19r, %o", "ADD%20'S%c\t %12-15r, %16-19r, %o", "ADD%20'S%c\t %12-15R, %16-19R, %o", "ADC%20'S%c\t %12-15r, %16-19r, %o", "ADC%20'S%c\t %12-15r, %16-19r, %o", "ADC%20'S%c\t %12-15R, %16-19R, %o", "SBC%20'S%c\t %12-15r, %16-19r, %o", "SBC%20'S%c\t %12-15r, %16-19r, %o", "SBC%20'S%c\t %12-15R, %16-19R, %o", "RSC%20'S%c\t %12-15r, %16-19r, %o", "RSC%20'S%c\t %12-15r, %16-19r, %o", "RSC%20'S%c\t %12-15R, %16-19R, %o", "MSR%c\t %C, %0-3r", "MSR%c\t %C, %o", "MRS%c\t %12-15R, %R", "TST%p%c\t %16-19r, %o", "TST%p%c\t %16-19r, %o", "TST%p%c\t %16-19R, %o", "TEQ%p%c\t %16-19r, %o", "TEQ%p%c\t %16-19r, %o", "TEQ%p%c\t %16-19R, %o", "CMP%p%c\t %16-19r, %o", "CMP%p%c\t %16-19r, %o", "CMP%p%c\t %16-19R, %o", "CMN%p%c\t %16-19r, %o", "CMN%p%c\t %16-19r, %o", "CMN%p%c\t %16-19R, %o", "ORR%20'S%c\t %12-15r, %16-19r, %o", "ORR%20'S%c\t %12-15r, %16-19r, %o", "ORR%20'S%c\t %12-15R, %16-19R, %o", "MOV%20'S%c\t %12-15r, %o", "MOV%20'S%c\t %12-15r, %0-3r", "LSL%20'S%c\t %12-15R, %q", "LSR%20'S%c\t %12-15R, %q", "ASR%20'S%c\t %12-15R, %q", "RRX%20'S%c\t %12-15r, %0-3r", "ROR%20'S%c\t %12-15R, %q", "BIC%20'S%c\t %12-15r, %16-19r, %o", "BIC%20'S%c\t %12-15r, %16-19r, %o", "BIC%20'S%c\t %12-15R, %16-19R, %o", "MVN%20'S%c\t %12-15r, %o", "MVN%20'S%c\t %12-15r, %o", "MVN%20'S%c\t %12-15R, %o", ArmDis.UNDEFINED_INSTRUCTION, "POP%c\t {%12-15r}\t \t ; (LDR%c %12-15r, %a)", "LDRB%t%c\t %12-15R, %a", "LDRT%c\t %12-15R, %a", "LDR%c\t %12-15r, %a", "STMFD%c\t %16-19R!, %m", "STMFD%c\t %16-19R!, %m", "STMFD%c\t %16-19R!, %m", "STMFD%c\t %16-19R!, %m", "STMFD%c\t %16-19R!, %m", "STMFD%c\t %16-19R!, %m", "STMFD%c\t %16-19R!, %m", "STMFD%c\t %16-19R!, %m", "STMFD%c\t %16-19R!, %m", "STMFD%c\t %16-19R!, %m", "STMFD%c\t %16-19R!, %m", "STMFD%c\t %16-19R!, %m", "STMFD%c\t %16-19R!, %m", "STMFD%c\t %16-19R!, %m", "STMFD%c\t %16-19R!, %m", "STMFD%c\t %16-19R!, %m", "PUSH%c\t %m", "STM%c\t %16-19R%21'!, %m%22'^", "STM%23?ID%24?BA%c\t %16-19R%21'!, %m%22'^", "LDMFD%c\t %16-19R!, %m", "LDMFD%c\t %16-19R!, %m", "LDMFD%c\t %16-19R!, %m", "LDMFD%c\t %16-19R!, %m", "LDMFD%c\t %16-19R!, %m", "LDMFD%c\t %16-19R!, %m", "LDMFD%c\t %16-19R!, %m", "LDMFD%c\t %16-19R!, %m", "LDMFD%c\t %16-19R!, %m", "LDMFD%c\t %16-19R!, %m", "LDMFD%c\t %16-19R!, %m", "LDMFD%c\t %16-19R!, %m", "LDMFD%c\t %16-19R!, %m", "LDMFD%c\t %16-19R!, %m", "LDMFD%c\t %16-19R!, %m", "LDMFD%c\t %16-19R!, %m", "POP%c\t %m", "LDM%c\t %16-19R%21'!, %m%22'^", "LDM%23?ID%24?BA%c\t %16-19R%21'!, %m%22'^", "B%24'L%c\t %b", "SVC%c\t %0-23x", ArmDis.UNDEFINED_INSTRUCTION};
            NUM = num;
            STR = str;
        }

        static long printArmAddress(StringBuilder out, long addr, int data) {
            long offset;
            boolean WRITEBACK_BIT_SET = (2097152 & data) != 0;
            boolean NEGATIVE_BIT_SET = (8388608 & data) == 0;
            boolean PRE_BIT_SET = (16777216 & data) != 0;
            long offset2 = 0;
            if ((983040 & data) == 983040 && (data & AddressItem.FLAG_MODE_HACKING) == 0) {
                long offset3 = data & 4095;
                out.append("[PC");
                if (PRE_BIT_SET) {
                    if (WRITEBACK_BIT_SET || NEGATIVE_BIT_SET || offset3 != 0) {
                        out.append(",#");
                        if (NEGATIVE_BIT_SET) {
                            out.append('-');
                        }
                        out.append(offset3);
                    }
                    if (NEGATIVE_BIT_SET) {
                        offset3 = -offset3;
                    }
                    offset = offset3 + 8 + addr;
                    out.append(']');
                    if (WRITEBACK_BIT_SET) {
                        out.append('!');
                    }
                } else {
                    out.append("], #");
                    if (NEGATIVE_BIT_SET) {
                        out.append('-');
                    }
                    out.append(offset3);
                    offset = addr + 8;
                }
                out.append("\t ; ");
                ArmDis.printAddr(out, addr, offset - addr);
                return 0L;
            }
            out.append('[').append(ArmDis.REG[(data >> 16) & 15]);
            if (PRE_BIT_SET) {
                if ((data & AddressItem.FLAG_MODE_HACKING) == 0) {
                    offset2 = data & 4095;
                    if (WRITEBACK_BIT_SET || NEGATIVE_BIT_SET || offset2 != 0) {
                        out.append(",#");
                        if (NEGATIVE_BIT_SET) {
                            out.append('-');
                        }
                        out.append(offset2);
                    }
                } else {
                    out.append(',');
                    if (NEGATIVE_BIT_SET) {
                        out.append('-');
                    }
                    ArmDis.decodeShift(out, data, true);
                }
                out.append(']');
                if (WRITEBACK_BIT_SET) {
                    out.append('!');
                    return offset2;
                }
                return offset2;
            }
            if ((data & AddressItem.FLAG_MODE_HACKING) == 0) {
                long offset4 = data & 4095;
                out.append("], #");
                if (NEGATIVE_BIT_SET) {
                    out.append('-');
                }
                out.append(offset4);
                return offset4;
            }
            out.append("], ");
            if (NEGATIVE_BIT_SET) {
                out.append('-');
            }
            ArmDis.decodeShift(out, data, true);
            return 0L;
        }

        static String getOpcode(ArmDis ret, long addr, int data) {
            String coproc = Coproc.getOpcode(ret, addr, data, false);
            if (coproc == null) {
                String neon = Neon.getOpcode(ret, addr, data, false);
                if (neon != null) {
                    return neon;
                }
                int si = STR.length;
                for (int i = 0; i < si; i++) {
                    int mask = NUM[(i * 2) + 1];
                    int value = NUM[i * 2];
                    if ((data & mask) == value && (((-268435456) & data) != -268435456 || ((-268435456) & mask) == -268435456 || (mask == 0 && value == 0))) {
                        boolean WRITEBACK_BIT_SET = (2097152 & data) != 0;
                        boolean IMMEDIATE_BIT_SET = (4194304 & data) != 0;
                        boolean NEGATIVE_BIT_SET = (8388608 & data) == 0;
                        boolean PRE_BIT_SET = (16777216 & data) != 0;
                        StringBuilder out = new StringBuilder();
                        int uReg = 16;
                        int UReg = 16;
                        boolean isUnpredictable = false;
                        int commentValue = 0;
                        long commentValueLong = 0;
                        String asm = STR[i];
                        int j = 0;
                        int sj = asm.length();
                        while (j < sj) {
                            char c = ArmDis.charAt(asm, sj, j);
                            if (c != '%') {
                                ArmDis.append(out, c);
                            } else {
                                boolean allowUnpredictable = false;
                                j++;
                                char c2 = ArmDis.charAt(asm, sj, j);
                                switch (c2) {
                                    case '%':
                                        out.append('%');
                                        continue;
                                    case LasmConstants.SHL /* 48 */:
                                    case LasmConstants.SHR /* 49 */:
                                    case '2':
                                    case LasmConstants.PROTO /* 51 */:
                                    case LasmConstants.GOTO /* 52 */:
                                    case LasmConstants.OP /* 53 */:
                                    case LasmConstants.SET_TOP /* 54 */:
                                    case LasmConstants.SKIP_NEXT /* 55 */:
                                    case LasmConstants.NIL /* 56 */:
                                    case LasmConstants.TRUE /* 57 */:
                                        j = ArmDis.decodeBitfield(asm, sj, j, data, ret);
                                        int value2 = ret.valuep;
                                        int width = ret.widthp;
                                        char c3 = ArmDis.charAt(asm, sj, j);
                                        switch (c3) {
                                            case '\'':
                                                j++;
                                                char c4 = ArmDis.charAt(asm, sj, j);
                                                if (value2 == (1 << width) - 1) {
                                                    ArmDis.append(out, c4);
                                                } else {
                                                    continue;
                                                }
                                                break;
                                            case '?':
                                                ArmDis.append(out, ArmDis.charAt(asm, sj, ((1 << width) + j) - value2));
                                                j += 1 << width;
                                                ArmDis.charAt(asm, sj, j);
                                                continue;
                                            case 'R':
                                                if (value2 == 15) {
                                                    isUnpredictable = true;
                                                }
                                                break;
                                            case LasmConstants.FLOAT /* 84 */:
                                            case 'r':
                                                break;
                                            case LasmConstants.HEXDIGIT /* 87 */:
                                                out.append(value2 + 1);
                                                commentValue = value2 + 1;
                                                continue;
                                            case 'X':
                                                out.append(Integer.toHexString(value2 & 15).toUpperCase(Locale.US));
                                                commentValue = value2;
                                                continue;
                                            case '`':
                                                j++;
                                                char c5 = ArmDis.charAt(asm, sj, j);
                                                if (value2 == 0) {
                                                    ArmDis.append(out, c5);
                                                } else {
                                                    continue;
                                                }
                                                break;
                                            case 'b':
                                                out.append(value2 * 8);
                                                commentValue = value2 * 8;
                                                continue;
                                            case Message.ERROR_FATAL /* 100 */:
                                                out.append(value2);
                                                commentValue = value2;
                                                continue;
                                            case 'x':
                                                out.append("0x");
                                                ToolsLight.prefixIntegerHex(out, 8, value2);
                                                if ((268435455 & data) == 267386880) {
                                                    out.append("\t ; IMB");
                                                    continue;
                                                } else if ((268435455 & data) == 267386881) {
                                                    out.append("\t ; IMBRange");
                                                }
                                                break;
                                            default:
                                                throw new RuntimeException("70 Invalid format char '" + c3 + "'; " + i + ListManager.TEXT_SEPARATOR + j + "; '" + asm + "'");
                                        }
                                        if (c3 == 'T') {
                                            value2++;
                                        }
                                        if (ArmDis.charAt(asm, sj, j + 1) == 'u') {
                                            j++;
                                            ArmDis.charAt(asm, sj, j);
                                            if (uReg == value2) {
                                                isUnpredictable = true;
                                            }
                                            uReg = value2;
                                        }
                                        if (ArmDis.charAt(asm, sj, j + 1) == 'U') {
                                            j++;
                                            ArmDis.charAt(asm, sj, j);
                                            if (UReg == value2) {
                                                isUnpredictable = true;
                                            }
                                            UReg = value2;
                                        }
                                        out.append(ArmDis.REG[value2]);
                                        continue;
                                    case LasmConstants.IS_VARARG /* 66 */:
                                        int offset = 0;
                                        if (!NEGATIVE_BIT_SET) {
                                            offset = -16777216;
                                        }
                                        long address = ((long) ((offset + (16777215 & data)) << 2)) + addr + 8;
                                        if ((16777216 & data) != 0) {
                                            address += 2;
                                        }
                                        ArmDis.printAddr(out, addr, address - addr);
                                        continue;
                                    case LasmConstants.MAXSTACKSIZE /* 67 */:
                                        if ((33554944 & data) == 512) {
                                            int sysm = (5177344 & data) >> 16;
                                            ArmDis.bankedRegname(out, sysm | ((data & 768) >> 4));
                                            continue;
                                        } else {
                                            out.append((4194304 & data) != 0 ? 'S' : 'C').append("PSR_");
                                            if ((524288 & data) != 0) {
                                                out.append('f');
                                            }
                                            if ((262144 & data) != 0) {
                                                out.append('s');
                                            }
                                            if ((131072 & data) != 0) {
                                                out.append(SearchMenuItem.XOR_MODE_SMALL);
                                            }
                                            if ((65536 & data) != 0) {
                                                out.append('c');
                                            }
                                        }
                                        break;
                                    case LasmConstants.LINE /* 69 */:
                                        int msb = (2031616 & data) >> 16;
                                        int lsb = (data & 3968) >> 7;
                                        int w = (msb - lsb) + 1;
                                        if (w > 0) {
                                            out.append('#').append(lsb).append(", #").append(w);
                                            continue;
                                        } else {
                                            out.append("<invalid: ").append(lsb).append(':').append(msb).append('>');
                                        }
                                        break;
                                    case LasmConstants.HEX /* 80 */:
                                        commentValueLong = printArmAddress(out, addr, 16777216 | data);
                                        continue;
                                    case 'R':
                                        int sysm2 = (5177344 & data) >> 16;
                                        ArmDis.bankedRegname(out, sysm2 | ((data & 768) >> 4));
                                        continue;
                                    case LasmConstants.NL /* 83 */:
                                        allowUnpredictable = true;
                                        break;
                                    case LasmConstants.DIGIT /* 85 */:
                                        if ((data & 240) == 96) {
                                            switch (data & 15) {
                                                case 15:
                                                    out.append("sy");
                                                    break;
                                                default:
                                                    out.append('#').append(data & 15);
                                                    continue;
                                            }
                                        } else {
                                            ArmDis.dataBarrierOption(out, data & 15);
                                            break;
                                        }
                                        break;
                                    case LasmConstants.EXP /* 86 */:
                                        int hi = (983040 & data) >> 4;
                                        int lo = data & 4095;
                                        int imm16 = hi | lo;
                                        out.append('#').append(imm16);
                                        commentValue = imm16;
                                        continue;
                                    case 'a':
                                        commentValueLong = printArmAddress(out, addr, data);
                                        continue;
                                    case 'b':
                                        int disp = ((16777215 & data) ^ AddressItem.FLAG_RANGE) - AddressItem.FLAG_RANGE;
                                        ArmDis.printAddr(out, addr, (disp * 4) + 8);
                                        continue;
                                    case 'c':
                                        if (((data >> 28) & 15) != 14) {
                                            out.append(ArmDis.COND[(data >> 28) & 15]);
                                        } else {
                                            continue;
                                        }
                                        break;
                                    case 'e':
                                        int imm = (data & 15) | ((1048320 & data) >> 4);
                                        out.append(imm);
                                        commentValue = imm;
                                        continue;
                                    case 'm':
                                        boolean started = false;
                                        out.append('{');
                                        for (int reg = 0; reg < 16; reg++) {
                                            if (((1 << reg) & data) != 0) {
                                                if (reg >= 2 && reg <= 12 && ((1 << (reg - 2)) & data) != 0 && ((1 << (reg - 1)) & data) != 0) {
                                                    out.setLength((out.length() - 1) - ArmDis.REG[reg - 1].length());
                                                    out.append('-');
                                                } else if (started) {
                                                    out.append(',');
                                                }
                                                started = true;
                                                out.append(ArmDis.REG[reg]);
                                            }
                                        }
                                        out.append('}');
                                        if (started) {
                                            continue;
                                        } else {
                                            isUnpredictable = true;
                                        }
                                        break;
                                    case 'o':
                                        if ((33554432 & data) != 0) {
                                            int rotate = (data & 3840) >> 7;
                                            int immed = data & 255;
                                            int a = ((immed << (32 - rotate)) | (immed >> rotate)) & (-1);
                                            int ix = 0;
                                            while (ix < 32 && ((a << ix) | (a >> (32 - ix))) > 255) {
                                                ix += 2;
                                            }
                                            if (ix != rotate) {
                                                out.append('#').append(immed).append(", ").append(rotate);
                                            } else {
                                                out.append('#').append(a);
                                            }
                                            commentValue = a;
                                            continue;
                                        } else {
                                            ArmDis.decodeShift(out, data, true);
                                        }
                                        break;
                                    case 'p':
                                        if ((61440 & data) == 61440) {
                                            out.append('P');
                                        } else {
                                            continue;
                                        }
                                        break;
                                    case 'q':
                                        ArmDis.decodeShift(out, data, false);
                                        continue;
                                    case 's':
                                        break;
                                    case 't':
                                        if ((18874368 & data) == 2097152) {
                                            out.append('T');
                                        } else {
                                            continue;
                                        }
                                        break;
                                    default:
                                        throw new RuntimeException("80 Invalid format char '" + c2 + "'; " + i + ListManager.TEXT_SEPARATOR + j + "; '" + asm + "'");
                                }
                                if ((5177344 & data) == 5177344) {
                                    int offset2 = ((data & 3840) >> 4) | (data & 15);
                                    if (PRE_BIT_SET) {
                                        if (offset2 != 0 || NEGATIVE_BIT_SET) {
                                            out.append("[PC,#");
                                            if (NEGATIVE_BIT_SET) {
                                                out.append('-');
                                            }
                                            out.append(offset2).append("]\t ; ");
                                        } else {
                                            out.append("[PC]\t ; ");
                                        }
                                        if (NEGATIVE_BIT_SET) {
                                            offset2 = -offset2;
                                        }
                                        ArmDis.printAddr(out, addr, offset2 + 8);
                                    } else {
                                        out.append("[PC], #");
                                        if (NEGATIVE_BIT_SET) {
                                            out.append('-');
                                        }
                                        out.append(offset2);
                                        if (!allowUnpredictable) {
                                            isUnpredictable = true;
                                        }
                                    }
                                } else {
                                    int offset3 = ((data & 3840) >> 4) | (data & 15);
                                    out.append('[').append(ArmDis.REG[(data >> 16) & 15]);
                                    if (PRE_BIT_SET) {
                                        if (IMMEDIATE_BIT_SET) {
                                            if (WRITEBACK_BIT_SET || NEGATIVE_BIT_SET || offset3 != 0) {
                                                out.append(",#");
                                                if (NEGATIVE_BIT_SET) {
                                                    out.append('-');
                                                }
                                                out.append(offset3);
                                            }
                                            if (NEGATIVE_BIT_SET) {
                                                offset3 = -offset3;
                                            }
                                            commentValue = offset3;
                                        } else {
                                            out.append(",");
                                            if (NEGATIVE_BIT_SET) {
                                                out.append('-');
                                            }
                                            out.append(ArmDis.REG[data & 15]);
                                            if (!allowUnpredictable && WRITEBACK_BIT_SET && (data & 15) == ((data >> 12) & 15)) {
                                                isUnpredictable = true;
                                            }
                                        }
                                        out.append(']');
                                        if (WRITEBACK_BIT_SET) {
                                            out.append('!');
                                        }
                                    } else {
                                        if (IMMEDIATE_BIT_SET) {
                                            out.append("], #");
                                            if (NEGATIVE_BIT_SET) {
                                                out.append('-');
                                            }
                                            out.append(offset3);
                                            if (NEGATIVE_BIT_SET) {
                                                offset3 = -offset3;
                                            }
                                            commentValue = offset3;
                                        } else {
                                            out.append("], ");
                                            if (NEGATIVE_BIT_SET) {
                                                out.append('-');
                                            }
                                            out.append(ArmDis.REG[data & 15]);
                                            if (!allowUnpredictable && (data & 15) == ((data >> 12) & 15)) {
                                                isUnpredictable = true;
                                            }
                                        }
                                        if (!allowUnpredictable && (WRITEBACK_BIT_SET || (!IMMEDIATE_BIT_SET && (data & 15) == 15))) {
                                            isUnpredictable = true;
                                        }
                                    }
                                }
                            }
                            j++;
                        }
                        if (commentValue > 32 || commentValue < -16) {
                            out.append("\t ; 0x").append(Integer.toHexString(commentValue).toUpperCase(Locale.US));
                        } else if (commentValueLong > 32 || commentValueLong < -16) {
                            out.append("\t ; 0x").append(Long.toHexString(commentValueLong).toUpperCase(Locale.US));
                        }
                        if (isUnpredictable) {
                            out.append(ArmDis.UNPREDICTABLE_INSTRUCTION);
                        }
                        return out.toString();
                    }
                }
                throw new RuntimeException("170 No ARM match for " + Integer.toHexString(data).toUpperCase(Locale.US) + ListManager.TEXT_SEPARATOR + Integer.toHexString(Integer.reverseBytes(data)).toUpperCase(Locale.US));
            }
            return coproc;
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Removed duplicated region for block: B:199:0x0417 A[Catch: NextAsm -> 0x0128, TryCatch #0 {NextAsm -> 0x0128, blocks: (B:12:0x004d, B:14:0x0057, B:15:0x005a, B:18:0x0068, B:491:0x0ac0, B:495:0x0ad0, B:500:0x0ae0, B:24:0x008d, B:29:0x00a3, B:43:0x00d9, B:39:0x00c7, B:40:0x00cc, B:42:0x00d6, B:44:0x00dc, B:45:0x00e6, B:46:0x00e9, B:47:0x0127, B:50:0x012b, B:53:0x0137, B:55:0x0141, B:57:0x014e, B:58:0x0152, B:60:0x015a, B:62:0x0160, B:66:0x0170, B:67:0x0174, B:69:0x017e, B:70:0x0182, B:72:0x0192, B:74:0x019c, B:75:0x01a0, B:78:0x01aa, B:80:0x01b4, B:82:0x01be, B:84:0x01c8, B:86:0x01d2, B:87:0x01de, B:89:0x01e8, B:90:0x01ec, B:95:0x0202, B:96:0x0206, B:97:0x020c, B:100:0x0218, B:104:0x0228, B:105:0x022c, B:107:0x0232, B:109:0x023c, B:112:0x0248, B:114:0x0252, B:115:0x0256, B:118:0x0264, B:120:0x026e, B:122:0x0278, B:123:0x027e, B:125:0x0288, B:126:0x028c, B:127:0x0296, B:129:0x02a0, B:131:0x02aa, B:133:0x02b0, B:135:0x02ba, B:138:0x02c6, B:140:0x02d0, B:141:0x02d4, B:144:0x02e4, B:146:0x02ee, B:147:0x02f2, B:148:0x02fd, B:152:0x0309, B:153:0x030d, B:154:0x0313, B:156:0x031d, B:158:0x032a, B:162:0x033c, B:164:0x0346, B:166:0x0354, B:167:0x0358, B:169:0x0371, B:172:0x037b, B:174:0x0385, B:176:0x038f, B:178:0x0399, B:179:0x039d, B:182:0x03b6, B:183:0x03c0, B:185:0x03ca, B:187:0x03d8, B:189:0x03de, B:191:0x03e4, B:193:0x03f2, B:194:0x03f6, B:197:0x040d, B:199:0x0417, B:201:0x0421, B:202:0x0427, B:204:0x0431, B:205:0x0435, B:207:0x0442, B:210:0x044d, B:212:0x0457, B:214:0x0461, B:216:0x046b, B:218:0x0471, B:220:0x0477, B:222:0x0485, B:223:0x0489, B:226:0x04a2, B:228:0x04ac, B:229:0x04b0, B:231:0x04bd, B:234:0x04c9, B:236:0x04d3, B:238:0x04d9, B:239:0x04e3, B:242:0x04f1, B:244:0x04fb, B:246:0x0501, B:247:0x0507, B:250:0x0527, B:253:0x0534, B:258:0x0549, B:259:0x054f, B:262:0x055b, B:270:0x057b, B:276:0x0597, B:277:0x05a2, B:279:0x05ac, B:281:0x05b2, B:283:0x05b8, B:285:0x05ca, B:287:0x05d2, B:290:0x05da, B:294:0x05ea, B:297:0x05f9, B:298:0x05fc, B:301:0x060f, B:303:0x0619, B:305:0x0623, B:311:0x0648, B:308:0x0637, B:310:0x0641, B:313:0x0652, B:307:0x062d, B:314:0x065e, B:316:0x0668, B:317:0x066e, B:320:0x067c, B:322:0x0686, B:324:0x068c, B:325:0x0692, B:327:0x069b, B:329:0x06a1, B:331:0x06a7, B:332:0x06ae, B:335:0x06bc, B:337:0x06cb, B:340:0x06d9, B:343:0x06e6, B:345:0x06ef, B:347:0x06f8, B:350:0x0704, B:351:0x0711, B:353:0x071b, B:354:0x071f, B:356:0x0729, B:358:0x0733, B:359:0x0737, B:361:0x0741, B:362:0x0745, B:364:0x074f, B:365:0x0753, B:367:0x075d, B:368:0x0763, B:371:0x076f, B:373:0x0777, B:375:0x0781, B:376:0x0785, B:378:0x078f, B:381:0x07a1, B:383:0x07aa, B:386:0x07b6, B:387:0x07d9, B:388:0x07dc, B:389:0x081a, B:390:0x081b, B:394:0x082e, B:395:0x0830, B:397:0x083c, B:398:0x083e, B:400:0x084a, B:401:0x084c, B:402:0x0864, B:405:0x088d, B:408:0x0897, B:411:0x08be, B:414:0x08e9, B:416:0x08f3, B:419:0x091f, B:431:0x0947, B:432:0x0949, B:433:0x0961, B:437:0x096e, B:439:0x097e, B:441:0x0988, B:448:0x09b1, B:450:0x09bd, B:445:0x099d, B:447:0x09a7, B:451:0x09c3, B:453:0x09cd, B:455:0x09d7, B:457:0x09e1, B:458:0x09ed, B:463:0x0a01, B:464:0x0a1e, B:467:0x0a2f, B:468:0x0a32, B:471:0x0a4c, B:473:0x0a56, B:476:0x0a60, B:478:0x0a6e, B:480:0x0a74, B:483:0x0a84, B:485:0x0a8d, B:486:0x0a9a, B:488:0x0aa4), top: B:507:0x004d }] */
        /* JADX WARN: Removed duplicated region for block: B:23:0x007e  */
        /* JADX WARN: Removed duplicated region for block: B:448:0x09b1 A[Catch: NextAsm -> 0x0128, TryCatch #0 {NextAsm -> 0x0128, blocks: (B:12:0x004d, B:14:0x0057, B:15:0x005a, B:18:0x0068, B:491:0x0ac0, B:495:0x0ad0, B:500:0x0ae0, B:24:0x008d, B:29:0x00a3, B:43:0x00d9, B:39:0x00c7, B:40:0x00cc, B:42:0x00d6, B:44:0x00dc, B:45:0x00e6, B:46:0x00e9, B:47:0x0127, B:50:0x012b, B:53:0x0137, B:55:0x0141, B:57:0x014e, B:58:0x0152, B:60:0x015a, B:62:0x0160, B:66:0x0170, B:67:0x0174, B:69:0x017e, B:70:0x0182, B:72:0x0192, B:74:0x019c, B:75:0x01a0, B:78:0x01aa, B:80:0x01b4, B:82:0x01be, B:84:0x01c8, B:86:0x01d2, B:87:0x01de, B:89:0x01e8, B:90:0x01ec, B:95:0x0202, B:96:0x0206, B:97:0x020c, B:100:0x0218, B:104:0x0228, B:105:0x022c, B:107:0x0232, B:109:0x023c, B:112:0x0248, B:114:0x0252, B:115:0x0256, B:118:0x0264, B:120:0x026e, B:122:0x0278, B:123:0x027e, B:125:0x0288, B:126:0x028c, B:127:0x0296, B:129:0x02a0, B:131:0x02aa, B:133:0x02b0, B:135:0x02ba, B:138:0x02c6, B:140:0x02d0, B:141:0x02d4, B:144:0x02e4, B:146:0x02ee, B:147:0x02f2, B:148:0x02fd, B:152:0x0309, B:153:0x030d, B:154:0x0313, B:156:0x031d, B:158:0x032a, B:162:0x033c, B:164:0x0346, B:166:0x0354, B:167:0x0358, B:169:0x0371, B:172:0x037b, B:174:0x0385, B:176:0x038f, B:178:0x0399, B:179:0x039d, B:182:0x03b6, B:183:0x03c0, B:185:0x03ca, B:187:0x03d8, B:189:0x03de, B:191:0x03e4, B:193:0x03f2, B:194:0x03f6, B:197:0x040d, B:199:0x0417, B:201:0x0421, B:202:0x0427, B:204:0x0431, B:205:0x0435, B:207:0x0442, B:210:0x044d, B:212:0x0457, B:214:0x0461, B:216:0x046b, B:218:0x0471, B:220:0x0477, B:222:0x0485, B:223:0x0489, B:226:0x04a2, B:228:0x04ac, B:229:0x04b0, B:231:0x04bd, B:234:0x04c9, B:236:0x04d3, B:238:0x04d9, B:239:0x04e3, B:242:0x04f1, B:244:0x04fb, B:246:0x0501, B:247:0x0507, B:250:0x0527, B:253:0x0534, B:258:0x0549, B:259:0x054f, B:262:0x055b, B:270:0x057b, B:276:0x0597, B:277:0x05a2, B:279:0x05ac, B:281:0x05b2, B:283:0x05b8, B:285:0x05ca, B:287:0x05d2, B:290:0x05da, B:294:0x05ea, B:297:0x05f9, B:298:0x05fc, B:301:0x060f, B:303:0x0619, B:305:0x0623, B:311:0x0648, B:308:0x0637, B:310:0x0641, B:313:0x0652, B:307:0x062d, B:314:0x065e, B:316:0x0668, B:317:0x066e, B:320:0x067c, B:322:0x0686, B:324:0x068c, B:325:0x0692, B:327:0x069b, B:329:0x06a1, B:331:0x06a7, B:332:0x06ae, B:335:0x06bc, B:337:0x06cb, B:340:0x06d9, B:343:0x06e6, B:345:0x06ef, B:347:0x06f8, B:350:0x0704, B:351:0x0711, B:353:0x071b, B:354:0x071f, B:356:0x0729, B:358:0x0733, B:359:0x0737, B:361:0x0741, B:362:0x0745, B:364:0x074f, B:365:0x0753, B:367:0x075d, B:368:0x0763, B:371:0x076f, B:373:0x0777, B:375:0x0781, B:376:0x0785, B:378:0x078f, B:381:0x07a1, B:383:0x07aa, B:386:0x07b6, B:387:0x07d9, B:388:0x07dc, B:389:0x081a, B:390:0x081b, B:394:0x082e, B:395:0x0830, B:397:0x083c, B:398:0x083e, B:400:0x084a, B:401:0x084c, B:402:0x0864, B:405:0x088d, B:408:0x0897, B:411:0x08be, B:414:0x08e9, B:416:0x08f3, B:419:0x091f, B:431:0x0947, B:432:0x0949, B:433:0x0961, B:437:0x096e, B:439:0x097e, B:441:0x0988, B:448:0x09b1, B:450:0x09bd, B:445:0x099d, B:447:0x09a7, B:451:0x09c3, B:453:0x09cd, B:455:0x09d7, B:457:0x09e1, B:458:0x09ed, B:463:0x0a01, B:464:0x0a1e, B:467:0x0a2f, B:468:0x0a32, B:471:0x0a4c, B:473:0x0a56, B:476:0x0a60, B:478:0x0a6e, B:480:0x0a74, B:483:0x0a84, B:485:0x0a8d, B:486:0x0a9a, B:488:0x0aa4), top: B:507:0x004d }] */
        /* JADX WARN: Removed duplicated region for block: B:450:0x09bd A[Catch: NextAsm -> 0x0128, TryCatch #0 {NextAsm -> 0x0128, blocks: (B:12:0x004d, B:14:0x0057, B:15:0x005a, B:18:0x0068, B:491:0x0ac0, B:495:0x0ad0, B:500:0x0ae0, B:24:0x008d, B:29:0x00a3, B:43:0x00d9, B:39:0x00c7, B:40:0x00cc, B:42:0x00d6, B:44:0x00dc, B:45:0x00e6, B:46:0x00e9, B:47:0x0127, B:50:0x012b, B:53:0x0137, B:55:0x0141, B:57:0x014e, B:58:0x0152, B:60:0x015a, B:62:0x0160, B:66:0x0170, B:67:0x0174, B:69:0x017e, B:70:0x0182, B:72:0x0192, B:74:0x019c, B:75:0x01a0, B:78:0x01aa, B:80:0x01b4, B:82:0x01be, B:84:0x01c8, B:86:0x01d2, B:87:0x01de, B:89:0x01e8, B:90:0x01ec, B:95:0x0202, B:96:0x0206, B:97:0x020c, B:100:0x0218, B:104:0x0228, B:105:0x022c, B:107:0x0232, B:109:0x023c, B:112:0x0248, B:114:0x0252, B:115:0x0256, B:118:0x0264, B:120:0x026e, B:122:0x0278, B:123:0x027e, B:125:0x0288, B:126:0x028c, B:127:0x0296, B:129:0x02a0, B:131:0x02aa, B:133:0x02b0, B:135:0x02ba, B:138:0x02c6, B:140:0x02d0, B:141:0x02d4, B:144:0x02e4, B:146:0x02ee, B:147:0x02f2, B:148:0x02fd, B:152:0x0309, B:153:0x030d, B:154:0x0313, B:156:0x031d, B:158:0x032a, B:162:0x033c, B:164:0x0346, B:166:0x0354, B:167:0x0358, B:169:0x0371, B:172:0x037b, B:174:0x0385, B:176:0x038f, B:178:0x0399, B:179:0x039d, B:182:0x03b6, B:183:0x03c0, B:185:0x03ca, B:187:0x03d8, B:189:0x03de, B:191:0x03e4, B:193:0x03f2, B:194:0x03f6, B:197:0x040d, B:199:0x0417, B:201:0x0421, B:202:0x0427, B:204:0x0431, B:205:0x0435, B:207:0x0442, B:210:0x044d, B:212:0x0457, B:214:0x0461, B:216:0x046b, B:218:0x0471, B:220:0x0477, B:222:0x0485, B:223:0x0489, B:226:0x04a2, B:228:0x04ac, B:229:0x04b0, B:231:0x04bd, B:234:0x04c9, B:236:0x04d3, B:238:0x04d9, B:239:0x04e3, B:242:0x04f1, B:244:0x04fb, B:246:0x0501, B:247:0x0507, B:250:0x0527, B:253:0x0534, B:258:0x0549, B:259:0x054f, B:262:0x055b, B:270:0x057b, B:276:0x0597, B:277:0x05a2, B:279:0x05ac, B:281:0x05b2, B:283:0x05b8, B:285:0x05ca, B:287:0x05d2, B:290:0x05da, B:294:0x05ea, B:297:0x05f9, B:298:0x05fc, B:301:0x060f, B:303:0x0619, B:305:0x0623, B:311:0x0648, B:308:0x0637, B:310:0x0641, B:313:0x0652, B:307:0x062d, B:314:0x065e, B:316:0x0668, B:317:0x066e, B:320:0x067c, B:322:0x0686, B:324:0x068c, B:325:0x0692, B:327:0x069b, B:329:0x06a1, B:331:0x06a7, B:332:0x06ae, B:335:0x06bc, B:337:0x06cb, B:340:0x06d9, B:343:0x06e6, B:345:0x06ef, B:347:0x06f8, B:350:0x0704, B:351:0x0711, B:353:0x071b, B:354:0x071f, B:356:0x0729, B:358:0x0733, B:359:0x0737, B:361:0x0741, B:362:0x0745, B:364:0x074f, B:365:0x0753, B:367:0x075d, B:368:0x0763, B:371:0x076f, B:373:0x0777, B:375:0x0781, B:376:0x0785, B:378:0x078f, B:381:0x07a1, B:383:0x07aa, B:386:0x07b6, B:387:0x07d9, B:388:0x07dc, B:389:0x081a, B:390:0x081b, B:394:0x082e, B:395:0x0830, B:397:0x083c, B:398:0x083e, B:400:0x084a, B:401:0x084c, B:402:0x0864, B:405:0x088d, B:408:0x0897, B:411:0x08be, B:414:0x08e9, B:416:0x08f3, B:419:0x091f, B:431:0x0947, B:432:0x0949, B:433:0x0961, B:437:0x096e, B:439:0x097e, B:441:0x0988, B:448:0x09b1, B:450:0x09bd, B:445:0x099d, B:447:0x09a7, B:451:0x09c3, B:453:0x09cd, B:455:0x09d7, B:457:0x09e1, B:458:0x09ed, B:463:0x0a01, B:464:0x0a1e, B:467:0x0a2f, B:468:0x0a32, B:471:0x0a4c, B:473:0x0a56, B:476:0x0a60, B:478:0x0a6e, B:480:0x0a74, B:483:0x0a84, B:485:0x0a8d, B:486:0x0a9a, B:488:0x0aa4), top: B:507:0x004d }] */
        /* JADX WARN: Removed duplicated region for block: B:500:0x0ae0 A[Catch: NextAsm -> 0x0128, TRY_LEAVE, TryCatch #0 {NextAsm -> 0x0128, blocks: (B:12:0x004d, B:14:0x0057, B:15:0x005a, B:18:0x0068, B:491:0x0ac0, B:495:0x0ad0, B:500:0x0ae0, B:24:0x008d, B:29:0x00a3, B:43:0x00d9, B:39:0x00c7, B:40:0x00cc, B:42:0x00d6, B:44:0x00dc, B:45:0x00e6, B:46:0x00e9, B:47:0x0127, B:50:0x012b, B:53:0x0137, B:55:0x0141, B:57:0x014e, B:58:0x0152, B:60:0x015a, B:62:0x0160, B:66:0x0170, B:67:0x0174, B:69:0x017e, B:70:0x0182, B:72:0x0192, B:74:0x019c, B:75:0x01a0, B:78:0x01aa, B:80:0x01b4, B:82:0x01be, B:84:0x01c8, B:86:0x01d2, B:87:0x01de, B:89:0x01e8, B:90:0x01ec, B:95:0x0202, B:96:0x0206, B:97:0x020c, B:100:0x0218, B:104:0x0228, B:105:0x022c, B:107:0x0232, B:109:0x023c, B:112:0x0248, B:114:0x0252, B:115:0x0256, B:118:0x0264, B:120:0x026e, B:122:0x0278, B:123:0x027e, B:125:0x0288, B:126:0x028c, B:127:0x0296, B:129:0x02a0, B:131:0x02aa, B:133:0x02b0, B:135:0x02ba, B:138:0x02c6, B:140:0x02d0, B:141:0x02d4, B:144:0x02e4, B:146:0x02ee, B:147:0x02f2, B:148:0x02fd, B:152:0x0309, B:153:0x030d, B:154:0x0313, B:156:0x031d, B:158:0x032a, B:162:0x033c, B:164:0x0346, B:166:0x0354, B:167:0x0358, B:169:0x0371, B:172:0x037b, B:174:0x0385, B:176:0x038f, B:178:0x0399, B:179:0x039d, B:182:0x03b6, B:183:0x03c0, B:185:0x03ca, B:187:0x03d8, B:189:0x03de, B:191:0x03e4, B:193:0x03f2, B:194:0x03f6, B:197:0x040d, B:199:0x0417, B:201:0x0421, B:202:0x0427, B:204:0x0431, B:205:0x0435, B:207:0x0442, B:210:0x044d, B:212:0x0457, B:214:0x0461, B:216:0x046b, B:218:0x0471, B:220:0x0477, B:222:0x0485, B:223:0x0489, B:226:0x04a2, B:228:0x04ac, B:229:0x04b0, B:231:0x04bd, B:234:0x04c9, B:236:0x04d3, B:238:0x04d9, B:239:0x04e3, B:242:0x04f1, B:244:0x04fb, B:246:0x0501, B:247:0x0507, B:250:0x0527, B:253:0x0534, B:258:0x0549, B:259:0x054f, B:262:0x055b, B:270:0x057b, B:276:0x0597, B:277:0x05a2, B:279:0x05ac, B:281:0x05b2, B:283:0x05b8, B:285:0x05ca, B:287:0x05d2, B:290:0x05da, B:294:0x05ea, B:297:0x05f9, B:298:0x05fc, B:301:0x060f, B:303:0x0619, B:305:0x0623, B:311:0x0648, B:308:0x0637, B:310:0x0641, B:313:0x0652, B:307:0x062d, B:314:0x065e, B:316:0x0668, B:317:0x066e, B:320:0x067c, B:322:0x0686, B:324:0x068c, B:325:0x0692, B:327:0x069b, B:329:0x06a1, B:331:0x06a7, B:332:0x06ae, B:335:0x06bc, B:337:0x06cb, B:340:0x06d9, B:343:0x06e6, B:345:0x06ef, B:347:0x06f8, B:350:0x0704, B:351:0x0711, B:353:0x071b, B:354:0x071f, B:356:0x0729, B:358:0x0733, B:359:0x0737, B:361:0x0741, B:362:0x0745, B:364:0x074f, B:365:0x0753, B:367:0x075d, B:368:0x0763, B:371:0x076f, B:373:0x0777, B:375:0x0781, B:376:0x0785, B:378:0x078f, B:381:0x07a1, B:383:0x07aa, B:386:0x07b6, B:387:0x07d9, B:388:0x07dc, B:389:0x081a, B:390:0x081b, B:394:0x082e, B:395:0x0830, B:397:0x083c, B:398:0x083e, B:400:0x084a, B:401:0x084c, B:402:0x0864, B:405:0x088d, B:408:0x0897, B:411:0x08be, B:414:0x08e9, B:416:0x08f3, B:419:0x091f, B:431:0x0947, B:432:0x0949, B:433:0x0961, B:437:0x096e, B:439:0x097e, B:441:0x0988, B:448:0x09b1, B:450:0x09bd, B:445:0x099d, B:447:0x09a7, B:451:0x09c3, B:453:0x09cd, B:455:0x09d7, B:457:0x09e1, B:458:0x09ed, B:463:0x0a01, B:464:0x0a1e, B:467:0x0a2f, B:468:0x0a32, B:471:0x0a4c, B:473:0x0a56, B:476:0x0a60, B:478:0x0a6e, B:480:0x0a74, B:483:0x0a84, B:485:0x0a8d, B:486:0x0a9a, B:488:0x0aa4), top: B:507:0x004d }] */
        /* JADX WARN: Removed duplicated region for block: B:513:0x0057 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:535:0x0074 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:624:0x00d6 A[SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        static int getOpcode(android.ext.ArmDis r47, long r48, java.lang.String r50, int r51) {
            /*
                Method dump skipped, instruction units count: 2982
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: android.ext.ArmDis.Arm.getOpcode(android.ext.ArmDis, long, java.lang.String, int):int");
        }
    }

    static class Input {
        int best = -1;
        int bestOut = -1;
        final String data;
        final int len;
        int pos;

        Input(String data) {
            String data2 = data.toUpperCase(Locale.US);
            this.data = data2;
            this.len = data2.length();
            reset();
        }

        void reset() {
            String data = this.data;
            int len = this.len;
            int pos = 0;
            while (pos < len && ArmDis.charAt(data, len, pos) <= ' ') {
                pos++;
            }
            this.pos = pos;
        }

        char get() {
            String data = this.data;
            int pos = this.pos;
            int len = this.len;
            char ret = ArmDis.charAt(data, len, pos);
            if (pos < len && ret <= ' ') {
                ret = ' ';
                while (pos < len && ArmDis.charAt(data, len, pos + 1) <= ' ') {
                    pos++;
                }
                this.pos = pos;
            }
            return ret;
        }

        void next() {
            this.pos++;
        }

        char getAndNext() {
            char ret = get();
            this.pos++;
            return ret;
        }

        int index(String[] vars) {
            int j;
            String data = this.data;
            int pos = this.pos;
            int len = this.len;
            int left = len - pos;
            int ret = -1;
            if (left >= 0) {
                for (int i = vars.length - 1; i >= 0; i--) {
                    String var = vars[i];
                    int sj = var.length();
                    if (sj == 0) {
                        ret = i;
                    } else {
                        if (sj <= left) {
                            j = 0;
                            while (j < sj && data.charAt(pos + j) == var.charAt(j)) {
                                j++;
                            }
                            if (j == sj) {
                                this.pos = pos + sj;
                                return i;
                            }
                        }
                        continue;
                    }
                }
            }
            return ret;
        }

        boolean check(char var) {
            if (get() != var) {
                return false;
            }
            this.pos++;
            return true;
        }

        boolean check(String var) {
            String data = this.data;
            int pos = this.pos;
            int len = this.len;
            int left = len - pos;
            if (left < 0) {
                return false;
            }
            int sj = var.length();
            if (sj == 0) {
                return true;
            }
            if (sj > left) {
                return false;
            }
            for (int j = 0; j < sj; j++) {
                if (data.charAt(pos + j) != var.charAt(j)) {
                    return false;
                }
            }
            this.pos = pos + sj;
            return true;
        }

        int getInt() {
            char cur = getAndNext();
            boolean neg = false;
            if (cur == '-') {
                cur = getAndNext();
                neg = true;
            }
            if (cur < '0' || cur > '9') {
                throw ArmDis.nextAsm;
            }
            int imm = 0;
            do {
                imm = (imm * 10) + (cur - '0');
                cur = getAndNext();
                if (cur < '0') {
                    break;
                }
            } while (cur <= '9');
            this.pos--;
            return neg ? -imm : imm;
        }

        long getHex() {
            char cur = getAndNext();
            if ((cur < '0' || cur > '9') && (cur < 'A' || cur > 'F')) {
                throw ArmDis.nextAsm;
            }
            long imm = 0;
            while (true) {
                imm = (imm << 4) + ((long) ((cur < '0' || cur > '9') ? (cur - 'A') + 10 : cur - '0'));
                cur = getAndNext();
                if (cur < '0' || cur > '9') {
                    if (cur < 'A' || cur > 'F') {
                        break;
                    }
                }
            }
            this.pos--;
            return imm;
        }

        float getFloat() {
            String data = this.data;
            int pos = this.pos;
            int len = data.length();
            if (pos >= len) {
                throw ArmDis.nextAsm;
            }
            int end = data.indexOf(59, pos);
            if (end == -1) {
                end = len;
            }
            try {
                float f = Float.parseFloat(data.substring(pos, end));
                this.pos = len;
                return f;
            } catch (NumberFormatException e) {
                throw ArmDis.nextAsm;
            }
        }
    }

    static class Thumb16 {
        static final short[] NUM;
        static final String[] STR;

        Thumb16() {
        }

        static {
            short[] num = {-16560, -1, -17792, -64, -16640, -1, -16624, -1, -16608, -1, -16592, -1, -16576, -1, -16640, -241, -18176, -768, -20224, -768, -16640, -256, -18848, -8, -18832, -8, 17920, -64, -17920, -64, -17856, -64, -17728, -64, -18864, -9, -19968, -64, -19904, -64, -19840, -64, -19776, -64, -16896, -256, 18304, -121, 18112, -1, 16384, -64, 16448, -64, 16512, -64, 16576, -64, 16640, -64, 16704, -64, 16768, -64, 16832, -64, 16896, -64, 16960, -64, 17024, -64, 17088, -64, 17152, -64, 17216, -64, 17280, -64, 17344, -64, -20480, -128, -20352, -128, 18176, -128, 17408, -256, 17664, -256, 17920, -256, -19456, -512, -17408, -512, 6144, -512, 6656, -512, 7168, -512, 7680, -512, 20992, -512, 23040, -512, 22016, -2560, 20480, -1536, 22528, -1536, 0, -64, 0, -2048, 2048, -2048, 4096, -2048, 8192, -2048, 10240, -2048, 12288, -2048, 14336, -2048, 18432, -2048, 24576, -2048, 26624, -2048, 28672, -2048, 30720, -2048, Short.MIN_VALUE, -2048, -30720, -2048, -28672, -2048, -26624, -2048, -24576, -2048, -22528, -2048, -16384, -2048, -14336, -2048, -8448, -256, -8704, -256, -8448, -256, -12288, -4096, -8192, -2048, 0, 0};
            String[] str = {"SEVL%c", "HLT\t %0-5x", "NOP%c", "YIELD%c", "WFE%c", "WFI%c", "SEV%c", "NOP%c\t {%4-7d}", "CBNZ\t %0-2r, %b%X", "CBZ\t %0-2r, %b%X", "IT%I%X", "CPSIE%2'A%1'I%0'F%X", "CPSID%2'A%1'I%0'F%X", "MOV%c\t %0-2r, %3-5r", "REV%c\t %0-2r, %3-5r", "REV16%c\t %0-2r, %3-5r", "REVSH%c\t %0-2r, %3-5r", "SETEND%3?BLE%X", "SXTH%c\t %0-2r, %3-5r", "SXTB%c\t %0-2r, %3-5r", "UXTH%c\t %0-2r, %3-5r", "UXTB%c\t %0-2r, %3-5r", "BKPT\t %0-7x", "BLX%c\t %3-6r%x", "NOP%c\t \t \t ; (MOV R8, R8)", "AND%C\t %0-2r, %3-5r", "EOR%C\t %0-2r, %3-5r", "LSL%C\t %0-2r, %3-5r", "LSR%C\t %0-2r, %3-5r", "ASR%C\t %0-2r, %3-5r", "ADC%C\t %0-2r, %3-5r", "SBC%C\t %0-2r, %3-5r", "ROR%C\t %0-2r, %3-5r", "TST%c\t %0-2r, %3-5r", "NEG%C\t %0-2r, %3-5r", "CMP%c\t %0-2r, %3-5r", "CMN%c\t %0-2r, %3-5r", "ORR%C\t %0-2r, %3-5r", "MUL%C\t %0-2r, %3-5r", "BIC%C\t %0-2r, %3-5r", "MVN%C\t %0-2r, %3-5r", "ADD%c\t SP, #%0-6W", "SUB%c\t SP, #%0-6W", "BX%c\t %S%x", "ADD%c\t %D, %S", "CMP%c\t %D, %S", "MOV%c\t %D, %S", "PUSH%c\t %N", "POP%c\t %O", "ADD%C\t %0-2r, %3-5r, %6-8r", "SUB%C\t %0-2r, %3-5r, %6-8r", "ADD%C\t %0-2r, %3-5r, #%6-8d", "SUB%C\t %0-2r, %3-5r, #%6-8d", "STRH%c\t %0-2r, [%3-5r,%6-8r]", "LDRH%c\t %0-2r, [%3-5r,%6-8r]", "LDRS%11?HB%c\t %0-2r, [%3-5r,%6-8r]", "STR%10'B%c\t %0-2r, [%3-5r,%6-8r]", "LDR%10'B%c\t %0-2r, [%3-5r,%6-8r]", "MOV%C\t %0-2r, %3-5r", "LSL%C\t %0-2r, %3-5r, #%6-10d", "LSR%C\t %0-2r, %3-5r, %s", "ASR%C\t %0-2r, %3-5r, %s", "MOV%C\t %8-10r, #%0-7d", "CMP%c\t %8-10r, #%0-7d", "ADD%C\t %8-10r, #%0-7d", "SUB%C\t %8-10r, #%0-7d", "LDR%c\t %8-10r, [PC,#%0-7W]\t ; (%0-7a)", "STR%c\t %0-2r, [%3-5r,#%6-10W]", "LDR%c\t %0-2r, [%3-5r,#%6-10W]", "STRB%c\t %0-2r, [%3-5r,#%6-10d]", "LDRB%c\t %0-2r, [%3-5r,#%6-10d]", "STRH%c\t %0-2r, [%3-5r,#%6-10H]", "LDRH%c\t %0-2r, [%3-5r,#%6-10H]", "STR%c\t %8-10r, [SP,#%0-7W]", "LDR%c\t %8-10r, [SP,#%0-7W]", "ADD%c\t %8-10r, PC, #%0-7W\t ; (ADR %8-10r, %0-7a)", "ADD%c\t %8-10r, SP, #%0-7W", "STMIA%c\t %8-10r!, %M", "LDMIA%c\t %8-10r%W, %M", "SVC%c\t %0-7d", "UDF%c\t #%0-7d", ArmDis.UNDEFINED_INSTRUCTION, "B%8-11c.N\t %0-7B%X", "B%c.N\t %0-10B%x", ArmDis.UNDEFINED_INSTRUCTION};
            NUM = num;
            STR = str;
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Removed duplicated region for block: B:59:0x01a2  */
        /* JADX WARN: Removed duplicated region for block: B:63:0x01be  */
        /* JADX WARN: Removed duplicated region for block: B:67:0x01e1  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        static java.lang.String getOpcode(long r30, int r32) {
            /*
                Method dump skipped, instruction units count: 1294
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: android.ext.ArmDis.Thumb16.getOpcode(long, int):java.lang.String");
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:163:0x0439  */
        /* JADX WARN: Removed duplicated region for block: B:19:0x0060  */
        /* JADX WARN: Removed duplicated region for block: B:212:0x0547 A[Catch: NextAsm -> 0x0117, TRY_LEAVE, TryCatch #0 {NextAsm -> 0x0117, blocks: (B:8:0x0025, B:10:0x002f, B:11:0x0032, B:14:0x003f, B:203:0x0525, B:207:0x0535, B:212:0x0547, B:20:0x0073, B:25:0x0089, B:39:0x00c4, B:35:0x00b3, B:36:0x00b8, B:38:0x00c0, B:40:0x00c7, B:41:0x00d1, B:42:0x00d4, B:43:0x0116, B:46:0x011a, B:49:0x0124, B:53:0x013a, B:55:0x0146, B:57:0x0156, B:60:0x015c, B:62:0x0164, B:64:0x0168, B:65:0x016a, B:68:0x0170, B:69:0x0174, B:71:0x0180, B:74:0x018a, B:75:0x0191, B:78:0x019f, B:80:0x01b1, B:81:0x01bf, B:83:0x01d1, B:84:0x01dd, B:87:0x01eb, B:97:0x021d, B:106:0x023d, B:112:0x025b, B:114:0x0267, B:115:0x0277, B:118:0x0287, B:120:0x0293, B:122:0x0299, B:123:0x02a2, B:126:0x02c7, B:129:0x02d6, B:131:0x02e2, B:134:0x02ed, B:137:0x02f3, B:138:0x02fb, B:146:0x035a, B:143:0x0314, B:144:0x0317, B:145:0x0359, B:147:0x036b, B:155:0x03b4, B:153:0x0383, B:154:0x03b3, B:156:0x03c5, B:157:0x03c8, B:158:0x040a, B:159:0x040b, B:161:0x041d, B:164:0x043d, B:166:0x0447, B:169:0x044d, B:172:0x0455, B:175:0x045b, B:176:0x045e, B:178:0x046a, B:179:0x0474, B:182:0x0484, B:184:0x0490, B:186:0x0496, B:187:0x049f, B:188:0x04b8, B:191:0x04c7, B:194:0x04db, B:196:0x04ed, B:197:0x04f5, B:199:0x0507, B:200:0x0511), top: B:216:0x0025 }] */
        /* JADX WARN: Removed duplicated region for block: B:224:0x002f A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:259:0x00c0 A[SYNTHETIC] */
        /* JADX WARN: Type inference failed for: r0v12 */
        /* JADX WARN: Type inference failed for: r0v84 */
        /* JADX WARN: Type inference failed for: r0v92 */
        /* JADX WARN: Type inference failed for: r22v1, types: [short] */
        /* JADX WARN: Type inference failed for: r22v10 */
        /* JADX WARN: Type inference failed for: r22v11 */
        /* JADX WARN: Type inference failed for: r22v12 */
        /* JADX WARN: Type inference failed for: r22v13 */
        /* JADX WARN: Type inference failed for: r22v14 */
        /* JADX WARN: Type inference failed for: r22v15 */
        /* JADX WARN: Type inference failed for: r22v17 */
        /* JADX WARN: Type inference failed for: r22v18 */
        /* JADX WARN: Type inference failed for: r22v19 */
        /* JADX WARN: Type inference failed for: r22v2 */
        /* JADX WARN: Type inference failed for: r22v20 */
        /* JADX WARN: Type inference failed for: r22v3, types: [int] */
        /* JADX WARN: Type inference failed for: r22v4 */
        /* JADX WARN: Type inference failed for: r22v5 */
        /* JADX WARN: Type inference failed for: r22v6 */
        /* JADX WARN: Type inference failed for: r22v7 */
        /* JADX WARN: Type inference failed for: r22v8 */
        /* JADX WARN: Type inference failed for: r22v9 */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        static int getOpcode(android.ext.ArmDis r35, long r36, java.lang.String r38, int r39) {
            /*
                Method dump skipped, instruction units count: 1514
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: android.ext.ArmDis.Thumb16.getOpcode(android.ext.ArmDis, long, java.lang.String, int):int");
        }
    }

    static class Thumb32 {
        static final int[] NUM;
        static final String[] STR;

        Thumb32() {
        }

        static {
            int[] num = {-206602235, -1, -141590528, -4, -390066289, -1044481, -390066273, -1044481, -390066257, -1044481, -390066240, -1044496, -390066224, -1044496, -390066208, -1044496, -390070032, -1048336, -389017713, -1044481, -389017697, -1044481, -389017681, -1044481, -389017649, -1044481, -389017633, -1044481, -389017617, -1044481, -389021441, -1048321, -88018816, -986896, -88018800, -986896, -88018784, -986896, -86970240, -986896, -86970224, -986896, -86970208, -986896, -116330496, -9375744, -206602000, -16, -205549743, -13, -205549759, -13, -205549744, -16, -205549760, -16, -205549728, -16, -74387216, -986896, -72290064, -986896, -136282112, -987136, -131010560, -9375744, -135233536, -987136, -206602240, -1, -206602239, -1, -206602238, -1, -206602237, -1, -206602236, -1, -206602240, -256, -135225344, -987136, -205549777, -1, -206601216, -225, -206600704, -225, -205484288, -983041, -401555456, -3080193, -376389632, -3080193, -203390976, -2035712, -206601984, -32, -388960256, -983056, -388960240, -983056, -206600960, -256, -206600448, -256, -203518208, -256, -209682432, -2035712, -397406464, -1044481, -389017777, -1044497, -402604032, -3080224, -377438208, -3080224, -99618688, -3904, -98570112, -3904, -97521536, -3904, -96472960, -3904, -95424384, -3904, -94375808, -3904, -398458880, -1048321, -389021569, -1048321, -92213248, -986896, -92213232, -986896, -92213216, -986896, -92213184, -986896, -92213168, -986896, -92213152, -986896, -92213120, -986896, -92213104, -986896, -92213088, -986896, -92213072, -986896, -91164672, -986896, -91164656, -986896, -91164640, -986896, -91164608, -986896, -91164592, -986896, -91164576, -986896, -91164544, -986896, -91164528, -986896, -91164512, -986896, -91164496, -986896, -90116096, -986896, -90116080, -986896, -90116064, -986896, -90116032, -986896, -90116016, -986896, -90116000, -986896, -90115968, -986896, -89067392, -986896, -88018944, -986896, -88018928, -986896, -88018912, -986896, -88018880, -986896, -88018864, -986896, -88018848, -986896, -86970368, -986896, -86970352, -986896, -86970336, -986896, -86970304, -986896, -86970288, -986896, -86970272, -986896, -85921792, -986896, -85921776, -986896, -85921760, -986896, -85921728, -986896, -85921712, -986896, -85921696, -986896, -83824640, -986896, -76484608, -986896, -100601856, -2035472, -98504704, -2035472, -96407552, -2035472, -94310400, -2035472, -390066368, -1044512, -216006656, -986912, -207618048, -986912, -81727488, -986912, -80678912, -986912, -79630336, -986912, -78581760, -986912, -100601728, -986944, -99553152, -986944, -98504576, -986944, -97456000, -986944, -96407424, -986944, -95358848, -986944, -82776064, -986944, -210829312, -32736, -368046336, -1011968, -359657728, -1011968, -351269120, -1011968, -340783360, -1011968, -267383040, -68120832, -258994432, -68120832, -250605824, -68120832, -240120064, -68120832, -363921408, -1081344, -361824256, -1081344, -390070160, -1048336, -83886080, -1048336, -83886064, -1048336, -76546048, -1048336, -75497472, -1048336, -73400320, -1048336, -71303168, -1048336, -69206016, -1048336, -69205920, -1048336, -397406464, -1044736, -263258112, -68190208, -261160960, -68190208, -133107712, -9375744, -81788928, -1048352, -80740352, -1048352, -79691776, -1048352, -78643200, -1048352, -77594624, -1048352, -71302976, -1048352, -70254400, -1048352, -356515840, -1015760, -356515808, -1015760, -213909504, -1015776, -205520896, -1015776, -134214144, -7336192, -82837504, -1048384, -71303040, -1048384, -211812352, -1015776, -133165568, -24113408, -218103808, -3112928, -209715200, -3112928, -234881024, -68124672, -230686720, -68124672, -224395264, -68124672, -222298112, -68124672, -369098752, -2064384, -367001600, -2064384, -364904448, -2064384, -362807296, -2064384, -360710144, -2064384, -352321536, -2064384, -348127232, -2064384, -346030080, -2064384, -341835776, -2064384, -339738624, -2064384, -398458880, -1048576, -268435456, -69173248, -266338304, -69173248, -264241152, -69173248, -262144000, -69173248, -260046848, -69173248, -251658240, -69173248, -247463936, -69173248, -245366784, -69173248, -241172480, -69173248, -239075328, -69173248, -394264576, -3145728, -393216000, -3145728, -385875968, -3145728, -384827392, -3145728, -373293056, -3145473, -372244480, -3145473, -381681664, -11534336, -380633088, -11534336, -396361728, -9437184, -395313152, -9437184, -134217728, -15728640, -133169152, -32505856, -205488128, -71249920, -209682432, -71249920, -268402688, -134164480, -268398592, -134164480, -268386304, -134164479, -268382208, -134164480, 0, 0};
            String[] str = {"SEVL%c.W", "DCPS%0-1d.W", "STLB%c.W\t %12-15r, [%16-19R]", "STLH%c.W\t %12-15r, [%16-19R]", "STL%c.W\t %12-15r, [%16-19R]", "STLEXB%c.W\t %0-3r, %12-15r, [%16-19R]", "STLEXH%c.W\t %0-3r, %12-15r, [%16-19R]", "STLEX%c.W\t %0-3r, %12-15r, [%16-19R]", "STLEXD%c.W\t %0-3r, %12-15r, %8-11r, [%16-19R]", "LDAB%c.W\t %12-15r, [%16-19R]", "LDAH%c.W\t %12-15r, [%16-19R]", "LDA%c.W\t %12-15r, [%16-19R]", "LDAEXB%c.W\t %12-15r, [%16-19R]", "LDAEXH%c.W\t %12-15r, [%16-19R]", "LDAEX%c.W\t %12-15r, [%16-19R]", "LDAEXD%c.W\t %12-15r, %8-11r, [%16-19R]", "CRC32B%8-11S, %16-19S, %0-3S.W", "CRC32H%9-11S, %16-19S, %0-3S.W", "CRC32W%8-11S, %16-19S, %0-3S.W", "CRC32CB%8-11S, %16-19S, %0-3S.W", "CRC32CH%8-11S, %16-19S, %0-3S.W", "CRC32CW%8-11S, %16-19S, %0-3S.W", "PLI%c.W\t %a", "DBG%c.W\t #%0-3d", "DMB%c.W\t %U", "DSB%c.W\t %U", "DMB%c.W\t %U", "DSB%c.W\t %U", "ISB%c.W\t %U", "SDIV%c.W\t %8-11r, %16-19r, %0-3r", "UDIV%c.W\t %8-11r, %16-19r, %0-3r", "HVC%c.W\t %V", "PLDW%c.W\t %a", "SMC%c.W\t %K", "NOP%c.W", "YIELD%c.W", "WFE%c.W", "WFI%c.W", "SEV%c.W", "NOP%c.W\t {%0-7d}", "UDF%c.W\t %H", "CLREX%c.W", "CPSIE.W%7'A%6'I%5'F%X.W", "CPSID.W%7'A%6'I%5'F%X.W", "BXJ%c.W\t %16-19r%x", "RFEDB%c.W\t %16-19r%21'!", "RFEIA%c.W\t %16-19r%21'!", "MRS%c.W\t %8-11r, %D", "CPS#%0-4d%X.W", "TBB%c.W\t [%16-19r,%0-3r]%x", "TBH%c.W\t [%16-19r, %0-3r,LSL #1]%x", "CPSIE%7'A%6'I%5'F, #%0-4d%X.W", "CPSID%7'A%6'I%5'F, #%0-4d%X.W", "SUBS%c.W\t PC, LR, #%0-7d", "MSR%c.W\t %C, %16-19r", "LDREX%c.W\t %12-15r, [%16-19r]", "LDREX%4?HB%c.W\t %12-15r, [%16-19r]", "SRSDB%c.W\t %16-19r%21'!, #%0-4d", "SRSIA%c.W\t %16-19r%21'!, #%0-4d", "SXTH%c.W\t %8-11r, %0-3r%R", "UXTH%c.W\t %8-11r, %0-3r%R", "SXTB16%c.W\t %8-11r, %0-3r%R", "UXTB16%c.W\t %8-11r, %0-3r%R", "SXTB%c.W\t %8-11r, %0-3r%R", "UXTB%c.W\t %8-11r, %0-3r%R", "STREX%c.W\t %8-11r, %12-15r, [%16-19r]", "LDREXD%c.W\t %12-15r, %8-11r, [%16-19r]", "SADD8%c.W\t %8-11r, %16-19r, %0-3r", "QADD8%c.W\t %8-11r, %16-19r, %0-3r", "SHADD8%c.W\t %8-11r, %16-19r, %0-3r", "UADD8%c.W\t %8-11r, %16-19r, %0-3r", "UQADD8%c.W\t %8-11r, %16-19r, %0-3r", "UHADD8%c.W\t %8-11r, %16-19r, %0-3r", "QADD%c.W\t %8-11r, %0-3r, %16-19r", "QDADD%c.W\t %8-11r, %0-3r, %16-19r", "QSUB%c.W\t %8-11r, %0-3r, %16-19r", "QDSUB%c.W\t %8-11r, %0-3r, %16-19r", "SADD16%c.W\t %8-11r, %16-19r, %0-3r", "QADD16%c.W\t %8-11r, %16-19r, %0-3r", "SHADD16%c.W\t %8-11r, %16-19r, %0-3r", "UADD16%c.W\t %8-11r, %16-19r, %0-3r", "UQADD16%c.W\t %8-11r, %16-19r, %0-3r", "UHADD16%c.W\t %8-11r, %16-19r, %0-3r", "REV%c.W\t %8-11r, %16-19r", "REV16%c.W\t %8-11r, %16-19r", "RBIT%c.W\t %8-11r, %16-19r", "REVSH%c.W\t %8-11r, %16-19r", "SASX%c.W\t %8-11r, %16-19r, %0-3r", "QASX%c.W\t %8-11r, %16-19r, %0-3r", "SHASX%c.W\t %8-11r, %16-19r, %0-3r", "UASX%c.W\t %8-11r, %16-19r, %0-3r", "UQASX%c.W\t %8-11r, %16-19r, %0-3r", "UHASX%c.W\t %8-11r, %16-19r, %0-3r", "SEL%c.W\t %8-11r, %16-19r, %0-3r", "CLZ%c.W\t %8-11r, %16-19r", "SSUB8%c.W\t %8-11r, %16-19r, %0-3r", "QSUB8%c.W\t %8-11r, %16-19r, %0-3r", "SHSUB8%c.W\t %8-11r, %16-19r, %0-3r", "USUB8%c.W\t %8-11r, %16-19r, %0-3r", "UQSUB8%c.W\t %8-11r, %16-19r, %0-3r", "UHSUB8%c.W\t %8-11r, %16-19r, %0-3r", "SSUB16%c.W\t %8-11r, %16-19r, %0-3r", "QSUB16%c.W\t %8-11r, %16-19r, %0-3r", "SHSUB16%c.W\t %8-11r, %16-19r, %0-3r", "USUB16%c.W\t %8-11r, %16-19r, %0-3r", "UQSUB16%c.W\t %8-11r, %16-19r, %0-3r", "UHSUB16%c.W\t %8-11r, %16-19r, %0-3r", "SSAX%c.W\t %8-11r, %16-19r, %0-3r", "QSAX%c.W\t %8-11r, %16-19r, %0-3r", "SHSAX%c.W\t %8-11r, %16-19r, %0-3r", "USAX%c.W\t %8-11r, %16-19r, %0-3r", "UQSAX%c.W\t %8-11r, %16-19r, %0-3r", "UHSAX%c.W\t %8-11r, %16-19r, %0-3r", "MUL%c.W\t %8-11r, %16-19r, %0-3r", "USAD8%c.W\t %8-11r, %16-19r, %0-3r", "LSL%20'S%c.W\t %8-11R, %16-19R, %0-3R", "LSR%20'S%c.W\t %8-11R, %16-19R, %0-3R", "ASR%20'S%c.W\t %8-11R, %16-19R, %0-3R", "ROR%20'S%c.W\t %8-11r, %16-19r, %0-3r", "STREX%4?HB%c.W\t %0-3r, %12-15r, [%16-19r]", "SSAT16%c.W\t %8-11r, #%0-4d, %16-19r", "USAT16%c.W\t %8-11r, #%0-4d, %16-19r", "SMUAD%4'X%c.W\t %8-11r, %16-19r, %0-3r", "SMULW%4?TB%c.W\t %8-11r, %16-19r, %0-3r", "SMUSD%4'X%c.W\t %8-11r, %16-19r, %0-3r", "SMMUL%4'R%c.W\t %8-11r, %16-19r, %0-3r", "SXTAH%c.W\t %8-11r, %16-19r, %0-3r%R", "UXTAH%c.W\t %8-11r, %16-19r, %0-3r%R", "SXTAB16%c.W\t %8-11r, %16-19r, %0-3r%R", "UXTAB16%c.W\t %8-11r, %16-19r, %0-3r%R", "SXTAB%c.W\t %8-11r, %16-19r, %0-3r%R", "UXTAB%c.W\t %8-11r, %16-19r, %0-3r%R", "SMUL%5?TB%4?TB%c.W\t %8-11r, %16-19r, %0-3r", "BFC%c.W\t %8-11r, %E", "TST%c.W\t %16-19r, %S", "TEQ%c.W\t %16-19r, %S", "CMN%c.W\t %16-19r, %S", "CMP%c.W\t %16-19r, %S", "TST%c.W\t %16-19r, %M", "TEQ%c.W\t %16-19r, %M", "CMN%c.W\t %16-19r, %M", "CMP%c.W\t %16-19r, %M", "MOV%20'S%c.W\t %8-11r, %S", "MVN%20'S%c.W\t %8-11r, %S", "STREXD%c.W\t %0-3r, %12-15r, %8-11r, [%16-19r]", "MLA%c.W\t %8-11r, %16-19r, %0-3r, %12-15r", "MLS%c.W\t %8-11r, %16-19r, %0-3r, %12-15r", "USADA8%c.W\t %8-11R, %16-19R, %0-3R, %12-15R", "SMULL%c.W\t %12-15R, %8-11R, %16-19R, %0-3R", "UMULL%c.W\t %12-15R, %8-11R, %16-19R, %0-3R", "SMLAL%c.W\t %12-15R, %8-11R, %16-19R, %0-3R", "UMLAL%c.W\t %12-15R, %8-11R, %16-19R, %0-3R", "UMAAL%c.W\t %12-15R, %8-11R, %16-19R, %0-3R", "LDREX%c.W\t %12-15r, [%16-19r,#%0-7W]", "MOV%20'S%c.W\t %8-11r, %M", "MVN%20'S%c.W\t %8-11r, %M", "PLD%c.W\t %a", "SMLAD%4'X%c.W\t %8-11R, %16-19R, %0-3R, %12-15R", "SMLAW%4?TB%c.W\t %8-11R, %16-19R, %0-3R, %12-15R", "SMLSD%4'X%c.W\t %8-11R, %16-19R, %0-3R, %12-15R", "SMMLA%4'R%c.W\t %8-11R, %16-19R, %0-3R, %12-15R", "SMMLS%4'R%c.W\t %8-11R, %16-19R, %0-3R, %12-15R", "SMLALD%4'X%c.W\t %12-15R, %8-11R, %16-19R, %0-3R", "SMLSLD%4'X%c.W\t %12-15R, %8-11R, %16-19R, %0-3R", "PKHBT%c.W\t %8-11r, %16-19r, %S", "PKHTB%c.W\t %8-11r, %16-19r, %S", "SBFX%c.W\t %8-11r, %16-19r, %F", "UBFX%c.W\t %8-11r, %16-19r, %F", "STR%wt%c.W\t %12-15r, %a", "SMLA%5?TB%4?TB%c.W\t %8-11r, %16-19r, %0-3r, %12-15r", "SMLAL%5?TB%4?TB%c.W\t %12-15r, %8-11r, %16-19r, %0-3r", "BFI%c.W\t %8-11r, %16-19r, %E", "LDR%wt%c.W\t %12-15r, %a", "SSAT%c.W\t %8-11r, #%0-4d, %16-19r%s", "USAT%c.W\t %8-11r, #%0-4d, %16-19r%s", "ADDW%c.W\t %8-11r, %16-19r, %I", "MOVW%c.W\t %8-11r, %J", "SUBW%c.W\t %8-11r, %16-19r, %I", "MOVT%c.W\t %8-11r, %J", "AND%20'S%c.W\t %8-11r, %16-19r, %S", "BIC%20'S%c.W\t %8-11r, %16-19r, %S", "ORR%20'S%c.W\t %8-11r, %16-19r, %S", "ORN%20'S%c.W\t %8-11r, %16-19r, %S", "EOR%20'S%c.W\t %8-11r, %16-19r, %S", "ADD%20'S%c.W\t %8-11r, %16-19r, %S", "ADC%20'S%c.W\t %8-11r, %16-19r, %S", "SBC%20'S%c.W\t %8-11r, %16-19r, %S", "SUB%20'S%c.W\t %8-11r, %16-19r, %S", "RSB%20'S%c.W\t %8-11r, %16-19r, %S", "STREX%c.W\t %8-11r, %12-15r, [%16-19r,#%0-7W]", "AND%20'S%c.W\t %8-11r, %16-19r, %M", "BIC%20'S%c.W\t %8-11r, %16-19r, %M", "ORR%20'S%c.W\t %8-11r, %16-19r, %M", "ORN%20'S%c.W\t %8-11r, %16-19r, %M", "EOR%20'S%c.W\t %8-11r, %16-19r, %M", "ADD%20'S%c.W\t %8-11r, %16-19r, %M", "ADC%20'S%c.W\t %8-11r, %16-19r, %M", "SBC%20'S%c.W\t %8-11r, %16-19r, %M", "SUB%20'S%c.W\t %8-11r, %16-19r, %M", "RSB%20'S%c.W\t %8-11r, %16-19r, %M", "STMIA%c.W\t %16-19r%21'!, %m", "LDMIA%c.W\t %16-19r%21'!, %m", "STMDB%c.W\t %16-19r%21'!, %m", "LDMDB%c.W\t %16-19r%21'!, %m", "STRD%c.W\t %12-15r, %8-11r, [%16-19r]", "LDRD%c.W\t %12-15r, %8-11r, [%16-19r]", "STRD%c.W\t %12-15r, %8-11r, [%16-19r,#%23`-%0-7W]%21'!%L", "LDRD%c.W\t %12-15r, %8-11r, [%16-19r,#%23`-%0-7W]%21'!%L", "STRD%c.W\t %12-15r, %8-11r, [%16-19r], #%23`-%0-7W%L", "LDRD%c.W\t %12-15r, %8-11r, [%16-19r], #%23`-%0-7W%L", "STR%w%c.W\t %12-15r, %a", "LDR%w%c.W\t %12-15r, %a", "UNDEFINED(BCC, COND=0xF).W", "UNDEFINED(BCC, COND=0xE).W", "B%22-25c.W\t %b%X", "B%c.W\t %B%x", "BLX%c.W\t %B%x", "BL%c.W\t %B%x", "\t \t ; <UNDEFINED> instruction: %0-31x.W"};
            NUM = num;
            STR = str;
        }

        static String psrName(int regno) {
            switch (regno) {
                case 0:
                    return "APSR";
                case 1:
                    return "IAPSR";
                case 2:
                    return "EAPSR";
                case 3:
                    return "PSR";
                case 4:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                default:
                    return "<unknown>";
                case 5:
                    return "IPSR";
                case 6:
                    return "EPSR";
                case 7:
                    return "IEPSR";
                case 8:
                    return "MSP";
                case 9:
                    return "PSP";
                case 16:
                    return "PRIMASK";
                case 17:
                    return "BASEPRI";
                case 18:
                    return "BASEPRI_MAX";
                case 19:
                    return "FAULTMASK";
                case 20:
                    return "CONTROL";
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Removed duplicated region for block: B:231:0x0958  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        static java.lang.String getOpcode(android.ext.ArmDis r59, long r60, int r62) {
            /*
                Method dump skipped, instruction units count: 2886
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: android.ext.ArmDis.Thumb32.getOpcode(android.ext.ArmDis, long, int):java.lang.String");
        }

        static int getOpcode(ArmDis ret, long addr, String data, int dbg) {
            return -1;
        }
    }

    public static String getArmOpcode(ArmDis ret, long addr, long data) {
        if (ret == null) {
            try {
                ret = new ArmDis();
            } catch (Throwable e) {
                String op = e.toString().replace("java.lang.RuntimeException:", "RE:");
                Log.d("Failed get OP 2", e);
                return op;
            }
        }
        String op2 = Arm.getOpcode(ret, addr, (int) data);
        return op2;
    }

    public static class AsmFailedException extends NumberFormatException {
        String data;
        EditText edit;
        String suggestion;

        public AsmFailedException(String s) {
            super(s);
        }

        public String getSuggestion() {
            return this.suggestion;
        }
    }

    public static int getArmOpcode(ArmDis ret, long addr, String data) {
        if (ret == null) {
            ret = new ArmDis();
        }
        int op = Arm.getOpcode(ret, addr, data, -1);
        if (op == -1) {
            String suggestion = getArmOpcode(ret, addr, ret.input.bestOut).trim();
            AsmFailedException e = new AsmFailedException(String.valueOf(Tools.stringFormat(Re.s(catch_.me_.if_.you_.can_.R.string.asm_failed), "ARM").trim()) + "\n'" + data + "'\n\n" + Tools.stringFormat(Re.s(catch_.me_.if_.you_.can_.R.string.asm_suggestion), "\n'" + suggestion + "'\n").trim());
            int pos = suggestion.indexOf(59);
            if (pos != -1) {
                suggestion = suggestion.substring(0, pos).trim();
            }
            e.suggestion = suggestion;
            e.data = data;
            throw e;
        }
        return op;
    }

    public static String getThumbOpcode(ArmDis ret, long addr, long data) {
        try {
            if ((63488 & data) == 63488 || (63488 & data) == 61440 || (63488 & data) == 59392) {
                long data2 = ((65535 & data) << 16) | ((((-65536) & data) >> 16) & 65535);
                if (ret == null) {
                    ret = new ArmDis();
                }
                String op = Thumb32.getOpcode(ret, addr, (int) data2);
                return op;
            }
            String op2 = Thumb16.getOpcode(addr, (int) data);
            return op2;
        } catch (Throwable e) {
            String op3 = e.toString().replace("java.lang.RuntimeException:", "RE:");
            Log.d("Failed get OP 1", e);
            return op3;
        }
    }

    public static int getThumbOpcode(ArmDis ret, long addr, String data) {
        if (ret == null) {
            ret = new ArmDis();
        }
        int op = Thumb16.getOpcode(ret, addr, data, -1);
        if (op == -1) {
            String suggestion = getThumbOpcode(ret, addr, ret.input.bestOut).trim();
            AsmFailedException e = new AsmFailedException(String.valueOf(Tools.stringFormat(Re.s(catch_.me_.if_.you_.can_.R.string.asm_failed), "Thumb").trim()) + "\n'" + data + "'\n\n" + Tools.stringFormat(Re.s(catch_.me_.if_.you_.can_.R.string.asm_suggestion), "\n'" + suggestion + "'\n").trim());
            int pos = suggestion.indexOf(59);
            if (pos != -1) {
                suggestion = suggestion.substring(0, pos).trim();
            }
            e.suggestion = suggestion;
            e.data = data;
            throw e;
        }
        return 65535 & op;
    }

    public static void main(String[] args) throws Throwable {
    }

    private static boolean test(int type, ArmDis armDis, long offset, int i, int data) {
        return false;
    }
}
