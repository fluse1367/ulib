package eu.software4you.ulib.core.configuration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Representation of a configuration document sub.
 */
public interface Configuration {

    /**
     * Uses a function to obtain two values from the supplied configuration instances with the same key and applies a mapping to them.
     *
     * @param c1     the first configuration
     * @param c2     the second configuration
     * @param key    the key
     * @param getter the function that obtains the values corresponding to the ke
     * @param mapper the mapping function
     * @return the mapping result
     */
    @NotNull
    static <C extends Configuration, T, R> Optional<R> map(@NotNull C c1, @NotNull C c2, @NotNull String key,
                                                           @NotNull BiFunction<? super C, String, Optional<T>> getter,
                                                           @NotNull BiFunction<T, T, R> mapper) {
        var val1 = getter.apply(c1, key);
        var val2 = getter.apply(c2, key);

        if (val1.isEmpty() || val2.isEmpty())
            return Optional.empty();

        return Optional.ofNullable(mapper.apply(val1.get(), val2.get()));
    }

    /**
     * Returns the root of this sub.
     * <p>
     * The root is the highest available parent sub.
     *
     * @return the root
     */
    @NotNull
    Configuration getRoot();

    /**
     * Determines whether this sub is the root.
     *
     * @return {@code true}, if this sub is the root
     */
    boolean isRoot();

    /**
     * Searches the configuration for a specific key and converts the value to the specified type.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return an optional wrapping the value, or empty optional if {@code path} not found or the value couldn't get converted
     */
    @NotNull Optional<Object> get(@NotNull String path);

    /**
     * Reads a value and attempts to convert it to the given type.
     *
     * @param clazz the type class
     * @param path  the key path; nodes seperated by {@code .}
     * @param <T>   the type
     * @return an optional wrapping the value, or an empty optional if there is no boolean at the path specified
     * @throws NumberFormatException    if {@code <T>} is a number type but the read value cannot be parsed to it
     * @throws IllegalArgumentException if read value is not of type {@code <T>}
     * @see String#format(String, Object...)
     */
    @NotNull <T> Optional<T> get(@NotNull Class<T> clazz, @NotNull String path);

    @NotNull
    default <T, R> Optional<R> map(@NotNull Configuration c, @NotNull String key,
                                   @NotNull BiFunction<? super Configuration, String, Optional<T>> getter,
                                   @NotNull BiFunction<T, T, R> mapper) {
        return Configuration.map(this, c, key, getter, mapper);
    }


    /**
     * Reads a list or an array and attempts to cast each element to the given type.
     * If not all elements can be casted, an empty optional is returned instead.
     *
     * @param clazz the type class
     * @param path  the key path; nodes seperated by {@code .}
     * @param <T>   the type
     * @return an optional wrapping the value, or an empty optional if there is no boolean at the path specified
     * @throws IllegalArgumentException if a entry is not of type {@code <T>}
     */
    @NotNull <T> Optional<List<T>> list(@NotNull Class<T> clazz, @NotNull String path);

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
     * Determines if this sub is empty, meaning is does not hold any values or other subs.
     *
     * @return {@code true} if this sub is empty, {@code false otherwise}
     */
    boolean isEmpty();

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
     * Determines whether a certain key has a value assigned to it (cannot be a sub).
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return {@code true}, if the {@code path} holds a value
     */
    boolean isSet(@NotNull String path);

    /**
     * Determines whether a certain key has a value assigned to it (can be a sub).
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return {@code true}, if the {@code path} is known
     */
    boolean contains(@NotNull String path);

    /**
     * Searches the configuration for a configuration-sub with a specific key.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return an optional wrapping the sub, or an empty optional if {@code path} not found
     */
    @NotNull
    Optional<? extends Configuration> getSub(@NotNull String path);

    /**
     * Creates a new sub at the given key.
     * <p>
     * Any previous associated value will be overwritten.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return the newly created sub
     */
    @NotNull
    Configuration createSub(@NotNull String path);

    /**
     * Collects all subs of this sub.
     *
     * @param deep if the sub-subs should also get collected (recursively)
     * @return a collection containing all subs.
     */
    @NotNull
    Collection<? extends Configuration> getSubs(boolean deep);

    /**
     * Determines whether a certain key holds a configuration-sub.
     * <p>
     * This method will also return false if the {@code path} is not set or if it holds a non-configuration-sub value.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return {@code true}, if the {@code path} holds a sub
     */
    boolean isSub(@NotNull String path);

    /**
     * Gets a sub, creates it if it does not already exist.
     * <p>
     * When creating the new sub, any previous associated value will be overwritten.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return the sub
     */
    @NotNull
    Configuration subAndCreate(@NotNull String path);

    /**
     * Harmonizes this sub to the given one.
     * <p>
     * Each value present in the base be set to the if it's not present in this sub. The base will not be modified.
     * <p>
     * You might want to purge the sub after a strict convergence.
     *
     * @param base   the sub to converge/harmonize to
     * @param strict if values not present in the base should be removed correspondingly
     * @param deep   if the converging should also be applied recursively to sub-subs
     * @see #purge(boolean)
     */
    void converge(@NotNull Configuration base, boolean strict, boolean deep);

    /**
     * Removes all subs that do not hold any values.
     *
     * @param deep if sub-subs should be purged as well (recursively)
     */
    void purge(boolean deep);

    /**
     * Reads a boolean.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return an optional wrapping the value, or an empty optional if there is no boolean at the path specified
     */
    @NotNull
    Optional<Boolean> bool(@NotNull String path);

    /**
     * Reads a signed 32 bit integer (int).
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return an optional wrapping the value, or an empty optional if there is no boolean at the path specified
     */
    @NotNull
    Optional<Integer> int32(@NotNull String path);


    /**
     * Reads a signed 64 bit integer (long).
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return an optional wrapping the value, or an empty optional if there is no boolean at the path specified
     */
    @NotNull
    Optional<Long> int64(@NotNull String path);


    /**
     * Reads a signed 32 bit decimal (float).
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return an optional wrapping the value, or an empty optional if there is no boolean at the path specified
     */
    @NotNull
    Optional<Float> dec32(@NotNull String path);


    /**
     * Reads a signed 64 bit decimal (double).
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return an optional wrapping the value, or an empty optional if there is no boolean at the path specified
     */
    @NotNull
    Optional<Double> dec64(@NotNull String path);

    /**
     * Reads a String and processes it with {@link String#format(String, Object...)} if {@code replacements} are given.
     *
     * @param path         the key path; nodes seperated by {@code .}
     * @param replacements the replacements
     * @return an optional wrapping the value, or an empty optional if there is no boolean at the path specified
     * @see String#format(String, Object...)
     */
    @NotNull
    default Optional<String> string(@NotNull String path, @NotNull Object... replacements) {
        return get(String.class, path)
                .map(str -> str.formatted(replacements));
    }


    /**
     * Reads a String type List and processes each entry with {@link String#format(String, Object...)}
     * if {@code replacements} are given.
     *
     * @param path         the key path; nodes seperated by {@code .}
     * @param replacements the replacements
     * @return the processed string list, or {@code def} if {@code path} does not exist
     * @see String#format(String, Object...)
     */
    @NotNull
    default Optional<List<String>> stringList(@NotNull String path, @NotNull Object... replacements) {
        return list(String.class, path)
                .map(list -> {
                    var res = new ArrayList<String>(list.size());
                    list.forEach(str -> {
                        res.add(str.formatted(replacements));
                    });
                    return res;
                });
    }
}
