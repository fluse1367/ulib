package eu.software4you.ulib.core.impl.inject;

import eu.software4you.ulib.core.inject.*;
import eu.software4you.ulib.core.reflect.ReflectUtil;
import eu.software4you.ulib.core.util.Expect;

import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class ClassLoaderDelegationHook {

    // for injection
    private final Class<? extends ClassLoader> targetClazz;
    private final HookInjection injection;
    private final Map<String, Collection<Class<?>[]>> additionalHooks;

    // for hook execution
    private final ClassLoaderDelegation delegation;
    private final Predicate<ClassLoader> filterClassLoader; // check class loader delegated
    private final BiPredicate<Class<?>, String> filterLoadingRequest; // requesting class, requested name

    public ClassLoaderDelegationHook(Class<? extends ClassLoader> targetClazz,
                                     Map<String, Collection<Class<?>[]>> additional,
                                     ClassLoaderDelegation delegation,
                                     Predicate<ClassLoader> filterClassLoader,
                                     BiPredicate<Class<?>, String> filterLoadingRequest) {
        this.targetClazz = targetClazz;
        this.additionalHooks = additional;
        this.injection = new HookInjection(targetClazz);

        this.delegation = delegation;
        this.filterClassLoader = filterClassLoader;
        this.filterLoadingRequest = filterLoadingRequest;
    }

    public Expect<Void, Exception> inject() {
        // delegate class finding/loading

        ReflectUtil.findUnderlyingMethod(targetClazz, "findClass", true, String.class)
                .ifPresent(into -> injection.<Class<?>>addHook(into, HookPoint.HEAD,
                        (p, c) -> hook_findClass((String) p[0], c)
                ));

        ReflectUtil.findUnderlyingMethod(targetClazz, "findClass", true, String.class, String.class)
                .ifPresent(into -> injection.<Class<?>>addHook(into, HookPoint.HEAD,
                        (p, c) -> hook_findClass((String) p[0], (String) p[1], c)
                ));

        ReflectUtil.findUnderlyingMethod(targetClazz, "loadClass", true, String.class, boolean.class)
                .ifPresent(into -> injection.<Class<?>>addHook(into, HookPoint.HEAD,
                        (p, c) -> hook_loadClass((String) p[0], (boolean) p[1], c)
                ));

        additionalHooks.forEach((name, coll) -> coll.forEach(params -> ReflectUtil
                .findUnderlyingMethod(targetClazz, name, true, params)
                .ifPresent(into -> injection.addHook(into, HookPoint.HEAD,
                        this::hookAdditional_findClass
                ))));

        // delegate resource finding

        ReflectUtil.findUnderlyingMethod(targetClazz, "findResource", true, String.class)
                .ifPresent(into -> injection.<URL>addHook(into, HookPoint.HEAD,
                        (p, c) -> hook_findResource((String) p[0], c)
                ));

        ReflectUtil.findUnderlyingMethod(targetClazz, "findResource", true, String.class, String.class)
                .ifPresent(into -> injection.<URL>addHook(into, HookPoint.HEAD,
                        (p, c) -> hook_findResource((String) p[0], (String) p[1], c)
                ));

        return injection.inject();
    }

    private Class<?> identifyClassLoadingRequestSource() {
        // walk through stack and find first class that is not a class loader anymore
        var frames = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(Stream::toList);

        boolean walkingClassLoaderChain = false;
        for (StackWalker.StackFrame frame : frames) {
            var cl = frame.getDeclaringClass();
            boolean isClassLoader = ClassLoader.class.isAssignableFrom(cl);

            if (isClassLoader && !walkingClassLoaderChain) {
                walkingClassLoaderChain = true;
            } else if (walkingClassLoaderChain && !isClassLoader) {
                // found class that is nod a class loader
                return cl;
            }
        }
        throw new IllegalStateException(); // a class loader cannot be the source of the class loading request
    }

    private boolean checkCl(Object source) {
        return source instanceof ClassLoader cl && filterClassLoader.test(cl);
    }

    private boolean check(Object source, String name) {
        return checkCl(source)
               && filterLoadingRequest.test(identifyClassLoadingRequestSource(), name);
    }

    // - actual hooks -

    // class finding/loading hooks

    private void hook_findClass(String name, Callback<Class<?>> cb) {
        if (!check(cb.self(), name))
            return;

        var cl = delegation.findClass(name);
        if (cl != null) {
            cb.setReturnValue(cl);
        }
    }

    private void hookAdditional_findClass(Object[] params, Callback<Class<?>> cb) {
        if (params.length == 0)
            return;

        if (!(params[0] instanceof String name)) {
            return;
        }

        hook_findClass(name, cb);
    }

    private void hook_findClass(String module, String name, Callback<Class<?>> cb) {
        if (!check(cb.self(), name))
            return;

        var cl = delegation.findClass(module, name);
        if (cl != null) {
            cb.setReturnValue(cl);
        }
    }

    private void hook_loadClass(String name, boolean resolve, Callback<Class<?>> cb) {
        if (!check(cb.self(), name))
            return;

        var cl = delegation.loadClass(name, resolve);
        if (cl != null) {
            cb.setReturnValue(cl);
        }
    }

    // resource finding hooks

    private void hook_findResource(String name, Callback<URL> cb) {
        if (!checkCl(cb.self()))
            return;

        var u = delegation.findResource(name);
        if (u != null) {
            cb.setReturnValue(u);
        }
    }

    private void hook_findResource(String module, String name, Callback<URL> cb) {
        if (!checkCl(cb.self()))
            return;

        var u = delegation.findResource(module, name);
        if (u != null) {
            cb.setReturnValue(u);
        }
    }
}