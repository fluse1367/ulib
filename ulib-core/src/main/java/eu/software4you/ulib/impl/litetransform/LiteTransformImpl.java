package eu.software4you.ulib.impl.litetransform;

import eu.software4you.litetransform.Hook;
import eu.software4you.litetransform.HookPoint;
import eu.software4you.litetransform.LiteTransform;
import eu.software4you.ulib.Agent;
import eu.software4you.ulib.Await;
import eu.software4you.ulib.inject.Impl;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

@Impl(LiteTransform.class)
final class LiteTransformImpl extends LiteTransform {
    @Await
    private static Agent agent;

    private void validate() {
        if (!Agent.available())
            throw new IllegalStateException("LiteTransform Injection not available!");
    }

    @SneakyThrows
    @Override
    public void autoHook0(Class<?> clazz) {
        validate();

        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Hook.class))
                continue;

            Object obj = null;
            if (!Modifier.isStatic(method.getModifiers())) {
                try {
                    Constructor<?> constr = clazz.getConstructor();
                    constr.setAccessible(true);
                    obj = constr.newInstance();
                } catch (NoSuchMethodException e) {
                    throw new NoSuchMethodError(e.getMessage());
                }
            }

            Hook hook = method.getAnnotation(Hook.class);

            String methodName = hook.method();
            String desc = "";
            if (methodName.contains("(")) {
                int index = methodName.indexOf("(");
                desc = methodName.substring(index);
                methodName = methodName.substring(0, index);
            }

            hook(method, obj, methodName, desc,
                    hook.clazz(), hook.at());
        }
    }

    @Override
    public void hook0(Method source, Object obj, Method into, HookPoint at) {
        validate();
        hook(source, obj, into.getName(), getDescriptor(into),
                into.getDeclaringClass().getName(), at);
    }

    @SneakyThrows
    @Override
    public void hook0(Method source, Object obj, String methodName, String methodDescriptor, String className, HookPoint at) {
        validate();

        agent.transform(Class.forName(className), new Transformer(source,
                Modifier.isStatic(source.getModifiers()) ? null : obj,
                className, methodName, methodDescriptor, at));
    }

    @SneakyThrows
    private String getDescriptor(Method method) { // from https://stackoverflow.com/a/45122250/8400001
        StringBuilder b = new StringBuilder("(");
        Arrays.stream(method.getParameterTypes()).map(this::getTypeSignature).forEach(b::append);
        return b.append(')').append(getTypeSignature(method.getReturnType())).toString();
    }

    /**
     * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html#type_signatures">https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html#type_signatures</a>
     */
    private String getTypeSignature(Class<?> clazz) {
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
            return String.format("L%s;", clazz.getName().replace("/", "."));
        }
    }
}
