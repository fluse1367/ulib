package eu.software4you.ulib.core.impl.inject;

import eu.software4you.ulib.core.inject.*;
import eu.software4you.ulib.core.reflect.Param;
import eu.software4you.ulib.core.reflect.ReflectUtil;
import eu.software4you.ulib.core.util.Expect;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiPredicate;

public final class DelegationInjector {

    public static DelegationInjector delegateTo(ClassLoader delegate, ClassLoader target, BiPredicate<Class<?>, String> filter) {
        var hook = new DelegationHook(
                (name, resolve) -> ReflectUtil.<Class<?>>call(delegate.getClass(), delegate, "loadClass()",
                        Param.fromMultiple(name, resolve)).getValue(),
                name -> ReflectUtil.<Class<?>>call(delegate.getClass(), delegate, "findClass()",
                        Param.fromMultiple(name)).getValue(),
                (module, name) -> ReflectUtil.<Class<?>>call(delegate.getClass(), delegate, "findClass()",
                        Param.fromMultiple(module, name)).getValue(),
                loader -> loader == target,
                filter
        );

        return new DelegationInjector(target.getClass(), hook, Collections.emptyMap());
    }

    private final Class<? extends ClassLoader> clazz;
    private final HookInjection injection;
    private final DelegationHook delegation;
    private final Method hookFindClass, hookFindAdditionalClass, hookFindModuleClass, hookLoadClass;
    private final Map<String, Collection<Class<?>[]>> additionalHooks;

    @SneakyThrows
    public DelegationInjector(Class<? extends ClassLoader> clazz, DelegationHook delegation, Map<String, Collection<Class<?>[]>> additionalHooks) {
        this.injection = new HookInjection(this.clazz = clazz);
        this.delegation = delegation;
        this.additionalHooks = additionalHooks;
        var c = DelegationHook.class;
        this.hookFindClass = c.getMethod("hook_findClass", String.class, Callback.class);
        this.hookFindModuleClass = c.getMethod("hook_findClass", String.class, String.class, Callback.class);
        this.hookLoadClass = c.getMethod("hook_loadClass", String.class, boolean.class, Callback.class);
        this.hookFindAdditionalClass = c.getMethod("hookAdditional_findClass", Object[].class, Callback.class);
    }

    public Expect<Void, Exception> inject() {
        ReflectUtil.findUnderlyingMethod(clazz, "findClass", true, String.class)
                .ifPresent(into -> inject(hookFindClass, into));
        ReflectUtil.findUnderlyingMethod(clazz, "findClass", true, String.class, String.class)
                .ifPresent(into -> inject(hookFindModuleClass, into));
        ReflectUtil.findUnderlyingMethod(clazz, "loadClass", true, String.class, boolean.class)
                .ifPresent(into -> inject(hookLoadClass, into));

        additionalHooks.forEach((name, coll) -> coll.forEach(params -> ReflectUtil
                .findUnderlyingMethod(clazz, name, true, params)
                .ifPresent(into -> inject(hookFindAdditionalClass, into))));

        // TODO: delegate resource finding?

        return injection.inject();
    }

    private void inject(Method hook, Method into) {
        injection.addHook(hook, this.delegation, into, HookPoint.HEAD);
    }
}
