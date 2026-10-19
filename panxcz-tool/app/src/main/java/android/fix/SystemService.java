package android.fix;

/* JADX INFO: loaded from: classes.dex */
public class SystemService {
    public static Object wrap(Object service) {
        if (service != null && ContextWrapper.isUseFix() && (service instanceof android.view.LayoutInflater) && !(service instanceof LayoutInflater)) {
            return new LayoutInflater((android.view.LayoutInflater) service);
        }
        return service;
    }
}
