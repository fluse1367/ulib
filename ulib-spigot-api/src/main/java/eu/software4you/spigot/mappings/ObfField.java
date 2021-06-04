package eu.software4you.spigot.mappings;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

/**
 * Represents a name-obfuscated field.
 */
public interface ObfField extends Obf<Field> {
    /**
     * Returns the field's type.
     *
     * @return the field's type
     */
    @NotNull
    ObfClass getType();
}
