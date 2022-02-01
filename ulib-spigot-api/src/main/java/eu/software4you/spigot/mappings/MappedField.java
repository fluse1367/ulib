package eu.software4you.spigot.mappings;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

/**
 * Represents a name-mapped field.
 */
public interface MappedField extends Mapped<Field> {
    /**
     * Returns the field's type.
     *
     * @return the field's type
     */
    @NotNull
    MappedClass type();
}
