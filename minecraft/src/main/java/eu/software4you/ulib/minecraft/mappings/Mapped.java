package eu.software4you.ulib.minecraft.mappings;

import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;

/**
 * Represents something that may be name-mapped.
 *
 * @param <T> the type of whatever this mapping is representing
 */
public interface Mapped<T> {
    /**
     * Returns the source (original) name
     *
     * @return the source name
     */
    @NotNull
    String sourceName();

    /**
     * Returns the mapped name.
     *
     * @return the mapped name
     */
    @NotNull
    String mappedName();

    /**
     * Determines if the representing {@link T} is mapped.
     * <p>
     * This method is equal to: {@code !Mapped#getSourceName().equals(Mapped#getMappedName())}.
     *
     * @return {@code true}, if the representing class is name-mapped
     */
    boolean mapped();

    /**
     * Attempts to find the representing {@link T} using {@link #mappedName()} as source.
     *
     * @return T
     */
    @NotNull
    Expect<T, ?> find();
}
