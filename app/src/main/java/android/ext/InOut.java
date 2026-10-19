package android.ext;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.CRC32;

/* JADX INFO: loaded from: classes.dex */
public class InOut {
    private static final int BUFFER_LEN = 10485760;
    private static final int CURRENT_INC = 1024;
    private static final int MAX_SIZE_WAIT = 40;
    public static final int QE_ALIGN = 4;
    public static volatile int byteOrderMask = 15;
    static boolean fifo;
    public static volatile int longSize;
    public static volatile boolean x64;
    private byte[] current;
    private int currentUsed;
    private volatile boolean started;
    private boolean truncate;
    private final ArrayList<byte[]> wait;
    private ByteBuffer writeBuffer;
    private byte[] writeData;
    private CRC32 crc = null;
    private InputStream in = null;
    private OutputStream out = null;
    private byte[] readData = new byte[4];
    private ByteBuffer readBuffer = ByteBuffer.wrap(this.readData);

    static {
        byteOrderMask = 15;
        boolean isX64 = false;
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            try {
                String[] abis = android.os.Build.SUPPORTED_64_BIT_ABIS;
                isX64 = abis != null && abis.length > 0;
            } catch (Throwable e) {
                Log.badImplementation(e);
            }
        }
        setX64(isX64);
        fifo = false;
    }

    static void setX64(boolean value) {
        x64 = value;
        longSize = value ? 8 : 4;
    }

    public InOut() {
        this.readBuffer.order(ByteOrder.LITTLE_ENDIAN);
        this.writeData = new byte[8];
        this.writeBuffer = ByteBuffer.wrap(this.writeData);
        this.writeBuffer.order(ByteOrder.LITTLE_ENDIAN);
        this.current = new byte[1024];
        this.wait = new ArrayList<>();
        this.started = false;
    }

    public void setStreams(InputStream in, OutputStream out) {
        InputStream in2;
        InputStream in3 = new BufferedInputStream(in, 65536);
        if (fifo) {
            in2 = in3;
        } else {
            OutputStream out2 = new EncodeOutputStream(out);
            in2 = new DecodeInputStream(in3);
            out = out2;
        }
        this.in = in2;
        this.out = out;
    }

    private static class DecodeInputStream extends InputStream {
        private static final int EMPTY = -2017;
        private static final int last = 112;
        private final InputStream base;
        private int prev = EMPTY;

        public DecodeInputStream(InputStream base) {
            this.base = base;
        }

        /* JADX WARN: Removed duplicated region for block: B:13:0x0024 A[PHI: r4
  0x0024: PHI (r4v3 'prev' int) = (r4v2 'prev' int), (r4v7 'prev' int) binds: [B:9:0x0017, B:11:0x001c] A[DONT_GENERATE, DONT_INLINE]] */
        @Override // java.io.InputStream
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public int read(byte[] r12, int r13, int r14) throws java.io.IOException {
            /*
                r11 = this;
                r10 = 112(0x70, float:1.57E-43)
                r9 = 97
                r8 = -1
                r5 = r13
                int r3 = r13 + r14
                int r4 = r11.prev
                java.io.InputStream r0 = r11.base
            Lc:
                if (r5 < r3) goto L15
                r11.prev = r4
                r7 = 0
                return r7
            L12:
                r1 = r2
                if (r1 != r8) goto L1e
            L15:
                r7 = -2017(0xfffffffffffff81f, float:NaN)
                if (r4 == r7) goto L24
                r1 = r4
                r4 = -2017(0xfffffffffffff81f, float:NaN)
                if (r1 == r8) goto L24
            L1e:
                byte r7 = (byte) r1
                r12[r5] = r7
                int r5 = r5 + 1
                goto Lc
            L24:
                int r2 = r0.read()
                if (r2 < r9) goto L12
                if (r2 > r10) goto L12
                int r1 = r2 + (-97)
                int r6 = r0.read()
                if (r6 < r9) goto L3c
                if (r6 > r10) goto L3c
                int r7 = r6 + (-97)
                int r7 = r7 * 16
                int r1 = r1 + r7
                goto L1e
            L3c:
                r4 = r6
                r1 = r2
                if (r1 != r8) goto L1e
                goto L15
            */
            throw new UnsupportedOperationException("Method not decompiled: android.ext.InOut.DecodeInputStream.read(byte[], int, int):int");
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            int prev = this.prev;
            if (prev != EMPTY) {
                this.prev = EMPTY;
                return prev;
            }
            InputStream base = this.base;
            int first = base.read();
            if (first >= 97 && first <= last) {
                int ch = first - 97;
                int second = base.read();
                if (second >= 97 && second <= last) {
                    return ch + ((second - 97) * 16);
                }
                this.prev = second;
                return first;
            }
            return first;
        }
    }

    private static class EncodeOutputStream extends OutputStream {
        private final OutputStream base;

        public EncodeOutputStream(OutputStream base) {
            this.base = base;
        }

        private int getChar(int hex) {
            if (hex >= 0 && hex <= 15) {
                return hex + 97;
            }
            return hex;
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            this.base.write(getChar(b & 15));
            this.base.write(getChar((b & 240) >> 4));
        }

        private byte[] getBuf(byte[] b, int off, int len) {
            byte[] send = new byte[(len * 2) + 4];
            send[0] = Message.CMD_CS_GET_MEMORY;
            send[1] = Message.CMD_CS_GET_MEMORY;
            for (int i = 0; i < len; i++) {
                byte ch = b[off + i];
                send[(i * 2) + 2 + 0] = (byte) getChar(ch & 15);
                send[(i * 2) + 2 + 1] = (byte) getChar((ch & 240) >> 4);
            }
            send[(len * 2) + 2 + 0] = 10;
            send[(len * 2) + 2 + 1] = 10;
            return send;
        }

        @Override // java.io.OutputStream
        public void write(byte[] b) throws IOException {
            this.base.write(getBuf(b, 0, b.length));
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            this.base.write(getBuf(b, off, len));
        }

        @Override // java.io.OutputStream, java.io.Flushable
        public void flush() throws IOException {
            this.base.flush();
        }
    }

    public static String dump(byte[] data, int start, int length) {
        if (data.length - 1 < start) {
            Log.w("dump failed: " + data.length + ' ' + start + ' ' + length);
            return "";
        }
        if (data.length < start + length) {
            Log.w("dump fix len: " + data.length + ' ' + start + ' ' + length);
            length = data.length - start;
        }
        int index = start;
        StringBuilder out = new StringBuilder();
        while (index < length) {
            int count = length - index;
            if (count > 32) {
                count = 32;
            }
            out.append(bytesToHex(data, start + index, count));
            out.append(SearchMenuItem.HEX_UTF_8);
            out.append(new String(data, start + index, count));
            out.append("'\n");
            index += count;
        }
        return out.toString();
    }

    public byte[] readPacket() throws IOException {
        byte[] skip = null;
        int skipped = 0;
        while (true) {
            byte b = readByte();
            if (b == 2) {
                break;
            }
            if (skip != null || (b != 10 && b != 59)) {
                if (skip == null) {
                    skip = new byte[4096];
                }
                if (skipped < skip.length) {
                    skip[skipped] = b;
                }
                skipped++;
                if (skipped >= skip.length || skipped % 10 == 0) {
                    Log.d("Bad input from daemon: " + ((int) b) + '\n' + dump(skip, 0, skipped));
                }
            }
        }
        if (skip != null) {
            Log.d("Bad input from daemon 2: " + skipped + '\n' + dump(skip, 0, skipped));
        }
        int len = readInt();
        if (len > BUFFER_LEN || len < 2) {
            Log.d("Bad len from daemon: " + len + ' ' + dump(this.readData, 0, 4));
            return null;
        }
        byte[] buffer = new byte[len];
        int read = 0;
        while (true) {
            if (read >= len) {
                break;
            }
            int actual = this.in.read(buffer, read, len - read);
            if (actual <= 0) {
                if (read == 0) {
                    read = -1;
                }
            } else {
                read += actual;
            }
        }
        if (buffer[len - 1] != 3) {
            Log.d("Bad end from daemon: " + ((int) buffer[len - 1]) + ListManager.TEXT_SEPARATOR + ((int) buffer[0]) + ListManager.TEXT_SEPARATOR + (len - 1) + ListManager.TEXT_SEPARATOR + read);
            return null;
        }
        return buffer;
    }

    private byte readByte() throws IOException {
        int data;
        do {
            data = this.in.read();
        } while (data == -1);
        return (byte) data;
    }

    private int readInt() throws IOException {
        int read = 0;
        while (true) {
            if (read >= 4) {
                break;
            }
            int actual = this.in.read(this.readData, read, 4 - read);
            if (actual <= 0) {
                if (read == 0) {
                }
            } else {
                read += actual;
            }
        }
        return this.readBuffer.getInt(0);
    }

    private void checkSize(int size, boolean truncate) {
        byte[] current = this.current;
        int currentUsed = this.currentUsed;
        if (currentUsed + size > current.length || truncate) {
            int newSize = currentUsed + size + 1024;
            byte[] curr = new byte[newSize];
            if (currentUsed > 0) {
                System.arraycopy(current, 0, curr, 0, currentUsed);
            }
            this.current = curr;
        }
    }

    public void truncate() {
        if (this.current.length > 65536) {
            this.truncate = true;
        }
    }

    public void writeBytes(byte[] buffer, int size) {
        checkSize(size, false);
        System.arraycopy(buffer, 0, this.current, this.currentUsed, size);
        this.currentUsed += size;
    }

    public void writeByte(byte value) {
        checkSize(1, false);
        byte[] bArr = this.current;
        int i = this.currentUsed;
        this.currentUsed = i + 1;
        bArr[i] = value;
    }

    public void startMessage(byte seq, byte cmd, int capacity) {
        checkSize(capacity + 2, this.truncate);
        this.truncate = false;
        byte[] current = this.current;
        current[0] = cmd;
        current[1] = seq;
        this.currentUsed = 2;
    }

    public void writeInt(int value) {
        this.writeBuffer.putInt(0, byteOrderMask ^ value);
        writeBytes(this.writeData, 4);
    }

    public void writeLong(long value) {
        if (x64) {
            this.writeBuffer.putLong(0, value);
            writeBytes(this.writeData, 8);
        } else {
            writeInt((int) value);
        }
    }

    public void writeLongLong(long value) {
        this.writeBuffer.putLong(0, value);
        writeBytes(this.writeData, 8);
    }

    public synchronized void send() throws IOException {
        if (!this.started) {
            if (this.currentUsed > 0) {
                synchronized (this.wait) {
                    this.wait.add(Arrays.copyOf(this.current, this.currentUsed));
                    if (this.wait.size() > 40) {
                        this.wait.remove(0);
                    }
                }
            }
        } else {
            sendWait();
            this.out.write(this.current, 0, this.currentUsed);
            this.out.flush();
        }
        this.currentUsed = 0;
    }

    public long crc() {
        if (this.currentUsed < 2) {
            return 0L;
        }
        CRC32 crc = this.crc;
        if (crc == null) {
            crc = new CRC32();
            this.crc = crc;
        } else {
            crc.reset();
        }
        crc.update(this.current, 2, this.currentUsed);
        return crc.getValue();
    }

    static String bytesToHex(byte[] bytes, int start, int length) {
        if (bytes.length - 1 < start) {
            Log.w("bytesToHex failed: " + bytes.length + ' ' + start + ' ' + length);
            return "";
        }
        if (bytes.length < start + length) {
            Log.w("bytesToHex fix len: " + bytes.length + ' ' + start + ' ' + length);
            length = bytes.length - start;
        }
        char[] hexChars = new char[length * 3];
        char[] hexArr = HexText.hexArray;
        for (int j = 0; j < length; j++) {
            int v = bytes[start + j] & 255;
            hexChars[(j * 3) + 0] = hexArr[v >>> 4];
            hexChars[(j * 3) + 1] = hexArr[v & 15];
            hexChars[(j * 3) + 2] = ' ';
        }
        return new String(hexChars);
    }

    private void debug(String name, byte[] bytes, int len) {
        Log.d(String.valueOf(name) + ": [" + len + "] " + bytesToHex(bytes, 0, len));
    }

    public void clear() {
        this.currentUsed = 0;
    }

    public void setStarted(boolean value) {
        this.started = value;
    }

    public void sendWait() throws IOException {
        ArrayList<byte[]> wait = this.wait;
        synchronized (wait) {
            if (wait.size() > 0) {
                OutputStream out = this.out;
                for (byte[] data : wait) {
                    out.write(data);
                    out.flush();
                }
                wait.clear();
                wait.trimToSize();
            }
        }
    }

    public boolean isStarted() {
        return this.started;
    }
}
