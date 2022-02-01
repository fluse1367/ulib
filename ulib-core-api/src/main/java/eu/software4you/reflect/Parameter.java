package eu.software4you.reflect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A wrapper class for any values, that holds class and an assignable value.
 * Used to unique determine methods in reflection.
 *
 * @param <C> the class
 * @see ReflectUtil#call(Class, Object, String, boolean, List[])
 */
public class Parameter<C> {

    private final Class<C> clazz;
    private C value;

    /**
     * @param clazz a class
     * @param value an assignable value to the class
     */
    public Parameter(Class<C> clazz, C value) {
        this.clazz = clazz;
        this.value = value;
    }

    /**
     * Constructs a new instance with the direct class of any value
     *
     * @param value the value
     * @param <C>   the class
     * @return the instance
     * @see #guess(Object)
     */
    public static <C> Parameter<C> from(C value) {
        return new Parameter<>((Class<C>) DataType.getPrimitive(value.getClass()), value);
    }

    /**
     * Constructs a new instance with the direct class of any value
     *
     * @param value the value
     * @param <C>   the class
     * @return the instance
     * @see #from(Object)
     */
    public static <C> Parameter<?> guess(C value) {
        return from(value);
    }

    /**
     * Constructs a list of new instances with the direct class of any value
     *
     * @param values the values
     * @param <C>    the class
     * @return a list of new instances
     * @see #from(Object)
     */
    @SafeVarargs
    public static <C> List<Parameter<C>> fromMultiple(C... values) {
        List<Parameter<C>> list = new ArrayList<>();
        for (C value : values) {
            list.add(from(value));
        }
        return list;
    }

    /**
     * Constructs a list of new instances with the direct class of any value
     *
     * @param values the values
     * @param <C>    the class
     * @return a list of new instances
     * @see #guess(Object)
     */
    @SafeVarargs
    public static <C> List<Parameter<?>> guessMultiple(C... values) {
        List<Parameter<?>> list = new ArrayList<>();
        for (C value : values) {
            list.add(guess(value));
        }
        return list;
    }

    /**
     * Constructs a new singleton list with one parameter
     *
     * @param value the value
     * @param clazz the class
     * @param <C>   the class
     * @return a list of new instances
     * @see #guess(Object)
     */
    public static <C> List<Parameter<?>> single(Class<C> clazz, C value) {
        return Collections.singletonList(new Parameter<>(clazz, value));
    }

    /**
     * @return the class
     */
    public Class<C> what() {
        return clazz;
    }

    /**
     * @return the value
     */
    public C get() {
        return value;
    }

    /**
     * Sets a new value
     *
     * @param value the value to set
     */
    public void set(C value) {
        this.value = value;
    }

}