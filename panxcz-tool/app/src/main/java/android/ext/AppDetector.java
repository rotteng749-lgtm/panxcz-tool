package android.ext;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.ext.ProcessList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import catch_.me_.if_.you_.can_.R;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
class AppDetector {
    private ProcessList.ProcessInfo processInfo;
    volatile boolean appSelectByUser = false;
    volatile Runnable callback = null;
    private volatile boolean inDetectApp = false;
    private boolean force = false;
    private String prefferedPkg = null;

    public AppDetector(ActivityManager am, PackageManager pm) {
        new ProcessList(am, pm);
    }

    void setAppInfo(ProcessList.ProcessInfo info) {
        info.loadIcon();
        info.resolveLibs();
        this.processInfo = info;
        Log.i("used: " + info.dump());
        MainService.instance.setProcessInfo(info);
    }

    public void setAppSelectByAuto() {
        this.appSelectByUser = false;
    }

    public boolean isAppSelectByUser() {
        return this.appSelectByUser;
    }

    public boolean checkAppSelect(boolean soft, Runnable callback) {
        this.callback = null;
        if (!this.appSelectByUser) {
            detectApp(true);
            this.callback = callback;
            return false;
        }
        if (soft) {
            detectApp(false);
        }
        return true;
    }

    public void detectApp(boolean force) {
        detectApp(force, null);
    }

    public void detectApp(boolean force, String prefferedPkg) {
        if (this.inDetectApp) {
            if (force && MainService.instance.mDaemonManager.isDaemonRun()) {
                Tools.showToast(R.string.data_collecting_in_progress, 0);
                return;
            }
            return;
        }
        this.inDetectApp = true;
        this.force = force;
        this.prefferedPkg = prefferedPkg;
        if (force) {
            Tools.showToast(R.string.start_collecting_data, 0);
        }
        staticPost();
    }

    private static void staticPost() {
        ThreadManager.getHandlerUiThread().post(new Runnable() { // from class: android.ext.AppDetector.1
            @Override // java.lang.Runnable
            public void run() {
                MainService.instance.mDaemonManager.getProcessList();
            }
        });
    }

    public void detectAppResume(final List<ProcessList.ProcessInfo> listProcesses) {
        if (this.inDetectApp) {
            try {
                int count = listProcesses.size();
                if (count == 0) {
                    Log.w("listProcesses empty");
                    if (this.force || this.processInfo == null) {
                        Tools.showToast(R.string.failed_find_package_name);
                        if (Config.vSpace) {
                            ConfigListAdapter.showHelp(R.string.help_game_not_listed_title);
                        }
                    }
                } else {
                    boolean found = false;
                    if (this.processInfo != null) {
                        Iterator<ProcessList.ProcessInfo> it = listProcesses.iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            if (it.next().pid == this.processInfo.pid) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (this.prefferedPkg != null) {
                        int cnt = 0;
                        Iterator<ProcessList.ProcessInfo> it2 = listProcesses.iterator();
                        while (true) {
                            if (!it2.hasNext()) {
                                break;
                            }
                            ProcessList.ProcessInfo ps = it2.next();
                            if (ps.packageName.equals(this.prefferedPkg)) {
                                cnt = 0 + 1;
                                if (cnt == 1) {
                                    setAppInfo(ps);
                                }
                                found = true;
                            }
                        }
                        if (cnt == 1) {
                            this.force = false;
                        }
                    }
                    if (!found) {
                        setAppInfo(listProcesses.get(0));
                    }
                    if (Config.vSpace && Config.vSpace64 == 0) {
                        Config.vSpace64 = (listProcesses.get(0).x64 && Config.vSpacePkg.contains("64")) ? (byte) 1 : (byte) 2;
                    }
                    if (this.force) {
                        final int vSpace = listProcesses.size();
                        ProcessList.AppInfo ai = new ProcessList.AppInfo(1, "stub");
                        ai.name = Re.s(R.string.help_game_not_listed_title);
                        ai.isStub = true;
                        ProcessList.ProcessInfo pi = new ProcessList.ProcessInfo(ai, 1, 1, "stub", 0, false, 0);
                        pi.icon = Tools.getDrawable(R.drawable.ic_help_circle_outline_white_24dp);
                        listProcesses.add(pi);
                        ListAdapter adapter = new ArrayAdapter<ProcessList.ProcessInfo>(MainService.context, listProcesses) { // from class: android.ext.AppDetector.2
                            @Override // android.ext.ArrayAdapter, android.widget.ArrayAdapter, android.widget.Adapter
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View v = super.getView(position, convertView, parent);
                                TextView tv = (TextView) v.findViewById(android.R.id.text1);
                                if (tv != null) {
                                    ProcessList.ProcessInfo pi2 = (ProcessList.ProcessInfo) listProcesses.get(position);
                                    if (pi2 != null) {
                                        try {
                                            tv.setText(pi2.toCharSequence());
                                        } catch (Throwable e) {
                                            Log.badImplementation(e);
                                        }
                                        tv.setTag(pi2);
                                        pi2.loadIcon(tv);
                                    }
                                    Tools.setTextAppearance(tv, R.style.ListItemText);
                                }
                                return v;
                            }
                        };
                        Alert.show(Alert.create(Tools.getDarkContext()).setCustomTitle(Tools.getCustomTitle(R.string.reset)).setAdapter(adapter, new DialogInterface.OnClickListener() { // from class: android.ext.AppDetector.3
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == vSpace) {
                                    ConfigListAdapter.showHelp(R.string.help_game_not_listed_title);
                                    return;
                                }
                                AppDetector.this.setAppInfo((ProcessList.ProcessInfo) listProcesses.get(which));
                                AppDetector.this.appSelectByUser = true;
                                Tools.dismiss(dialog);
                                Runnable call = AppDetector.this.callback;
                                if (call != null) {
                                    AppDetector.this.callback = null;
                                    call.run();
                                }
                            }
                        }));
                    }
                }
            } finally {
                this.inDetectApp = false;
            }
        }
    }
}
