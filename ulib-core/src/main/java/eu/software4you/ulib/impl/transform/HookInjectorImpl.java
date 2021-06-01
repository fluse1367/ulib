package eu.software4you.ulib.impl.transform;

import eu.software4you.libex.function.BoolFunc;
import eu.software4you.libex.function.Callb;
import eu.software4you.libex.function.Func;
import eu.software4you.reflect.ReflectUtil;
import eu.software4you.transform.Hook;
import eu.software4you.transform.HookInjector;
import eu.software4you.transform.HookPoint;
import eu.software4you.ulib.Agent;
import eu.software4you.ulib.Await;
import eu.software4you.ulib.ULib;
import eu.software4you.ulib.inject.Impl;
import lombok.SneakyThrows;
import lombok.val;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

@Impl(HookInjector.class)
final class HookInjectorImpl extends HookInjector {
    @Await
    private static Agent agent;

    public HookInjectorImpl() {
        Callb.put(
                /* [0] Hook runner */
                HookRunner::runHooks,

                /* [1] Callback#isReturning() */
                (BoolFunc<Callback<?>>) Callback::isReturning,

                /* [2] Callback#getReturnValue() */
                (Func<Callback<?>, ?>) Callback::getReturnValue,

                /* [3] caller class determination */
                () -> ReflectUtil.getCallerClass(4)
        );
    }

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
        HookRunner.delHooks(clazz).forEach((className, li) ->
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
        HookRunner.delHooks(inst).forEach((className, li) ->
                li.forEach(desc -> unref(className, desc)));
        if (unhookStatic) {
            unhookStatic0(inst.getClass());
        }
    }

    @Override
    protected void directHook0(Method source, Object obj, Method into, HookPoint at) {
        directHook0(source, obj, into.getDeclaringClass().getName(), into.getName(),
                Util.getDescriptor(into), at);
    }

    @Override
    protected void directHook0(Method source, Object obj, String className, String methodName, String methodDescriptor, HookPoint at) {
        Agent.verifyAvailable();
        inject(source, obj, className, methodName, methodDescriptor, at);
    }

    @Override
    protected void directUnhook0(Method source, Object sourceInst, Method into, HookPoint at) {
        HookRunner.delHook(source, sourceInst, Util.fullDescriptor(into), at.ordinal());
        unref(into.getDeclaringClass().getName(), into.getName() + Util.getDescriptor(into));
    }

    @Override
    protected void directUnhook0(Method source, Object sourceInst, String className, String methodName, String methodDescriptor, HookPoint at) {
        HookRunner.delHook(source, sourceInst, Util.fullDescriptor(className, methodName, methodDescriptor), at.ordinal());
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

        HookRunner.addHook(source, sourceInst, fullDescriptor, at.ordinal());


        String desc = methodName + Util.resolveDescriptor(className, methodName, methodDescriptor);
        if (injected.containsKey(className)) {
            if (injected.get(className).contains(desc)) {
                return; // the target method is already injected
            }
        } else {
            injected.put(className, new ArrayList<>());
        }
        val li = injected.get(className);

        List<String> methods = new ArrayList<>(li.size() + 1);
        methods.addAll(li);
        methods.add(desc);

        try {
            val cl = Class.forName(className, false, source.getDeclaringClass().getClassLoader());
            agent.transform(cl, new Transformer(
                    className, methods, ULib.logger()));
        } catch (Throwable thr) {
            ULib.logger().log(Level.WARNING, thr, () -> "Agent transformation failure (" + fullDescriptor + ")");
            return;
        }
        // add desc only after successful transformation
        li.add(desc);
    }
}
