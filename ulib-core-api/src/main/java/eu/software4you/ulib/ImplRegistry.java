package eu.software4you.ulib;

import eu.software4you.common.collection.Pair;
import eu.software4you.reflect.ReflectUtil;
import lombok.val;

import java.util.*;

public final class ImplRegistry {
    private static final Map<Class<?>, Pair<Object, Collection<Class<?>>>> implementations = new HashMap<>();

    private static Class<?> caller() {
        Class<?> caller;
        int depth = 2;
        do {
            caller = ReflectUtil.getCallerClass(depth++);
        } while (caller == ImplRegistry.class);
        return caller;
    }

    private static Object check(Class<?> clazz) {
        if (!implementations.containsKey(clazz)) {
            throw new IllegalStateException(String.format("'%s' has no registration", clazz.getName()));
        }
        val p = implementations.get(clazz);
        val receivers = p.getSecond();
        Class<?> caller = caller();
        if (!receivers.contains(caller)) {
            throw new SecurityException(String.format("'%s' does not have permission to retrieve a registration for '%s'",
                    caller.getName(), clazz.getName()));
        }
        return p.getFirst();
    }

    private static void place(Class<?> clazz, Object object, Collection<Class<?>> access) {
        if (implementations.containsKey(clazz)) {
            throw new IllegalStateException(String.format("'%s' already has a registration", clazz.getName()));
        }
        Class<?> caller = caller();
        if (!clazz.isAssignableFrom(caller)) {
            throw new SecurityException(String.format("'%s' does not have permission create a registration for '%s'",
                    caller.getName(), clazz.getName()));
        }
        implementations.put(clazz, new Pair<>(object, access));
    }

    public static <T> void put(Class<? extends T> clazz, T instance) {
        put(clazz, instance, Collections.singletonList(clazz));
    }

    public static <T> void put(Class<? extends T> clazz, T instance, Class<?> access) {
        put(clazz, instance, Collections.singletonList(access));
    }

    public static <T> void put(Class<? extends T> clazz, T instance, Class<?>... access) {
        put(clazz, instance, Arrays.asList(access));
    }

    public static <T> void put(Class<? extends T> clazz, T instance, Collection<Class<?>> access) {
        place(clazz, instance, access);
    }

    public static <T> T get(Class<T> clazz) {
        Object t = check(clazz);
        if (clazz.isInstance(t))
            return (T) t;
        throw new IllegalStateException(String.format("Registration of '%s' is not an instance: %s", clazz.getName(), t.getClass().getName()));
    }
}
