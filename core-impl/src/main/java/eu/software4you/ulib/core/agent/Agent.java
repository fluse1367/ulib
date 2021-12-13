package eu.software4you.ulib.core.agent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.instrument.ClassFileTransformer;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.jar.JarFile;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Agent {
    @Getter
    private static Agent instance;
    private final Consumer<Class<?>> transformClass;
    private final Consumer<ClassFileTransformer> addTransformer;
    private final Consumer<JarFile> appendJar;

    static void init() {
        if (available())
            return;

        var props = System.getProperties();

        Object[] array = (Object[]) props.get("ulib.javaagent");
        try {
            //noinspection unchecked
            init((Consumer<Class<?>>) array[0],
                    (Consumer<ClassFileTransformer>) array[1],
                    (Consumer<JarFile>) array[2]
            );
        } catch (ClassCastException e) {
            throw new IllegalStateException("Invalid javaagent access array: " + Arrays.toString(array), e);
        } finally {
            props.remove("ulib.javaagent");
        }
    }

    @SneakyThrows
    static void init(Consumer<Class<?>> transformClass,
                     Consumer<ClassFileTransformer> addTransformer,
                     Consumer<JarFile> appendJar) {
        if (available())
            return;
        instance = new Agent(transformClass, addTransformer, appendJar);
    }

    public static boolean available() {
        return instance != null;
    }

    public static void verifyAvailable() {
        if (!available())
            throw new IllegalStateException("Agent not available");
    }

    public void addTransformer(ClassFileTransformer transformer) {
        addTransformer.accept(transformer);
    }

    public void transform(Class<?> clazz) {
        transformClass.accept(clazz);
    }

    public void appendJar(JarFile jar) {
        appendJar.accept(jar);
    }
}
