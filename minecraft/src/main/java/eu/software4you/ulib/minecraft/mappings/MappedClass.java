package eu.software4you.ulib.minecraft.mappings;

import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a name-mapped class.
 * <p>
 * Note: {@link #sourceName()} and {@link #mappedName()} return fully qualified class names.
 */
public interface MappedClass extends Mapped<Class<?>> {
    /**
     * Attempts to find the class using {@link #mappedName()}.
     * <p>
     * Does not initialize the class.
     *
     * @return the class
     * @see Class#forName(String)
     */
    @NotNull
    Expect<Class<?>, ?> find();
}
