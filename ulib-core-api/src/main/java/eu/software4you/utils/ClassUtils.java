package eu.software4you.utils;

import eu.software4you.ulib.ULib;
import lombok.val;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

public class ClassUtils {
    /**
     * Returns whether a certain class exists.
     *
     * @param className the fully qualified name of the desired class.
     * @return true if the class exists or false if it does not.
     */
    public static boolean isClass(@NotNull String className) {
        try {
            Class.forName(className);
            return true;
        } catch (Throwable thr) {
            return false;
        }
    }

    /**
     * Tries to load a certain class.
     *
     * @param className the fully qualified name of the desired class.
     * @return the {@code Class} object for the class with the specified name, or if the class does not exist {@code null}.
     */
    @Nullable
    public static Class<?> forName(@NotNull String className) {
        try {
            return Class.forName(className);
        } catch (Throwable thr) {
            ULib.logger().log(Level.SEVERE, thr, () -> String.format("%s could not be loaded", className));
        }
        return null;
    }

    /**
     * Tries to obtain a value of an enum.
     *
     * @param enumName  the fully qualified name of the desired enum.
     * @param enumEntry the fully qualified name of the desired entry.
     * @return the {@code Class} object for the class with the specified name, or if the class does not exist {@code null}.
     */
    @Nullable
    public static Object getEnumEntry(@NotNull String enumName, @NotNull String enumEntry) {
        try {
            Class<?> enumClass = Class.forName(enumName);
            if (!enumClass.isAssignableFrom(Enum.class)) {
                ULib.logger().log(Level.SEVERE, () -> String.format("%s is not an enumeration", enumClass.getSimpleName()));
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
     * Tries to obtain a value of an enum.
     *
     * @param enumClass the class of enum
     * @param enumEntry the fully qualified name of the desired entry.
     * @return the {@code Class} object for the class with the specified name, or if the class does not exist {@code null}.
     */
    @Nullable
    public static Object getEnumEntry(@NotNull Class<? extends Enum<?>> enumClass, @NotNull String enumEntry) {
        try {
            return enumClass.getMethod("valueOf", String.class).invoke(null, enumEntry);
        } catch (Throwable thr) {
            ULib.logger().log(Level.SEVERE, thr, () -> String.format("%s could not be found in %s", enumEntry, enumClass.getName()));
        }
        return null;
    }

    /**
     * Searches a class and it's superclasses for a certain field.
     *
     * @param clazz     the class to search in
     * @param fieldName the name of the field to find
     * @return (1) The field (2) {@code null}, if nothing found
     */
    @Nullable
    public static Field findUnderlyingField(@NotNull Class<?> clazz, @NotNull String fieldName) {
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
     * Searches a class and it's superclasses for a certain (declared) field.
     *
     * @param clazz     the class to search in
     * @param fieldName the name of the field to find
     * @return (1) The field (2) {@code null}, if nothing found
     */
    @Nullable
    public static Field findUnderlyingDeclaredField(@NotNull Class<?> clazz, @NotNull String fieldName) {
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
     * Collects all fields from a certain class and it's superclasses.
     *
     * @param clazz the class to search in
     * @return The collection of fields
     */
    @NotNull
    public static Collection<Field> findUnderlyingFields(@NotNull Class<?> clazz) {
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
     * Collects all (declared) fields from a certain class and it's superclasses.
     *
     * @param clazz the class to search in
     * @return The collection of fields
     */
    @NotNull
    public static Collection<Field> findUnderlyingDeclaredFields(@NotNull Class<?> clazz) {
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
     * Searches a class and it's superclasses for a certain method.
     *
     * @param clazz      the class to search in
     * @param methodName the name of the method to find
     * @return (1) The method (2) {@code null}, if nothing found
     */
    @Nullable
    public static Method findUnderlyingMethod(@NotNull Class<?> clazz, @NotNull String methodName, @NotNull Class<?>... parameterTypes) {
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
     * Searches a class and it's superclasses for a certain (declared) method.
     *
     * @param clazz      the class to search in
     * @param methodName the name of the method to find
     * @return (1) The method (2) {@code null}, if nothing found
     */
    @Nullable
    public static Method findUnderlyingDeclaredMethod(@NotNull Class<?> clazz, @NotNull String methodName, @NotNull Class<?>... parameterTypes) {
        Class<?> current = clazz;
        do {
            val cl = current;
            ULib.logger().finer(() -> String.format("Searching for %s(%s) in %s", methodName, ArrayUtils.toString(parameterTypes), cl));
            try {
                return current.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException ignored) {
            }
        } while ((current = current.getSuperclass()) != null);
        ULib.logger().finer(() -> String.format("%s(%s) not found at all", methodName, ArrayUtils.toString(parameterTypes)));
        return null;
    }
}
