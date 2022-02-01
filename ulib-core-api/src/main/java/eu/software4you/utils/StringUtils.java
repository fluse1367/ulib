package eu.software4you.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class StringUtils {

    /**
     * Generates a random alphanumeric {@link String} with capital and lowercase letters
     *
     * @param length of {@link String} to be generated
     * @return {@link String}
     */
    @NotNull
    public static String randomString(int length) {
        return randomString(length, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ000111222333444555666777888999");
    }

    /**
     * Generates a random {@link String} from a provided charset.
     *
     * @param length of {@link String}
     * @param chars  characters to be used for generation
     * @return {@link String}
     */
    @NotNull
    public static String randomString(int length, @NotNull String chars) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < length; i++)
            b.append(chars.charAt(r.nextInt(chars.length())));
        return b.toString();
    }

    /**
     * Checks whether every element in the array occurs in the string
     *
     * @param string the {@link String}
     * @param array  the {@link String[]} array
     * @return {@link Boolean}
     */
    public static boolean containsAllOfArray(@NotNull String string, @NotNull String... array) {
        for (String s : array)
            if (!string.contains(s))
                return false;
        return array.length > 0;
    }

    /**
     * Like {@link #containsAllOfArray(String, String...)} but it ignores the case of the letters
     */
    public static boolean containsAllOfArrayIgnoreCase(@NotNull String string, @NotNull String... array) {
        for (String s : array)
            if (!string.toLowerCase().contains(s.toLowerCase()))
                return false;
        return array.length > 0;
    }

    /**
     * Checks whether at least one element in the array occurs in the string
     *
     * @param string the {@link String}
     * @param array  the {@link String[]} array
     * @return {@link Boolean}
     */
    public static boolean containsOneOfArray(@NotNull String string, @NotNull String... array) {
        for (String s : array)
            if (string.contains(s))
                return true;
        return false;
    }

    /**
     * Like {@link #containsOneOfArray(String, String...)} but it ignores the case of the letters
     */
    public static boolean containsOneOfArrayIgnoreCase(@NotNull String string, @NotNull String... array) {
        for (String s : array)
            if (string.toLowerCase().contains(s.toLowerCase()))
                return true;
        return false;
    }

    /**
     * Checks whether every element in the array equals the string
     *
     * @param string the {@link String}
     * @param array  the {@link String[]} array
     * @return {@link Boolean}
     */
    public static boolean equalsAllOfWholeArray(@NotNull String string, @NotNull String... array) {
        for (String s : array)
            if (!string.equals(s))
                return false;
        return array.length > 0;
    }

    /**
     * Like {@link #equalsAllOfWholeArray(String, String...)} but it ignores the case of the letters
     */
    public static boolean equalsAllOfWholeArrayIgnoreCase(@NotNull String string, @NotNull String... array) {
        for (String s : array)
            if (!string.equalsIgnoreCase(s))
                return false;
        return array.length > 0;
    }

    /**
     * Checks whether at least one element in the array equals the string
     *
     * @param string the {@link String}
     * @param array  the {@link String[]} array
     * @return {@link Boolean}
     */
    public static boolean equalsOneOfWholeArray(@NotNull String string, @NotNull String... array) {
        for (String s : array)
            if (string.equals(s))
                return true;
        return false;
    }

    /**
     * Like {@link #equalsOneOfWholeArray(String, String...)} but it ignores the case of the letters
     */
    public static boolean equalsOneOfWholeArrayIgnoreCase(@NotNull String string, @NotNull String... array) {
        for (String s : array)
            if (string.equalsIgnoreCase(s))
                return true;
        return false;
    }

    /**
     * Prints out each element of an array
     */
    public static void println(Object[] array) {
        System.out.println(Arrays.toString(array));
    }

    public enum CHARS {
        ALPHABET_UPPER_CASE("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
        ALPHABET_LOWER_CASE("abcdefghijklmnopqrstuvwxyz"),
        NUMBERS("0123456789");

        final String chars;

        CHARS(String chars) {
            this.chars = chars;
        }

        @Override
        public String toString() {
            return chars;
        }
    }
}
