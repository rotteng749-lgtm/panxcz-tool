package android.ext;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class CharSequenceBuilder {
    private final List<CharSequence> array = new ArrayList();

    public CharSequenceBuilder append(CharSequence cs) {
        this.array.add(cs);
        return this;
    }

    public CharSequenceBuilder appendJoin(CharSequence delim, CharSequence[] arr) {
        int s = arr.length;
        for (int i = 0; i < s; i++) {
            if (i != 0) {
                this.array.add(delim);
            }
            this.array.add(arr[i]);
        }
        return this;
    }

    public CharSequenceBuilder appendFormat(String format, CharSequence... arr) {
        int offset = 0;
        int i = 0;
        while (true) {
            int start = format.indexOf("%s", offset);
            if (start < 0) {
                this.array.add(format.substring(offset, format.length()));
                return this;
            }
            this.array.add(format.substring(offset, start));
            this.array.add(arr[i]);
            offset = start + 2;
            i++;
        }
    }

    public CharSequence toCharSequence() {
        return Tools.concat((CharSequence[]) this.array.toArray(new CharSequence[this.array.size()]));
    }

    public int size() {
        return this.array.size();
    }
}
