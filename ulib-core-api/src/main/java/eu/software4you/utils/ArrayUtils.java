package eu.software4you.utils;

public class ArrayUtils {
    /**
     * Checks whether an {@link Object} occurs in the array
     */
    public static boolean arrayContains(Object[] array, Object o) {
        for (Object obj : array) {
            if (obj.equals(o))
                return true;
        }
        return false;
    }

    /**
     * Checks whether an {@link T} occurs in the array
     */
    public static <T> boolean contains(T[] array, T obj) {
        for (T t : array) {
            if (t.equals(obj))
                return true;
        }
        return false;
    }

    /**
     * Checks whether an {@link String} occurs in the array
     */
    public static boolean arrayContainsStringIgnoreCase(String[] array, String str) {
        for (String s : array) {
            if (s.equalsIgnoreCase(str))
                return true;
        }
        return false;
    }

    public static boolean arrayContainsStringArrayIgnoreCase(String[] array, String... str) {
        for (String s : array) {
            if (arrayContainsStringIgnoreCase(str, s))
                return true;
        }
        return false;
    }

    public static boolean arrayContainsWholeStringArrayIgnoreCase(String[] array, String... str) {
        for (String s : array) {
            if (!arrayContainsStringIgnoreCase(str, s))
                return false;
        }
        return true;
    }

}
