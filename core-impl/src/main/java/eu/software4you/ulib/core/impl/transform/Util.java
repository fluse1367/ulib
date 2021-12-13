package eu.software4you.ulib.core.impl.transform;

import eu.software4you.ulib.core.api.common.collection.Pair;
import eu.software4you.ulib.core.api.transform.Hook;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

final class Util {
    static String fullDescriptor(Method method) {
        return fullDescriptor(method.getDeclaringClass().getName(),
                method.getName(), getDescriptor(method), method.getDeclaringClass().getClassLoader());
    }

    static String fullDescriptor(String className, String methodName, String methodDescriptor, ClassLoader cl) {
        return String.format("%s.%s%s", className.replace(".", "/"),
                methodName, resolveDescriptor(className, methodName, methodDescriptor, cl));
    }

    @SneakyThrows
    static String resolveDescriptor(String className, String methodName, String methodDescriptor, ClassLoader classLoader) {
        if (methodDescriptor.isEmpty()) {
            Class<?> cl = Class.forName(className, false, classLoader);
            if (methodName.equals("<init>")) {
                return getDescriptor(cl.getDeclaredConstructors()[0]);
            }

            Method method = null;
            for (Method dm : cl.getDeclaredMethods()) {
                if (dm.getName().equals(methodName)) {
                    method = dm;
                    break;
                }
            }
            if (method == null)
                throw new IllegalArgumentException(String.format("Method %s not found in %s", methodName, className));
            return getDescriptor(method);
        }
        return methodDescriptor;
    }

    static String getDescriptor(Method method) { // from https://stackoverflow.com/a/45122250/8400001
        StringBuilder b = new StringBuilder("(");
        Arrays.stream(method.getParameterTypes()).map(Util::getTypeSignature).forEach(b::append);
        return b.append(')').append(getTypeSignature(method.getReturnType())).toString();
    }

    static String getDescriptor(Constructor<?> constructor) {
        StringBuilder b = new StringBuilder("(");
        Arrays.stream(constructor.getParameterTypes()).map(Util::getTypeSignature).forEach(b::append);
        return b.append(")V").toString();
    }

    /**
     * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html#type_signatures" target="_blank">https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html#type_signatures</a>
     */
    static String getTypeSignature(Class<?> clazz) {
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

    static Pair<String, String> resolveMethod(Hook hook) {
        return resolveMethod(hook.value());
    }

    static Pair<String, String> resolveMethod(String methodDesc) {
        if (methodDesc.contains("(")) {
            int index = methodDesc.indexOf("(");
            String name = methodDesc.substring(0, index);
            String descriptor = methodDesc.substring(index);
            return new Pair<>(name, descriptor);
        }

        return new Pair<>(methodDesc, "");
    }
}
