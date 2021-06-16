package eu.software4you.configuration;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * Representation of a configuration document sub.
 */
public interface Sub {

    /**
     * Returns the root of this sub.
     * <p>
     * The root is the highest available parent sub.
     *
     * @return the root
     */
    @NotNull
    Sub getRoot();

    /**
     * Determines whether this sub is the root.
     *
     * @return {@code true}, if this sub is the root
     */
    boolean isRoot();

    /**
     * Sets what to do if a value cannot be converted to the requested type.
     * <p>
     * There are two options:
     * <ul>
     *     <li>Return the supplied default value</li>
     *     <li>Throw an exception</li>
     * </ul>
     * By default the default value will be returned.
     * <p>
     * The policy applies to all subs of the current document.
     *
     * @param throwing {@code true} if an exception should be thrown, {@code false} if the default value should be returned
     */
    void setConversionPolicy(boolean throwing);

    /**
     * Searches the configuration for a specific key and converts the value to a specified type.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @param <T>  the type to convert to
     * @return the value, or {@code null} if {@code path} not found
     * @throws IllegalArgumentException if conversion policy is set to throwing and the value cannot be converted
     * @see #setConversionPolicy(boolean)
     */
    @Nullable
    default <T> T get(@NotNull String path) {
        return get(path, null);
    }

    /**
     * Searches the configuration for a specific key and converts the value to a specified type.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @param def  the default value that will be returned if {@code path} not found
     * @param <T>  the type to convert to
     * @return the value
     * @throws IllegalArgumentException if conversion policy is set to throwing and the value cannot be converted
     * @see #setConversionPolicy(boolean)
     */
    @Nullable
    @Contract("_, !null -> !null")
    <T> T get(@NotNull String path, @Nullable T def) throws IllegalArgumentException;

    /**
     * Collects all keys from this sub.
     *
     * @param deep if keys from lower subs should be collected as well (flattened)
     * @return a collection with all the keys
     */
    @NotNull
    Collection<String> getKeys(boolean deep);

    /**
     * Collects all values from this sub.
     *
     * @param deep if values from lower subs should be collected as well (flattened)
     * @return a key-value map
     */
    @NotNull
    Map<String, Object> getValues(boolean deep);

    /**
     * Sets a key to a specific value in the configuration.
     * <p>
     * A {@code null} value removes the key from the configuration.
     * <p>
     * Any previous associated value will be overwritten.
     *
     * @param path  the key path; nodes seperated by {@code .}
     * @param value the value
     * @throws IllegalStateException if the sub cannot hold keyed values. This is the case if the sub was directly loaded with not-keyed data.
     */
    void set(@NotNull String path, @Nullable Object value);

    /**
     * Determines whether a certain key holds any value.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return {@code true}, if the {@code path} holds a value
     */
    boolean isSet(@NotNull String path);

    /**
     * Determines whether a certain key holds is included in this sub.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return {@code true}, if the {@code path} is known
     */
    boolean contains(@NotNull String path);

    /**
     * Clears all data from this sub.
     */
    void reset();

    /**
     * Searches the configuration for a configuration-sub with a specific key.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return the sub, or {@code null} if {@code path} not found
     */
    @Nullable
    Sub getSub(@NotNull String path);

    /**
     * Creates a new sub at the given key.
     * <p>
     * Any previous associated value will be overwritten.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return the newly created sub
     */
    @NotNull
    Sub createSub(@NotNull String path);

    /**
     * Collects all subs of this sub.
     *
     * @return a collection containing all subs.
     */
    @NotNull
    Collection<? extends Sub> getSubs();

    /**
     * Determines whether a certain key holds a configuration-sub.
     * <p>
     * This method will also return false if the {@code path} is not set or if it holds a non-configuration-sub value.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return {@code true}, if the {@code path} holds a sub
     */
    boolean isSub(@NotNull String path);
}
