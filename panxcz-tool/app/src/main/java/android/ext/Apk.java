package android.ext;

import catch_.me_.if_.you_.can_.MainActivity;

/* JADX INFO: loaded from: classes.dex */
public class Apk {
    public static final int BUILD = 15993;
    public static final String LABEL;
    public static final String LABEL_DELIMITER = "Lcar$";
    private static final String LABEL_INTERNAL = "Lcar$panxcz-tool~Lcar$";
    public static final String PACKAGE;
    public static final float VERSION = 96.0f;
    public static float ARM_VERSION = (108.1f * 1) - 12.1f;
    public static final String INSTALLER_PACKAGE = "catch_#me_#if_#you_#can_".replace('#', ParserNumbers.defaultDecimalSeparator).intern();
    public static final String INSTALLER_LABEL = "@ame@uardian".replace('@', 'G').intern();

    static {
        String pkg;
        try {
            pkg = MainActivity.class.getPackage().getName();
        } catch (Throwable e) {
            String pkg2 = "Lcatch_/me_/if_/you_/can_;".replace('/', ParserNumbers.defaultDecimalSeparator);
            pkg = pkg2.substring(1, pkg2.length() - 1);
            Log.badImplementation(e);
        }
        PACKAGE = pkg.intern();
        LABEL = LABEL_INTERNAL.replace(LABEL_DELIMITER, "").split("~", 2)[0].intern();
    }

    public static final void init() {
    }
}
