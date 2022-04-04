package eu.software4you.ulib.core.reflect;

import eu.software4you.ulib.core.collection.Pair;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A wrapper for any values, that holds a class and an assignable value.
 * Used to unique determine methods in reflection.
 *
 * @param <V> the value type
 */
@Getter
public class Param<V> extends Pair<Class<? extends V>, V> {
    public Param(@NotNull Class<? extends V> clazz, @Nullable V value) {
        super(clazz, value);
    }

    public Class<? extends V> getClazz() {
        return getFirst();
    }

    public V getValue() {
        return getSecond();
    }

    /**
     * Constructs a new instance with the direct class of any value
     *
     * @param value the value
     * @param <V>   the value type
     * @return the instance
     */
    @NotNull
    public static <V> Param<?> from(@NotNull V value) {
        //noinspection unchecked
        return new Param<>((Class<V>) value.getClass(), value);
    }

    /**
     * Constructs a list of new instances with the direct class of any value
     *
     * @param values the values
     * @param <V>    the value type
     * @return a list of new instances
     * @see #from(Object)
     */
    @SafeVarargs
    @NotNull
    public static <V> List<Param<?>> fromMultiple(@NotNull V... values) {
        List<Param<?>> list = new ArrayList<>(values.length);
        for (V value : values) {
            list.add(from(value));
        }
        return list;
    }

    /**
     * Constructs a new list with multiple parameters from pair values
     *
     * @param pairs the type-value pairs
     * @param <V>   the value type
     * @return a list of new instances
     */
    @SafeVarargs
    @NotNull
    public static <V> List<Param<?>> multiple(@NotNull Pair<Class<? extends V>, V>... pairs) {
        List<Param<?>> list = new ArrayList<>(pairs.length);
        for (var pair : pairs) {
            list.add(new Param<>(pair.getFirst(), pair.getSecond()));
        }
        return list;
    }

    /**
     * Constructs a new singleton list with one parameter
     *
     * @param value the value
     * @param clazz the class
     * @param <V>   the class
     * @return a list of new instances
     */
    @NotNull
    public static <V> List<Param<?>> single(@NotNull Class<V> clazz, @Nullable V value) {
        return Collections.singletonList(new Param<>(clazz, value));
    }

    public static <V1> List<Param<?>> listOf(Class<? extends V1> clazz1, V1 value1) {
        return Collections.singletonList(new Param<>(clazz1, value1));
    }

    public static <V1, V2> List<Param<?>> listOf(Class<? extends V1> clazz1, V1 value1,
                                                 Class<? extends V2> clazz2, V2 value2) {
        return Arrays.asList(
                new Param<>(clazz1, value1),
                new Param<>(clazz2, value2)
        );
    }

    public static <V1, V2, V3> List<Param<?>> listOf(Class<? extends V1> clazz1, V1 value1,
                                                     Class<? extends V2> clazz2, V2 value2,
                                                     Class<? extends V3> clazz3, V3 value3) {
        return Arrays.asList(
                new Param<>(clazz1, value1),
                new Param<>(clazz2, value2),
                new Param<>(clazz3, value3)
        );
    }

    public static <V1, V2, V3, V4> List<Param<?>> listOf(Class<? extends V1> clazz1, V1 value1,
                                                         Class<? extends V2> clazz2, V2 value2,
                                                         Class<? extends V3> clazz3, V3 value3,
                                                         Class<? extends V4> clazz4, V4 value4) {
        return Arrays.asList(
                new Param<>(clazz1, value1),
                new Param<>(clazz2, value2),
                new Param<>(clazz3, value3),
                new Param<>(clazz4, value4)
        );
    }

    public static <V1, V2, V3, V4, V5> List<Param<?>> listOf(Class<? extends V1> clazz1, V1 value1,
                                                             Class<? extends V2> clazz2, V2 value2,
                                                             Class<? extends V3> clazz3, V3 value3,
                                                             Class<? extends V4> clazz4, V4 value4,
                                                             Class<? extends V5> clazz5, V5 value5) {
        return Arrays.asList(
                new Param<>(clazz1, value1),
                new Param<>(clazz2, value2),
                new Param<>(clazz3, value3),
                new Param<>(clazz4, value4),
                new Param<>(clazz5, value5)
        );
    }

    public static <V1, V2, V3, V4, V5, V6> List<Param<?>> listOf(Class<? extends V1> clazz1, V1 value1,
                                                                 Class<? extends V2> clazz2, V2 value2,
                                                                 Class<? extends V3> clazz3, V3 value3,
                                                                 Class<? extends V4> clazz4, V4 value4,
                                                                 Class<? extends V5> clazz5, V5 value5,
                                                                 Class<? extends V6> clazz6, V6 value6) {
        return Arrays.asList(
                new Param<>(clazz1, value1),
                new Param<>(clazz2, value2),
                new Param<>(clazz3, value3),
                new Param<>(clazz4, value4),
                new Param<>(clazz5, value5),
                new Param<>(clazz6, value6)
        );
    }

}
