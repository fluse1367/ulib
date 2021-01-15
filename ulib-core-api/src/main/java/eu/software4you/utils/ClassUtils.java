package eu.software4you.utils;

import eu.software4you.ulib.ULib;
import org.apache.commons.lang.ArrayUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ClassUtils {
    /**
     * @param className the fully qualified name of the desired class.
     * @return true if the class exists or false if it does not.
     */
    public static boolean isClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (Throwable thr) {
            return false;
        }
    }

    /**
     * @param className the fully qualified name of the desired class.
     * @return the {@code Class} object for the class with the
     * specified name, or if the class does not exist null.
     */
    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (Throwable thr) {
            ULib.getInstance().exception(thr, String.format("%s could not be loaded", className));
        }
        return null;
    }

    /**
     * @param enumName  the fully qualified name of the desired enum.
     * @param enumEntry the fully qualified name of the desired entry.
     * @return the {@code Class} object for the class with the
     * specified name, or if the class does not exist null.
     */
    public static Object getEnumEntry(String enumName, String enumEntry) {
        try {
            Class<?> enumClass = Class.forName(enumName);
            if (!enumClass.isAssignableFrom(Enum.class)) {
                ULib.getInstance().error(String.format("%s is not an enumeration", enumClass.getSimpleName()));
                return null;
            }
            Class<? extends Enum<?>> e = (Class<? extends Enum<?>>) enumClass;
            return getEnumEntry(e, enumEntry);
        } catch (Throwable thr) {
            thr.printStackTrace();
        }
        return null;
    }

    /**
     * @param enumClass the class of enum
     * @param enumEntry the fully qualified name of the desired entry.
     * @return the {@code Class} object for the class with the
     * specified name, or if the class does not exist null.
     */
    public static Object getEnumEntry(Class<? extends Enum<?>> enumClass, String enumEntry) {
        try {
            return enumClass.getMethod("valueOf", String.class).invoke(null, enumEntry);
        } catch (Throwable thr) {
            ULib.getInstance().exception(thr, String.format("%s could not be found in %s", enumEntry, enumClass.getName()));
        }
        return null;
    }

    /**
     * @param clazz     the class to search in
     * @param fieldName the name of the field to find
     * @return (1) The field (2) null, if nothing found
     */
    public static Field findUnderlyingField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        do {
            try {
                return current.getField(fieldName);
            } catch (Exception ignored) {
            }
        } while ((current = current.getSuperclass()) != null);
        return null;
    }

    /**
     * @param clazz     the class to search in
     * @param fieldName the name of the field to find
     * @return (1) The field (2) null, if nothing found
     */
    public static Field findUnderlyingDeclaredField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        do {
            try {
                return current.getDeclaredField(fieldName);
            } catch (Exception ignored) {
            }
        } while ((current = current.getSuperclass()) != null);
        return null;
    }

    /**
     * @param clazz the class to search in
     * @return The collection of fields
     */
    public static Collection<Field> findUnderlyingFields(Class<?> clazz) {
        List<Field> cache = new ArrayList<>();
        Class<?> current = clazz;
        do {
            try {
                cache.addAll(Arrays.asList(current.getFields()));
            } catch (Exception ignored) {
            }
        } while ((current = current.getSuperclass()) != null);
        return cache;
    }

    /**
     * @param clazz the class to search in
     * @return The collection of fields
     */
    public static Collection<Field> findUnderlyingDeclaredFields(Class<?> clazz) {
        List<Field> cache = new ArrayList<>();
        Class<?> current = clazz;
        do {
            try {
                cache.addAll(Arrays.asList(current.getDeclaredFields()));
            } catch (Exception ignored) {
            }
        } while ((current = current.getSuperclass()) != null);
        return cache;
    }

    /**
     * @param clazz      the class to search in
     * @param methodName the name of the method to find
     * @return (1) The method (2) null, if nothing found
     */
    public static Method findUnderlyingMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        Class<?> current = clazz;
        do {
            try {
                return current.getMethod(methodName, parameterTypes);
            } catch (Exception ignored) {
            }
        } while ((current = current.getSuperclass()) != null);
        return null;
    }

    /**
     * @param clazz      the class to search in
     * @param methodName the name of the method to find
     * @return (1) The method (2) null, if nothing found
     */
    public static Method findUnderlyingDeclaredMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        Class<?> current = clazz;
        do {
            ULib.getInstance().debug(String.format("Searching for %s(%s) in %s", methodName, ArrayUtils.toString(parameterTypes), current.toString()));
            try {
                return current.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException ignored) {
            }
        } while ((current = current.getSuperclass()) != null);
        ULib.getInstance().debug(String.format("%s(%s) not found at all", methodName, ArrayUtils.toString(parameterTypes)));
        return null;
    }
}
