package eu.software4you.utils;

import eu.software4you.ulib.ImplRegistry;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public abstract class JarLoader {
    private static JarLoader impl;

    private static JarLoader impl() {
        if (impl == null) {
            impl = ImplRegistry.get(JarLoader.class);
        }
        return impl;
    }

    /**
     * Loads a JAR-File (from {@link File}) into the Runtime. Works with java 8+.
     *
     * @param file the jar file to load
     */
    public static void load(File file) {
        impl().load0(file);
    }

    /**
     * Loads a JAR-File (from {@link URL}) into the Runtime. Only works with java 8.
     *
     * @param url the {@link URL} to load
     */
    public static void load(URL url) {
        impl().load0(url);
    }

    /**
     * Loads a JAR-File (from {@link URL}) into the Runtime. Only works with java 8.
     *
     * @param url         the {@link URL} to load
     * @param classLoader the {@link URLClassLoader} to load the jar file with
     */
    public static void load(URL url, URLClassLoader classLoader) {
        impl().load0(url, classLoader);
    }

    protected abstract void load0(File file);

    protected abstract void load0(URL url);

    protected abstract void load0(URL url, URLClassLoader classLoader);
}