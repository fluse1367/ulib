package eu.software4you.ulib;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import java.lang.instrument.ClassFileTransformer;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import java.util.logging.Logger;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Agent {
    private static Agent instance;
    private final BiConsumer<Class<?>, ClassFileTransformer> transform;
    private final Consumer<JarFile> appendJar;

    @SneakyThrows
    static void init(Logger logger) {
        if (available())
            return;

        logger.fine("Agent init!");
        val props = System.getProperties();

        Object objTransform = props.get("ulib.javaagent.transform");
        if (!(objTransform instanceof BiConsumer))
            throw new IllegalStateException("transform not a  BiConsumer: " + objTransform);

        Object objAppendJar = props.get("ulib.javaagent.appendJar");
        if (!(objAppendJar instanceof Consumer))
            throw new IllegalStateException("appendJar not a Consumer: " + objAppendJar);

        props.remove("ulib.javaagent.transform");
        props.remove("ulib.javaagent.appendJar");

        instance = new Agent(((BiConsumer<Class<?>, ClassFileTransformer>) objTransform), ((Consumer<JarFile>) objAppendJar));
        ImplInjector.inject(instance, Class.forName("eu.software4you.ulib.impl.utils.JarLoaderImpl"));
        ImplInjector.inject(instance, Class.forName("eu.software4you.ulib.impl.transform.HookInjectorImpl"));

        logger.fine("Agent init done!");
    }

    public static boolean available() {
        return instance != null;
    }

    public static void verifyAvailable() {
        if (!available())
            throw new IllegalStateException("Agent not available");
    }

    public void transform(Class<?> clazz, ClassFileTransformer transformer) {
        transform.accept(clazz, transformer);
    }

    public void appendJar(JarFile jar) {
        appendJar.accept(jar);
    }
}
