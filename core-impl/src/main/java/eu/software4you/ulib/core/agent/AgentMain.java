package eu.software4you.ulib.core.agent;

import lombok.SneakyThrows;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.function.Consumer;
import java.util.jar.JarFile;

public final class AgentMain {

    private final Instrumentation instrumentation;

    private AgentMain(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        var agent = new AgentMain(inst);
        System.getProperties().put("ulib.javaagent", new Object[]{
                (Consumer<Class<?>>) agent::transform,
                (Consumer<ClassFileTransformer>) agent::addTransformer,
                (Consumer<JarFile>) agent::appendJar
        });
    }

    @SneakyThrows
    public void transform(Class<?> clazz) {
        instrumentation.retransformClasses(clazz);
    }

    public void addTransformer(ClassFileTransformer transformer) {
        instrumentation.addTransformer(transformer, true);
    }

    public void appendJar(JarFile jar) {
        instrumentation.appendToSystemClassLoaderSearch(jar);
    }
}
