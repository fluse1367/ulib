package eu.software4you.ulib.core.util;

import eu.software4you.ulib.core.function.ParamFunc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Conversions {

    /**
     * Attempts to convert the given object into an int.
     *
     * @param o the value to convert
     * @return an expect object wrapping the operation result
     */
    @NotNull
    public static Expect<Integer, NumberFormatException> tryInt(Object o) {
        return tryConvert(o, in -> in instanceof Integer i ? i : Integer.parseInt(in.toString()));
    }

    /**
     * Attempts to convert the given object into a long.
     *
     * @param o the value to convert
     * @return an expect object wrapping the operation result
     */
    @NotNull
    public static Expect<Long, NumberFormatException> tryLong(Object o) {
        return tryConvert(o, in -> in instanceof Long l ? l : Long.parseLong(in.toString()));
    }

    /**
     * Attempts to convert the given object into a float.
     *
     * @param o the value to convert
     * @return an expect object wrapping the operation result
     */
    @NotNull
    public static Expect<Float, NumberFormatException> tryFloat(Object o) {
        return tryConvert(o, in -> in instanceof Float f ? f : Float.parseFloat(in.toString()));
    }

    /**
     * Attempts to convert the given object into a double.
     *
     * @param o the value to convert
     * @return an expect object wrapping the operation result
     */
    @NotNull
    public static Expect<Double, NumberFormatException> tryDouble(Object o) {
        return tryConvert(o, in -> in instanceof Double d ? d : Double.parseDouble(in.toString()));
    }

    /**
     * Attempts to convert the given object into a boolean.
     * If the object is applicable as integer (according to {@link #tryInt(Object)}), a value other than 0 will be interpreted as true.
     *
     * @param o the value to convert
     * @return an expect object wrapping the operation result
     */
    @NotNull
    public static Expect<Boolean, ?> tryBoolean(@Nullable Object o) {
        return tryConvert(o, in -> in instanceof Boolean b ? b :
                tryInt(in).toOptional()
                        .map(i -> i != 0)
                        .orElseGet(() -> Boolean.parseBoolean(o.toString()))
        );
    }

    /**
     * Attempts to convert the specified object to another object. Any object thrown will be caught.
     *
     * @param input     the object to convert
     * @param converter the converting function
     * @return an optional wrapping the value if the converting function executed successful and returned a non-null value, an empty optional otherwise
     */
    @NotNull
    public static <I, R, X extends Exception> Expect<R, X> tryConvert(@Nullable I input, @Nullable ParamFunc<I, R, X> converter) {
        if (Conditions.nil(input, converter))
            return Expect.empty();
        return Expect.compute(() -> converter.apply(input));
    }

    /**
     * Attempts to cast the elements of an iterable to a specific type. In case of success the original iterable object is returned.
     * <p>
     * This method allows {@code null}-values.
     *
     * @param it   the input iterable
     * @param type the type to cast the elements to
     * @return an optional wrapping the cast iterable
     */
    @NotNull
    public static <T> Optional<Iterable<T>> safecast(@NotNull Class<T> type, @NotNull Iterable<?> it) {
        for (Object o : it) {
            if (o != null && !type.isInstance(o))
                return Optional.empty();
        }

        //noinspection unchecked
        return Optional.of((Iterable<T>) it);
    }

    /**
     * @see #safecast(Class, Iterable)
     */
    @NotNull
    public static <T> Optional<Collection<T>> safecast(@NotNull Class<T> type, @NotNull Collection<?> coll) {
        //noinspection unchecked,rawtypes
        return (Optional) safecast(type, (Iterable<?>) coll);
    }

    /**
     * @see #safecast(Class, Iterable)
     */
    @NotNull
    public static <T> Optional<List<T>> safecast(@NotNull Class<T> type, @NotNull List<?> li) {
        //noinspection unchecked,rawtypes
        return (Optional) safecast(type, (Iterable<?>) li);
    }

    /**
     * @see #safecast(Class, Iterable)
     */
    @NotNull
    public static <T> Optional<Set<T>> safecast(@NotNull Class<T> type, @NotNull Set<?> s) {
        //noinspection unchecked,rawtypes
        return (Optional) safecast(type, (Iterable<?>) s);
    }

    /**
     * Attempts to cast the elements of a map to specific types. In case of success the original map object is returned.
     * <p>
     * This method allows {@code null}-values.
     *
     * @param kType the type to cast key elements to
     * @param vType the type to cast value elements to
     * @param map   the input map
     * @return an optional wrapping the cast map
     */
    public static <K, V> Optional<Map<K, V>> safecast(@NotNull Class<K> kType, @NotNull Class<V> vType, @NotNull Map<?, ?> map) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            var key = entry.getKey();
            var val = entry.getValue();

            if ((key != null && !kType.isInstance(key)) || (val != null && !vType.isInstance(val)))
                return Optional.empty();
        }

        //noinspection unchecked
        return Optional.of((Map<K, V>) map);
    }

    /**
     * Converts a byte array into the appropriate hex representation.
     *
     * @param bytes the byte array
     * @return the hex string
     */
    @NotNull
    public static String toHex(byte[] bytes) {
        var b = new StringBuilder();
        for (byte by : bytes) {
            b.append(Integer.toHexString(Byte.toUnsignedInt(by) | 0x100));
        }
        return b.toString();
    }

    /**
     * Generates a roman number from a decimal one.
     *
     * @param num decimal input number
     * @return the roman number
     */
    @NotNull
    public static String toRoman(int num) {
        int[] dec = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] romanLiterals = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        StringBuilder roman = new StringBuilder();

        for (int i = 0; i < dec.length; i++) {
            while (num >= dec[i]) {
                num -= dec[i];
                roman.append(romanLiterals[i]);
            }
        }
        return roman.toString();
    }
}
