package lasm;

import android.ext.ListManager;

/* JADX INFO: loaded from: classes.dex */
public class ParseException extends Exception {
    private static final long serialVersionUID = 1;
    public Token currentToken;
    protected String eol;
    public int[][] expectedTokenSequences;
    public String[] tokenImage;

    public ParseException(Token currentTokenVal, int[][] expectedTokenSequencesVal, String[] tokenImageVal) {
        super(initialise(currentTokenVal, expectedTokenSequencesVal, tokenImageVal));
        this.eol = System.getProperty("line.separator", ListManager.NEW_LINE);
        this.currentToken = currentTokenVal;
        this.expectedTokenSequences = expectedTokenSequencesVal;
        this.tokenImage = tokenImageVal;
    }

    public ParseException() {
        this.eol = System.getProperty("line.separator", ListManager.NEW_LINE);
    }

    public ParseException(String message) {
        super(message);
        this.eol = System.getProperty("line.separator", ListManager.NEW_LINE);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
        this.eol = System.getProperty("line.separator", ListManager.NEW_LINE);
    }

    private static String initialise(Token currentToken, int[][] expectedTokenSequences, String[] tokenImage) {
        String retval;
        String eol = System.getProperty("line.separator", ListManager.NEW_LINE);
        StringBuffer expected = new StringBuffer();
        int maxSize = 0;
        for (int i = 0; i < expectedTokenSequences.length; i++) {
            if (maxSize < expectedTokenSequences[i].length) {
                maxSize = expectedTokenSequences[i].length;
            }
            for (int j = 0; j < expectedTokenSequences[i].length; j++) {
                expected.append(tokenImage[expectedTokenSequences[i][j]]).append(' ');
            }
            if (expectedTokenSequences[i][expectedTokenSequences[i].length - 1] != 0) {
                expected.append("...");
            }
            expected.append(eol).append("    ");
        }
        String retval2 = "Encountered \"";
        Token tok = currentToken.next;
        int i2 = 0;
        while (true) {
            if (i2 >= maxSize) {
                break;
            }
            if (i2 != 0) {
                retval2 = String.valueOf(retval2) + " ";
            }
            if (tok.kind == 0) {
                retval2 = String.valueOf(retval2) + tokenImage[0];
                break;
            }
            retval2 = String.valueOf(String.valueOf(String.valueOf(String.valueOf(retval2) + " " + tokenImage[tok.kind]) + " \"") + add_escapes(tok.image)) + " \"";
            tok = tok.next;
            i2++;
        }
        String retval3 = String.valueOf(String.valueOf(retval2) + "\" at line " + currentToken.next.beginLine + ", column " + currentToken.next.beginColumn) + "." + eol;
        if (expectedTokenSequences.length == 1) {
            retval = String.valueOf(retval3) + "Was expecting:" + eol + "    ";
        } else {
            retval = String.valueOf(retval3) + "Was expecting one of:" + eol + "    ";
        }
        return String.valueOf(retval) + expected.toString();
    }

    static String add_escapes(String str) {
        StringBuffer retval = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            switch (str.charAt(i)) {
                case 0:
                    break;
                case '\b':
                    retval.append("\\b");
                    break;
                case '\t':
                    retval.append("\\t");
                    break;
                case '\n':
                    retval.append("\\n");
                    break;
                case '\f':
                    retval.append("\\f");
                    break;
                case '\r':
                    retval.append("\\r");
                    break;
                case '\"':
                    retval.append("\\\"");
                    break;
                case '\'':
                    retval.append("\\'");
                    break;
                case '\\':
                    retval.append("\\\\");
                    break;
                default:
                    char ch = str.charAt(i);
                    if (ch < ' ' || ch > '~') {
                        String s = "0000" + Integer.toString(ch, 16);
                        retval.append("\\u" + s.substring(s.length() - 4, s.length()));
                    } else {
                        retval.append(ch);
                    }
                    break;
            }
        }
        return retval.toString();
    }
}
