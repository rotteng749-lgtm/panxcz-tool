package android.ext;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.PrivateKey;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

/* JADX INFO: loaded from: classes.dex */
public class ZipSignature {
    byte[] beforeAlgorithmIdBytes = {Message.CMD_CS_SET_TEMP_PATH, Message.CMD_CS_LOAD_RESULTS};
    byte[] algorithmIdBytes = {Message.CMD_CS_SET_TEMP_PATH, 9, 6, 5, Message.CMD_CS_ALTER, 14, 3, 2, Message.CMD_SC_MEM, 5, 0};
    byte[] afterAlgorithmIdBytes = {4, Message.CMD_SC_MEMORY_ITEM};
    MessageDigest md;
    Cipher cipher;

    public ZipSignature() {
        try {
            this.md = MessageDigest.getInstance("SHA1");
            this.cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void initSign(PrivateKey privateKey) throws InvalidKeyException {
        this.cipher.init(1, privateKey);
    }

    public void update(byte[] data) {
        this.md.update(data);
    }

    public void update(byte[] data, int offset, int count) {
        this.md.update(data, offset, count);
    }

    public byte[] sign() throws BadPaddingException, IllegalBlockSizeException {
        this.cipher.update(this.beforeAlgorithmIdBytes);
        this.cipher.update(this.algorithmIdBytes);
        this.cipher.update(this.afterAlgorithmIdBytes);
        this.cipher.update(this.md.digest());
        return this.cipher.doFinal();
    }
}
