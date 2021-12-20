package eu.software4you.ulib.core.api.dependencies;

import eu.software4you.ulib.core.api.internal.Providers;
import eu.software4you.ulib.core.api.reflect.ReflectUtil;

import java.io.File;
import java.util.function.Consumer;

import static eu.software4you.ulib.core.api.dependencies.Repositories.mavenCentral;

/**
 * Loads libraries (from maven repositories).
 * <p>
 * By default the fallback policy is set to {@code true}.
 */
public abstract class Dependencies {
    private static Dependencies getInstance() {
        return Providers.get(Dependencies.class);
    }

    /**
     * Resets the fallback policy to the implementation default.
     */
    public static void resetFallbackPolicy() {
        getInstance().resetFallbackPolicy0();
    }

    /**
     * Sets the behavior if a dependency fails to load.
     *
     * @param fallback {@code true}, if the dependency should be loaded via the system class loader instead
     */
    public static void setFallbackPolicy(boolean fallback) {
        getInstance().setFallbackPolicy0(fallback);
    }

    /**
     * (Down-)loads a maven library from the maven-central repository with the class loader of the calling class.
     *
     * @param coords the library maven coordinates
     * @see Repositories#mavenCentral()
     * @see #depend(String, Repository)
     */
    public static void depend(String coords) {
        getInstance().depend0(coords, mavenCentral(), ReflectUtil.getCallerClass().getClassLoader());
    }

    /**
     * (Down-)loads a maven library from the maven-central repository.
     *
     * @param coords the library maven coordinates
     * @param loader the loader to load the file
     * @see Repositories#mavenCentral()
     * @see #depend(String, Repository, Consumer)
     */
    public static void depend(String coords, Consumer<File> loader) {
        getInstance().depend0(coords, mavenCentral(), loader);
    }

    /**
     * (Down-)loads a maven library with the class loader of the calling class.
     *
     * @param coords     the library maven coordinates
     * @param repository the repository to search in
     * @see #depend(String, Repository, ClassLoader)
     */
    public static void depend(String coords, Repository repository) {
        getInstance().depend0(coords, repository, ReflectUtil.getCallerClass().getClassLoader());
    }

    /**
     * (Down-)loads a maven library with a specific class loader.
     * <p>
     * Allows for the same library with different versions to be loaded simultaneously (via different class loaders).
     * <b>This is achieved by injecting code into the class loaders.</b>
     * No code will be injected into the system class loader, instead the respective files will be added to
     *
     * @param coords     the library maven coordinates
     * @param repository the repository to search in
     * @param source     the class loader that should load the library
     * @see Repositories
     * @see eu.software4you.ulib.core.api.transform.HookInjector
     */
    public static void depend(String coords, Repository repository, ClassLoader source) {
        getInstance().depend0(coords, repository, source);
    }

    /**
     * (Down-)loads a maven library. Requires a loader that effectively loads the library.
     *
     * @param coords     the library maven coordinates
     * @param repository the repository to search in
     * @param loader     the loader to load the file
     * @see Repositories
     */
    public static void depend(String coords, Repository repository, Consumer<File> loader) {
        getInstance().depend0(coords, repository, loader);
    }

    /**
     * (Down-)loads a maven library from the maven-central repository with the class loader of the calling class.
     * Only loads the library if the {@code testClass} is <b>not</b> available with the class loader of the calling class.
     *
     * @param coords    the library maven coordinates
     * @param testClass fully qualified name of the test-class
     * @see Repositories#mavenCentral()
     * @see #depend(String, Repository)
     */
    public static void depend(String coords, String testClass) {
        getInstance().depend0(coords, testClass, mavenCentral(), ReflectUtil.getCallerClass().getClassLoader());
    }

    /**
     * (Down-)loads a maven library from the maven-central repository.
     * Only loads the library if the {@code testClass} is <b>not</b> available with the class loader of the calling class.
     *
     * @param coords    the library maven coordinates
     * @param testClass fully qualified name of the test-class
     * @param loader    the loader to load the file
     * @see Repositories#mavenCentral()
     * @see #depend(String, Repository, Consumer)
     */
    public static void depend(String coords, String testClass, Consumer<File> loader) {
        getInstance().depend0(coords, testClass, ReflectUtil.getCallerClass().getClassLoader(), mavenCentral(), loader);
    }

    /**
     * (Down-)loads a maven library with the class loader of the calling class.
     * Only loads the library if the {@code testClass} is <b>not</b> available with the class loader of the calling class.
     *
     * @param coords     the library maven coordinates
     * @param testClass  fully qualified name of the test-class
     * @param repository the repository to search in
     * @see #depend(String, Repository, ClassLoader)
     */
    public static void depend(String coords, String testClass, Repository repository) {
        getInstance().depend0(coords, testClass, repository, ReflectUtil.getCallerClass().getClassLoader());
    }

    /**
     * (Down-)loads a maven library with a specific class loader.
     * Only loads the library if the {@code testClass} is <b>not</b> available with the class loader of the calling class.
     * <p>
     * Allows for the same library with different versions to be loaded simultaneously (via different class loaders).
     * <b>This is achieved by injecting code into the class loaders.</b>
     *
     * @param coords     the library maven coordinates
     * @param testClass  fully qualified name of the test-class
     * @param repository the repository to search in
     * @param source     the class loader that should load the library
     * @see Repositories
     * @see eu.software4you.ulib.core.api.transform.HookInjector
     */
    public static void depend(String coords, String testClass, Repository repository, ClassLoader source) {
        getInstance().depend0(coords, testClass, repository, source);
    }

    // basically the same as above, but with class-testing

    /**
     * (Down-)loads a maven library. Requires a loader that effectively loads the library.
     * Only loads the library if the {@code testClass} is <b>not</b> available with the class loader of the calling class.
     *
     * @param coords     the library maven coordinates
     * @param testClass  fully qualified name of the test-class
     * @param testLoader the class loader to search in for the {@code testClass}
     * @param repository the repository to search in
     * @param loader     the loader to load the file
     * @see Repositories
     */
    public static void depend(String coords, String testClass, ClassLoader testLoader, Repository repository, Consumer<File> loader) {
        getInstance().depend0(coords, testClass, testLoader, repository, loader);
    }

    protected abstract void resetFallbackPolicy0();

    protected abstract void setFallbackPolicy0(boolean fallback);

    protected abstract void depend0(String coords, Repository repository, ClassLoader source);

    protected abstract void depend0(String coords, Repository repository, Consumer<File> loader);

    protected abstract void depend0(String coords, String testClass, Repository repository, ClassLoader source);

    protected abstract void depend0(String coords, String testClass, ClassLoader testLoader, Repository repository, Consumer<File> loader);
}
