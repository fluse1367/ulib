package eu.software4you.ulib;

import eu.software4you.ulib.impl.reflect.ReflectUtilImpl;
import eu.software4you.ulib.inject.Factory;
import eu.software4you.ulib.inject.Impl;
import eu.software4you.ulib.inject.InjectionException;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.logging.Logger;

public final class ImplInjector {

    static Logger logger;

    static void autoInject(Class<?> impl) {
        // check for @Impl
        if (impl.isAnnotationPresent(Impl.class)) {
            if (!Modifier.isFinal(impl.getModifiers())) { // reject not final implementations
                logger.warning(() -> String.format("Implementation %s invalid: not final", impl));
                return;
            }
            Impl im = impl.getDeclaredAnnotation(Impl.class);

            for (Class<?> target : im.value()) {
                autoInject(impl, target);
            }
        }
    }

    @SneakyThrows
    private static void autoInject(Class<?> impl, Class<?> target) {
        int injected = 0;

        // look out of @Await
        for (Field into : target.getDeclaredFields()) {
            if (!into.isAnnotationPresent(Await.class)
                || !Modifier.isStatic(into.getModifiers()))
                continue;
            // @Await found, inject

            // ImplFactory
            if (into.getType() != into.getDeclaringClass()) {
                if (into.getType() == ImplFactory.class
                    // check if type parameter of `into` is compatible with the factory
                    && into.getGenericType() instanceof ParameterizedType type
                    && type.getActualTypeArguments().length == 1) {
                    var param = type.getActualTypeArguments()[0];

                    Class<?> cl;
                    if (param instanceof Class<?> c) {
                        cl = c;
                    } else if (param instanceof ParameterizedType pp && pp.getRawType() instanceof Class<?> c) {
                        cl = c;
                    } else {
                        continue;
                    }

                    if (cl.isAssignableFrom(impl)) {
                        // type param is compatible
                        injectImplFactory(impl, into);
                        injected++;
                    }
                }
                continue;
            }
            logger.finest(() -> String.format("Injecting %s into %s", impl, into));

            // direct implementation with default constructor
            Constructor<?> constructor = impl.getDeclaredConstructor();
            constructor.setAccessible(true);

            inject(constructor.newInstance(), into);
            injected++;
        }
        if (injected == 0)
            throw new InjectionException(impl, target, "Target does not qualify");
    }

    private static void injectImplFactory(Class<?> impl, Field into) {
        for (Constructor<?> constructor : impl.getDeclaredConstructors()) {
            if (!constructor.isAnnotationPresent(Factory.class)) {
                continue;
            }
            logger.finest(() -> String.format("Injecting %s as factory into %s", impl, into));
            constructor.setAccessible(true);

            @SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"}) ImplFactory<?> fun = new ImplFactory<>() {
                @SneakyThrows
                @Override
                public Object fabricate(Object... objects) {
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
        Class<?> caller = ReflectUtilImpl.retrieveCallerClass(1);
        Class<?> inst = instance.getClass();
        if (!inst.isAssignableFrom(caller)) {
            throw new InjectionException(instance, into, String.format("caller (%s) has insufficient permission", caller));
        }

        logger.finer(() -> String.format("%s: Injecting %s into %s", caller, inst, into));

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
