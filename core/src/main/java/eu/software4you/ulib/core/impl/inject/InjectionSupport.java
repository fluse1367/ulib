package eu.software4you.ulib.core.impl.inject;

import eu.software4you.ulib.core.collection.Pair;
import eu.software4you.ulib.core.function.BiParamTask;
import eu.software4you.ulib.core.inject.*;
import eu.software4you.ulib.core.reflect.ReflectUtil;

import java.lang.reflect.*;
import java.util.Arrays;

public final class InjectionSupport {
    public static Class<?> findTargetClass(Hook hook, Class<?> declaring, ClassLoader lookupLoader) {
        String clazz;
        if ((clazz = hook.clazz()).isBlank()) {

            if (!declaring.isAnnotationPresent(Hooks.class) || (clazz = declaring.getAnnotation(Hooks.class).value()).isBlank())
                throw new IllegalArgumentException("No target class specified");
        }
        final String clazzName = clazz;

        return ReflectUtil.forName(clazzName, false, lookupLoader)
                .orElseThrow(() -> new IllegalArgumentException("Unable to load class " + clazzName));
    }

    public static BiParamTask<? super Object[], ? super Callback<?>, ?> buildCall(Method method, Object invoke) {
        var types = method.getParameterTypes();
        if (method.isAnnotationPresent(FluentHookParams.class)) {
            if (!Arrays.equals(types, new Object[]{Object[].class, Callback.class}))
                throw new IllegalArgumentException("FluentHookParams annotation present but hook method (%s) has invalid signature"
                        .formatted(method.getName()));

            return (paramArray, cb) -> method.invoke(invoke, paramArray, cb);
        }
        if (types.length == 0 || types[types.length - 1] != Callback.class)
            throw new IllegalArgumentException("Hook method (%s) does not accept Callback object"
                    .formatted(method.getName()));

        return (paramArray, cb) -> {
            Object[] params = new Object[paramArray.length + 1];
            params[paramArray.length] = cb;
            System.arraycopy(paramArray, 0, params, 0, paramArray.length);
            method.invoke(invoke, params);
        };
    }

    public static String resolveDescriptor(Hook hook, Class<?> target) {
        String descriptor = hook.value();

        var resolved = resolveMethod(hook);
        if (!resolved.getSecond().isBlank())
            return descriptor;

        if (resolved.getFirst().equals("<init>")) {
            // descriptor is empty, therefore get default constructor
            var targetConstructor = target.getDeclaredConstructors()[0];
            return "<init>" + getDescriptor(targetConstructor);
        }

        var targetMethod = Arrays.stream(target.getDeclaredMethods())
                .filter(method -> method.getName().equals(resolved.getFirst()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Hook target `%s` not found in %s"
                        .formatted(hook.value(), target.getName())));
        return getSignature(targetMethod);
    }

    public static String getSignature(Method method) {
        return method.getName() + getDescriptor(method);
    }

    public static String getDescriptor(Method method) { // from https://stackoverflow.com/a/45122250/8400001
        StringBuilder b = new StringBuilder("(");
        Arrays.stream(method.getParameterTypes()).map(InjectionSupport::getTypeSignature).forEach(b::append);
        return b.append(')').append(getTypeSignature(method.getReturnType())).toString();
    }

    public static String getDescriptor(Constructor<?> constructor) {
        StringBuilder b = new StringBuilder("(");
        Arrays.stream(constructor.getParameterTypes()).map(InjectionSupport::getTypeSignature).forEach(b::append);
        return b.append(")V").toString();
    }

    /**
     * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html#type_signatures" target="_blank">https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html#type_signatures</a>
     */
    public static String getTypeSignature(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == boolean.class)
                return "Z";
            if (clazz == byte.class)
                return "B";
            if (clazz == char.class)
                return "C";
            if (clazz == short.class)
                return "S";
            if (clazz == int.class)
                return "I";
            if (clazz == long.class)
                return "J";
            if (clazz == float.class)
                return "F";
            if (clazz == double.class)
                return "D";
            if (clazz == void.class)
                return "V";
            throw new IllegalStateException(); // make compiler happy
        } else if (clazz.isArray()) {
            return String.format("[%s", getTypeSignature(clazz.getComponentType()));
        } else {
            return String.format("L%s;", clazz.getName().replace(".", "/"));
        }
    }

    public static Pair<String, String> resolveMethod(Hook hook) {
        return resolveMethod(hook.value());
    }

    public static Pair<String, String> resolveMethod(String methodDesc) {
        if (methodDesc.contains("(")) {
            int index = methodDesc.indexOf("(");
            String name = methodDesc.substring(0, index);
            String descriptor = methodDesc.substring(index);
            return new Pair<>(name, descriptor);
        }

        return new Pair<>(methodDesc, "");
    }

    public static void checkInvoke(Method hook, Object invoke) {
        if (Modifier.isStatic(hook.getModifiers()) == (invoke != null))
            throw new IllegalArgumentException("Invalid invoke object for hook `%s`".formatted(hook));
    }
}
