package eu.software4you.ulib.core.inject;

import eu.software4you.ulib.core.impl.inject.DelegationHook;
import eu.software4you.ulib.core.impl.inject.DelegationInjector;
import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Hook injection utility.
 *
 * @see HookInjection
 */
public class InjectUtil {

    /**
     * @see #injectLoaderDelegation(ClassLoaderDelegation, Predicate, BiPredicate, Class)
     */
    public static Expect<Void, Exception> injectLoaderDelegation(ClassLoaderDelegation delegation, ClassLoader target) {
        return injectLoaderDelegation(delegation, (requester, request) -> true, target);
    }

    /**
     * @see #injectLoaderDelegation(ClassLoaderDelegation, Predicate, BiPredicate, Class)
     */
    public static Expect<Void, Exception> injectLoaderDelegation(ClassLoaderDelegation delegation,
                                                                 BiPredicate<Class<?>, String> filter,
                                                                 ClassLoader target) {
        return injectLoaderDelegation(delegation, cl -> cl == target, filter, target.getClass());
    }

    /**
     * Injects a hook into the given target class loader that will be called on any class loading request.
     * <p>
     * Whenever (any instance of) the target class loader receives a request to load or find a certain class,
     * the request is forwarded to the given delegation container instead, provided the request passes the filters.
     * If the delegation returns a {@code null} value, the request will be handled by the original class loader as usual.
     * If the delegation returns a {@code non-null} value, the usual computation of the class loader will be aborted
     * and the hook-computed value will be returned instead.
     *
     * @param delegation    the delegation that requests will be forwarded to
     * @param filterLoader  the predicate that determines if a particular class loader instance should forward a request
     * @param filterRequest the predicate that determines if a particular class loading request should be forwarded;
     *                      the first argument is the class that is initially requesting the class that is to be loaded,
     *                      the second argument is the fully qualified name of the class that is to be loaded
     * @param target        the target class that will be injected
     * @return the execution result
     * @see ClassLoaderDelegation#delegateToClassLoader(ClassLoader)
     */
    @NotNull
    public static Expect<Void, Exception> injectLoaderDelegation(ClassLoaderDelegation delegation,
                                                                 Predicate<ClassLoader> filterLoader,
                                                                 BiPredicate<Class<?>, String> filterRequest,
                                                                 Class<? extends ClassLoader> target) {
        return new DelegationInjector(target,
                new DelegationHook(delegation, filterLoader, filterRequest),
                Collections.emptyMap()
        ).inject();
    }

}
