package eu.software4you.ulib.core.impl.dependencies;

import eu.software4you.ulib.core.ULib;
import eu.software4you.ulib.core.api.transform.Callback;
import eu.software4you.ulib.core.api.utils.ClassUtils;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class DelegationHook {

    private final Logger logger;
    private final ClassLoader loaderDelegate;
    private final Method methodFindClass, methodLoadClass;
    private final BiPredicate<ClassLoader, String> filter;

    @SneakyThrows
    public DelegationHook(ClassLoader delegate, BiPredicate<ClassLoader, String> filter) {
        (this.logger = ULib.logger()).finest("Delegation hook init with delegate: " + delegate);

        this.loaderDelegate = delegate;
        this.filter = filter;

        var cl = delegate.getClass();
        this.methodFindClass = ClassUtils.findUnderlyingDeclaredMethod(cl, "findClass", String.class);
        this.methodLoadClass = ClassUtils.findUnderlyingDeclaredMethod(cl, "loadClass", String.class, boolean.class);

        if (!Objects.requireNonNull(methodFindClass, "findClass(String) not found on " + cl)
                .trySetAccessible())
            throw new IllegalArgumentException("No access to " + methodFindClass);

        if (!Objects.requireNonNull(methodLoadClass, "loadClass(String, boolean) not found on " + cl)
                .trySetAccessible())
            throw new IllegalArgumentException("No access to " + methodLoadClass);
    }

    private boolean check(Object source, String name) {
        return source != loaderDelegate
               && source instanceof ClassLoader cl
               && this.filter.test(cl, name);
    }

    @SneakyThrows
    private Class<?> delegationFindClass(String name) {
        logger.finest("Finding delegated class: " + name);
        return (Class<?>) methodFindClass.invoke(loaderDelegate, name);
    }

    @SneakyThrows
    private Class<?> delegationLoadClassClass(String name, boolean resolve) {
        logger.finest("Loading delegated class: " + name);
        return (Class<?>) methodLoadClass.invoke(loaderDelegate, name, resolve);
    }

    private Class<?> identifyClassLoadingRequestSource() {
        // walk through stack and find first class that is not a class loader anymore
        var frames = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(Stream::toList);

        //if (true) throw new UnsupportedOperationException("TBD: " + Arrays.toString(frames.toArray()));

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
                return cl;
            }
        }
        throw new IllegalStateException(); // a class loader cannot be the source of the class loading request
    }

    private boolean verifyLoadingRequest() {
        var source = identifyClassLoadingRequestSource();
        var loader = source.getClassLoader();
        logger.finest("Source is " + source + " loaded by " + loader);
        return loader != loaderDelegate;
    }

    /* actual hooks */

    // hooks into the findClass method of the target loader
    public void findClass(String name, Callback<Class<?>> cb) throws ClassNotFoundException {
        logger.finest("Class finding request: " + name);
        if (verifyLoadingRequest() && check(cb.self(), name))
            cb.setReturnValue(delegationFindClass(name));
    }

    // hooks into the loadClass method of the target loader
    public void loadClass(String name, boolean resolve, Callback<Class<?>> cb) throws ClassNotFoundException {
        logger.finest("Class loading request: " + name);
        if (verifyLoadingRequest() && check(cb.self(), name))
            cb.setReturnValue(delegationLoadClassClass(name, resolve));
    }
}
