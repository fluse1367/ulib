package eu.software4you.utils;

import com.google.gson.internal.JavaVersion;
import eu.software4you.reflection.ReflectUtil;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.jar.JarFile;

public class JarLoader {
    static Consumer<JarFile> loader;

    /**
     * Loads a JAR-File (from {@link File}) into the Runtime. Works with java 8+.
     *
     * @param file the jar file to load
     */
    @SneakyThrows
    public static void load(File file) {
        if (JavaVersion.isJava9OrLater()) {
            loader.accept(new JarFile(file));
            return;
        }

        load(file.toURI().toURL());
    }

    // Java 8 Only

    /**
     * Loads a JAR-File (from {@link URL}) into the Runtime. Only works with java 8.
     *
     * @param url the {@link URL} to load
     */
    public static void load(URL url) {
        ClassLoader cl = JarLoader.class.getClassLoader();

        URLClassLoader ucl;
        if (cl instanceof URLClassLoader) {
            ucl = (URLClassLoader) cl;
        } else {
            ClassLoader sysCl = ClassLoader.getSystemClassLoader();
            if (sysCl instanceof URLClassLoader) {
                ucl = (URLClassLoader) sysCl;
            } else {
                throw new UnsupportedOperationException("Cannot attach URL (" + url.toString() + ") to System Class Loader.");
            }
        }

        load(url, ucl);
    }

    /**
     * Loads a JAR-File (from {@link URL}) into the Runtime. Only works with java 8.
     *
     * @param url the {@link URL} to load
     * @param classLoader the {@link URLClassLoader} to load the jar file with
     */
    @SneakyThrows
    public static void load(URL url, URLClassLoader classLoader) {
        ReflectUtil.forceCall(URLClassLoader.class, classLoader, "addURL()",
                Collections.singletonList(new ReflectUtil.Parameter<>(URL.class, url)));
    }

}