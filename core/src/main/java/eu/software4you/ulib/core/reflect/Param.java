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

}
