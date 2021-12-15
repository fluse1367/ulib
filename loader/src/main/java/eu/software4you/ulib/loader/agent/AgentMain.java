package eu.software4you.ulib.loader.agent;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.function.Consumer;
import java.util.jar.JarFile;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AgentMain {

    private static AgentMain agent;
    private final Instrumentation instrumentation;

    public static void premain(String agentArgs, Instrumentation inst) {
        agentmain(agentArgs, inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        if (agent != null)
            return; // no double init

        agent = new AgentMain(inst);
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
