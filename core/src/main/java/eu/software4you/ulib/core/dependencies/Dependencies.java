package eu.software4you.ulib.core.dependencies;

import eu.software4you.ulib.core.impl.maven.MavenController;
import eu.software4you.ulib.core.inject.ClassLoaderDelegation;
import eu.software4you.ulib.core.inject.InjectUtil;
import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

/**
 * Access point for requiring maven artifacts.
 *
 * @see Repository#of(String)
 */
public final class Dependencies {

    /**
     * Attempts to require a maven artifact.
     *
     * @param coords       the maven coordinates of the artifact
     * @param repositories the repositories to search the artifact in
     * @return an expect object wrapping either a stream with the provided jar files, or an exception on execution failure
     * @see Repository#of(String)
     */
    @NotNull
    public static Expect<Stream<Path>, Exception> require(@NotNull String coords, @NotNull Collection<Repository> repositories) {
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
    public static Expect<Stream<Path>, Exception> require(@NotNull String coords, @NotNull Repository repository) {
        return require(coords, toColl(repository));
    }

    /**
     * Attempts to require maven artifacts and inject a class loading hook into the given class loader.
     *
     * @param coordsRepositoriesMap the maven coordinates of an artifact mapped to the respective repositories to search the artifact in
     * @param injectionTarget       the class loader to inject the class loading hook in
     * @return an expect object wrapping the execution result
     */
    @NotNull
    public static Expect<Void, Exception> requireAndInject(@NotNull Map<String, Collection<Repository>> coordsRepositoriesMap,
                                                           @NotNull ClassLoader injectionTarget) {
        return Expect.compute(() -> {
            // require artifacts and fetch file urls
            var urls = coordsRepositoriesMap.entrySet().stream()
                    .flatMap(en -> require(en.getKey(), en.getValue()).orElseThrow())
                    .map(Path::toUri)
                    .map(uri -> Expect.compute(uri::toURL).orElseThrow())
                    .toArray(URL[]::new);

            // inject
            InjectUtil.injectLoaderDelegation(new ClassLoaderDelegation(new URLClassLoader(urls)), injectionTarget)
                    .rethrow();
        });
    }

    /**
     * Attempts to require maven artifacts and inject a class loading hook into the given class loader.
     *
     * @param coordsRepositoryMap the maven coordinates of an artifact mapped to the respective repository to search the artifact in
     * @param injectionTarget     the class loader to inject the class loading hook in
     * @return an expect object wrapping the execution result
     */
    @NotNull
    public static Expect<Void, Exception> requireInject(@NotNull Map<String, Repository> coordsRepositoryMap, @NotNull ClassLoader injectionTarget) {
        @SuppressWarnings("unchecked")
        Map<String, Collection<Repository>> map = Map.ofEntries(
                coordsRepositoryMap.entrySet().stream()
                        .map(en -> new AbstractMap.SimpleEntry<>(en.getKey(), toColl(en.getValue())))
                        .toArray(Map.Entry[]::new));
        return requireAndInject(map, injectionTarget);
    }

    /**
     * Attempts to require maven artifacts and inject a class loading hook into the given class loader.
     *
     * @param coords          the maven coordinates of the artifacts
     * @param repositories    the repositories to search the artifact in
     * @param injectionTarget the class loader to inject the class loading hook in
     * @return an expect object wrapping the execution result
     */
    @NotNull
    public static Expect<Void, Exception> requireInject(@NotNull Collection<String> coords, @NotNull Collection<Repository> repositories,
                                                        @NotNull ClassLoader injectionTarget) {
        @SuppressWarnings("unchecked")
        Map<String, Collection<Repository>> map = Map.ofEntries(coords.stream()
                .map(coords_ -> new AbstractMap.SimpleEntry<>(coords_, repositories))
                .toArray(Map.Entry[]::new));
        return requireAndInject(map, injectionTarget);
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
    public static Expect<Void, Exception> requireInject(@NotNull String coords, @NotNull Collection<Repository> repositories,
                                                        @NotNull ClassLoader injectionTarget) {
        return requireInject(Collections.singleton(coords), repositories, injectionTarget);
    }

    /**
     * Attempts to require maven artifacts and inject a class loading hook into the given class loader.
     *
     * @param coords          the maven coordinates of the artifacts
     * @param repository      the repository to search the artifacts in
     * @param injectionTarget the class loader to inject the class loading hook in
     * @return an expect object wrapping the execution result
     */
    @NotNull
    public static Expect<Void, Exception> requireInject(@NotNull Collection<String> coords, @NotNull Repository repository,
                                                        @NotNull ClassLoader injectionTarget) {
        return requireInject(coords, toColl(repository), injectionTarget);
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
    public static Expect<Void, Exception> requireInject(@NotNull String coords, @NotNull Repository repository,
                                                        @NotNull ClassLoader injectionTarget) {
        return requireInject(Map.of(coords, repository), injectionTarget);
    }

    private static Collection<Repository> toColl(Repository repository) {
        var central = Repository.mavenCentral();
        return central == repository ? Collections.singletonList(repository) : Arrays.asList(central, repository);
    }
}
