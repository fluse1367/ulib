package eu.software4you.spigot.mappings;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a name-obfuscated class.
 * <p>
 * Note: {@link #getName()} and {@link #getObfuscatedName()} return fully qualified class names.
 */
public interface ObfClass extends Obf<Class<?>> {
    /**
     * Attempts to find the class using {@link #getObfuscatedName()}.
     * <p>
     * Does not initialize the class.
     *
     * @return the class
     * @see Class#forName(String)
     */
    @NotNull
    Class<?> find();
}
