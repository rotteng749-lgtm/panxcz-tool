package android.ext;

import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class Ls {
    public static void main(String[] args) {
        File file = new File(args[0]);
        File[] files = file.listFiles();
        if (files != null) {
            for (File item : files) {
                if (item != null) {
                    android.util.Log.i("Ls", item.getAbsolutePath());
                }
            }
        }
    }
}
