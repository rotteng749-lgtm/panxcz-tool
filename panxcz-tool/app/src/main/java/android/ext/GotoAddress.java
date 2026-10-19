package android.ext;

/* JADX INFO: compiled from: MemoryContentAdapter.java */
/* JADX INFO: loaded from: classes.dex */
class GotoAddress {
    long address;
    long source;

    public GotoAddress(long address, long source) {
        this.address = address;
        this.source = source;
    }

    public String getAddress() {
        return AddressItem.getStringAddress(this.address, 32);
    }

    public long getSource() {
        return this.source;
    }
}
