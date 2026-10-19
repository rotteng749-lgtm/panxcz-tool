package android.ext;

import android.content.SharedPreferences;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class ListSpeed {
    private static final String EN_DEFAULT = "0.000000001;0.00000001;0.0000001;0.000001;0.00001;0.0001;0.001;0.002;0.005;0.01;0.02;0.05;0.1;0.2;0.5;0.6;0.75;0.9;1;1.2;1.3;1.5;2;3;4;5;6;9;12;15;20;30;60;120;180;300;600;1200;2400;3600";
    private static final String LIST_SPEEDS = "list-speeds";
    public static final double MAX = 1.0E9d;
    public static final double MIN = 1.0E-9d;
    private int currentIndex = 0;
    private double currentSpeed = 1.0d;
    double[] list = {1.0d};

    public ListSpeed() {
        SharedPreferences storage = Tools.getSharedPreferences();
        String loaded = storage.getString(LIST_SPEEDS, EN_DEFAULT);
        String localeConfig = ParserNumbers.fixSeparatorsToLocale(loaded);
        set(localeConfig, false);
    }

    public static String getDefault() {
        return ParserNumbers.fixSeparatorsToLocale(EN_DEFAULT);
    }

    public void setSpeedOne() {
        setCloseSpeed(1.0d);
    }

    public void setCloseSpeed(double val) {
        indexToValue(val);
    }

    private void indexToValue(double val) {
        int found = 0;
        double best = Double.MAX_VALUE;
        for (int i = 0; i < this.list.length; i++) {
            double diff = Math.abs(val - this.list[i]);
            if (diff < best) {
                found = i;
                best = diff;
            }
        }
        this.currentIndex = found;
        this.currentSpeed = this.list[found];
    }

    public double getSpeed() {
        return this.currentSpeed;
    }

    public void toNext() {
        updateIndex(1);
    }

    public void toPrev() {
        updateIndex(-1);
    }

    private void updateIndex(int diff) {
        int index = this.currentIndex + diff;
        if (index < 0) {
            index = 0;
        }
        if (index >= this.list.length) {
            index = this.list.length - 1;
        }
        this.currentIndex = index;
        this.currentSpeed = this.list[index];
    }

    public String get() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (double speed : this.list) {
            if (first) {
                first = false;
            } else {
                sb.append(';');
            }
            sb.append(format(speed));
        }
        String out = sb.toString();
        return out;
    }

    public static String format(double speed) {
        int digit = 9 - Math.min(Math.max((int) Math.log10(speed), 0), 9);
        String pat = "%,." + digit + 'f';
        String ret = Tools.stringFormat(pat, Double.valueOf(speed));
        String rep = "[" + ParserNumbers.decimalSeparator + "]?0*$";
        if (digit == 0) {
            return ret;
        }
        String out = ret.replaceAll(rep, "");
        return out;
    }

    public void set(String localeConfig) {
        set(localeConfig, true);
    }

    private void set(String localeConfig, boolean save) {
        int used;
        int used2;
        if (localeConfig.length() == 0) {
            localeConfig = getDefault();
        }
        String[] speeds = localeConfig.split(";");
        double[] list = new double[speeds.length + 1];
        int used3 = 0;
        boolean haveOne = false;
        int length = speeds.length;
        int i = 0;
        while (true) {
            used = used3;
            if (i >= length) {
                break;
            }
            String speed = speeds[i];
            try {
                Double value = Double.valueOf(Tools.parseTime(speed));
                if (value.doubleValue() < 1.0E-9d || value.doubleValue() > 1.0E9d) {
                    Log.w("Speed outside range: " + value + " (1.0E-9" + ListManager.TEXT_SEPARATOR + "1.0E9)");
                    used3 = used;
                } else {
                    if (!haveOne && value.doubleValue() == 1.0d) {
                        haveOne = true;
                    }
                    used3 = used + 1;
                    list[used] = value.doubleValue();
                }
            } catch (NumberFormatException e) {
                Log.w("Failed parse speed: '" + speed + SearchMenuItem.HEX_UTF_8, e);
                used3 = used;
            }
            i++;
        }
        if (haveOne) {
            used2 = used;
        } else {
            used2 = used + 1;
            list[used] = 1.0d;
        }
        if (list.length > used2) {
            list = Arrays.copyOf(list, used2);
        }
        if (Config.speedsParams != 0) {
            Arrays.sort(list);
            if (Config.speedsParams == 2) {
                double prev = 0.0d;
                int used4 = 0;
                for (int i2 = 0; i2 < list.length; i2++) {
                    double value2 = list[i2];
                    if (i2 > 0 && value2 == prev) {
                        Log.w("Speed duplicate: " + value2 + " (" + i2 + ')');
                    } else {
                        if (i2 != used4) {
                            list[used4] = value2;
                        }
                        used4++;
                        prev = value2;
                    }
                }
                if (list.length > used4) {
                    list = Arrays.copyOf(list, used4);
                }
            }
        }
        this.list = list;
        indexToValue(this.currentSpeed);
        if (save) {
            String enConfig = ParserNumbers.fixSeparators(get());
            new SPEditor().putString(LIST_SPEEDS, enConfig, EN_DEFAULT).commit();
        }
    }

    public static class MN {
        final int M;
        final int N;

        public MN(int m, int n) {
            this.M = m;
            this.N = n;
        }
    }

    public static MN getMN(double speed) {
        int dig = (int) Math.ceil(Math.log10(speed));
        int N = (int) (2.0d * Math.pow(10.0d, 9 - Math.max(0, dig)));
        int M = (int) (((double) N) * speed);
        if (M == 0) {
            M = 1;
        }
        int gcd = Tools.gcd(M, N);
        return new MN(M / gcd, N / gcd);
    }
}
