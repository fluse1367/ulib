package eu.software4you.utils;

import eu.software4you.reflect.Parameter;
import eu.software4you.reflect.ReflectUtil;
import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.function.Consumer;
import java.util.jar.JarFile;

public class JarLoader {
    static Consumer<JarFile> loader;
    private static URLClassLoader urlClassLoader = null;

    private static URLClassLoader getUrlClassLoader() {
        if (urlClassLoader != null)
            return urlClassLoader;

        ClassLoader cl = JarLoader.class.getClassLoader();
        if (cl instanceof URLClassLoader || (cl = ClassLoader.getSystemClassLoader()) instanceof URLClassLoader) {
            urlClassLoader = (URLClassLoader) cl;
        }
        return urlClassLoader;
    }

    private static boolean jre9LoaderAvailable() {
        URLClassLoader ucl = getUrlClassLoader();
        if (ucl != null)
            return false;
        // we're in java 9
        if (loader == null) {
            throw new UnsupportedOperationException("Cannot attach jar files to the jvm.");
        }
        return true;
    }

    private static URLClassLoader getUrlClassLoaderSafe() {
        URLClassLoader ucl = getUrlClassLoader();
        if (ucl == null) {
            throw new UnsupportedOperationException("Cannot attach jar files to the jvm.");
        }
        return ucl;
    }

    /**
     * Loads a JAR-File (from {@link File}) into the Runtime. Works with java 8+.
     *
     * @param file the jar file to load
     */
    @SneakyThrows
    public static void load(File file) {
        if (jre9LoaderAvailable()) {
            loader.accept(new JarFile(file));
            return;
        }

        load(file.toURI().toURL());
    }

    /**
     * Loads a JAR-File (from {@link URL}) into the Runtime. Only works with java 8.
     *
     * @param url the {@link URL} to load
     */
    public static void load(URL url) {
        load(url, getUrlClassLoaderSafe());
    }

    /**
     * Loads a JAR-File (from {@link URL}) into the Runtime. Only works with java 8.
     *
     * @param url         the {@link URL} to load
     * @param classLoader the {@link URLClassLoader} to load the jar file with
     */
    @SneakyThrows
    public static void load(URL url, URLClassLoader classLoader) {
        ReflectUtil.forceCall(URLClassLoader.class, classLoader, "addURL()",
                Parameter.single(URL.class, url));
    }

}