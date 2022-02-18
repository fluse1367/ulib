package eu.software4you.ulib.core.api.util;

import eu.software4you.ulib.core.api.util.value.Unsettled;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class Conversions {

    /**
     * Attempts to convert the given object into an int.
     *
     * @param o the value to convert
     * @return an optional wrapping the value if conversation was successful, an empty optional otherwise
     */
    public static Optional<Integer> tryInt(Object o) {
        return tryConvert(o, in -> in instanceof Integer i ? i : Integer.parseInt(in.toString()));
    }

    /**
     * Attempts to convert the given object into a long.
     *
     * @param o the value to convert
     * @return an optional wrapping the value if conversation was successful, an empty optional otherwise
     */
    public static Optional<Long> tryLong(Object o) {
        return tryConvert(o, in -> in instanceof Long l ? l : Long.parseLong(in.toString()));
    }

    /**
     * Attempts to convert the given object into a float.
     *
     * @param o the value to convert
     * @return an optional wrapping the value if conversation was successful, an empty optional otherwise
     */
    public static Optional<Float> tryFloat(Object o) {
        return tryConvert(o, in -> in instanceof Float f ? f : Float.parseFloat(in.toString()));
    }

    /**
     * Attempts to convert the given object into a double.
     *
     * @param o the value to convert
     * @return an optional wrapping the value if conversation was successful, an empty optional otherwise
     */
    public static Optional<Double> tryDouble(Object o) {
        return tryConvert(o, in -> in instanceof Double d ? d : Double.parseDouble(in.toString()));
    }

    /**
     * Attempts to convert the given object into a boolean.
     * If the object is applicable as integer (according to {@link #tryInt(Object)}), a value other than 0 will be interpreted as true.
     *
     * @param o the value to convert
     * @return an optional wrapping the value if conversation was successful, an empty optional otherwise
     */
    public static Optional<Boolean> tryBoolean(@Nullable Object o) {
        return tryConvert(o, in -> in instanceof Boolean b ? b :
                tryInt(in).map(i -> i != 0).orElseGet(() -> Boolean.parseBoolean(o.toString())));
    }

    /**
     * Attempts to convert the specified object to another object. Any object thrown will be caught.
     *
     * @param input     the object to convert
     * @param converter the converting function
     * @return an optional wrapping the value if the converting function executed successful and returned a non-null value, an empty optional otherwise
     */
    public static <I, R> Optional<R> tryConvert(@Nullable I input, @Nullable Function<I, R> converter) {
        if (Checks.nil(input, converter))
            return Optional.empty();
        return Unsettled.execute(() -> converter.apply(input)).get();
    }

    /**
     * Converts a byte array into the appropriate hex representation.
     *
     * @param bytes the byte array
     * @return the hex string
     */
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
