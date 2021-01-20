package eu.software4you.ulib;

import com.google.gson.internal.JavaVersion;

import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;

public final class Agent {
    private static Instrumentation instrumentation;

    public static void agentmain(final String agentArgs, Instrumentation instrumentation) {
        init(instrumentation);
    }

    public static void premain(final String agentArgs, Instrumentation instrumentation) {
        init(instrumentation);
    }

    private static void init(Instrumentation inst) {
        if (!JavaVersion.isJava9OrLater())
            return; // with java 8 just append jars to the classpath via the SystemClassLoader
        instrumentation = inst;
    }

    private static void check() {
        if (instrumentation == null)
            throw new IllegalStateException("Please bootstrap the application via the uLib launcher.");
    }

    static void add(JarFile jar) {
        check();
        instrumentation.appendToSystemClassLoaderSearch(jar);
    }
}
