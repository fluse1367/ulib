package eu.software4you.ulib;

import com.google.gson.internal.JavaVersion;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;

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
        if (!JavaVersion.isJava9OrLater())
            return; // with java 8 just append jars to the classpath via the SystemClassLoader
        if (agent != null)
            return;

        agent = new Agent(inst);
        ImplInjector.inject(agent, Class.forName("eu.software4you.ulib.impl.utils.JarLoaderImpl"));
    }

    public static boolean available() {
        return agent != null;
    }

    public void add(JarFile jar) {
        instrumentation.appendToSystemClassLoaderSearch(jar);
    }
}
