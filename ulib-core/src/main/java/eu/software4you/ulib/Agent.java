package eu.software4you.ulib;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
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
        if (available())
            return;

        agent = new Agent(inst);
        ImplInjector.inject(agent, Class.forName("eu.software4you.ulib.impl.utils.JarLoaderImpl"));
        ImplInjector.inject(agent, Class.forName("eu.software4you.ulib.impl.litetransform.InjectorImpl"));
    }

    public static boolean available() {
        return agent != null;
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
