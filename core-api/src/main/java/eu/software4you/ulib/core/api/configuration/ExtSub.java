package eu.software4you.ulib.core.api.configuration;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * A {@link Sub} with extended functionality and shortcuts.
 */
public interface ExtSub extends Sub {

    /**
     * Searches the configuration for a specific key and converts the value to a specified type.
     * <p>
     * In contrast to {@link #get(String)}, this method never throws a exception.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @param <T>  the type to convert to
     * @return an {@link Optional} holding the value
     */
    @NotNull
    default <T> Optional<T> get2(@NotNull String path) {
        return get2(path, null);
    }

    /**
     * Searches the configuration for a specific key and converts the value to a specified type.
     * <p>
     * In contrast to {@link #get(String, Object)}, this method never throws a exception.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @param def  the default value that will be returned if {@code path} not found
     * @param <T>  the type to convert to
     * @return an {@link Optional} holding the value
     */
    @NotNull <T> Optional<T> get2(@NotNull String path, @Nullable T def) throws IllegalArgumentException;

    /**
     * Gets a sub, creates it if it does not already exist.
     * <p>
     * When creating the new sub, any previous associated value will be overwritten.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return the sub
     */
    @NotNull
    Sub subAndCreate(@NotNull String path);

    /* primitive types */

    /**
     * Reads a boolean. Defaults to {@code false}.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return the read boolean, or {@code false} if {@code path} does not exist
     */
    default boolean bool(@NotNull String path) {
        return bool(path, false);
    }

    /**
     * Reads a boolean.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @param def  default value that will be returned if {@code path} does not exist
     * @return the boolean
     */
    boolean bool(@NotNull String path, boolean def);

    /**
     * Reads a signed 32 bit integer (int). Defaults to {@code -1}.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return the read integer, or {@code -1} if {@code path} does not exist
     */
    default int int32(@NotNull String path) {
        return int32(path, -1);
    }

    /**
     * Reads a signed 32 bit integer (int).
     *
     * @param path the key path; nodes seperated by {@code .}
     * @param def  default value that will be returned if {@code path} does not exist
     * @return the integer
     */
    int int32(@NotNull String path, int def);

    /**
     * Reads a signed 64 bit integer (long). Defaults to {@code -1}.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return the read long, or {@code -1} if {@code path} does not exist
     */
    default long int64(@NotNull String path) {
        return int64(path, -1L);
    }

    /**
     * Reads a signed 64 bit integer (long).
     *
     * @param path the key path; nodes seperated by {@code .}
     * @param def  default value that will be returned if {@code path} does not exist
     * @return the long
     */
    long int64(@NotNull String path, long def);

    /**
     * Reads a signed 32 bit decimal (float). Defaults to {@code -1}.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return the read float, or {@code -1} if {@code path} does not exist
     */
    default float dec32(@NotNull String path) {
        return dec32(path, -1f);
    }

    /**
     * Reads a signed 32 bit decimal (float).
     *
     * @param path the key path; nodes seperated by {@code .}
     * @param def  default value that will be returned if {@code path} does not exist
     * @return the float
     */
    float dec32(@NotNull String path, float def);

    /**
     * Reads a signed 64 bit decimal (double). Defaults to {@code -1}.
     *
     * @param path the key path; nodes seperated by {@code .}
     * @return the read double, or {@code -1} if {@code path} does not exist
     */
    default double dec64(@NotNull String path) {
        return dec64(path, -1d);
    }

    /**
     * Reads a signed 64 bit decimal (double).
     *
     * @param path the key path; nodes seperated by {@code .}
     * @param def  default value that will be returned if {@code path} does not exist
     * @return the double
     */
    double dec64(@NotNull String path, double def);

    /**
     * Reads a String and processes it with {@link String#format(String, Object...)} if {@code replacements} are given.
     *
     * @param path         the key path; nodes seperated by {@code .}
     * @param replacements the replacements
     * @return the processed string, or {@code null} if {@code path} does not exist
     * @see String#format(String, Object...)
     */
    @Nullable
    default String string(@NotNull String path, Object... replacements) {
        return string(path, null, replacements);
    }

    /* reference types */

    /**
     * Reads a String and processes it with {@link String#format(String, Object...)} if {@code replacements} are given.
     *
     * @param path         the key path; nodes seperated by {@code .}
     * @param def          default value that will be returned if {@code path} does not exist
     * @param replacements the replacements
     * @return the processed string, or {@code def} if {@code path} does not exist
     * @see String#format(String, Object...)
     */
    @Nullable
    @Contract("_, !null, _ -> !null")
    default String string(@NotNull String path, @Nullable String def, Object... replacements) {
        return get(String.class, path, def, replacements);
    }

    /**
     * Reads a String type List and processes each entry with {@link String#format(String, Object...)}
     * if {@code replacements} are given.
     *
     * @param path         the key path; nodes seperated by {@code .}
     * @param replacements the replacements
     * @return the processed string list, or {@code null} if {@code path} does not exist
     * @see String#format(String, Object...)
     */
    @Nullable
    default List<String> stringList(@NotNull String path, Object... replacements) {
        return stringList(path, null, replacements);
    }

    /**
     * Reads a String type List and processes each entry with {@link String#format(String, Object...)}
     * if {@code replacements} are given.
     *
     * @param path         the key path; nodes seperated by {@code .}
     * @param replacements the replacements
     * @param def          default value that will be returned if {@code path} does not exist
     * @return the processed string list, or {@code def} if {@code path} does not exist
     * @see String#format(String, Object...)
     */
    @Nullable
    @Contract("_, !null, _ -> !null")
    default List<String> stringList(@NotNull String path, @Nullable List<String> def, Object... replacements) {
        return list(String.class, path, def, replacements);
    }

    /**
     * Reads a value and tries to convert it to the given type. If the value is a String type, it is processed
     * with {@link String#format(String, Object...)} if {@code replacements} are given.
     *
     * @param clazz        the type class
     * @param path         the key path; nodes seperated by {@code .}
     * @param replacements the replacements
     * @param <T>          the type
     * @return the converted value, or {@code null} if {@code path} does not exist
     * @throws NumberFormatException    if {@code <T>} is a number type but the read value cannot be parsed to it
     * @throws IllegalArgumentException if read value is not of type {@code <T>}
     * @see String#format(String, Object...)
     */
    @Nullable
    default <T> T get(@NotNull Class<T> clazz, @NotNull String path, Object... replacements) {
        return get(clazz, path, null, replacements);
    }

    /**
     * Reads a value and tries to convert it to the given type. If the value is a String type, it is processed
     * with {@link String#format(String, Object...)} if {@code replacements} are given.
     *
     * @param clazz        the type class
     * @param path         the key path; nodes seperated by {@code .}
     * @param def          default value that will be returned if {@code path} does not exist
     * @param replacements the replacements
     * @param <T>          the type
     * @return the converted value, or {@code def} if {@code path} does not exist
     * @throws NumberFormatException    if {@code <T>} is a number type but the read value cannot be parsed to it
     * @throws IllegalArgumentException if read value is not of type {@code <T>}
     * @see String#format(String, Object...)
     */
    @Nullable
    @Contract("_, _, !null, _ -> !null")
    <T> T get(@NotNull Class<T> clazz, @NotNull String path, @Nullable T def, Object... replacements);

    /**
     * Reads a list and tries to convert each entry to the given type. If the entries are a String type,
     * they are processed with {@link String#format(String, Object...)} if {@code replacements} are given.
     *
     * @param clazz        the type class
     * @param path         the key path; nodes seperated by {@code .}
     * @param replacements the replacements
     * @param <T>          the type
     * @return the list containing the converted values, or {@code null} if {@code path} does not exist
     * @throws IllegalArgumentException if a entry is not of type {@code <T>}
     * @see String#format(String, Object...)
     */
    @Nullable
    default <T> List<T> list(@NotNull Class<T> clazz, @NotNull String path, Object... replacements) {
        return list(clazz, path, null, replacements);
    }

    /**
     * Reads a list and tries to convert each entry to the given type. If the entries are a String type,
     * they are processed with {@link String#format(String, Object...)} if {@code replacements} are given.
     *
     * @param clazz        the type class
     * @param path         the key path; nodes seperated by {@code .}
     * @param def          default value that will be returned if {@code path} does not exist
     * @param replacements the replacements
     * @param <T>          the type
     * @return the list containing the converted values, or {@code def} if {@code path} does not exist
     * @throws IllegalArgumentException if a entry is not of type {@code <T>}
     * @see String#format(String, Object...)
     */
    @Nullable
    @Contract("_, _, !null, _ -> !null")
    <T> List<T> list(@NotNull Class<T> clazz, @NotNull String path, @Nullable List<T> def, Object... replacements);
}
