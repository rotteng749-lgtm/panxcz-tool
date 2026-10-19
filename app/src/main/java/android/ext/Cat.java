package android.ext;

import java.io.FileInputStream;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public class Cat {
    public static void main(String[] args) throws Exception {
        String file = args[0];
        byte[] buf = new byte[8192];
        InputStream is = new FileInputStream(file);
        while (true) {
            int read = is.read(buf);
            if (read >= 0) {
                System.out.write(buf, 0, read);
            } else {
                is.close();
                return;
            }
        }
    }
}
