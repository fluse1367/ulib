package eu.software4you.ulib;

import lombok.SneakyThrows;
import lombok.val;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.jar.JarFile;

public final class AgentMain {

    private final Instrumentation instrumentation;

    private AgentMain(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        val agent = new AgentMain(inst);
        System.getProperties().put("ulib.javaagent", new Object[]{
                (BiConsumer<Class<?>, ClassFileTransformer>) agent::transform,
                (Consumer<JarFile>) agent::appendJar
        });
    }

    @SneakyThrows
    public void transform(Class<?> clazz, ClassFileTransformer transformer) {
        instrumentation.addTransformer(transformer, true);
        try {
            instrumentation.retransformClasses(clazz);
        } finally {
            instrumentation.removeTransformer(transformer);
        }
    }

    public void appendJar(JarFile jar) {
        instrumentation.appendToSystemClassLoaderSearch(jar);
    }
}
