package eu.software4you.ulib;

import eu.software4you.utils.IOUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import java.util.logging.Logger;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Agent {
    private static Agent instance;
    private final Consumer<Class<?>> transformClass;
    private final Consumer<ClassFileTransformer> addTransformer;
    private final Consumer<JarFile> appendJar;

    static void init(Logger logger) {
        if (available())
            return;

        logger.finer(() -> "Agent pre init");

        var props = System.getProperties();

        Object[] array = (Object[]) props.get("ulib.javaagent");
        try {
            //noinspection unchecked
            init(logger,
                    (Consumer<Class<?>>) array[0],
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
    static void init(Logger logger,
                     Consumer<Class<?>> transformClass,
                     Consumer<ClassFileTransformer> addTransformer,
                     Consumer<JarFile> appendJar) {
        if (available())
            return;

        logger.fine(() -> "Agent init!");

        instance = new Agent(transformClass, addTransformer, appendJar);
        instance.loadUlibEx(logger);
        ImplInjector.inject(instance, Class.forName("eu.software4you.ulib.impl.dependencies.DependencyLoaderImpl"));
        ImplInjector.inject(instance, Class.forName("eu.software4you.ulib.impl.transform.HookInjectorImpl"));

        logger.fine(() -> "Agent init done!");
    }

    @SneakyThrows
    private void loadUlibEx(Logger logger) {
        logger.fine("Loading libex");

        // extract ulibex
        File libex = new File(Properties.getInstance().CACHE_DIR, "libex.jar");
        logger.fine(() -> "Attempt to extract libex to " + libex);

        boolean extract = true;
        if (libex.exists()) {
            logger.fine(() -> "Libex already exists! Checking version.");

            String ver = Agent.class.getPackage().getImplementationVersion();
            String exVer = AgentUtil.getVer(libex);

            logger.fine(() -> "Libex version: " + exVer);

            if (ver.equals(exVer)) {
                logger.fine(() -> "Version valid, using existing libex");
                extract = false;
            } else {
                logger.fine(() -> "Version invalid, rewriting libex");
            }
        }

        if (extract) {
            logger.fine(() -> "Extracting libex to " + libex);
            //noinspection ConstantConditions
            IOUtil.write(Agent.class.getResourceAsStream("/libex"), new FileOutputStream(libex));
        }

        logger.fine(() -> "Loading " + libex);
        // load ulibex
        appendJar.accept(new JarFile(libex));
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
