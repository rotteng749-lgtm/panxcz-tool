package android.app;

import android.os.IBinder;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
class ANRManagerProxy {
    private IBinder mRemote;

    public ANRManagerProxy(IBinder remote) {
        this.mRemote = remote;
    }

    public IBinder asBinder() {
        return this.mRemote;
    }

    public boolean notifyWNR(int pid, String reason) throws RemoteException {
        return true;
    }

    public void notifyLightWeightANR(int pid, String reason, int message) throws RemoteException {
    }

    public void informMessageDump(String msgInfo) throws RemoteException {
    }
}
