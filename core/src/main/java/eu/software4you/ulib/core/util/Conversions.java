package eu.software4you.ulib.core.util;

import eu.software4you.ulib.core.function.ParamFunc;
import org.jetbrains.annotations.Nullable;

public class Conversions {

    /**
     * Attempts to convert the given object into an int.
     *
     * @param o the value to convert
     * @return an expect object wrapping the operation result
     */
    public static Expect<Integer, NumberFormatException> tryInt(Object o) {
        return tryConvert(o, in -> in instanceof Integer i ? i : Integer.parseInt(in.toString()));
    }

    /**
     * Attempts to convert the given object into a long.
     *
     * @param o the value to convert
     * @return an expect object wrapping the operation result
     */
    public static Expect<Long, NumberFormatException> tryLong(Object o) {
        return tryConvert(o, in -> in instanceof Long l ? l : Long.parseLong(in.toString()));
    }

    /**
     * Attempts to convert the given object into a float.
     *
     * @param o the value to convert
     * @return an expect object wrapping the operation result
     */
    public static Expect<Float, NumberFormatException> tryFloat(Object o) {
        return tryConvert(o, in -> in instanceof Float f ? f : Float.parseFloat(in.toString()));
    }

    /**
     * Attempts to convert the given object into a double.
     *
     * @param o the value to convert
     * @return an expect object wrapping the operation result
     */
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
    public static <I, R, X extends Exception> Expect<R, X> tryConvert(@Nullable I input, @Nullable ParamFunc<I, R, X> converter) {
        if (Conditions.nil(input, converter))
            return Expect.empty();
        return Expect.compute(() -> converter.apply(input));
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
