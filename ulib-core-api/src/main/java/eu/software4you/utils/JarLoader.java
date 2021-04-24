package eu.software4you.utils;

import eu.software4you.ulib.Await;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public abstract class JarLoader {
    @Await
    private static JarLoader impl;

    /**
     * Loads a JAR-File (from {@link File}) into the Runtime. Works with java 8+.
     *
     * @param file the jar file to load
     */
    public static void load(@NotNull File file) {
        impl.load0(file);
    }

    /**
     * Loads a JAR-File (from {@link URL}) into the Runtime. Only works with java 8.
     *
     * @param url the {@link URL} to load
     */
    public static void load(@NotNull URL url) {
        impl.load0(url);
    }

    /**
     * Loads a JAR-File (from {@link URL}) into the Runtime. Only works with java 8.
     *
     * @param url         the {@link URL} to load
     * @param classLoader the {@link URLClassLoader} to load the jar file with
     */
    public static void load(@NotNull URL url, @NotNull URLClassLoader classLoader) {
        impl.load0(url, classLoader);
    }

    protected abstract void load0(File file);

    protected abstract void load0(URL url);

    protected abstract void load0(URL url, URLClassLoader classLoader);
}