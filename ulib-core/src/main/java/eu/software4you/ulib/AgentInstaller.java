package eu.software4you.ulib;

import com.sun.tools.attach.VirtualMachine;
import eu.software4you.io.IOUtil;
import eu.software4you.ulib.agentex.Loader;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

final class AgentInstaller {
    private final Logger logger;
    private String pid;
    private String agentPath;

    private AgentInstaller(Logger logger) {
        this.logger = logger;
    }

    static void install(Logger logger) {
        if (!Agent.available() && new AgentInstaller(logger).load()) {
            Agent.init(logger);
        }
    }

    private boolean load() {
        try {
            logger.finer("Extracting agent ...");

            agentPath = extractAgent();
            logger.fine(() -> "Agent file: " + agentPath);

            pid = String.valueOf(ProcessHandle.current().pid());

            if (System.getProperty("jdk.attach.allowAttachSelf", "false").equals("true")) {
                logger.finer("Self attach!");
                attachSelf();
            } else {
                logger.finer("External attach!");
                if (!attachEx()) {
                    return false;
                }
            }

        } catch (Throwable e) {
            logger.log(Level.WARNING, e, () -> "Could not load agent");
            return false;
        }

        logger.fine(() -> "Agent successfully loaded!");
        return true;
    }

    @SneakyThrows
    private boolean attachEx() {
        String bin = AgentUtil.getJavaBin();

        if (bin == null) {
            logger.log(Level.SEVERE, () -> "Unable to find java executable!");
            return false;
        }

        logger.finer(() -> "Java executable: " + bin);

        String jarPath = new File(Loader.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();

        List<String> cmd = Arrays.asList(bin, "-cp", jarPath, Loader.class.getName(), /* main args */ pid, agentPath);

        logger.finer(() -> "Starting process: " + Arrays.toString(cmd.toArray()));

        var p = new ProcessBuilder(cmd).start();

        Thread thrOut, thrErr;
        (thrOut = IOUtil.redirect(p.getInputStream(), System.out)).start();
        (thrErr = IOUtil.redirect(p.getErrorStream(), System.err)).start();

        try {
            if (!p.waitFor(10, TimeUnit.SECONDS)) {
                p.destroyForcibly();
                logger.warning("External Agent not terminated within 10 seconds!");
                return false;
            } else if (p.exitValue() != 0) {
                logger.warning("External Agent failed: " + p.exitValue());
                return false;
            }
            return true;
        } finally {
            if (thrOut.isAlive()) {
                logger.warning("External agent stdout redirect still active");
                thrOut.interrupt();
            }
            if (thrErr.isAlive()) {
                logger.warning("External agent stderr redirect still active");
                thrErr.interrupt();
            }
        }
    }

    @SneakyThrows
    private void attachSelf() {
        logger.fine(() -> "Loading agent: " + agentPath);

        logger.fine(() -> "JVM pid is: " + pid);

        logger.fine(() -> "Attach!");
        VirtualMachine vm = VirtualMachine.attach(pid);

        logger.fine(() -> "Loading agent " + agentPath);
        vm.loadAgent(agentPath);
    }

    @SneakyThrows
    private String extractAgent() {

        File file = new File(Properties.getInstance().CACHE_DIR, "agent.jar");
        logger.fine(() -> "Attempt to extract agent to " + file);


        String ver = AgentInstaller.class.getPackage().getImplementationVersion();

        if (file.exists()) {
            logger.fine(() -> "Agent already exists! Checking version.");
            try {
                String agentVer = AgentUtil.getVer(file);
                logger.fine(() -> "Agent version: " + agentVer);
                if (agentVer != null && agentVer.equals(ver)) {
                    logger.fine(() -> "Version valid, use existing agent ...");
                    return file.getPath();
                }
            } catch (Throwable ignored) {
            }
            logger.fine(() -> "Version invalid, rewrite agent jar ...");
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
            logger.finer("Writing " + cl.getName());
            out.putNextEntry(new ZipEntry(String.format("%s.class", cl.getName().replace('.', '/'))));
            out.write(readClass(cl));
            out.flush();
            out.closeEntry();
        }

        out.flush();
        out.close();

        logger.fine(() -> "Agent extracted");

        return file.getPath();
    }

    @SneakyThrows
    private byte[] readClass(Class<?> cl) {
        logger.finest(() -> "Reading bytes of " + cl.getName());

        var in = cl.getResourceAsStream(String.format("/%s.class", cl.getName().replace(".", "/")));
        if (in == null) {
            throw new IllegalStateException("Cannot read class " + cl.getName());
        }

        return IOUtil.read(in);
    }
}
