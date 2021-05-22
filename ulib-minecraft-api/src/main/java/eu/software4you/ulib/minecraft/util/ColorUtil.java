package eu.software4you.ulib.minecraft.util;

/**
 * Utility for minecraft (chat) colors/formatting.
 */
public class ColorUtil {
    public static final String REGEX_REPLACE_RAW = "§$1";
    public static final String REGEX_REPLACE_FORMATTED = REGEX_REPLACE_RAW.replace("§", "&");


    public static final String REGEX_TARGET_RAW_COLORS = "(?i)&([0-9a-f])";
    public static final String REGEX_TARGET_RAW_FORMATS = "(?i)&([l-or])";
    public static final String REGEX_TARGET_RAW_MAGIC = "(?i)&([k])";
    public static final String REGEX_TARGET_RAW_ALL = "(?i)&([0-9a-fk-or])";

    public static final String REGEX_TARGET_FORMATTED_COLORS = REGEX_TARGET_RAW_COLORS.replace("&", "§");
    public static final String REGEX_TARGET_FORMATTED_FORMATS = REGEX_TARGET_RAW_FORMATS.replace("&", "§");
    public static final String REGEX_TARGET_FORMATTED_MAGIC = REGEX_TARGET_RAW_MAGIC.replace("&", "§");
    public static final String REGEX_TARGET_FORMATTED_ALL = REGEX_TARGET_RAW_ALL.replace("&", "§");

    /**
     * Replaces all color codes from a string with the actual color ({@code §}).
     *
     * @param input the string
     * @return the processed string
     */
    public static String replaceColors(String input) {
        return input.replaceAll(REGEX_TARGET_RAW_COLORS, REGEX_REPLACE_RAW);
    }

    /**
     * Replaces all formatting codes from a string with the actual formatting ({@code §}).
     *
     * @param input the string
     * @return the processed string
     */
    public static String replaceFormats(String input) {
        return input.replaceAll(REGEX_TARGET_RAW_FORMATS, REGEX_REPLACE_RAW);
    }

    /**
     * Replaces all magic formatting codes ({@code &k}) from a string with the actual formatting ({@code §k}).
     *
     * @param input the string
     * @return the processed string
     */
    public static String replaceMagic(String input) {
        return input.replaceAll(REGEX_TARGET_RAW_MAGIC, REGEX_REPLACE_RAW);
    }

    /**
     * Replaces all formatting & color codes from a string with the actual formatting/color ({@code §}).
     *
     * @param input the string
     * @return the processed string
     */
    public static String replaceAll(String input) {
        return input.replaceAll(REGEX_TARGET_RAW_ALL, REGEX_REPLACE_RAW);
    }

    /**
     * Replaces all colors from a string with the respective code ({@code &}).
     *
     * @param input the string
     * @return the processed string
     */
    public static String unreplaceColors(String input) {
        return input.replaceAll(REGEX_TARGET_FORMATTED_COLORS, REGEX_REPLACE_FORMATTED);
    }

    /**
     * Replaces all formatting from a string with the respective code ({@code &}).
     *
     * @param input the string
     * @return the processed string
     */
    public static String unreplaceFormats(String input) {
        return input.replaceAll(REGEX_TARGET_FORMATTED_FORMATS, REGEX_REPLACE_FORMATTED);
    }

    /**
     * Replaces all magic formatting ({@code §k}) from a string with the respective code ({@code &}).
     *
     * @param input the string
     * @return the processed string
     */
    public static String unreplaceMagic(String input) {
        return input.replaceAll(REGEX_TARGET_FORMATTED_MAGIC, REGEX_REPLACE_FORMATTED);
    }

    /**
     * Replaces all formatting & color from a string with the respective code ({@code &}).
     *
     * @param input the string
     * @return the processed string
     */
    public static String unreplaceAll(String input) {
        return input.replaceAll(REGEX_TARGET_FORMATTED_ALL, REGEX_REPLACE_FORMATTED);
    }

    /**
     * Removes all colors from a string.
     *
     * @param input the string
     * @return the processed string
     */
    public static String stripColors(String input) {
        return input.replaceAll(REGEX_TARGET_FORMATTED_COLORS, "");
    }

    /**
     * Removes all formatting (except magic) from a string.
     *
     * @param input the string
     * @return the processed string
     */
    public static String stripFormats(String input) {
        return input.replaceAll(REGEX_TARGET_FORMATTED_FORMATS, "");
    }

    /**
     * Removes magic formatting ({@code §k}) from a string.
     *
     * @param input the string
     * @return the processed string
     */
    public static String stripMagic(String input) {
        return input.replaceAll(REGEX_TARGET_FORMATTED_MAGIC, "");
    }

    /**
     * Removes all formatting & color from a string.
     *
     * @param input the string
     * @return the processed string
     */
    public static String stripAll(String input) {
        return input.replaceAll(REGEX_TARGET_FORMATTED_ALL, "");
    }
}
