package eu.software4you.ulib.core.api.utils;

public class CheckUtils {

    public static boolean isNumber(String s) {
        return isInteger(s) || isDouble(s) || isFloat(s) || isLong(s);
    }

    public static boolean isInteger(String s) {
        if (!isDouble(s) && !isFloat(s) && (!couldBeLong(s) || (couldBeLong(s) && Long.parseLong(s) < Integer.MAX_VALUE)))
            try {
                Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return false;
            }
        return true;
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isFloat(String s) {
        try {
            Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isLong(String s) {
        if (!isInteger(s))
            try {
                Long.parseLong(s);
            } catch (NumberFormatException e) {
                return false;
            }
        return true;
    }

    private static boolean couldBeLong(String s) {
        try {
            Long.parseLong(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

}
