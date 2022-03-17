package eu.software4you.ulib.core.dependencies;

import eu.software4you.ulib.core.impl.maven.RepositoryImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents a maven remote repository.
 */
public interface Repository {

    /**
     * Maven central repository ({@code https://repo1.maven.org/maven2/}).
     *
     * @see <a href="https://maven.apache.org/repository/" target="_blank">https://maven.apache.org/repository/</a>
     */
    static Repository mavenCentral() {
        return RepositoryImpl.of("central");
    }


    /**
     * Jitpack repository ({@code https://jitpack.io}).
     *
     * @see <a href="https://jitpack.io" target="_blank">https://jitpack.io</a>
     */
    static Repository jitpack() {
        return RepositoryImpl.of("jitpack");
    }

    /**
     * JCenter repository ({@code https://jcenter.bintray.com}).
     *
     * @see <a href="https://bintray.com/" target="_blank">https://bintray.com/</a>
     */
    static Repository jcenter() {
        return RepositoryImpl.of("jcenter");
    }

    /**
     * Sonatype releases repository ({@code https://oss.sonatype.org/content/repositories/releases}).
     *
     * @see <a href="https://oss.sonatype.org/" target="_blank">https://oss.sonatype.org/</a>
     */
    static Repository sonatype() {
        return RepositoryImpl.of("sonatype");
    }

    /**
     * Creates a new repository instance (if cache does not already contain {@code id}).
     *
     * @param id  the id
     * @param url the url
     * @return the newly created (or cached) repository
     */
    @NotNull
    static Repository of(@NotNull String id, @NotNull String url) {
        return RepositoryImpl.of(id, url);
    }

    /**
     * Returns a cached repository.
     *
     * @param id the id
     * @return an optional wrapping the or cached repository, or an empty optional if the {@code id} is not cached
     */
    @NotNull
    static Optional<Repository> of(@NotNull String id) {
        return Optional.ofNullable(RepositoryImpl.of(id));
    }

    /**
     * @return the id of the repository
     */
    String getId();

    /**
     * @return the url of the repository
     */
    String getUrl();

}
