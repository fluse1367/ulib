package eu.software4you.ulib.core.common;

import org.jetbrains.annotations.NotNull;

/**
 * Represents something that has a key.
 *
 * @param <T> type of the key
 */
public interface Keyable<T> {
    /**
     * @return the key
     */
    @NotNull
    T getKey();
}
