package eu.software4you.ulib.core.agent;

import com.sun.tools.attach.VirtualMachine;
import eu.software4you.ulib.core.agent.agentex.Loader;
import eu.software4you.ulib.core.api.io.IOUtil;
import eu.software4you.ulib.core.launch.Properties;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

public final class AgentInstaller {
    private String pid;
    private String agentPath;

    private AgentInstaller() {
    }

    public static void install() {
        if (!Agent.available() && new AgentInstaller().load()) {
            Agent.init();
        }
    }

    private boolean load() {
        try {
            agentPath = extractAgent();
            pid = String.valueOf(ProcessHandle.current().pid());

            if (System.getProperty("jdk.attach.allowAttachSelf", "false").equals("true")) {
                attachSelf();
            } else {
                if (!attachEx()) {
                    return false;
                }
            }

        } catch (Throwable e) {
            return false;
        }
        return true;
    }

    @SneakyThrows
    private boolean attachEx() {
        String bin = AgentUtil.getJavaBin();

        if (bin == null) {
            return false;
        }

        String jarPath = new File(Loader.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        List<String> cmd = Arrays.asList(bin, "-cp", jarPath, Loader.class.getName(), /* main args */ pid, agentPath);

        var p = new ProcessBuilder(cmd).start();

        Thread thrOut, thrErr;
        (thrOut = IOUtil.redirect(p.getInputStream(), System.out)).start();
        (thrErr = IOUtil.redirect(p.getErrorStream(), System.err)).start();

        try {
            if (!p.waitFor(10, TimeUnit.SECONDS)) {
                p.destroyForcibly();
                return false;
            } else return p.exitValue() == 0;
        } finally {
            if (thrOut.isAlive()) {
                thrOut.interrupt();
            }
            if (thrErr.isAlive()) {
                thrErr.interrupt();
            }
        }
    }

    @SneakyThrows
    private void attachSelf() {
        VirtualMachine vm = VirtualMachine.attach(pid);
        vm.loadAgent(agentPath);
    }

    @SneakyThrows
    private String extractAgent() {
        File file = new File(Properties.getInstance().CACHE_DIR, "agent.jar");
        String ver = AgentInstaller.class.getPackage().getImplementationVersion();

        if (file.exists()) {
            try {
                String agentVer = AgentUtil.getVer(file);
                if (agentVer != null && agentVer.equals(ver)) {
                    return file.getPath();
                }
            } catch (Throwable ignored) {
            }
        }

        Manifest manifest = new Manifest(new ByteArrayInputStream(String.format(
                """
                        Manifest-Version: 1.0
                        Implementation-Version: %s
                        Agent-Class: %s
                        Can-Redefine-Classes: true
                        Can-Retransform-Classes: true
                        """,
                ver, AgentMain.class.getName()).getBytes()));
        JarOutputStream out = new JarOutputStream(new FileOutputStream(file), manifest);

        for (Class<?> cl : new Class<?>[]{AgentMain.class}) {
            out.putNextEntry(new ZipEntry(String.format("%s.class", cl.getName().replace('.', '/'))));
            out.write(readClass(cl));
            out.flush();
            out.closeEntry();
        }

        out.flush();
        out.close();

        return file.getPath();
    }

    @SneakyThrows
    private byte[] readClass(Class<?> cl) {
        var in = cl.getResourceAsStream(String.format("/%s.class", cl.getName().replace(".", "/")));
        if (in == null) {
            throw new IllegalStateException("Cannot read class " + cl.getName());
        }

        return IOUtil.read(in);
    }
}
