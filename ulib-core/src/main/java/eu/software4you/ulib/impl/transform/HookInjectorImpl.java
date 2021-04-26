package eu.software4you.ulib.impl.transform;

import eu.software4you.transform.Hook;
import eu.software4you.transform.HookInjector;
import eu.software4you.transform.HookPoint;
import eu.software4you.ulib.Agent;
import eu.software4you.ulib.Await;
import eu.software4you.ulib.inject.Impl;
import lombok.SneakyThrows;
import lombok.val;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Impl(HookInjector.class)
final class HookInjectorImpl extends HookInjector {
    @Await
    private static Agent agent;

    private final Map<String, List<String>> injected = new ConcurrentHashMap<>();

    @Override
    protected void hookStatic0(Class<?> clazz) {
        Agent.verifyAvailable();

        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Hook.class) || !Modifier.isStatic(method.getModifiers()))
                continue;
            inject(method.getAnnotation(Hook.class), method, null);
        }
    }

    @Override
    protected void unhookStatic0(Class<?> clazz) {
        Hooks.delHooks(clazz).forEach((className, li) ->
                li.forEach(desc -> unref(className, desc)));
    }

    @Override
    protected void hook0(Object inst, boolean hookStatic) {
        Agent.verifyAvailable();

        for (Method method : inst.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Hook.class) || (!hookStatic && Modifier.isStatic(method.getModifiers())))
                continue;
            inject(method.getAnnotation(Hook.class), method, inst);
        }
    }

    @Override
    protected void unhook0(Object inst, boolean unhookStatic) {
        Hooks.delHooks(inst).forEach((className, li) ->
                li.forEach(desc -> unref(className, desc)));
        if (unhookStatic) {
            unhookStatic0(inst.getClass());
        }
    }

    @Override
    protected void directHook0(Method source, Object obj, Method into, HookPoint at) {
        directHook0(source, obj, into.getName(), Util.getDescriptor(into),
                into.getDeclaringClass().getName(), at);
    }

    @Override
    protected void directHook0(Method source, Object obj, String className, String methodName, String methodDescriptor, HookPoint at) {
        Agent.verifyAvailable();
        inject(source, obj, className, methodName, methodDescriptor, at);
    }

    @Override
    protected void directUnhook0(Method source, Object sourceInst, Method into, HookPoint at) {
        Hooks.delHook(source, sourceInst, Util.fullDescriptor(into), at);
        unref(into.getDeclaringClass().getName(), into.getName() + Util.getDescriptor(into));
    }

    @Override
    protected void directUnhook0(Method source, Object sourceInst, String className, String methodName, String methodDescriptor, HookPoint at) {
        Hooks.delHook(source, sourceInst, Util.fullDescriptor(className, methodName, methodDescriptor), at);
        unref(className, methodName + Util.resolveDescriptor(className, methodName, methodDescriptor));
    }

    private void unref(String className, String desc) {
        if (!injected.containsKey(className)) {
            return;
        }
        val li = injected.get(className);
        li.remove(desc);
        if (li.isEmpty())
            injected.remove(className);
    }

    private void inject(Hook hook, Method source, Object obj) {
        val p = Util.resolveMethod(hook);

        String className = hook.clazz();
        if (className.isEmpty()) {
            Class<?> declaring = source.getDeclaringClass();
            if (!declaring.isAnnotationPresent(eu.software4you.transform.Hooks.class)) {
                throw new IllegalArgumentException("Empty fully qualified class name without @Hooks annotation being present.");
            }
            className = declaring.getAnnotation(eu.software4you.transform.Hooks.class).value();
        }

        inject(source, obj, className, p.getFirst(), p.getSecond(), hook.at());
    }

    @SneakyThrows
    private void inject(Method source, Object sourceInst, String className, String methodName, String methodDescriptor, HookPoint at) {
        String fullDescriptor = Util.fullDescriptor(className, methodName, methodDescriptor);

        Hooks.addHook(source, sourceInst, fullDescriptor, at);


        String desc = methodName + Util.resolveDescriptor(className, methodName, methodDescriptor);
        if (injected.containsKey(className)) {
            if (injected.get(className).contains(desc)) {
                return; // the target method is already injected
            }
        } else {
            injected.put(className, new ArrayList<>());
        }
        val li = injected.get(className);

        List<String> methods = new ArrayList<>(li);
        methods.add(desc);

        TransformerDepend.$();
        agent.transform(Class.forName(className), new Transformer(
                className, methods));

        // add desc only after successful transformation
        li.add(desc);
    }
}
