package eu.software4you.ulib;

import eu.software4you.function.ConstructingFunction;
import eu.software4you.reflect.ReflectUtil;
import eu.software4you.ulib.inject.Impl;
import eu.software4you.ulib.inject.ImplConst;
import eu.software4you.ulib.inject.InjectionException;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class ImplInjector {

    static void autoInject(Class<?> impl) {
        // check for @Impl
        if (impl.isAnnotationPresent(Impl.class)) {
            Impl im = impl.getDeclaredAnnotation(Impl.class);

            autoInject(impl, im.value());
        }
    }

    @SneakyThrows
    private static void autoInject(Class<?> impl, Class<?> target) {
        // look out of @Await
        for (Field into : target.getDeclaredFields()) {
            if (!into.isAnnotationPresent(Await.class)
                    || !Modifier.isStatic(into.getModifiers()))
                continue;
            // @Await found, inject

            // ConstructingFunction
            if (into.getType() != into.getDeclaringClass()) {
                if (into.getType() != ConstructingFunction.class) {
                    return;
                }

                injectConstructingFunction(impl, into);

                return;
            }
            ULib.get().getLogger().finer(String.format("Injecting %s into %s", impl, into));

            // direct implementation with default constructor
            Constructor<?> constructor = impl.getDeclaredConstructor();
            constructor.setAccessible(true);

            inject(constructor.newInstance(), into);

            break;
        }
    }

    private static void injectConstructingFunction(Class<?> impl, Field into) {
        for (Constructor<?> constructor : impl.getDeclaredConstructors()) {
            if (!constructor.isAnnotationPresent(ImplConst.class)) {
                continue;
            }
            ULib.get().getLogger().finer(String.format("Injecting %s as constructing function into %s", impl, into));
            constructor.setAccessible(true);

            ConstructingFunction<?> fun = new ConstructingFunction<Object>() {
                @SneakyThrows
                @Override
                public Object apply(Object... objects) {
                    return constructor.newInstance(objects);
                }
            };
            inject(fun, into);

            break;
        }
    }

    @SneakyThrows
    private static void inject(Object instance, Field into) {
        into.setAccessible(true);
        if (into.get(null) != null) {
            throw new InjectionException(instance, into, "already injected");
        }
        into.set(null, instance);
    }

    public static <T> T inject(T instance, Class<?> into) {
        Class<?> caller = ReflectUtil.getCallerClass();
        Class<?> inst = instance.getClass();
        if (!inst.isAssignableFrom(caller)) {
            throw new InjectionException(instance, into, String.format("caller (%s) has insufficient permission", caller));
        }

        if (caller != Agent.class) {
            ULib.get().getLogger().finer(String.format("%s: Injecting %s into %s", caller, inst, into));
        }

        for (Field field : into.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Await.class)
                    || !Modifier.isStatic(field.getModifiers())
                    || !field.getType().isInstance(instance)) {
                continue;
            }
            inject(instance, field);
            return instance;
        }

        throw new InjectionException(instance, into, "missing injection point");
    }

}
