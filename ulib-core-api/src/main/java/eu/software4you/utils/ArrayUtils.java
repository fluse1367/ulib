package eu.software4you.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

}
