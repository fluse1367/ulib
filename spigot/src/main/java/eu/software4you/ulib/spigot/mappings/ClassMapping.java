package eu.software4you.ulib.spigot.mappings;

import eu.software4you.ulib.core.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

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
    @NotNull
    Optional<MappedField> fieldFromSource(String sourceName);

    /**
     * Returns a specific field.
     *
     * @param mappedName the <b>mapped</b> field name
     * @return the mapping, or {@code null} if not found
     */
    @NotNull
    Optional<MappedField> fieldFromMapped(String mappedName);

    /**
     * Returns all method mappings.
     *
     * @return all method mappings.
     */
    @NotNull
    Collection<MappedMethod> methods();

    /**
     * Returns all methods with a certain name.
     * <p>
     * There may be multiple methods with the same name.
     *
     * @param sourceName the <b>source</b> (original) method name
     * @return the mapping, or {@code null} if not found
     */
    @NotNull
    Collection<MappedMethod> methodsFromSource(String sourceName);

    /**
     * Returns a specific method.
     * <p>
     * There may be multiple methods with the same name. In this case, this method returns the first method.
     *
     * @param sourceName the <b>source</b> (original) method name
     * @return the mapping, or {@code null} if not found
     */
    @NotNull
    default Optional<MappedMethod> methodFromSource(String sourceName) {
        return methodsFromSource(sourceName).stream().findFirst();
    }

    /**
     * Returns a specific method with certain parameter types.
     * <p>
     * Because there may be multiple methods with the same name, this method selects the method by its return types.
     *
     * @param sourceName the <b>source</b> (original) method name
     * @param param0     the 1st parameter type
     * @param params     additional parameter types
     * @return the mapping, or {@code null} if not found
     */
    @NotNull
    default Optional<MappedMethod> methodFromSource(String sourceName, MappedClass param0, MappedClass... params) {
        return methodFromSource(sourceName, ArrayUtil.concat(param0, params));
    }

    /**
     * Returns a specific method with certain parameter types.
     * <p>
     * Because there may be multiple methods with the same name, this method selects the method by its return types.
     *
     * @param sourceName the <b>source</b> (original) method name
     * @param params     parameter types
     * @return the mapping, or {@code null} if not found
     */
    @NotNull
    Optional<MappedMethod> methodFromSource(String sourceName, MappedClass[] params);

    /**
     * Returns all methods with a certain name.
     * <p>
     * There may be multiple methods with the same name.
     *
     * @param mappedName the <b>mapped</b> method name
     * @return the mapping, or {@code null} if not found
     */
    @NotNull
    Collection<MappedMethod> methodsFromMapped(String mappedName);

    /**
     * Returns a specific method.
     * <p>
     * There may be multiple methods with the same name. In this case, this method returns the first method.
     *
     * @param mappedName the <b>mapped</b> method name
     * @return the mapping, or {@code null} if not found
     */
    @NotNull
    default Optional<MappedMethod> methodFromMapped(String mappedName) {
        return methodsFromMapped(mappedName).stream().findFirst();
    }

    /**
     * Returns a specific method with certain parameter types.
     * <p>
     * Because there may be multiple methods with the same name, this method selects the method by its return types.
     *
     * @param mappedName the <b>mapped</b> method name
     * @param param0     the 1st parameter type
     * @param params     additional parameter types
     * @return the mapping, or {@code null} if not found
     */
    @NotNull
    default Optional<MappedMethod> methodFromMapped(String mappedName, MappedClass param0, MappedClass... params) {
        return methodFromMapped(mappedName, ArrayUtil.concat(param0, params));
    }

    /**
     * Returns a specific method with certain parameter types.
     * <p>
     * Because there may be multiple methods with the same name, this method selects the method by its return types.
     *
     * @param mappedName the <b>mapped</b> method name
     * @param params     parameter types
     * @return the mapping, or {@code null} if not found
     */
    @NotNull
    Optional<MappedMethod> methodFromMapped(String mappedName, MappedClass[] params);
}
