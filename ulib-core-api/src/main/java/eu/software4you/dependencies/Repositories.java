package eu.software4you.dependencies;

import eu.software4you.ulib.Await;

/**
 * Collection of common maven repositories.
 *
 * @see #of(String, String)
 */
public abstract class Repositories {
    @Await
    private static Repositories impl;

    /**
     * Maven central repository ({@code https://repo1.maven.org/maven2/}).
     *
     * @see <a href="https://maven.apache.org/repository/" target="_blank">https://maven.apache.org/repository/</a>
     */
    public static Repository mavenCentral() {
        return of("central");
    }


    /**
     * Jitpack repository ({@code https://jitpack.io}).
     *
     * @see <a href="https://jitpack.io" target="_blank">https://jitpack.io</a>
     */
    public static Repository jitpack() {
        return of("jitpack");
    }

    /**
     * JCenter repository ({@code https://jcenter.bintray.com}).
     *
     * @see <a href="https://bintray.com/" target="_blank">https://bintray.com/</a>
     */
    public static Repository jcenter() {
        return of("jcenter");
    }

    /**
     * Sonatype releases repository ({@code https://oss.sonatype.org/content/repositories/releases}).
     *
     * @see <a href="https://oss.sonatype.org/" target="_blank">https://oss.sonatype.org/</a>
     */
    public static Repository sonatype() {
        return of("sonatype");
    }

    /**
     * Creates a new repository instance (if cache does not already contain {@code id}).
     *
     * @param id  the id
     * @param url the url
     * @return the newly created (or cached) repository
     */
    public static Repository of(String id, String url) {
        return impl.of0(id, url);
    }

    /**
     * Returns a cached repository.
     *
     * @param id the id
     * @return the or cached repository, or {@code null} if the {@code id} is not cached
     */
    public static Repository of(String id) {
        return impl.of0(id);
    }

    public abstract Repository of0(String id, String url);

    public abstract Repository of0(String id);
}
