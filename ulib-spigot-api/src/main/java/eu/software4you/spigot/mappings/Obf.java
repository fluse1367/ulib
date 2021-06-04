package eu.software4you.spigot.mappings;

import org.jetbrains.annotations.NotNull;

/**
 * Represents something that may be name-obfuscated.
 *
 * @param <T> the type of whatever this mapping is representing
 */
public interface Obf<T> {
    /**
     * Returns the un-obfuscated (original) name
     *
     * @return the un-obfuscated name
     */
    @NotNull
    String getName();

    /**
     * Returns the class's obfuscated name.
     *
     * @return the obfuscated name
     */
    @NotNull
    String getObfuscatedName();

    /**
     * Determines if the representing class is obfuscated.
     * <p>
     * This method is equal to: {@code !Obf#getName().equals(Obf#getObfuscatedName())}.
     *
     * @return {@code true}, if the representing class is name-obfuscated
     */
    boolean isObfuscated();

    /**
     * Attempts to find whatever this object is representing using {@link #getObfuscatedName()} as source.
     * <p>
     * This method rather throws an exception than returning a {@code null} value on failure.
     *
     * @return T
     */
    @NotNull
    T find();
}
