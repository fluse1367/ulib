package eu.software4you.ulib.core.impl.transform;

import eu.software4you.ulib.core.ULib;
import eu.software4you.ulib.core.api.reflect.ReflectUtil;
import eu.software4you.ulib.core.api.transform.Hook;
import eu.software4you.ulib.core.api.transform.HookPoint;
import eu.software4you.ulib.core.impl.Agent;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;

public final class HookInjectorImpl extends eu.software4you.ulib.core.api.transform.HookInjector {
    private final Map<String, List<String>> injected = new ConcurrentHashMap<>(); // class -> methods

    public HookInjectorImpl() {
        if (!Agent.available())
            return;
        System.getProperties().put("ulib.hooking", new Object[]{
                /* [0] Hook runner */
                (Function<Object[], ?>) HookRunner::runHooks,

                /* [1] Callback#isReturning() */
                (Function<Callback<?>, Boolean>) Callback::isReturning,

                /* [2] Callback#getReturnValue() */
                (Function<Callback<?>, ?>) Callback::getReturnValue,

                /* [3] caller class determination */
                (Supplier<Class<?>>) () -> ReflectUtil.getCallerClass(4)
        });
        Agent.getInstance().addTransformer(new HookInjector(ULib.logger(), injected));
    }

    @Override
    protected void hookStatic0(Class<?> clazz) {
        Agent.verifyAvailable();

        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Hook.class) || !Modifier.isStatic(method.getModifiers()))
                continue;
            inject(method.getAnnotation(Hook.class), method, null, clazz.getClassLoader());
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
            inject(method.getAnnotation(Hook.class), method, inst, inst.getClass().getClassLoader());
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
                Util.getDescriptor(into), at, into.getDeclaringClass().getClassLoader());
    }

    @Override
    protected void directHook0(Method source, Object obj, String className, String methodName, String methodDescriptor, HookPoint at, ClassLoader cl) {
        Agent.verifyAvailable();
        inject(source, obj, className, methodName, methodDescriptor, at, cl);
    }

    @Override
    protected void directUnhook0(Method source, Object sourceInst, Method into, HookPoint at) {
        HookRunner.delHook(source, sourceInst, Util.fullDescriptor(into), at.ordinal());
        unref(into.getDeclaringClass().getName(), into.getName() + Util.getDescriptor(into));
    }

    @Override
    protected void directUnhook0(Method source, Object sourceInst, String className, String methodName, String methodDescriptor, HookPoint at, ClassLoader cl) {
        HookRunner.delHook(source, sourceInst, Util.fullDescriptor(className, methodName, methodDescriptor, cl), at.ordinal());
        unref(className, methodName + Util.resolveDescriptor(className, methodName, methodDescriptor, cl));
    }

    private void unref(String className, String desc) {
        if (!injected.containsKey(className)) {
            return;
        }
        var li = injected.get(className);
        li.remove(desc);
        if (li.isEmpty())
            injected.remove(className);
    }

    private void inject(Hook hook, Method source, Object obj, ClassLoader cl) {
        var p = Util.resolveMethod(hook);

        String className = hook.clazz();
        if (className.isEmpty()) {
            Class<?> declaring = source.getDeclaringClass();
            if (!declaring.isAnnotationPresent(eu.software4you.ulib.core.api.transform.Hooks.class)) {
                throw new IllegalArgumentException("Empty fully qualified class name without @Hooks annotation being present.");
            }
            className = declaring.getAnnotation(eu.software4you.ulib.core.api.transform.Hooks.class).value();
        }

        inject(source, obj, className, p.getFirst(), p.getSecond(), hook.at(), cl);
    }

    @SneakyThrows
    private void inject(Method source, Object sourceInst, String className, String methodName, String methodDescriptor, HookPoint at, ClassLoader loader) {
        String fullDescriptor = Util.fullDescriptor(className, methodName, methodDescriptor, loader);

        HookRunner.addHook(source, sourceInst, fullDescriptor, at.ordinal());

        String desc = methodName + Util.resolveDescriptor(className, methodName, methodDescriptor, loader);
        if (injected.containsKey(className)) {
            if (injected.get(className).contains(desc)) {
                return; // the target method is already injected
            }
        } else {
            injected.put(className, new ArrayList<>());
        }
        var methods = injected.get(className);
        methods.add(desc);

        try {
            var cl = Class.forName(className, false, source.getDeclaringClass().getClassLoader());
            Agent.getInstance().transform(cl);
        } catch (Throwable thr) {
            ULib.logger().log(Level.WARNING, thr, () -> "Agent transformation failure (" + fullDescriptor + ")");
        }
    }
}