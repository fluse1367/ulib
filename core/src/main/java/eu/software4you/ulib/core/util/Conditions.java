package eu.software4you.ulib.core.util;

import eu.software4you.ulib.core.reflect.ReflectUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * A class containing shortcuts designed to be used with static import inside if-clauses.
 */
public class Conditions {

    // size checks

    /**
     * Determines if the first object is greater than the second one (as specified by {@link Comparable#compareTo(Object)}).
     *
     * @param a the first object
     * @param b the second object
     * @return {@code true} if the first object is greater than the second one, {@code false} otherwise
     */
    public static <N extends Comparable<? super N>> boolean gt(@NotNull N a, @NotNull N b) {
        return a.compareTo(b) > 0;
    }

    /**
     * Determines if the first object is greater than or equal to the second one (as specified by {@link Comparable#compareTo(Object)}).
     *
     * @param a the first object
     * @param b the second object
     * @return {@code true} if the first object is greater than or equal to the second one, {@code false} otherwise
     */
    public static <N extends Comparable<? super N>> boolean gte(@NotNull N a, @NotNull N b) {
        return a.compareTo(b) >= 0;
    }

    /**
     * Determines if the first object is less than the second one (as specified by {@link Comparable#compareTo(Object)}).
     *
     * @param a the first object
     * @param b the second object
     * @return {@code true} if the first object is less than the second one, {@code false} otherwise
     */
    public static <N extends Comparable<? super N>> boolean lt(@NotNull N a, @NotNull N b) {
        return a.compareTo(b) < 0;
    }

    /**
     * Determines if the first object is less than or equal to the second one (as specified by {@link Comparable#compareTo(Object)}).
     *
     * @param a the first object
     * @param b the second object
     * @return {@code true} if the first object is less than or equal to the second one, {@code false} otherwise
     */
    public static <N extends Comparable<? super N>> boolean lte(@NotNull N a, @NotNull N b) {
        return a.compareTo(b) <= 0;
    }

    /**
     * Determines if the first object is equal to the second one (as specified by {@link Comparable#compareTo(Object)}).
     *
     * @param a the first object
     * @param b the second object
     * @return {@code true} if the first object is equal to the second one, {@code false} otherwise
     */
    public static <N extends Comparable<? super N>> boolean eq(@NotNull N a, @NotNull N b) {
        return a.compareTo(b) == 0;
    }

    /**
     * Determines if the comparing object is located between the two boundaries (as specified by {@link Comparable#compareTo(Object)}).
     * <p>
     * Effectively this method determines if the lower boundary is less than, and the upper boundary more than the comparing object.
     *
     * @param a the first boundary
     * @param n the comparing object
     * @param b the second boundary
     * @return {@code true} if the comparing object located between the two boundaries, {@code false} otherwise
     */
    public static <N extends Comparable<? super N>> boolean bt(@NotNull N a, @NotNull N n, @NotNull N b) {
        N lower = lt(a, b) ? a : b;
        N upper = gt(b, a) ? b : a;

        return lt(lower, n) && gt(upper, n);
    }

    // type checks

    /**
     * Checks weather a certain object can be converted to an integer (as specified by {@link Conversions#tryInt(Object)}).
     *
     * @param o the object to test
     * @return {@code true} if conversation can be accomplished, {@code false} otherwise
     */
    public static boolean int32(@Nullable Object o) {
        return Conversions.tryInt(o).isPresent();
    }

    /**
     * Checks weather a certain object can be converted to a long (as specified by {@link Conversions#tryLong(Object)}).
     *
     * @param o the object to test
     * @return {@code true} if conversation can be accomplished, {@code false} otherwise
     */
    public static boolean int64(@Nullable Object o) {
        return Conversions.tryLong(o).isPresent();
    }

    /**
     * Checks weather a certain object can be converted to a float (as specified by {@link Conversions#tryFloat(Object)}).
     *
     * @param o the object to test
     * @return {@code true} if conversation can be accomplished, {@code false} otherwise
     */
    public static boolean dec32(@Nullable Object o) {
        return Conversions.tryFloat(o).isPresent();
    }

    /**
     * Checks weather a certain object can be converted to a double (as specified by {@link Conversions#tryDouble(Object)}).
     *
     * @param o the object to test
     * @return {@code true} if conversation can be accomplished, {@code false} otherwise
     */
    public static boolean dec64(@Nullable Object o) {
        return Conversions.tryDouble(o).isPresent();
    }

    // array checks

    /**
     * Checks weather all elements within the supplied array are null.
     *
     * @param objects the objects to check
     * @return {@code true} if the array is empty or all elements are null, {@code false} otherwise
     */
    public static boolean nil(@Nullable Object... objects) {
        return Stream.of(objects).noneMatch(Objects::nonNull);
    }

    /**
     * Checks weather all elements within the supplied array are not null.
     *
     * @param objects the objects to check
     * @return {@code true} if the array is empty or all elements are not null, {@code false} otherwise
     */
    public static boolean nNil(@Nullable Object... objects) {
        return Stream.of(objects).noneMatch(Objects::isNull);
    }

    /**
     * Checks weather the elements within a certain array are all the same (as specified by {@link Object#equals(Object)}).
     *
     * @param array the array
     * @return {@code true} if the array is not empty and all elements are the same, {@code false} otherwise
     */
    public static boolean same(@Nullable Object... array) {
        return nNil((Object) array)
               && Stream.of(array).distinct().count() == 1;
    }

    /**
     * Checks weather the elements within a certain array are unique (only occur one time; as specified by {@link Object#equals(Object)}).
     *
     * @param array the array
     * @return {@code true} if the array is not empty and all elements are unique, {@code false} otherwise
     */
    public static boolean unique(@Nullable Object... array) {
        return nNil((Object) array)
               && array.length > 0
               && array.length == Stream.of(array).distinct().count();
    }

    /**
     * Checks weather the elements within a certain array all equal to a certain other object (as specified by {@link Object#equals(Object)}).
     *
     * @param obj   the object that is expected
     * @param array the array to test
     * @return {@code true} if the array is not empty and all elements equal {@code obj}, {@code false} otherwise
     */
    public static boolean all(@NotNull Object obj, @Nullable Object... array) {
        return nNil(obj, array)
               && array.length > 0
               && Stream.of(array).allMatch(obj::equals);
    }

    // array contain checks

    /**
     * Checks weather a certain object occurs within the specified array (as specified by {@link Object#equals(Object)}).
     *
     * @param obj   the object to search
     * @param array the array to search the object in
     * @return {@code true} if {@code obj} occurs within {@code array}, {@code false} otherwise
     */
    public static boolean in(@Nullable Object obj, @Nullable Object... array) {
        return nNil(obj, array)
               && Arrays.asList(array).contains(obj);
    }

    /**
     * Checks weather a certain String occurs within the specified string array (as specified by {@link String#equalsIgnoreCase(String)}).
     *
     * @param s       the string to search
     * @param strings the array to search the string in
     * @return {@code true} if {@code s} occurs within {@code strings}, {@code false} otherwise
     */
    public static boolean inIC(@NotNull String s, @Nullable String... strings) {
        return nNil(s, strings)
               && Stream.of(strings).anyMatch(s::equalsIgnoreCase);
    }

    /**
     * Checks weather a certain (inner) array occurs within another (outer) array (in order; as specified by {@link Object#equals(Object)}).
     *
     * @param inner the array to search
     * @param outer the array to search the other array in
     * @return {@code true} if the {@code inner} array occurs within the {@code outer} array, {@code false} otherwise
     */
    public static boolean in(@Nullable Object[] inner, @Nullable Object[] outer) {
        if (nil(inner, outer) || inner.length > outer.length)
            return false;

        int i = 0;
        for (Object obj : outer) {
            if (nNil(obj) && obj.equals(inner[i]) && i++ >= (inner.length - 1)) {
                return true;
            }
            i = 0;
        }

        return false;
    }

    /**
     * Checks weather a certain (inner) String array occurs within another (outer) String array (in order; as specified by {@link String#equalsIgnoreCase(String)}).
     *
     * @param inner the array to search
     * @param outer the array to search the other array in
     * @return {@code true} if the {@code inner} array occurs within the {@code outer} array, {@code false} otherwise
     */
    public static boolean inIC(@Nullable String[] inner, @Nullable String[] outer) {
        if (nil(inner, outer) || inner.length > outer.length)
            return false;

        int i = 0;
        for (String s : outer) {
            if (nNil(s) && s.equalsIgnoreCase(inner[i]) && i++ >= (inner.length - 1)) {
                return true;
            }
            i = 0;
        }

        return false;
    }

    // misc checks

    /**
     * Checks weather a certain class is available to the current class loader.
     * This method does not load/initialize the class if it is found.
     *
     * @param name the fully qualified name of the class
     * @param init if the class should be initialized in case of loading success
     * @return {@code true} if the class could be loaded, {@code false} otherwise
     */
    public static boolean clazz(@NotNull String name, boolean init) {
        return ReflectUtil.tryWithLoaders(l -> ReflectUtil.forName(name, init, l)).isPresent();
    }
}
