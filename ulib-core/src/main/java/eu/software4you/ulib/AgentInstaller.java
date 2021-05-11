package eu.software4you.ulib;

import com.google.gson.internal.JavaVersion;
import com.sun.tools.attach.VirtualMachine;
import eu.software4you.ulib.agentex.Loader;
import eu.software4you.utils.IOUtil;
import lombok.SneakyThrows;
import lombok.val;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
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
        boolean self = !JavaVersion.isJava9OrLater()
                || System.getProperty("jdk.attach.allowAttachSelf", "false").equals("true");

        try {
            if (self && toolsLoadingRequired() && !loadTools()) { // load tools beforehand
                return false;
            }

            logger.finer("Extracting agent ...");

            agentPath = extractAgent();
            logger.fine(() -> "Agent file: " + agentPath);

            pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

            if (self) {
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
        String javaBin = String.format("%s%sbin%2$sjava", System.getProperty("java.home"), File.separator);


        if (!new File(javaBin).exists()) {
            if (!new File(javaBin += ".exe").exists()) {
                logger.log(Level.SEVERE, () -> "Unable to find java executable!");
                return false;
            }
        }

        String bin = javaBin;
        logger.finer(() -> "Java executable: " + bin);

        StringBuilder cp = new StringBuilder(AgentInstaller.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        if (toolsLoadingRequired()) {
            cp.append(":").append(toolsFile().getPath());
        }

        List<String> cmd = Arrays.asList(javaBin, "-cp", cp.toString(), Loader.class.getName(), /* main args */ pid, agentPath);

        logger.finer(() -> "Starting process: " + Arrays.toString(cmd.toArray()));

        val p = new ProcessBuilder(cmd)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();
        if (!p.waitFor(10, TimeUnit.SECONDS)) {
            p.destroyForcibly();
            logger.warning("External Agent not terminated within 10 seconds!");
            return false;
        } else if (p.exitValue() != 0) {
            logger.warning("External Agent failed: " + p.exitValue());
            return false;
        }
        return true;
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

        File file = new File(Properties.getInstance().DATA_DIR, "agent.jar");
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

        logger.fine(() -> "Agent extracted");

        return file.getPath();
    }

    @SneakyThrows
    private byte[] readClass(Class<?> cl) {
        logger.finest(() -> "Reading bytes of " + cl.getName());

        val in = cl.getResourceAsStream(String.format("/%s.class", cl.getName().replace(".", "/")));
        if (in == null) {
            throw new IllegalStateException("Cannot read class " + cl.getName());
        }

        return IOUtil.read(in);
    }

    private boolean toolsLoadingRequired() {
        try {
            Class.forName("com.sun.tools.attach.VirtualMachine");
        } catch (ClassNotFoundException e) {
            return true;
        }
        return false;
    }

    @SneakyThrows
    private File toolsFile() {
        File tools = new File(System.getProperty("java.home"), "/../lib/tools.jar");
        if (!tools.exists()) {
            throw new FileNotFoundException("tools.jar not found: " + tools.getAbsolutePath());
        }
        return tools;
    }

    private boolean loadTools() {
        try {
            logger.fine(() -> "Locating tools.jar");
            File tools = toolsFile();

            logger.fine(() -> "Loading " + tools);

            ClassLoader cl = getClass().getClassLoader();
            if (cl instanceof URLClassLoader || (cl = ClassLoader.getSystemClassLoader()) instanceof URLClassLoader) {
                URLClassLoader ucl = (URLClassLoader) cl;
                Method addUrl = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                addUrl.setAccessible(true);
                addUrl.invoke(ucl, tools.toURI().toURL());
            } else {
                throw new IllegalStateException("cannot access a url class loader");
            }
        } catch (Throwable thr) {
            logger.warning(() -> "Could not load agent: cannot load tools.jar but it is required in java 8");
            logger.warning(thr.getMessage());
            logger.log(Level.FINEST, thr, () -> "");
            return false;
        }
        return true;
    }
}
