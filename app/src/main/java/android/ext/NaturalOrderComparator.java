package android.ext;

/* JADX INFO: loaded from: classes.dex */
public class NaturalOrderComparator {
    private static int compareRight(String a, String b) {
        int bias = 0;
        int ia = 0;
        int ib = 0;
        int lena = a.length();
        int lenb = b.length();
        while (true) {
            char ca = ia >= lena ? (char) 0 : a.charAt(ia);
            char cb = ib >= lenb ? (char) 0 : b.charAt(ib);
            boolean isNotDigitA = ca > '9' || '0' > ca;
            boolean isNotDigitB = cb > '9' || '0' > cb;
            if (!isNotDigitA || !isNotDigitB) {
                if (isNotDigitA) {
                    return -1;
                }
                if (isNotDigitB) {
                    return 1;
                }
                if (ca == 0 && cb == 0) {
                    return bias;
                }
                if (bias == 0) {
                    if (ca < cb) {
                        bias = -1;
                    } else if (ca > cb) {
                        bias = 1;
                    }
                }
                ia++;
                ib++;
            } else {
                return bias;
            }
        }
    }

    public static int compare(Object o1, Object o2) {
        int bias;
        String a = o1.toString();
        String b = o2.toString();
        int ia = 0;
        int ib = 0;
        int lena = a.length();
        int lenb = b.length();
        while (true) {
            int nzb = 0;
            int nza = 0;
            char ca = ia >= lena ? (char) 0 : a.charAt(ia);
            char cb = ib >= lenb ? (char) 0 : b.charAt(ib);
            while (true) {
                if (ca != ' ' && ca != '0') {
                    break;
                }
                if (ca == '0') {
                    nza++;
                } else {
                    nza = 0;
                }
                ia++;
                ca = ia >= lena ? (char) 0 : a.charAt(ia);
            }
            while (true) {
                if (cb != ' ' && cb != '0') {
                    break;
                }
                if (cb == '0') {
                    nzb++;
                } else {
                    nzb = 0;
                }
                ib++;
                cb = ib >= lenb ? (char) 0 : b.charAt(ib);
            }
            if (ca > '9' || cb > '9' || '0' > ca || '0' > cb || (bias = compareRight(a.substring(ia), b.substring(ib))) == 0) {
                if (ca == 0 && cb == 0) {
                    return nza - nzb;
                }
                if (ca < cb) {
                    return -1;
                }
                if (ca > cb) {
                    return 1;
                }
                ia++;
                ib++;
            } else {
                return bias;
            }
        }
    }

    static char charAt(String s, int len, int i) {
        if (i >= len) {
            return (char) 0;
        }
        return s.charAt(i);
    }
}
