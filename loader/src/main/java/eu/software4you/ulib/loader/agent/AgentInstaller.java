package eu.software4you.ulib.loader.agent;

import com.sun.tools.attach.VirtualMachine;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public final class AgentInstaller {

    private final String path;

    @SneakyThrows
    public AgentInstaller() {
        this.path = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
    }

    private String pid;

    public boolean install() {
        try {
            pid = String.valueOf(ProcessHandle.current().pid());

            if (System.getProperty("jdk.attach.allowAttachSelf", "false").equals("true")) {
                attachSelf();
            } else if (!attachEx()) {
                return false;
            }

        } catch (Throwable e) {
            return false;
        }
        return true;
    }

    @SneakyThrows
    private boolean attachEx() {
        String bin = getJavaBin();

        if (bin == null) {
            return false;
        }

        List<String> cmd = Arrays.asList(bin, "-cp", path, LoaderExternal.class.getName(), /* main args */ pid, path);

        var p = new ProcessBuilder(cmd).start();

        Thread thrOut, thrErr;
        (thrOut = redirect(p.getInputStream(), System.out, Thread::new)).start();
        (thrErr = redirect(p.getErrorStream(), System.err, Thread::new)).start();

        try {
            if (!p.waitFor(10, TimeUnit.SECONDS)) {
                p.destroyForcibly();
                return false;
            } else return p.exitValue() == 0;
        } finally {
            if (p.isAlive())
                p.destroyForcibly();

            if (thrOut.isAlive()) {
                thrOut.interrupt();
            }
            if (thrErr.isAlive()) {
                thrErr.interrupt();
            }
        }
    }

    private static Thread redirect(InputStream in, OutputStream out, ThreadFactory factory) {
        //noinspection Convert2Lambda
        return factory.newThread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                int b;
                while (!Thread.currentThread().isInterrupted() && (b = in.read()) != -1) {
                    out.write(b);
                }
            }
        });
    }

    @SneakyThrows
    private void attachSelf() {
        VirtualMachine vm = VirtualMachine.attach(pid);
        vm.loadAgent(path);
    }

    private String getJavaBin() {
        return ProcessHandle.current().info().command().orElseGet(() -> {
            var bin = Path.of(System.getProperty("java.home"), "bin", "java");
            return !Files.exists(bin) && !Files.exists(bin = bin.resolve(".exe")) ? null : bin.toString();
        });
    }
}
