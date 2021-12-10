package eu.software4you.ulib.core.api.dependencies;

import eu.software4you.ulib.core.ULib;
import eu.software4you.ulib.core.api.reflect.ReflectUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * A utility to load (jar-) files.
 */
public abstract class DependencyLoader {
    private static DependencyLoader impl() {
        return ULib.service(DependencyLoader.class);
    }

    /**
     * Removes the {@code classLoader} from all internal references.
     * <p>
     * The {@code classLoader} will no longer load any classes from additionally loaded libraries (files loaded using this class and {@link Dependencies}).
     * Already loaded classes will not be "unloaded" by this method.
     *
     * @param classLoader the class loader
     */
    public static void free(ClassLoader classLoader) {
        impl().free0(classLoader);
    }

    /**
     * Loads a JAR-File (from {@link File}) with the system class loader.
     *
     * @param file the jar file to load
     */
    public static void sysLoad(@NotNull File file) {
        impl().sysLoad0(file);
    }

    /**
     * Tries to load a JAR-File (from {@link File}) with the {@link ClassLoader} from the calling class.
     * This method is equivalent with {@code load(file, false)}.
     * <p>
     * The file's classes will only be available exclusively to classes loaded with that specific {@code classLoader}.
     *
     * @param file the jar file to load
     * @see Dependencies#depend(String, Repository, ClassLoader)
     */
    public static void load(@NotNull File file) {
        impl().load0(file, ReflectUtil.getCallerClass().getClassLoader(), false, true);
    }

    /**
     * Tries to load a JAR-File (from {@link File}) with the {@link ClassLoader} from the calling class.
     * <p>
     * The file's classes will only be available exclusively to classes loaded with that specific {@code classLoader}.
     *
     * @param file     the jar file to load
     * @param fallback if the file should be loaded via the system class loader as fallback procedure on failure
     * @see Dependencies#depend(String, Repository, ClassLoader)
     */
    public static void load(@NotNull File file, boolean fallback) {
        impl().load0(file, ReflectUtil.getCallerClass().getClassLoader(), fallback, true);
    }

    /**
     * Attempts to load a JAR-File (from {@link File}) with the provided {@link ClassLoader}.
     * This method is equivalent with {@code load(file, classLoader, false)}.
     * <p>
     * The file's classes will only be available exclusively to classes loaded with that specific {@code classLoader}.
     *
     * @param file the jar file to load
     * @see Dependencies#depend(String, Repository, ClassLoader)
     */
    public static void load(@NotNull File file, @NotNull ClassLoader classLoader) {
        impl().load0(file, classLoader, false, true);
    }

    /**
     * Attempts to load a JAR-File (from {@link File}) with the provided {@link ClassLoader}.
     * <p>
     * The file's classes will only be available exclusively to classes loaded with that specific {@code classLoader}.
     *
     * @param file     the jar file to load
     * @param fallback if the file should be loaded via the system class loader as fallback procedure on failure
     * @see Dependencies#depend(String, Repository, ClassLoader)
     */
    public static void load(@NotNull File file, @NotNull ClassLoader classLoader, boolean fallback) {
        impl().load0(file, classLoader, fallback, true);
    }

    /**
     * Attempts to load a JAR-File (from {@link File}) with the provided {@link ClassLoader}.
     * <p>
     * If {@code exclusive} is {@code true}, no fallback will be attempted, regardless to the value of {@code fallback}.
     *
     * @param file      the jar file to load
     * @param fallback  if the file should be loaded via the system class loader as fallback procedure on failure
     * @param exclusive if the file should be available exclusively to classes loaded with that specific {@code classLoader}
     * @see Dependencies#depend(String, Repository, ClassLoader)
     */
    public static void load(@NotNull File file, @NotNull ClassLoader classLoader, boolean fallback, boolean exclusive) {
        impl().load0(file, classLoader, fallback, exclusive);
    }

    /**
     * Tries to load a JAR-File (from {@link URL}) with the {@link ClassLoader} from the calling class.
     * This method <b>does not</b> fall back to system class loader loading.
     *
     * @param url the {@link URL} to load
     */
    public static void load(@NotNull URL url) {
        impl().load0(url);
    }

    /**
     * Tries to load a JAR-File (from {@link URL}).
     *
     * @param url         the {@link URL} to load
     * @param classLoader the {@link URLClassLoader} to load the jar file with
     */
    public static void load(@NotNull URL url, @NotNull URLClassLoader classLoader) {
        impl().load0(url, classLoader);
    }

    protected abstract void free0(ClassLoader classLoader);

    protected abstract void sysLoad0(File file);

    protected abstract void load0(File file, ClassLoader classLoader, boolean fallback, boolean exclusive);

    protected abstract void load0(URL url);

    protected abstract void load0(URL url, URLClassLoader classLoader);
}