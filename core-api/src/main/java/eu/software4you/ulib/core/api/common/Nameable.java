package eu.software4you.ulib.core.api.common;

import org.jetbrains.annotations.Nullable;

/**
 * Represents something that may have a name.
 */
public interface Nameable {

    /**
     * Returns the name.
     *
     * @return the name
     */
    @Nullable String getName();
}
