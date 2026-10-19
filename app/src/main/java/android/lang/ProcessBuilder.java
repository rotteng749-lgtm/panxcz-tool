package android.lang;

import android.ext.DaemonLoader;
import android.ext.SearchMenuItem;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public final class ProcessBuilder {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static volatile boolean loaded;
    private String[] command;
    private File directory;
    private Map<String, String> environment;

    public static native boolean loaded();

    static {
        $assertionsDisabled = !ProcessBuilder.class.desiredAssertionStatus();
        loaded = false;
    }

    public static Process exec(String[] cmdarray, String[] envp, File dir) throws IOException {
        if (!loaded) {
            loaded = true;
            DaemonLoader.initNative();
        }
        return new ProcessBuilder(cmdarray).environment(envp).directory(dir).start();
    }

    public ProcessBuilder(String... command) {
        this.command = command;
    }

    ProcessBuilder environment(String[] envp) {
        if (!$assertionsDisabled && this.environment != null) {
            throw new AssertionError();
        }
        if (envp != null) {
            this.environment = ProcessEnvironment.emptyEnvironment(envp.length);
            if (!$assertionsDisabled && this.environment == null) {
                throw new AssertionError();
            }
            for (String envstring : envp) {
                if (envstring.indexOf(0) != -1) {
                    envstring = envstring.replaceFirst("\u0000.*", "");
                }
                int eqlsign = envstring.indexOf(61, 0);
                if (eqlsign != -1) {
                    this.environment.put(envstring.substring(0, eqlsign), envstring.substring(eqlsign + 1));
                }
            }
        }
        return this;
    }

    public ProcessBuilder directory(File directory) {
        this.directory = directory;
        return this;
    }

    public Process start() throws IOException {
        String[] cmdarray = (String[]) this.command.clone();
        for (String arg : cmdarray) {
            if (arg == null) {
                throw new NullPointerException();
            }
        }
        String prog = cmdarray[0];
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkExec(prog);
        }
        String dir = this.directory == null ? null : this.directory.toString();
        for (int i = 1; i < cmdarray.length; i++) {
            if (cmdarray[i].indexOf(0) >= 0) {
                throw new IOException("invalid null character in command");
            }
        }
        try {
            return ProcessImpl.start(cmdarray, this.environment, dir, false);
        } catch (IOException | IllegalArgumentException e) {
            String exceptionInfo = ": " + e.getMessage();
            Throwable cause = e;
            if ((e instanceof IOException) && security != null) {
                try {
                    security.checkRead(prog);
                } catch (SecurityException se) {
                    exceptionInfo = "";
                    cause = se;
                }
            }
            throw new IOException("Cannot run program \"" + prog + SearchMenuItem.HEX_UTF_16LE + (dir == null ? "" : " (in directory \"" + dir + "\")") + exceptionInfo, cause);
        }
    }
}
