package eu.software4you.ulib.impl.litetransform;

import eu.software4you.litetransform.Hook;
import eu.software4you.litetransform.HookPoint;
import eu.software4you.litetransform.Injector;
import eu.software4you.litetransform.LiteTransform;
import eu.software4you.ulib.Agent;
import eu.software4you.ulib.Await;
import eu.software4you.ulib.inject.Impl;
import lombok.SneakyThrows;

import java.lang.reflect.*;

@Impl(LiteTransform.class)
final class InjectorImpl implements Injector {
    @Await
    private static Agent agent;

    private void validate() {
        if (!Agent.available())
            throw new IllegalStateException("LiteTransform Injection not available!");
    }

    @SneakyThrows
    @Override
    public void scan(Class<?> clazz) {
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
            hook(method, obj, hook.method(), hook.descriptor(),
                    hook.clazz(), hook.at());
        }
    }

    @Override
    public void hook(Method source, Object obj, Method into, HookPoint at) {
        validate();
        hook(source, obj, into.getName(), getDescriptor(into),
                into.getDeclaringClass().getName(), at);
    }

    @SneakyThrows
    @Override
    public void hook(Method source, Object obj, String methodName, String methodDescriptor, String className, HookPoint at) {
        validate();

        agent.transform(Class.forName(className), new Transformer(source,
                Modifier.isStatic(source.getModifiers()) ? null : obj,
                className, methodName, methodDescriptor, at));
    }

    @SneakyThrows
    private String getDescriptor(Method method) { // from https://stackoverflow.com/a/45122250/8400001
        Field signatureField = Method.class.getDeclaredField("signature");
        signatureField.setAccessible(true);
        String signature = (String) signatureField.get(method);
        if (signature != null) {
            return signature;
        }

        StringBuilder b = new StringBuilder("(");
        for (Class<?> c : method.getParameterTypes()) {
            signature = Array.newInstance(c, 0).toString();
            b.append(signature, 1, signature.indexOf('@'));
        }
        b.append(')');
        if (method.getReturnType() == void.class) {
            b.append("V");
        } else {
            signature = Array.newInstance(method.getReturnType(), 0).toString();
            b.append(signature, 1, signature.indexOf('@'));
        }
        return b.toString();
    }
}
