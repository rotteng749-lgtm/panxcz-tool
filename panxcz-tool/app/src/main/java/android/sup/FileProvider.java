package android.sup;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.ext.AddressItem;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import java.io.File;
import java.io.FileNotFoundException;

/* JADX INFO: loaded from: classes.dex */
public class FileProvider extends ContentProvider {
    private static final String[] COLUMNS = {"_display_name", "_size"};

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return true;
    }

    @Override // android.content.ContentProvider
    public void attachInfo(Context context, ProviderInfo info) {
        super.attachInfo(context, info);
        if (info.exported) {
            throw new SecurityException("Provider must not be exported");
        }
        if (!info.grantUriPermissions) {
            throw new SecurityException("Provider must grant uri permissions");
        }
    }

    public static Uri getUriForFile(Context context, String authority, File file) {
        return new Uri.Builder().scheme("content").authority(authority).encodedPath(file.getAbsolutePath()).build();
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int i;
        File file = getApk(uri);
        if (projection == null) {
            projection = COLUMNS;
        }
        String[] cols = new String[projection.length];
        Object[] values = new Object[projection.length];
        int length = projection.length;
        int i2 = 0;
        int i3 = 0;
        while (i2 < length) {
            String col = projection[i2];
            if ("_display_name".equals(col)) {
                cols[i3] = "_display_name";
                i = i3 + 1;
                values[i3] = file.getName();
            } else if ("_size".equals(col)) {
                cols[i3] = "_size";
                i = i3 + 1;
                values[i3] = Long.valueOf(file.length());
            } else {
                i = i3;
            }
            i2++;
            i3 = i;
        }
        String[] cols2 = copyOf(cols, i3);
        Object[] values2 = copyOf(values, i3);
        MatrixCursor cursor = new MatrixCursor(cols2, 1);
        cursor.addRow(values2);
        return cursor;
    }

    private static File getApk(Uri uri) {
        String path = uri.getPath();
        if (path == null || !path.endsWith(".apk")) {
            File apk = new File("/wrong_path/" + path);
            return apk;
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        File apk2 = new File(path);
        return apk2;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return "application/vnd.android.package-archive";
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("No external inserts");
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("No external updates");
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        return ParcelFileDescriptor.open(getApk(uri), AddressItem.FLAG_VALUE_NOT_EQUAL);
    }

    private static String[] copyOf(String[] original, int newLength) {
        String[] result = new String[newLength];
        System.arraycopy(original, 0, result, 0, newLength);
        return result;
    }

    private static Object[] copyOf(Object[] original, int newLength) {
        Object[] result = new Object[newLength];
        System.arraycopy(original, 0, result, 0, newLength);
        return result;
    }
}
