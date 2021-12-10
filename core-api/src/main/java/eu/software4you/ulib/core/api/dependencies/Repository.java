package eu.software4you.ulib.core.api.dependencies;

/**
 * Represents a maven repository.
 */
public interface Repository {

    /**
     * Returns the id of the repository.
     *
     * @return the id of the repository
     */
    String getId();

    /**
     * Returns the url of the repository.
     *
     * @return the url of the repository
     */
    String getUrl();
}
