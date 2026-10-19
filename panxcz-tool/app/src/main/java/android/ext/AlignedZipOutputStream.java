package android.ext;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

/* JADX INFO: loaded from: classes.dex */
public class AlignedZipOutputStream extends ZipOutputStream {
    private static final int ALIGN_BASE = 4;
    private byte[] buf;
    private int bytes;
    private CountedOutputStream cos;
    private final CRC32 crc;
    private ZipEntry current;
    private boolean entryOpen;

    public AlignedZipOutputStream(OutputStream os) {
        super(new CountedOutputStream(new BufferedOutputStream(os, 65536)));
        this.entryOpen = false;
        this.current = null;
        this.crc = new CRC32();
        this.bytes = 0;
        this.buf = null;
        this.cos = CountedOutputStream.lastInstance;
        setComment("");
    }

    @Override // java.util.zip.ZipOutputStream
    public void setComment(String comment) {
        super.setComment("");
    }

    @Override // java.util.zip.ZipOutputStream
    public void putNextEntry(ZipEntry ze) throws IOException {
        ze.setComment("");
        this.current = ze;
        if (this.entryOpen) {
            closeEntry();
        }
        if (ze.getMethod() == 0) {
            flush();
            int offset = this.cos.length + 30 + ze.getName().length();
            ze.setExtra(new byte[(4 - (offset % 4)) % 4]);
            ze.setCompressedSize(ze.getSize());
        }
        super.putNextEntry(ze);
        this.entryOpen = true;
        this.crc.reset();
        this.bytes = 0;
    }

    @Override // java.util.zip.ZipOutputStream
    public void closeEntry() throws IOException {
        try {
            if (this.current != null && this.current.getMethod() == 0) {
                if (this.current.getCrc() != this.crc.getValue()) {
                    Log.w("CRC mismatch: " + toString(this.current) + " != " + this.crc.getValue());
                    this.current.setCrc(this.crc.getValue());
                }
                if (this.current.getSize() != this.bytes) {
                    Log.w("Size mismatch: " + toString(this.current) + " != " + this.bytes);
                    this.current.setSize(this.bytes);
                }
                if (this.current.getCompressedSize() != this.current.getSize()) {
                    Log.w("CompressedSize mismatch: " + toString(this.current) + " != " + this.current.getSize());
                    this.current.setCompressedSize(this.current.getSize());
                }
            }
            super.closeEntry();
            this.entryOpen = false;
            this.current = null;
            this.crc.reset();
            this.bytes = 0;
        } catch (ZipException e) {
            throw new RuntimeException("Failed close entry: " + toString(this.current), e);
        }
    }

    public static String toString(ZipEntry ze) {
        return ze == null ? "null" : String.valueOf(ze.getName()) + ", method: " + ze.getMethod() + ", crc: 0x" + Long.toHexString(ze.getCrc()) + ", size: " + ze.getSize() + ", compressed size: " + ze.getCompressedSize();
    }

    @Override // java.util.zip.ZipOutputStream, java.util.zip.DeflaterOutputStream, java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] buffer, int offset, int byteCount) throws IOException {
        super.write(buffer, offset, byteCount);
        this.crc.update(buffer, offset, byteCount);
        this.bytes += byteCount;
    }

    @Override // java.util.zip.DeflaterOutputStream, java.io.FilterOutputStream, java.io.OutputStream
    public void write(int i) throws IOException {
        byte[] buf = this.buf;
        if (buf == null) {
            buf = new byte[1];
            this.buf = buf;
        }
        buf[0] = (byte) (i & 255);
        write(buf, 0, 1);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] buffer) throws IOException {
        write(buffer, 0, buffer.length);
    }

    private static class CountedOutputStream extends OutputStream {
        static CountedOutputStream lastInstance;
        int length = 0;
        private OutputStream os;

        public CountedOutputStream(OutputStream os) {
            this.os = os;
            lastInstance = this;
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.os.close();
        }

        @Override // java.io.OutputStream, java.io.Flushable
        public void flush() throws IOException {
            this.os.flush();
        }

        @Override // java.io.OutputStream
        public void write(byte[] buffer) throws IOException {
            this.os.write(buffer);
            this.length += buffer.length;
        }

        @Override // java.io.OutputStream
        public void write(byte[] buffer, int offset, int count) throws IOException {
            this.os.write(buffer, offset, count);
            this.length += count;
        }

        @Override // java.io.OutputStream
        public void write(int oneByte) throws IOException {
            this.os.write(oneByte);
            this.length++;
        }

        public boolean equals(Object o) {
            return this.os.equals(o);
        }

        public int hashCode() {
            return this.os.hashCode();
        }

        public String toString() {
            return this.os.toString();
        }
    }
}
