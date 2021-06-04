package eu.software4you.spigot.mappings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Representation of a name-obfuscated class with it's members.
 */
public interface ClassMapping extends ObfClass {
    /**
     * Returns all field mappings.
     *
     * @return all field mappings.
     */
    @NotNull
    Collection<ObfField> getFields();

    /**
     * Returns a specific field.
     *
     * @param originalName the <b>un-obfuscated</b> field name
     * @return the mapping, or {@code null} if not found
     */
    @Nullable
    ObfField getField(String originalName);

    /**
     * Returns a specific field.
     *
     * @param obfuscatedName the <b>obfuscated</b> field name
     * @return the mapping, or {@code null} if not found
     */
    @Nullable
    ObfField searchField(String obfuscatedName);

    /**
     * Returns all method mappings.
     *
     * @return all method mappings.
     */
    @NotNull
    Collection<ObfMethod> getMethods();

    /**
     * Returns a specific method.
     *
     * @param originalName the <b>un-obfuscated</b> method name
     * @return the mapping, or {@code null} if not found
     */
    @Nullable
    ObfMethod getMethod(String originalName);

    /**
     * Returns a specific method.
     *
     * @param obfuscatedName the <b>obfuscated</b> method name
     * @return the mapping, or {@code null} if not found
     */
    @Nullable
    ObfMethod searchMethod(String obfuscatedName);
}
