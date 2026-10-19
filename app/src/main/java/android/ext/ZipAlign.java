package android.ext;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/* JADX INFO: loaded from: classes.dex */
public class ZipAlign {
    public static void fixFile(File apk) throws Throwable {
        if (apk == null) {
            return;
        }
        try (RandomAccessFile raf = new RandomAccessFile(apk, "rw")) {
            long length = raf.length();
            if (length == 0) {
                length = apk.length();
            }
            if (length < 22) {
                Log.w("ZipAlign small size: " + length + ' ' + apk.getAbsolutePath());
            }
        } catch (Throwable e) {
            Log.e("ZipAlign fail", e);
        }
    }

    private static short readShort(RandomAccessFile raf) throws IOException {
        int ch1 = raf.read();
        int ch2 = raf.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (short) ((ch2 << 8) + (ch1 << 0));
    }

    public static int readInt(RandomAccessFile raf) throws IOException {
        int ch1 = raf.read();
        int ch2 = raf.read();
        int ch3 = raf.read();
        int ch4 = raf.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
    }
}
