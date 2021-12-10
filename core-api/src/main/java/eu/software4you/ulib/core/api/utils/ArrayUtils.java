package eu.software4you.ulib.core.api.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ArrayUtils {
    /**
     * Checks whether an {@link Object} occurs in the array
     */
    public static boolean arrayContains(@NotNull Object[] array, @Nullable Object o) {
        for (Object obj : array) {
            if (obj.equals(o))
                return true;
        }
        return false;
    }

    /**
     * Checks whether an {@link T} occurs in the array
     */
    public static <T> boolean contains(@NotNull T[] array, @Nullable T obj) {
        for (T t : array) {
            if (t.equals(obj))
                return true;
        }
        return false;
    }

    /**
     * Checks whether an {@link String} occurs in the array
     */
    public static boolean arrayContainsStringIgnoreCase(@NotNull String[] array, @Nullable String str) {
        for (String s : array) {
            if (s.equalsIgnoreCase(str))
                return true;
        }
        return false;
    }

    public static boolean arrayContainsStringArrayIgnoreCase(@NotNull String[] array, @NotNull String... str) {
        for (String s : array) {
            if (arrayContainsStringIgnoreCase(str, s))
                return true;
        }
        return false;
    }

    public static boolean arrayContainsWholeStringArrayIgnoreCase(@NotNull String[] array, @NotNull String... str) {
        for (String s : array) {
            if (!arrayContainsStringIgnoreCase(str, s))
                return false;
        }
        return true;
    }

    /**
     * Concatenates an array onto a value
     *
     * @param a   the first value
     * @param arr the array to add
     * @param <T> the array type
     * @return the concatenated result
     */
    public static <T> T[] concat(T a, T[] arr) {
        T[] strs = Arrays.copyOf(arr, arr.length + 1);
        strs[0] = a;
        System.arraycopy(arr, 0, strs, 1, arr.length);
        return strs;
    }

    /**
     * Concatenates a value onto an array.
     *
     * @param arr the array
     * @param a   the value to add
     * @param <T> the array type
     * @return the concatenated result
     */
    public static <T> T[] concat(T[] arr, T a) {
        T[] array = Arrays.copyOf(arr, arr.length + 1);
        array[arr.length] = a;
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
