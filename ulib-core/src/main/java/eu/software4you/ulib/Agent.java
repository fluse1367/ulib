package eu.software4you.ulib;

import eu.software4you.utils.IOUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import java.util.logging.Logger;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Agent {
    private static Agent instance;
    private final BiConsumer<Class<?>, ClassFileTransformer> transform;
    private final Consumer<JarFile> appendJar;

    static void init(Logger logger) {
        if (available())
            return;

        logger.finer(() -> "Agent pre init");

        val props = System.getProperties();

        Object[] array = (Object[]) props.get("ulib.javaagent");
        if (array == null || array.length != 2) {
            throw new IllegalStateException("Invalid javaagent access array: " + Arrays.toString(array));
        }

        Object objTransform = array[0];
        if (!(objTransform instanceof BiConsumer))
            throw new IllegalStateException("transform not a BiConsumer: " + objTransform);

        Object objAppendJar = array[1];
        if (!(objAppendJar instanceof Consumer))
            throw new IllegalStateException("appendJar not a Consumer: " + objAppendJar);

        props.remove("ulib.javaagent");

        //noinspection unchecked
        init(logger,
                (BiConsumer<Class<?>, ClassFileTransformer>) objTransform,
                ((Consumer<JarFile>) objAppendJar));
    }

    @SneakyThrows
    static void init(Logger logger, BiConsumer<Class<?>, ClassFileTransformer> transform, Consumer<JarFile> appendJar) {
        if (available())
            return;

        logger.fine(() -> "Agent init!");

        instance = new Agent(transform, appendJar);
        instance.loadUlibEx(logger);
        ImplInjector.inject(instance, Class.forName("eu.software4you.ulib.impl.dependencies.DependencyLoaderImpl"));
        ImplInjector.inject(instance, Class.forName("eu.software4you.ulib.impl.transform.HookInjectorImpl"));

        logger.fine(() -> "Agent init done!");
    }

    @SneakyThrows
    private void loadUlibEx(Logger logger) {
        logger.fine("Loading libex");

        // extract ulibex
        File libex = new File(Properties.getInstance().DATA_DIR, "libex.jar");
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

    public void transform(Class<?> clazz, ClassFileTransformer transformer) {
        transform.accept(clazz, transformer);
    }

    public void appendJar(JarFile jar) {
        appendJar.accept(jar);
    }
}
