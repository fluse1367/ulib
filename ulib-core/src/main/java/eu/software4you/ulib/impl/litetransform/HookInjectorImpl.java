package eu.software4you.ulib.impl.litetransform;

import eu.software4you.common.collection.Pair;
import eu.software4you.litetransform.Hook;
import eu.software4you.litetransform.HookInjector;
import eu.software4you.litetransform.HookPoint;
import eu.software4you.ulib.Agent;
import eu.software4you.ulib.Await;
import eu.software4you.ulib.inject.Impl;
import lombok.SneakyThrows;
import lombok.val;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

@Impl(HookInjector.class)
final class HookInjectorImpl extends HookInjector {
    @Await
    private static Agent agent;

    @Override
    public void hookStatic0(Class<?> clazz) {
        Agent.verifyAvailable();

        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Hook.class) || !Modifier.isStatic(method.getModifiers()))
                continue;
            transform(method.getAnnotation(Hook.class), method, null);
        }
    }

    @Override
    protected void hook0(Object inst, boolean hookStatic) {
        Agent.verifyAvailable();

        for (Method method : inst.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Hook.class) || (!hookStatic && Modifier.isStatic(method.getModifiers())))
                continue;
            transform(method.getAnnotation(Hook.class), method, inst);
        }
    }

    @Override
    public void directHook0(Method source, Object obj, Method into, HookPoint at) {
        directHook0(source, obj, into.getName(), getDescriptor(into),
                into.getDeclaringClass().getName(), at);
    }

    @Override
    public void directHook0(Method source, Object obj, String methodName, String methodDescriptor, String className, HookPoint at) {
        Agent.verifyAvailable();
        transform(source, obj, methodName, methodDescriptor, className, at);
    }

    private void transform(Hook hook, Method method, Object obj) {
        val p = resolveMethod(hook);
        transform(method, obj, p.getFirst(), p.getSecond(), hook.clazz(), hook.at());
    }

    @SneakyThrows
    private void transform(Method source, Object obj, String methodName, String methodDescriptor, String className, HookPoint at) {
        TransformerDepend.$();
        agent.transform(Class.forName(className), new Transformer(source,
                Modifier.isStatic(source.getModifiers()) ? null : obj,
                className, methodName, methodDescriptor, at));
    }

    private Pair<String, String> resolveMethod(Hook hook) {
        String methodName = hook.method();
        String desc = "";
        if (methodName.contains("(")) {
            int index = methodName.indexOf("(");
            desc = methodName.substring(index);
            methodName = methodName.substring(0, index);
        }

        return new Pair<>(methodName, desc);
    }

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
