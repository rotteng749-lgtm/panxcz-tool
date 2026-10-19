package android.ext;

import android.widget.SectionIndexer;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseAdapterIndexer extends BaseAdapterLC implements SectionIndexer {
    public static final int SECTIONS_COUNT = 20;

    @Override // android.widget.SectionIndexer
    public Object[] getSections() {
        Object[] ret = new Object[20];
        double sectionSize = ((double) getCount()) / 20.0d;
        for (int i = 0; i < 20; i++) {
            ret[i] = Integer.valueOf((int) (((double) i) * sectionSize));
        }
        return ret;
    }

    @Override // android.widget.SectionIndexer
    public int getPositionForSection(int sectionIndex) {
        int count = getCount();
        double sectionSize = ((double) count) / 20.0d;
        int ret = (int) (((double) sectionIndex) * sectionSize);
        if (ret < 0) {
            ret = 0;
        }
        return ret >= count ? count - 1 : ret;
    }

    @Override // android.widget.SectionIndexer
    public int getSectionForPosition(int position) {
        int count = getCount();
        double sectionSize = ((double) count) / 20.0d;
        int ret = (int) (((double) position) / sectionSize);
        if (ret < 0) {
            ret = 0;
        }
        if (ret >= 20) {
            return 19;
        }
        return ret;
    }
}
