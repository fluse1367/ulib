package eu.software4you.common;

import org.jetbrains.annotations.NotNull;

/**
 * Represents something that has a key.
 *
 * @param <T> type of the key
 */
public interface Keyable<T> {
    /**
     * Returns the key
     *
     * @return the key
     */
    @NotNull
    T getKey();
}
