package eu.software4you.ulib;

import com.google.gson.internal.JavaVersion;
import com.sun.tools.attach.VirtualMachine;
import lombok.SneakyThrows;
import lombok.val;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.management.ManagementFactory;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

final class AgentInstaller {
    private final Logger logger;

    private AgentInstaller(Logger logger) {
        this.logger = logger;
    }

    static void install(Logger logger) {
        if (!Agent.available() && new AgentInstaller(logger).load()) {
            Agent.init(logger);
        }
    }

    private boolean load() {
        if (JavaVersion.isJava9OrLater() && !System.getProperty("jdk.attach.allowAttachSelf", "false").equals("true")) {
            logger.warning("Cannot load agent: self attach is not permitted");
            logger.warning("Please set the system property 'jdk.attach.allowAttachSelf' to 'true' (-Djdk.attach.allowAttachSelf=true)");
            return false;
        }

        try {

            logger.fine("Loading agent ...");

            String agentPath = extractAgent();
            logger.fine("Agent file: " + agentPath);

            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            logger.fine("JVM pid is: " + pid);

            logger.fine("Attach!");
            VirtualMachine vm = VirtualMachine.attach(pid);

            logger.fine("Loading agent " + agentPath);
            vm.loadAgent(agentPath);
        } catch (Throwable e) {
            logger.log(Level.WARNING, "Could not load agent", e);
            return false;
        }

        logger.fine("Agent successfully loaded!");
        return true;
    }

    @SneakyThrows
    private String extractAgent() {

        File file = new File(Properties.getInstance().DATA_DIR, "agent.jar");
        logger.fine("Attempt to extract agent to " + file);


        String ver = Agent.class.getPackage().getImplementationVersion();

        if (file.exists()) {
            logger.fine("Agent already exists! Checking version.");
            try {
                JarFile jar = new JarFile(file);
                String agentVer = jar.getManifest().getMainAttributes().getValue("Implementation-Version");
                logger.fine("Agent version: " + agentVer);
                if (agentVer != null && agentVer.equals(ver)) {
                    logger.fine("Version valid, use existing agent ...");
                    return file.getPath();
                }
            } catch (Throwable ignored) {
            }
            logger.fine("Version invalid, rewrite agent jar ...");
        }

        Manifest manifest = new Manifest(new ByteArrayInputStream(String.format(
                "Manifest-Version: 1.0\n" +
                        "Implementation-Version: %s\n" +
                        "Agent-Class: %s\n" +
                        "Can-Redefine-Classes: true\n" +
                        "Can-Retransform-Classes: true\n",
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

        logger.fine("Agent extracted");

        return file.getPath();
    }

    @SneakyThrows
    private byte[] readClass(Class<?> cl) {
        logger.finest("Reading bytes of " + cl.getName());

        val in = cl.getResourceAsStream(String.format("/%s.class", cl.getName().replace(".", "/")));
        if (in == null) {
            throw new IllegalStateException("Cannot read class " + cl.getName());
        }

        val out = new ByteArrayOutputStream();

        byte[] buff = new byte[1024];
        int len;
        while ((len = in.read(buff)) != -1) {
            out.write(buff, 0, len);
        }
        out.flush();
        in.close();

        return out.toByteArray();
    }
}
