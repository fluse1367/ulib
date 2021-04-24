package eu.software4you.ulib;

import com.google.gson.internal.JavaVersion;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import eu.software4you.utils.FileUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.management.ManagementFactory;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Agent {
    private static Agent agent;
    private final Instrumentation instrumentation;

    public static void agentmain(final String agentArgs, Instrumentation instrumentation) {
        init(instrumentation);
    }

    public static void premain(final String agentArgs, Instrumentation instrumentation) {
        init(instrumentation);
    }

    @SneakyThrows
    private static void init(Instrumentation inst) {
        if (available())
            return;

        agent = new Agent(inst);
        ImplInjector.inject(agent, Class.forName("eu.software4you.ulib.impl.utils.JarLoaderImpl"));
        ImplInjector.inject(agent, Class.forName("eu.software4you.ulib.impl.litetransform.HookInjectorImpl"));
    }

    public static boolean available() {
        return agent != null;
    }

    public static void verifyAvailable() {
        if (!available())
            throw new IllegalStateException("Agent not available");
    }

    static void selfAttach() {
        if (available())
            return;

        Logger logger = ULib.impl.getLogger();

        if (JavaVersion.isJava9OrLater() && !System.getProperty("jdk.attach.allowAttachSelf", "false").equals("true")) {
            logger.warning("Cannot load agent: self attach is not permitted");
            logger.warning("Please set the system property 'jdk.attach.allowAttachSelf' to 'true' (-Djdk.attach.allowAttachSelf=true)");
            return;
        }

        logger.info("Loading agent ...");

        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        logger.fine("JVM pid is: " + pid);

        try {
            logger.fine("Attach!");
            VirtualMachine vm = VirtualMachine.attach(pid);

            File self = FileUtils.getClassFile(Agent.class);
            if (self == null) {
                logger.warning("Cannot load agent: not in jar file");
                return;
            }
            logger.fine("Agent file: " + self);

            vm.loadAgent(self.getAbsolutePath());

        } catch (AttachNotSupportedException | IOException e) {
            logger.log(Level.WARNING, "Could not attach the agent", e);
            return;
        } catch (AgentLoadException | AgentInitializationException e) {
            logger.log(Level.WARNING, "Could not load the agent", e);
            return;
        }

        logger.fine("Agent successfully loaded!");

    }

    public void transform(Class<?> clazz, ClassFileTransformer transformer) {
        instrumentation.addTransformer(transformer, true);
        try {
            instrumentation.retransformClasses(clazz);
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        } finally {
            instrumentation.removeTransformer(transformer);
        }
    }

    public void add(JarFile jar) {
        instrumentation.appendToSystemClassLoaderSearch(jar);
    }
}
