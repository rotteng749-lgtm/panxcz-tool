package android.ext;

import java.lang.reflect.Method;
import java.util.Random;

/* JADX INFO: loaded from: classes.dex */
public class JVM {
    static final int RAND_NEXT_BOOLEAN = 15;
    static final int RAND_NEXT_DOUBLE = 17;
    static final int RAND_NEXT_FLOAT = 16;
    static final int RAND_NEXT_GAUSSIAN = 18;
    static final int RAND_NEXT_INT = 12;
    static final int RAND_NEXT_INT_BOUND = 13;
    static final int RAND_NEXT_LONG = 14;
    private static final boolean dbg = false;

    private static native void hook(int i, Method method, Method method2);

    public static native double randD(int i, double d);

    public static native long randQ(int i, long j);

    static {
        android.util.Log.d("AndroidStart", "SJ I");
    }

    public static void load() {
        try {
            android.util.Log.d("AndroidStart", "SJ L");
            RandomImpl.classInit();
            RandomImp.classInit();
            String[] names = {"nextInt", "nextInt", "nextLong", "nextBoolean", "nextFloat", "nextDouble", "nextGaussian"};
            Method[] srcs = new Method[names.length];
            Method[] dsts = new Method[names.length];
            int i = 0;
            while (i < names.length) {
                Class[] cls = i != 1 ? new Class[0] : new Class[]{Integer.TYPE};
                String name = names[i];
                Method src = Random.class.getDeclaredMethod(name, cls);
                Method dst = RandomImpl.class.getDeclaredMethod(name, cls);
                srcs[i] = src;
                dsts[i] = dst;
                i++;
            }
            for (int i2 = names.length - 1; i2 >= 0; i2--) {
                try {
                    hook(i2 + 12, srcs[i2], dsts[i2]);
                } catch (Throwable e) {
                    android.util.Log.d("AndroidStart", "SJ E " + i2, e);
                }
            }
            android.util.Log.d("AndroidStart", "SJ Z");
        } catch (Throwable e2) {
            android.util.Log.d("AndroidStart", "SJ E", e2);
        }
    }

    static class RandomImpl extends Random {
        private static final long serialVersionUID = 3905348978240129619L;

        RandomImpl() {
        }

        static void classInit() {
        }

        @Override // java.util.Random
        public int nextInt() {
            return RandomImp.nextInt(this);
        }

        @Override // java.util.Random
        public int nextInt(int bound) {
            return RandomImp.nextInt(this, bound);
        }

        @Override // java.util.Random
        public long nextLong() {
            return RandomImp.nextLong(this);
        }

        @Override // java.util.Random
        public boolean nextBoolean() {
            return RandomImp.nextBoolean(this);
        }

        @Override // java.util.Random
        public float nextFloat() {
            return RandomImp.nextFloat(this);
        }

        @Override // java.util.Random
        public double nextDouble() {
            return RandomImp.nextDouble(this);
        }

        @Override // java.util.Random
        public synchronized double nextGaussian() {
            return RandomImp.nextGaussian(this);
        }

        @Override // java.util.Random
        public int next(int bits) {
            return super.next(bits);
        }
    }

    static class RandomImp extends Random {
        private static final double DOUBLE_UNIT = 1.1102230246251565E-16d;
        private static final long serialVersionUID = 3905348978240129619L;

        RandomImp() {
        }

        static void classInit() {
        }

        public static int nextInt(RandomImpl rnd) {
            int def = rnd.next(32);
            return (int) JVM.randQ(12, def);
        }

        public static int nextInt(RandomImpl rnd, int bound) {
            int r;
            if (bound <= 0) {
                throw new IllegalArgumentException("bound must be positive");
            }
            int r2 = rnd.next(31);
            int m = bound - 1;
            if ((bound & m) == 0) {
                r = (int) ((((long) bound) * ((long) r2)) >> 31);
            } else {
                int u = r2;
                while (true) {
                    r = u % bound;
                    if ((u - r) + m >= 0) {
                        break;
                    }
                    u = rnd.next(31);
                }
            }
            return ((int) JVM.randQ(13, r)) % bound;
        }

        public static long nextLong(RandomImpl rnd) {
            long def = (((long) rnd.next(32)) << 32) + ((long) rnd.next(32));
            return JVM.randQ(14, def);
        }

        public static boolean nextBoolean(RandomImpl rnd) {
            boolean def = rnd.next(1) != 0;
            return JVM.randQ(15, (long) (def ? 1 : 0)) % 2 != 0;
        }

        public static float nextFloat(RandomImpl rnd) {
            float def = rnd.next(24) / 1.6777216E7f;
            return (float) JVM.randD(16, def);
        }

        public static double nextDouble(RandomImpl rnd) {
            double def = ((((long) rnd.next(26)) << 27) + ((long) rnd.next(27))) * DOUBLE_UNIT;
            return JVM.randD(17, def);
        }

        public static double nextGaussian(RandomImpl rnd) {
            double def = ((((long) rnd.next(26)) << 27) + ((long) rnd.next(27))) * DOUBLE_UNIT;
            return (10.4d * JVM.randD(18, def)) - 5.2d;
        }
    }

    public static void m1() {
    }

    public static void m2() {
    }

    public void m3() {
    }

    public void m4() {
    }
}
