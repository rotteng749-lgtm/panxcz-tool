package android.ext;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/* JADX INFO: loaded from: classes.dex */
public class Hash {
    public static final String toString(MessageDigest digest) {
        byte[] bytes = digest.digest();
        byte[] out = new byte[8];
        System.arraycopy(bytes, 0, out, 0, 4);
        System.arraycopy(bytes, bytes.length - 4, out, 4, 4);
        return Tools.byteArrayToHexString(out);
    }

    public static final String hash(Object o, String alg) {
        try {
            MessageDigest digest = MessageDigest.getInstance(alg);
            if (o instanceof File) {
                File f = (File) o;
                FileInputStream fis = new FileInputStream(f);
                byte[] dataBytes = new byte[1024];
                while (true) {
                    int nread = fis.read(dataBytes);
                    if (nread == -1) {
                        break;
                    }
                    digest.update(dataBytes, 0, nread);
                }
                fis.close();
            } else {
                digest.update(o.toString().getBytes());
            }
            return toString(digest);
        } catch (Throwable e) {
            Log.e("hash fail", e);
            String error = e.toString();
            return error;
        }
    }
}
