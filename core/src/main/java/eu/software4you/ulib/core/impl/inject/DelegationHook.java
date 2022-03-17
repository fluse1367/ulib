package eu.software4you.ulib.core.impl.inject;

import eu.software4you.ulib.core.inject.Callback;
import eu.software4you.ulib.core.inject.FluentHookParams;
import lombok.SneakyThrows;

import java.util.function.*;
import java.util.stream.Stream;

public final class DelegationHook {
    private final BiFunction<String, Boolean, Class<?>> delegateLoadClass;
    private final Function<String, Class<?>> delegateFindClass;
    private final BiFunction<String, String, Class<?>> delegateFindModuleClass;
    private final Predicate<ClassLoader> filterClassLoader; // check class loader delegated
    private final BiPredicate<Class<?>, String> filterLoadingRequest; // requesting class, requested name

    @SneakyThrows
    public DelegationHook(BiFunction<String, Boolean, Class<?>> delegateLoadClass,
                          Function<String, Class<?>> delegateFindClass,
                          BiFunction<String, String, Class<?>> delegateFindModuleClass,
                          Predicate<ClassLoader> filterClassLoader,
                          BiPredicate<Class<?>, String> filterLoadingRequest
    ) {
        this.delegateLoadClass = delegateLoadClass;
        this.delegateFindClass = delegateFindClass;
        this.delegateFindModuleClass = delegateFindModuleClass;
        this.filterClassLoader = filterClassLoader;
        this.filterLoadingRequest = filterLoadingRequest;
    }

    private Class<?> delegationFindClass(String name) {
        return delegateFindClass.apply(name);
    }

    private Class<?> delegationFindClass(String module, String name) {
        return delegateFindModuleClass.apply(module, name);
    }

    private Class<?> delegationLoadClassClass(String name, boolean resolve) {
        return delegateLoadClass.apply(name, resolve);
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

    private boolean check(Object source, String name) {
        return source instanceof ClassLoader cl && filterClassLoader.test(cl)
               && filterLoadingRequest.test(identifyClassLoadingRequestSource(), name);
    }

    /* actual hooks */

    // hooks into the findClass method of the target loader
    public void hook_findClass(String name, Callback<Class<?>> cb) {
        if (!check(cb.self(), name))
            return;

        var cl = delegationFindClass(name);
        if (cl != null) {
            cb.setReturnValue(cl);
        }
    }

    @FluentHookParams
    public void hookAdditional_findClass(Object[] params, Callback<Class<?>> cb) {
        if (params.length == 0)
            return;

        if (!(params[0] instanceof String name)) {
            return;
        }

        hook_findClass(name, cb);
    }

    public void hook_findClass(String module, String name, Callback<Class<?>> cb) {
        if (!check(cb.self(), name))
            return;

        var cl = delegationFindClass(module, name);
        if (cl != null) {
            cb.setReturnValue(cl);
        }
    }

    // hooks into the loadClass method of the target loader
    public void hook_loadClass(String name, boolean resolve, Callback<Class<?>> cb) {
        if (!check(cb.self(), name))
            return;

        var cl = delegationLoadClassClass(name, resolve);
        if (cl != null) {
            cb.setReturnValue(cl);
        }
    }
}
