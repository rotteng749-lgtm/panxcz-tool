package android.ext;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* JADX INFO: loaded from: classes.dex */
public class BufferReader {
    private final ByteBuffer buffer;
    private final byte[] data;

    public BufferReader(byte[] data) {
        this.data = data;
        this.buffer = ByteBuffer.wrap(data);
        this.buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public byte readByte() throws IOException {
        return this.buffer.get();
    }

    public int readInt() throws IOException {
        return this.buffer.getInt();
    }

    public long readLong() throws IOException {
        return InOut.x64 ? this.buffer.getLong() : ((long) readInt()) & ParserNumbers.MAX_UINT;
    }

    public long readLongLong() throws IOException {
        return this.buffer.getLong();
    }

    public void read(byte[] arr) throws IOException {
        this.buffer.get(arr);
    }

    public void read(byte[] arr, int dstOffset, int byteCount) throws IOException {
        this.buffer.get(arr, dstOffset, byteCount);
    }

    public String readString(int count, String old) throws IOException {
        if (count == 0) {
            return "";
        }
        int pos = this.buffer.position();
        String ret = null;
        byte[] data = this.data;
        if (old != null && old.length() == count) {
            boolean same = true;
            int i = 0;
            while (true) {
                if (i >= count) {
                    break;
                }
                if (old.charAt(i) == data[pos + i]) {
                    i++;
                } else {
                    same = false;
                    break;
                }
            }
            if (same) {
                ret = old;
            }
        }
        if (ret == null) {
            ret = new String(data, pos, count);
        }
        this.buffer.position(pos + count);
        return ret;
    }

    public void skip(int count) {
        this.buffer.position(this.buffer.position() + count);
    }

    public int size() {
        return this.buffer.capacity();
    }

    public int getPosition() {
        return this.buffer.position();
    }

    public void reset() {
        this.buffer.position(2);
    }

    public byte getSeq() {
        int old = this.buffer.position();
        this.buffer.position(1);
        byte ret = -1;
        try {
            ret = readByte();
        } catch (IOException e) {
            Log.e("getSeq fail", e);
        }
        this.buffer.position(old);
        return ret;
    }

    // #region debug-point
    public byte[] copyRange(int start, int count) {
        int size = size();
        if (start < 0) {
            start = 0;
        }
        if (start > size) {
            start = size;
        }
        if (count < 0) {
            count = 0;
        }
        if (start + count > size) {
            count = size - start;
        }
        byte[] out = new byte[count];
        System.arraycopy(this.data, start, out, 0, count);
        return out;
    }

    public static String toHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        char[] hex = "0123456789abcdef".toCharArray();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            int v = b & 255;
            sb.append(hex[v >>> 4]);
            sb.append(hex[v & 15]);
        }
        return sb.toString();
    }
    // #endregion debug-point
}
