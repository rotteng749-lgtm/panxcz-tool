package android.lang;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public final class ProcessImpl {
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !ProcessImpl.class.desiredAssertionStatus();
    }

    public static int getFD(FileDescriptor fileDescriptor) {
        try {
            Field descriptor = FileDescriptor.class.getDeclaredField("descriptor");
            descriptor.setAccessible(true);
            return ((Integer) descriptor.get(fileDescriptor)).intValue();
        } catch (Throwable e) {
            throw new RuntimeException("Failed get fd", e);
        }
    }

    private ProcessImpl() {
    }

    private static byte[] toCString(String s) {
        if (s == null) {
            return null;
        }
        byte[] bytes = s.getBytes();
        byte[] result = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, result, 0, bytes.length);
        result[result.length - 1] = 0;
        return result;
    }

    static Process start(String[] cmdarray, Map<String, String> environment, String dir, boolean redirectErrorStream) throws IOException {
        if (!$assertionsDisabled && (cmdarray == null || cmdarray.length <= 0)) {
            throw new AssertionError();
        }
        byte[][] args = new byte[cmdarray.length - 1][];
        int size = args.length;
        for (int i = 0; i < args.length; i++) {
            args[i] = cmdarray[i + 1].getBytes();
            size += args[i].length;
        }
        byte[] argBlock = new byte[size];
        int i2 = 0;
        for (byte[] arg : args) {
            System.arraycopy(arg, 0, argBlock, i2, arg.length);
            i2 += arg.length + 1;
        }
        int[] envc = new int[1];
        byte[] envBlock = ProcessEnvironment.toEnvironmentBlock(environment, envc);
        return new UNIXProcess(toCString(cmdarray[0]), argBlock, args.length, envBlock, envc[0], toCString(dir), new int[]{-1, -1, -1}, redirectErrorStream);
    }
}
