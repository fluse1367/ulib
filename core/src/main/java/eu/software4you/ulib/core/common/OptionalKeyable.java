package eu.software4you.ulib.core.common;

import java.util.Optional;

/**
 * Represents something that may have a key.
 *
 * @param <T> type of the key
 */
public interface OptionalKeyable<T> extends Keyable<Optional<T>> {
}
