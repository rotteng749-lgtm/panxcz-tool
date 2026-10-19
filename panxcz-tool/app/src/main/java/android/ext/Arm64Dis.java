package android.ext;

import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class Arm64Dis {
    private static final int Sys_AT = 65536;
    private static final int Sys_DC = 131072;
    private static final int Sys_IC = 196608;
    private static final int Sys_SYS = 0;
    private static final int Sys_SYS_OFFSET = 16;
    private static final int Sys_TLBI = 262144;
    private static final int arg_Cm = 32;
    private static final int arg_Cn = 31;
    private static final int arg_Dn = 143;
    private static final int arg_Dt = 142;
    private static final int arg_F16 = 176;
    private static final int arg_F32 = 177;
    private static final int arg_F64 = 178;
    private static final int arg_FPidx = 114;
    private static final int arg_FPidxk = 162;
    private static final int arg_FPjn2 = 151;
    private static final int arg_FPjt = 112;
    private static final int arg_FPjt2 = 150;
    private static final int arg_FPk5t = 189;
    private static final int arg_FPm = 85;
    private static final int arg_FPn = 119;
    private static final int arg_FPnj = 148;
    private static final int arg_FPst = 89;
    private static final int arg_FPt = 82;
    private static final int arg_FPz2m = 129;
    private static final int arg_FPz2n = 128;
    private static final int arg_FPz2t = 127;
    private static final int arg_FPz3m = 141;
    private static final int arg_FPz3n = 133;
    private static final int arg_FPz3t = 132;
    private static final int arg_FPz4n = 134;
    private static final int arg_FPz4t = 139;
    private static final int arg_FPz5d = 192;
    private static final int arg_FPz5m = 190;
    private static final int arg_FPz5n = 186;
    private static final int arg_FPz5t = 184;
    private static final int arg_Hm = 118;
    private static final int arg_Hn = 117;
    private static final int arg_Ht = 116;
    private static final int arg_NONE = 0;
    private static final int arg_Qi = 42;
    private static final int arg_Qi1 = 46;
    private static final int arg_Qi2 = 45;
    private static final int arg_Qi3 = 44;
    private static final int arg_Qn = 110;
    private static final int arg_Qt = 105;
    private static final int arg_R2n = 161;
    private static final int arg_RCTX = 195;
    private static final int arg_RSP = 193;
    private static final int arg_Rd = 77;
    private static final int arg_Rd1 = 78;
    private static final int arg_Rm = 12;
    private static final int arg_Rn = 7;
    private static final int arg_RnS = 4;
    private static final int arg_Rom = 91;
    private static final int arg_Rsom = 98;
    private static final int arg_Rt = 9;
    private static final int arg_Rt1 = 79;
    private static final int arg_RtS = 3;
    private static final int arg_SKIP = 1;
    private static final int arg_Sn = 106;
    private static final int arg_St = 111;
    private static final int arg_Vd16b = 182;
    private static final int arg_Vd4s = 183;
    private static final int arg_Vm16b = 181;
    private static final int arg_Vm2d = 180;
    private static final int arg_Vm4s = 107;
    private static final int arg_VmH1 = 122;
    private static final int arg_VmHs = 154;
    private static final int arg_VmT = 131;
    private static final int arg_VmT3 = 169;
    private static final int arg_VmTs = 153;
    private static final int arg_VmTs2 = 155;
    private static final int arg_VmTs4b = 179;
    private static final int arg_Vmzq = 125;
    private static final int arg_Vn116b = 156;
    private static final int arg_Vn16b = 104;
    private static final int arg_Vn1d = 187;
    private static final int arg_Vn216b = 157;
    private static final int arg_Vn2d = 136;
    private static final int arg_Vn2h = 137;
    private static final int arg_Vn316b = 158;
    private static final int arg_Vn416b = 159;
    private static final int arg_Vn4s = 109;
    private static final int arg_VnH1 = 121;
    private static final int arg_VnT = 130;
    private static final int arg_VnT2 = 164;
    private static final int arg_VnT3 = 135;
    private static final int arg_VnTa = 149;
    private static final int arg_Vnj = 113;
    private static final int arg_Vnj2 = 146;
    private static final int arg_Vnz = 138;
    private static final int arg_Vnz3 = 165;
    private static final int arg_Vnzq = 124;
    private static final int arg_Vnzq2 = 166;
    private static final int arg_Vt16b = 103;
    private static final int arg_Vt1d = 188;
    private static final int arg_Vt2B = 63;
    private static final int arg_Vt2D = 66;
    private static final int arg_Vt2H = 64;
    private static final int arg_Vt2S = 65;
    private static final int arg_Vt2T = 36;
    private static final int arg_Vt2d = 175;
    private static final int arg_Vt3B = 55;
    private static final int arg_Vt3D = 58;
    private static final int arg_Vt3H = 56;
    private static final int arg_Vt3S = 57;
    private static final int arg_Vt3T = 37;
    private static final int arg_Vt4B = 68;
    private static final int arg_Vt4D = 71;
    private static final int arg_Vt4H = 69;
    private static final int arg_Vt4S = 70;
    private static final int arg_Vt4T = 38;
    private static final int arg_Vt4s = 108;
    private static final int arg_VtB = 47;
    private static final int arg_VtD = 50;
    private static final int arg_VtH = 48;
    private static final int arg_VtH1 = 120;
    private static final int arg_VtS = 49;
    private static final int arg_VtT = 35;
    private static final int arg_VtT3 = 168;
    private static final int arg_VtT4 = 170;
    private static final int arg_Vtj = 160;
    private static final int arg_Vtj2 = 145;
    private static final int arg_Vtjq = 115;
    private static final int arg_Vtz = 140;
    private static final int arg_Vtz3 = 152;
    private static final int arg_Vtzq = 123;
    private static final int arg_Vtzq2 = 163;
    private static final int arg_Wd = 80;
    private static final int arg_Wm = 101;
    private static final int arg_Wn = 100;
    private static final int arg_Wt = 81;
    private static final int arg_Xd = 102;
    private static final int arg_Xm = 43;
    private static final int arg_XmS = 196;
    private static final int arg_Xn = 33;
    private static final int arg_XnS = 40;
    private static final int arg_Xt = 194;
    private static final int arg_Xt_opt = 26;
    private static final int arg_amountj = 92;
    private static final int arg_amountj2 = 95;
    private static final int arg_amountj3 = 96;
    private static final int arg_amountjs = 94;
    private static final int arg_amountk2_opt = 173;
    private static final int arg_amountk_opt = 172;
    private static final int arg_amountz = 93;
    private static final int arg_at = 18;
    private static final int arg_b = 34;
    private static final int arg_c = 13;
    private static final int arg_dc = 21;
    private static final int arg_exts = 99;
    private static final int arg_fbits = 185;
    private static final int arg_i = 5;
    private static final int arg_i1 = 51;
    private static final int arg_i12 = 61;
    private static final int arg_i16 = 67;
    private static final int arg_i2 = 52;
    private static final int arg_i24 = 62;
    private static final int arg_i3 = 59;
    private static final int arg_i32 = 72;
    private static final int arg_i4 = 53;
    private static final int arg_i6 = 60;
    private static final int arg_i8 = 54;
    private static final int arg_i_opt = 15;
    private static final int arg_ib = 8;
    private static final int arg_ic = 25;
    private static final int arg_im4_opt = 87;
    private static final int arg_imm64 = 174;
    private static final int arg_imm8 = 171;
    private static final int arg_is4_opt = 84;
    private static final int arg_iz4_opt = 86;
    private static final int arg_j = 11;
    private static final int arg_j12_opt = 6;
    private static final int arg_j16_opt = 10;
    private static final int arg_j_opt = 90;
    private static final int arg_jz = 191;
    private static final int arg_labeli4 = 14;
    private static final int arg_labelij1 = 2;
    private static final int arg_nRt = 88;
    private static final int arg_offe = 41;
    private static final int arg_offs = 39;
    private static final int arg_prf_op = 83;
    private static final int arg_pstate = 16;
    private static final int arg_sh = 17;
    private static final int arg_shift8 = 167;
    private static final int arg_shiftj_opt = 97;
    private static final int arg_shlshift = 147;
    private static final int arg_shrshift = 144;
    private static final int arg_simd0 = 126;
    private static final int arg_sysreg = 30;
    private static final int arg_tlbi = 27;
    private static final int arg_z = 73;
    private static final int arg_z2 = 75;
    private static final int arg_z3 = 74;
    private static final int arg_z4 = 76;
    private static final int args_max = 8;
    private static final boolean debug = false;

    private static int SysOp(int code) {
        switch (code) {
            case 904:
                return Sys_IC;
            case 936:
                return 196609;
            case 945:
                return 131072;
            case 946:
                return 131073;
            case 947:
                return 131074;
            case 948:
                return 131075;
            case 949:
                return 131076;
            case 950:
                return 131077;
            case 960:
                return 65536;
            case 961:
                return 65537;
            case 962:
                return 65538;
            case 963:
                return 65539;
            case 968:
                return 65540;
            case 969:
                return 65541;
            case 978:
                return 131078;
            case 980:
                return 131079;
            case 982:
                return 131080;
            case 1010:
                return 131081;
            case 1012:
                return 131082;
            case 1014:
                return 131083;
            case 1032:
                return 262144;
            case 1033:
                return 262145;
            case 1034:
                return 262146;
            case 1035:
                return 262147;
            case 1037:
                return 262148;
            case 1039:
                return 262149;
            case 1041:
                return 262150;
            case 1043:
                return 262151;
            case 1045:
                return 262152;
            case 1047:
                return 262153;
            case 1048:
                return 262154;
            case 1049:
                return 262155;
            case 1050:
                return 262156;
            case 1051:
                return 262157;
            case 1053:
                return 262158;
            case 1055:
                return 262159;
            case 1065:
                return 262160;
            case 1067:
                return 262161;
            case 1069:
                return 262162;
            case 1071:
                return 262163;
            case 1073:
                return 262164;
            case 1075:
                return 262165;
            case 1077:
                return 262166;
            case 1079:
                return 262167;
            case 1080:
                return 262168;
            case 1081:
                return 262169;
            case 1082:
                return 262170;
            case 1083:
                return 262171;
            case 1085:
                return 262172;
            case 1087:
                return 262173;
            case 7073:
                return 131084;
            case 7075:
                return 131085;
            case 7076:
                return 131086;
            case 7081:
                return 196610;
            case 7121:
                return 131087;
            case 7123:
                return 131088;
            case 7125:
                return 131089;
            case 7129:
                return 131090;
            case 7137:
                return 131091;
            case 7139:
                return 131092;
            case 7141:
                return 131093;
            case 7145:
                return 131094;
            case 7147:
                return 131095;
            case 7149:
                return 131096;
            case 7153:
                return 131097;
            case 7155:
                return 131098;
            case 7157:
                return 131099;
            case 9152:
                return 65542;
            case 9153:
                return 65543;
            case 9156:
                return 65544;
            case 9157:
                return 65545;
            case 9158:
                return 65546;
            case 9159:
                return 65547;
            case 9217:
                return 262174;
            case 9218:
                return 262175;
            case 9221:
                return 262176;
            case 9222:
                return 262177;
            case 9224:
                return 262178;
            case 9225:
                return 262179;
            case 9228:
                return 262180;
            case 9229:
                return 262181;
            case 9230:
                return 262182;
            case 9233:
                return 262183;
            case 9237:
                return 262184;
            case 9240:
                return 262185;
            case 9241:
                return 262186;
            case 9244:
                return 262187;
            case 9245:
                return 262188;
            case 9246:
                return 262189;
            case 9248:
                return 262190;
            case 9249:
                return 262191;
            case 9250:
                return 262192;
            case 9251:
                return 262193;
            case 9252:
                return 262194;
            case 9253:
                return 262195;
            case 9254:
                return 262196;
            case 9255:
                return 262197;
            case 9257:
                return 262198;
            case 9261:
                return 262199;
            case 9265:
                return 262200;
            case 9269:
                return 262201;
            case 9272:
                return 262202;
            case 9273:
                return 262203;
            case 9276:
                return 262204;
            case 9277:
                return 262205;
            case 9278:
                return 262206;
            case 13248:
                return 65548;
            case 13249:
                return 65549;
            case 13320:
                return 262207;
            case 13321:
                return 262208;
            case 13325:
                return 262209;
            case 13329:
                return 262210;
            case 13333:
                return 262211;
            case 13336:
                return 262212;
            case 13337:
                return 262213;
            case 13341:
                return 262214;
            case 13353:
                return 262215;
            case 13357:
                return 262216;
            case 13361:
                return 262217;
            case 13365:
                return 262218;
            case 13368:
                return 262219;
            case 13369:
                return 262220;
            case 13373:
                return 262221;
            default:
                return 0;
        }
    }

    private static boolean MoveWidePreferred(int sf, int immN, int imms, int immr) {
        if (sf == 1 && immN != 1) {
            return false;
        }
        if (sf == 0 && (immN != 0 || (imms >> 5) != 0)) {
            return false;
        }
        if (imms < 16) {
            return ((-immr) & 15) <= 15 - imms;
        }
        int width = sf == 1 ? 64 : 32;
        return imms >= width + (-15) && (immr & 15) <= imms - (width + (-15));
    }

    private static final int line() {
        return 0;
    }

    private static String disasm_str(String s, int n) {
        if (s == null) {
            return Searcher.MASK_PLACEHOLDER;
        }
        int pos = 0;
        while (n > 0) {
            int pos2 = s.indexOf(0, pos);
            if (pos2 < 0) {
                return Searcher.MASK_PLACEHOLDER;
            }
            pos = pos2 + 1;
            n--;
        }
        int sj = s.length();
        if (pos == sj) {
            return Searcher.MASK_PLACEHOLDER;
        }
        int zero = s.indexOf(0, pos);
        return zero >= 0 ? s.substring(pos, zero) : pos != 0 ? s.substring(pos) : s;
    }

    private static String disasm_sysreg(int p, int k, int n, int m, int j) {
        String t = null;
        switch (p) {
            case 2:
                switch (k) {
                    case 0:
                        switch (n) {
                            case 0:
                                switch (m) {
                                    case 0:
                                        t = "?\u0000?\u0000OSDTRRX_EL1";
                                        break;
                                    case 1:
                                    case 4:
                                    case 5:
                                    default:
                                        j = m;
                                        switch (j) {
                                            case 4:
                                                t = "DBGBVR0_EL1\u0000DBGBVR1_EL1\u0000DBGBVR2_EL1\u0000DBGBVR3_EL1\u0000DBGBVR4_EL1\u0000DBGBVR5_EL1\u0000DBGBVR6_EL1\u0000DBGBVR7_EL1";
                                                break;
                                            case 5:
                                                t = "DBGBCR0_EL1\u0000DBGBCR1_EL1\u0000DBGBCR2_EL1\u0000DBGBCR3_EL1\u0000DBGBCR4_EL1\u0000DBGBCR5_EL1\u0000DBGBCR6_EL1\u0000DBGBCR7_EL1";
                                                break;
                                            case 6:
                                                t = "DBGWVR0_EL1\u0000DBGWVR1_EL1\u0000DBGWVR2_EL1\u0000DBGWVR3_EL1\u0000DBGWVR4_EL1\u0000DBGWVR5_EL1\u0000DBGWVR6_EL1\u0000DBGWVR7_EL1";
                                                break;
                                            case 7:
                                                t = "DBGWCR0_EL1\u0000DBGWCR1_EL1\u0000DBGWCR2_EL1\u0000DBGWCR3_EL1\u0000DBGWCR4_EL1\u0000DBGWCR5_EL1\u0000DBGWCR6_EL1\u0000DBGWCR7_EL1";
                                                break;
                                        }
                                        break;
                                    case 2:
                                        t = "MDCCINT_EL1\u0000?\u0000MDSCR_EL1";
                                        break;
                                    case 3:
                                        t = "?\u0000?\u0000OSDTRTX_EL1";
                                        break;
                                    case 6:
                                        t = "?\u0000?\u0000OSECCR_EL1";
                                        break;
                                }
                                break;
                            case 1:
                                if (m == 0) {
                                    t = "MDRAR_EL1\u0000?\u0000?\u0000?\u0000OSLAR_EL1";
                                } else if (j == 4) {
                                    j = m;
                                    t = "OSLSR_EL1\u0000?\u0000OSDLR_EL1\u0000DBGPRCR_EL1";
                                }
                                break;
                            case 7:
                                if (j == 6) {
                                    j = m;
                                    t = "?\u0000?\u0000?\u0000?\u0000?\u0000?\u0000?\u0000?\u0000DBGCLAIMSET_EL1\u0000DBGCLAIMCLR_EL1\u0000?\u0000?\u0000?\u0000?\u0000DBGAUTHSTATUS_EL1";
                                }
                                break;
                        }
                        break;
                    case 3:
                        if (n == 0 && j == 0) {
                            j = m;
                            t = "?\u0000MDCCSR_EL0\u0000?\u0000?\u0000DBGDTR_EL0\u0000DBGDTRRX_EL0";
                        }
                        break;
                    case 4:
                        if (n == 0 && m == 7) {
                            t = "DBGVCR32_EL2";
                        }
                        break;
                }
                break;
            case 3:
                switch (k) {
                    case 0:
                        switch (n) {
                            case 0:
                                if (m == 0) {
                                    t = "MIDR_EL1\u0000?\u0000?\u0000?\u0000?\u0000MPIDR_EL1\u0000REVIDR_EL1\u0000?\u0000ID_PFR0_EL1\u0000ID_PFR1_EL1\u0000ID_DFR0_EL1\u0000ID_AFR0_EL1\u0000ID_MMFR0_EL1\u0000ID_MMFR1_EL1\u0000ID_MMFR2_EL1\u0000ID_MMFR3_EL1\u0000ID_ISAR0_EL1\u0000ID_ISAR1_EL1\u0000ID_ISAR2_EL1\u0000ID_ISAR2_EL1\u0000ID_ISAR3_EL1\u0000ID_ISAR4_EL1\u0000ID_ISAR5_EL1\u0000ID_MMFR4_EL1\u0000?\u0000MVFR0_EL1\u0000MVFR1_EL1\u0000MVFR2_EL1\u0000?\u0000?\u0000?\u0000?\u0000?\u0000ID_A64PFR0_EL1\u0000ID_A64PFR1_EL1\u0000?\u0000?\u0000ID_A64ZFR0_EL1\u0000?\u0000?\u0000?\u0000ID_A64DFR0_EL1\u0000ID_A64DFR1_EL1\u0000?\u0000?\u0000ID_A64AFR0_EL1\u0000ID_A64AFR1_EL1\u0000?\u0000?\u0000ID_A64ISAR0_EL1\u0000ID_A64ISAR1_EL1\u0000?\u0000?\u0000?\u0000?\u0000?\u0000?\u0000ID_A64MMFR0_EL1\u0000ID_A64MMFR1_EL1\u0000ID_A64MMFR2_EL1";
                                }
                                break;
                            case 1:
                                switch (m) {
                                    case 0:
                                        t = "SCTLR_EL1\u0000ACTLR_EL1\u0000CPACR_EL1";
                                        break;
                                    case 2:
                                        t = "ZCR_EL1";
                                        break;
                                }
                                break;
                            case 2:
                                if (m == 0) {
                                    t = "TTBR0_EL1\u0000TTBR1_EL1\u0000TCR_EL1";
                                }
                                break;
                            case 4:
                                switch (m) {
                                    case 0:
                                        t = "SPSR_EL1\u0000ELR_EL1";
                                        break;
                                    case 1:
                                        t = "SP_EL0";
                                        break;
                                    case 2:
                                        t = "SPSel\u0000?\u0000CurrentEL\u0000PAN\u0000UAO";
                                        break;
                                    case 6:
                                        t = "ICC_PMR_EL1";
                                        break;
                                }
                                break;
                            case 5:
                                switch (m) {
                                    case 1:
                                        t = "AFSR0_EL1\u0000AFSR1_EL1";
                                        break;
                                    case 2:
                                        t = "ESR_EL1";
                                        break;
                                    case 3:
                                        t = "ERRIDR_EL1\u0000ERRSELR_EL1";
                                        break;
                                    case 4:
                                        t = "ERXFR_EL1\u0000ERXCTLR_EL1\u0000ERXSTATUS_EL1\u0000ERXADDR_EL1";
                                        break;
                                    case 5:
                                        t = "ERXMISC0_EL1\u0000ERXMISC1_EL1";
                                        break;
                                }
                                break;
                            case 6:
                                if (m == 0) {
                                    t = "FAR_EL1";
                                }
                                break;
                            case 7:
                                if (m == 4) {
                                    t = "PAR_EL1";
                                }
                                break;
                            case 9:
                                switch (m) {
                                    case 9:
                                        t = "PMSCR_EL1\u0000?\u0000PMSICR_EL1\u0000PMSIRR_EL1\u0000PMSFCR_EL1\u0000PMSEVFR_EL1\u0000PMSLATFR_EL1\u0000PMSIDR_EL1\u0000PMSIDR_EL1";
                                        break;
                                    case 10:
                                        t = "PMBLIMITR_EL1\u0000PMBPTR_EL1\u0000?\u0000PMBSR_EL1\u0000?\u0000?\u0000?\u0000PMBIDR_EL1";
                                        break;
                                    case 14:
                                        t = "?\u0000PMINTENSET_EL1\u0000PMINTENCLR_EL1";
                                        break;
                                }
                                break;
                            case 10:
                                if (m == 4) {
                                    t = "LORSA_EL1\u0000LOREA_EL1\u0000LORN_EL1\u0000LORC_EL1\u0000?\u0000?\u0000?\u0000LORID_EL1";
                                } else if (m != 4 && j == 0) {
                                    j = m;
                                    t = "?\u0000?\u0000MAIR_EL1\u0000AMAIR_EL1";
                                }
                                break;
                            case 12:
                                switch (m) {
                                    case 0:
                                        t = "VBAR_EL1\u0000RVBAR_EL1\u0000RMR_EL1";
                                        break;
                                    case 1:
                                        t = "ISR_EL1\u0000DISR_EL1";
                                        break;
                                    case 8:
                                        t = "ICC_IAR0_EL1\u0000ICC_EOIR0_EL1\u0000ICC_HPPIR0_EL1\u0000ICC_BPR0_EL1\u0000ICC_AP0R0_EL1\u0000ICC_AP0R1_EL1\u0000ICC_AP0R2_EL1\u0000ICC_AP0R3_EL1";
                                        break;
                                    case 9:
                                        t = "ICC_AP1R0_EL1\u0000ICC_AP1R1_EL1\u0000ICC_AP1R2_EL1\u0000ICC_AP1R3_EL1";
                                        break;
                                    case 11:
                                        t = "?\u0000ICC_DIR_EL1\u0000?\u0000ICC_RPR_EL1\u0000?\u0000ICC_SGI1R_EL1\u0000ICC_ASGI1R_EL1\u0000ICC_SGI0R_EL1";
                                        break;
                                    case 12:
                                        t = "ICC_IAR1_EL1\u0000ICC_EOIR1_EL1\u0000ICC_HPPIR1_EL1\u0000ICC_BPR1_EL1\u0000ICC_CTLR_EL1\u0000ICC_SRE_EL1\u0000ICC_IGRPEN0_EL1\u0000ICC_IGRPEN1_EL1";
                                        break;
                                }
                                break;
                            case 13:
                                if (m == 0) {
                                    t = "?\u0000CONTEXTIDR_EL1\u0000?\u0000?\u0000TPIDR_EL1";
                                }
                                break;
                            case 14:
                                if (m == 1) {
                                    t = "CNTKCTL_EL1";
                                }
                                break;
                        }
                        break;
                    case 1:
                        if (n == 0 && m == 0) {
                            t = "CCSIDR_EL1\u0000CLIDR_EL1\u0000?\u0000?\u0000?\u0000?\u0000?\u0000AIDR_EL1";
                        }
                        break;
                    case 2:
                        if (n == 0 && m == 0) {
                            t = "CSSELR_EL1";
                        }
                        break;
                    case 3:
                        switch (n) {
                            case 0:
                                if (m == 0) {
                                    t = "?\u0000CTR_EL0\u0000?\u0000?\u0000?\u0000?\u0000?\u0000DCZID_EL0";
                                }
                                break;
                            case 4:
                                switch (m) {
                                    case 2:
                                        t = "NZCV\u0000DAIF";
                                        break;
                                    case 4:
                                        t = "FPCR\u0000FPSR";
                                        break;
                                    case 5:
                                        t = "DSPSR_EL0\u0000DLR_EL0";
                                        break;
                                }
                                break;
                            case 9:
                                switch (m) {
                                    case 12:
                                        t = "PMCR_EL0\u0000PMCNTENSET_EL0\u0000PMCNTENCLR_EL0\u0000PMOVSCLR_EL0\u0000PMSWINC_EL0\u0000PMSELR_EL0\u0000PMCEID0_EL0\u0000PMCEID1_EL0";
                                        break;
                                    case 13:
                                        t = "PMCCNTR_EL0\u0000PMXEVTYPER_EL0\u0000PMXEVCNTR_EL0";
                                        break;
                                    case 14:
                                        t = "PMUSERENR_EL0\u0000?\u0000?\u0000PMOVSSET_EL0";
                                        break;
                                }
                                break;
                            case 13:
                                if (m == 0) {
                                    t = "?\u0000?\u0000TPIDR_EL0\u0000TPIDRRO_EL0";
                                }
                                break;
                            case 14:
                                switch (m) {
                                    case 0:
                                        t = "CNTFRQ_EL0\u0000CNTPCT_EL0\u0000CNTVCT_EL0";
                                        break;
                                    case 2:
                                        t = "CNTP_TVAL_EL0\u0000CNTP_CTL_EL0\u0000CNTP_CVAL_EL0";
                                        break;
                                    case 3:
                                        t = "CNTV_TVAL_EL0\u0000CNTV_CTL_EL0\u0000CNTV_CVAL_EL0";
                                        break;
                                }
                                break;
                        }
                        break;
                    case 4:
                        switch (n) {
                            case 0:
                                if (m == 0) {
                                    t = "VPIDR_EL2\u0000?\u0000?\u0000?\u0000?\u0000VMPIDR_EL2";
                                }
                                break;
                            case 1:
                                switch (m) {
                                    case 0:
                                        t = "SCTLR_EL2\u0000ACTLR_EL2";
                                        break;
                                    case 1:
                                        t = "HCR_EL2\u0000MDCR_EL2\u0000CPTR_EL2\u0000HSTR_EL2\u0000?\u0000?\u0000?\u0000HACR_EL2";
                                        break;
                                    case 2:
                                        t = "ZCR_EL2";
                                        break;
                                }
                                break;
                            case 2:
                                switch (m) {
                                    case 0:
                                        t = "TTBR0_EL2\u0000?\u0000TCR_EL2";
                                        break;
                                    case 1:
                                        t = "VTTBR0_EL2\u0000?\u0000VTCR_EL2";
                                        break;
                                }
                                break;
                            case 3:
                                if (m == 0) {
                                    t = "DACR32_EL2";
                                }
                                break;
                            case 4:
                                switch (m) {
                                    case 0:
                                        t = "SPSR_EL2\u0000ELR_EL2";
                                        break;
                                    case 1:
                                        t = "SP_EL1";
                                        break;
                                    case 3:
                                        t = "SPSR_irq\u0000SPSR_abt\u0000SPSR_und\u0000SPSR_fiq";
                                        break;
                                }
                                break;
                            case 5:
                                switch (m) {
                                    case 0:
                                        t = "?\u0000IFSR32_EL2";
                                        break;
                                    case 1:
                                        t = "AFSR0_EL2\u0000AFSR1_EL2";
                                        break;
                                    case 2:
                                        t = "ESR_EL2\u0000?\u0000?\u0000VSESR_EL2";
                                        break;
                                    case 3:
                                        t = "FPEXC32_EL2";
                                        break;
                                }
                                break;
                            case 6:
                                if (m == 0) {
                                    t = "FAR_EL2\u0000?\u0000?\u0000?\u0000HPFAR_EL2";
                                }
                                break;
                            case 9:
                                if (m == 9) {
                                    t = "PMSCR_EL2";
                                }
                                break;
                            case 10:
                                switch (m) {
                                    case 2:
                                        t = "MAIR_EL2";
                                        break;
                                    case 3:
                                        t = "AMAIR_EL2";
                                        break;
                                }
                                break;
                            case 12:
                                switch (m) {
                                    case 0:
                                        t = "VBAR_EL2\u0000RVBAR_EL2\u0000RMR_EL2";
                                        break;
                                    case 1:
                                        t = "?\u0000VDISR_EL2";
                                        break;
                                    case 8:
                                        t = "ICH_AP0R0_EL2\u0000ICH_AP0R1_EL2\u0000ICH_AP0R2_EL2\u0000ICH_AP0R3_EL2";
                                        break;
                                    case 9:
                                        t = "ICH_AP1R0_EL2\u0000ICH_AP1R1_EL2\u0000ICH_AP1R2_EL2\u0000ICH_AP1R3_EL2\u0000ICC_SRE_EL2";
                                        break;
                                    case 11:
                                        t = "ICH_HCR_EL2\u0000ICH_VTR_EL2\u0000ICH_MISR_EL2\u0000ICH_EISR_EL2\u0000?\u0000ICH_ELRSR_EL2\u0000?\u0000ICH_VMCR_EL2";
                                        break;
                                    case 12:
                                        t = "ICH_LR0_EL2\u0000ICH_LR1_EL2\u0000ICH_LR2_EL2\u0000ICH_LR3_EL2\u0000ICH_LR4_EL2\u0000ICH_LR5_EL2\u0000ICH_LR6_EL2\u0000ICH_LR7_EL2";
                                        break;
                                    case 13:
                                        t = "ICH_LR8_EL2\u0000ICH_LR9_EL2\u0000ICH_LR10_EL2\u0000ICH_LR11_EL2\u0000ICH_LR12_EL2\u0000ICH_LR13_EL2\u0000ICH_LR14_EL2\u0000ICH_LR15_EL2";
                                        break;
                                }
                                break;
                            case 13:
                                if (m == 0) {
                                    t = "?\u0000CONTEXTIDR_EL2\u0000TPIDR_EL2";
                                }
                                break;
                            case 14:
                                switch (m) {
                                    case 0:
                                        t = "?\u0000?\u0000?\u0000CNTVOFF_EL2";
                                        break;
                                    case 1:
                                        t = "CNTHCTL_EL2";
                                        break;
                                    case 2:
                                        t = "CNTHP_TVAL_EL2\u0000CNTHP_CTL_EL2\u0000CNTHP_CVAL_EL2";
                                        break;
                                    case 3:
                                        t = "CNTHV_TVAL_EL2\u0000CNTHV_CTL_EL2\u0000CNTHV_CVAL_EL2";
                                        break;
                                }
                                break;
                        }
                        break;
                    case 5:
                        if (n == 4 && m == 0) {
                            t = "SPSR_EL12\u0000ELR_EL12";
                        }
                        break;
                    case 6:
                        if (n == 4 && m == 1) {
                            t = "SP_EL2";
                        }
                        break;
                    case 7:
                        if (n == 14 && m == 2) {
                            t = "CNTPS_TVAL_EL1\u0000CNTPS_CTL_EL1\u0000CNTPS_CVAL_EL1";
                        }
                        break;
                }
                break;
        }
        if (t != null) {
            return disasm_str(t, j);
        }
        return null;
    }

    /*  JADX ERROR: JadxRuntimeException in pass: SimplifyVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't remove SSA var: r0v1 'e'  int, still in use, count: 3, list:
          (r0v1 'e' int A[D('e' int)]) from 0x0029: ARITH (r0v1 'e' int A[D('e' int)]) - (r12v1 'i' int A[D('i' int)]) A[WRAPPED]
          (r0v1 'e' int A[D('e' int)]) from 0x0030: MOVE (r12v2 'i' int A[D('i' int)]) = (r0v1 'e' int A[D('e' int)]) (LINE:628)
          (r0v1 'e' int A[D('e' int)]) from 0x0038: ARITH (r12v4 'i' int) = (r12v3 'i' int A[D('i' int)]) + (r0v1 'e' int A[D('e' int)]) (LINE:631)
        	at jadx.core.utils.InsnRemover.removeSsaVar(InsnRemover.java:162)
        	at jadx.core.utils.InsnRemover.unbindResult(InsnRemover.java:127)
        	at jadx.core.utils.InsnRemover.unbindInsn(InsnRemover.java:91)
        	at jadx.core.utils.InsnRemover.unbindArgUsage(InsnRemover.java:174)
        	at jadx.core.utils.InsnRemover.unbindAllArgs(InsnRemover.java:106)
        	at jadx.core.utils.InsnRemover.unbindInsn(InsnRemover.java:90)
        	at jadx.core.utils.InsnRemover.unbindArgUsage(InsnRemover.java:174)
        	at jadx.core.dex.instructions.args.InsnArg.wrapInstruction(InsnArg.java:141)
        	at jadx.core.dex.visitors.SimplifyVisitor.simplifyArgs(SimplifyVisitor.java:116)
        	at jadx.core.dex.visitors.SimplifyVisitor.simplifyInsn(SimplifyVisitor.java:132)
        	at jadx.core.dex.visitors.SimplifyVisitor.simplifyArgs(SimplifyVisitor.java:114)
        	at jadx.core.dex.visitors.SimplifyVisitor.simplifyInsn(SimplifyVisitor.java:132)
        	at jadx.core.dex.visitors.SimplifyVisitor.simplifyBlock(SimplifyVisitor.java:86)
        	at jadx.core.dex.visitors.SimplifyVisitor.visit(SimplifyVisitor.java:71)
        */
    private static long disasm_dbm(int r10, int r11, int r12) {
        /*
            r4 = -1
            r8 = 1
            int r6 = r10 << 6
            r7 = r11 ^ (-1)
            r7 = r7 & 63
            r0 = r6 | r7
            r1 = 6
        Lc:
            if (r1 < 0) goto L13
            int r6 = r8 << r1
            r6 = r6 & r0
            if (r6 == 0) goto L16
        L13:
            if (r1 >= r8) goto L19
        L15:
            return r4
        L16:
            int r1 = r1 + (-1)
            goto Lc
        L19:
            int r0 = r8 << r1
            int r1 = r0 + (-1)
            r11 = r11 & r1
            if (r11 == r1) goto L15
            r12 = r12 & r1
            int r6 = r11 + 1
            int r6 = 64 - r6
            long r2 = r4 >>> r6
            long r6 = r2 >>> r12
            int r8 = r0 - r12
            long r8 = r2 << r8
            long r2 = r6 | r8
            r4 = r2
            r12 = r0
        L31:
            r6 = 64
            if (r12 >= r6) goto L15
            long r6 = r2 << r12
            long r4 = r4 | r6
            int r12 = r12 + r0
            goto L31
        */
        throw new UnsupportedOperationException("Method not decompiled: android.ext.Arm64Dis.disasm_dbm(int, int, int):long");
    }

    public static int[] args() {
        return new int[8];
    }

    /* JADX WARN: Removed duplicated region for block: B:1336:0x4acf  */
    /* JADX WARN: Removed duplicated region for block: B:1344:0x4aee  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.StringBuilder disasm(int[] r75, long r76, int r78, java.lang.StringBuilder r79) {
        /*
            Method dump skipped, instruction units count: 27642
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.ext.Arm64Dis.disasm(int[], long, int, java.lang.StringBuilder):java.lang.StringBuilder");
    }

    private static void sprintf_x(StringBuilder str, int val) {
        if (val < 0 && val >= -268435455) {
            val = -val;
            str.append('-');
        }
        str.append("0x");
        str.append(Integer.toHexString(val).toUpperCase(Locale.US));
    }

    private static void sprintf_x(StringBuilder str, long val) {
        if (val < 0 && val >= -1152921504606846975L) {
            val = -val;
            str.append('-');
        }
        str.append("0x");
        str.append(Long.toHexString(val).toUpperCase(Locale.US));
    }
}
