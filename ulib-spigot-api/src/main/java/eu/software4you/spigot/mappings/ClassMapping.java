package eu.software4you.spigot.mappings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Representation of a name-mapped class with it's members.
 */
public interface ClassMapping extends MappedClass {
    /**
     * Returns all field mappings.
     *
     * @return all field mappings.
     */
    @NotNull
    Collection<MappedField> fields();

    /**
     * Returns a specific field.
     *
     * @param sourceName the <b>source</b> (original) field name
     * @return the mapping, or {@code null} if not found
     */
    @Nullable
    MappedField fieldFromSource(String sourceName);

    /**
     * Returns a specific field.
     *
     * @param mappedName the <b>mapped</b> field name
     * @return the mapping, or {@code null} if not found
     */
    @Nullable
    MappedField fieldFromMapped(String mappedName);

    /**
     * Returns all method mappings.
     *
     * @return all method mappings.
     */
    @NotNull
    Collection<MappedMethod> methods();

    /**
     * Returns a specific method.
     *
     * @param sourceName the <b>source</b> (original) method name
     * @return the mapping, or {@code null} if not found
     */
    @Nullable
    MappedMethod methodFromSource(String sourceName);

    /**
     * Returns a specific method.
     *
     * @param mappedName the <b>mapped</b> method name
     * @return the mapping, or {@code null} if not found
     */
    @Nullable
    MappedMethod methodFromMapped(String mappedName);
}
