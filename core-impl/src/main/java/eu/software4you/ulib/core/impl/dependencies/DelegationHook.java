package eu.software4you.ulib.core.impl.dependencies;

import eu.software4you.ulib.core.api.transform.Callback;
import eu.software4you.ulib.core.api.utils.ClassUtils;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.BiPredicate;

public class DelegationHook {

    private final ClassLoader loaderDelegate;
    private final Method methodFindClass, methodLoadClass;
    private final BiPredicate<ClassLoader, String> filter;

    @SneakyThrows
    public DelegationHook(ClassLoader delegate, BiPredicate<ClassLoader, String> filter) {
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
        return (Class<?>) methodFindClass.invoke(loaderDelegate, name);
    }

    @SneakyThrows
    private Class<?> delegationLoadClassClass(String name, boolean resolve) {
        return (Class<?>) methodLoadClass.invoke(loaderDelegate, name, resolve);
    }

    /* actual hooks */

    // hooks into the findClass method of the target loader
    public void findClass(String name, Callback<Class<?>> cb) throws ClassNotFoundException {
        if (check(cb.self(), name))
            cb.setReturnValue(delegationFindClass(name));
    }

    // hooks into the loadClass method of the target loader
    public void loadClass(String name, boolean resolve, Callback<Class<?>> cb) throws ClassNotFoundException {
        if (check(cb.self(), name))
            cb.setReturnValue(delegationLoadClassClass(name, resolve));
    }
}
