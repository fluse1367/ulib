package eu.software4you.ulib.core.api.util;

import java.util.Arrays;

public class ArrayUtil {
    /**
     * Concatenates an array onto a value
     *
     * @param t   the first value
     * @param arr the array to add
     * @param <T> the array type
     * @return the concatenated result
     */
    public static <T> T[] concat(T t, T[] arr) {
        T[] array = Arrays.copyOf(arr, arr.length + 1);
        array[0] = t;
        System.arraycopy(arr, 0, array, 1, arr.length);
        return array;
    }

    /**
     * Concatenates a value onto an array.
     *
     * @param arr the array
     * @param t   the value to add
     * @param <T> the array type
     * @return the concatenated result
     */
    public static <T> T[] concat(T[] arr, T t) {
        T[] array = Arrays.copyOf(arr, arr.length + 1);
        array[arr.length] = t;
        return array;
    }

    /**
     * Concatenates two arrays.
     *
     * @param a   the first array
     * @param b   the second array
     * @param <T> the array type
     * @return the concatenated result
     */
    public static <T> T[] concat(T[] a, T[] b) {
        T[] array = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, array, a.length, b.length);
        return array;
    }

}
