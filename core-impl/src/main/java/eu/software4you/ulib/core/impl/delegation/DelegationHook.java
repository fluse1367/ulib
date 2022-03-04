package eu.software4you.ulib.core.impl.delegation;

import eu.software4you.ulib.core.ULib;
import eu.software4you.ulib.core.api.transform.Callback;
import eu.software4you.ulib.core.api.transform.FluentHookParams;
import lombok.SneakyThrows;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Stream;

public final class DelegationHook {

    private final Logger logger;
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

        this.logger = ULib.logger();
        logger.finest("Delegation hook init: " + this);
    }

    private Class<?> delegationFindClass(String name) {
        logger.finest("Finding delegated class: " + name);
        return delegateFindClass.apply(name);
    }

    private Class<?> delegationFindClass(String module, String name) {
        logger.finest("Finding delegated class: " + name + " from module " + module);
        return delegateFindModuleClass.apply(module, name);
    }

    @SneakyThrows
    private Class<?> delegationLoadClassClass(String name, boolean resolve) {
        logger.finest("Loading delegated class: " + name);
        return delegateLoadClass.apply(name, resolve);
    }

    private Class<?> identifyClassLoadingRequestSource() {
        // walk through stack and find first class that is not a class loader anymore
        var frames = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(Stream::toList);

        logger.finest("Walking stack");
        boolean walkingClassLoaderChain = false;
        for (StackWalker.StackFrame frame : frames) {
            var cl = frame.getDeclaringClass();
            logger.finest("Class: " + cl);
            boolean isClassLoader = ClassLoader.class.isAssignableFrom(cl);

            if (isClassLoader && !walkingClassLoaderChain) {
                walkingClassLoaderChain = true;
                logger.finest("starting walking through class loader chain");
            } else if (walkingClassLoaderChain && !isClassLoader) {
                // found class that is nod a class loader
                logger.finest("Class is NOT a class loader anymore");
                logger.finest("Source is " + cl + " loaded by " + cl.getClassLoader());
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
    public void hookFindClass(String name, Callback<Class<?>> cb) {
        logger.finest("Class finding request: " + name);
        if (check(cb.self(), name)) {
            var cl = delegationFindClass(name);
            if (cl != null) {
                cb.setReturnValue(cl);
            }
        }
    }

    @FluentHookParams
    public void hookFindClassAdditional(Object[] params, Callback<Class<?>> cb) {
        if (params.length == 0)
            return;

        if (!(params[0] instanceof String name)) {
            return;
        }

        hookFindClass(name, cb);
    }

    public void hookFindClass(String module, String name, Callback<Class<?>> cb) {
        logger.finest("Class finding request: " + name + " from module " + module);
        if (check(cb.self(), name)) {
            var cl = delegationFindClass(module, name);
            if (cl != null) {
                cb.setReturnValue(cl);
            }
        }
    }

    // hooks into the loadClass method of the target loader
    public void hookLoadClass(String name, boolean resolve, Callback<Class<?>> cb) {
        logger.finest("Class loading request: " + name);
        if (check(cb.self(), name)) {
            var cl = delegationLoadClassClass(name, resolve);
            if (cl != null) {
                cb.setReturnValue(cl);
            }
        }
    }
}
