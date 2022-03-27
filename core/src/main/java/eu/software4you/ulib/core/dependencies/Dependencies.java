package eu.software4you.ulib.core.dependencies;

import eu.software4you.ulib.core.impl.maven.MavenController;
import eu.software4you.ulib.core.inject.ClassLoaderDelegation;
import eu.software4you.ulib.core.inject.InjectUtil;
import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Stream;

/**
 * Access point for requiring maven artifacts.
 *
 * @see Repository#of(String)
 */
public class Dependencies {

    /**
     * Attempts to require a maven artifact.
     *
     * @param coords       the maven coordinates of the artifact
     * @param repositories the repositories to search the artifact in
     * @return an expect object wrapping either a stream with the provided jar files, or an exception on execution failure
     * @see Repository#of(String)
     */
    @NotNull
    public static Expect<Stream<File>, Exception> require(@NotNull String coords, @NotNull Collection<Repository> repositories) {
        return Expect.compute(MavenController::require, coords, repositories);
    }

    /**
     * Attempts to require a maven artifact.
     *
     * @param coords     the maven coordinates of the artifact
     * @param repository the repository to search the artifact in
     * @return an expect object wrapping either a stream with the provided jar files, or an exception on execution failure
     * @see Repository#of(String)
     */
    @NotNull
    public static Expect<Stream<File>, Exception> require(@NotNull String coords, @NotNull Repository repository) {
        return require(coords, toColl(repository));
    }

    /**
     * Attempts to require a maven artifact and inject a class loading hook into the given class loader.
     *
     * @param coords          the maven coordinates of the artifact
     * @param repositories    the repositories to search the artifact in
     * @param injectionTarget the class loader to inject the class loading hook in
     * @return an expect object wrapping the execution result
     */
    @NotNull
    public static Expect<Void, Exception> requireInject(@NotNull String coords, @NotNull Collection<Repository> repositories, @NotNull ClassLoader injectionTarget) {
        return Expect.compute(() -> {
            // require artifacts and fetch file urls
            var urls = require(coords, repositories)
                    .orElseRethrow()
                    .map(File::toURI)
                    .map(uri -> Expect.compute(uri::toURL).orElseThrow())
                    .toArray(URL[]::new);

            ClassLoader cl = new URLClassLoader(urls);
            /*
                Check if `injectionTarget` is one of `cl`'s parents, if so, re-init `cl` with null parent.
                If the injection target instance is one of cl's parent an infinite recursion will occur,
                  because a regular class loader will always "ask" the parent first.
                If the parent happens to be the injection target it will then (bc of the injected hook)
                  "ask" `cl` again for the class (the recursion starts there).
             */
            ClassLoader parent = cl;
            do {
                if ((parent = parent.getParent()) == injectionTarget) {
                    cl = new URLClassLoader(urls, null);
                    break;
                }
            } while (parent != null);

            // finally, inject
            InjectUtil.injectLoaderDelegation(new ClassLoaderDelegation(cl), injectionTarget)
                    .rethrow();
        });
    }

    /**
     * Attempts to require a maven artifact and inject a class loading hook into the given class loader.
     *
     * @param coords          the maven coordinates of the artifact
     * @param repository      the repository to search the artifact in
     * @param injectionTarget the class loader to inject the class loading hook in
     * @return an expect object wrapping the execution result
     */
    @NotNull
    public static Expect<Void, Exception> requireInject(@NotNull String coords, @NotNull Repository repository, @NotNull ClassLoader injectionTarget) {
        return requireInject(coords, toColl(repository), injectionTarget);
    }

    private static Collection<Repository> toColl(Repository repository) {
        var central = Repository.mavenCentral();
        return central == repository ? Collections.singletonList(repository) : Arrays.asList(central, repository);
    }
}
