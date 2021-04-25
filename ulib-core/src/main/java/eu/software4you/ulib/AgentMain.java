package eu.software4you.ulib;

import lombok.val;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
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
        val props = System.getProperties();
        props.put("ulib.javaagent.transform", (BiConsumer<Class<?>, ClassFileTransformer>) agent::transform);
        props.put("ulib.javaagent.appendJar", (Consumer<JarFile>) agent::appendJar);
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

    public void appendJar(JarFile jar) {
        instrumentation.appendToSystemClassLoaderSearch(jar);
    }
}
