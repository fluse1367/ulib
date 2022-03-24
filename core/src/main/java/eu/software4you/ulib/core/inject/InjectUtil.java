package eu.software4you.ulib.core.inject;

import eu.software4you.ulib.core.impl.inject.DelegationInjector;
import eu.software4you.ulib.core.util.Expect;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

// TODO: javadocs
public class InjectUtil {

    public static Expect<Void, Exception> injectLoaderDelegation(ClassLoader source, ClassLoader target) {
        return injectLoaderDelegation(source, target, (clazz, name) -> true);
    }

    public static Expect<Void, Exception> injectLoaderDelegation(ClassLoader source, ClassLoader target,
                                                                 BiPredicate<Class<?>, String> filter) {
        return DelegationInjector.delegateTo(source, target, filter).inject();
    }

    /**
     * Injects a class loading call into the given target class loader.
     * <p>
     * Whenever (any instance of) the target class loader receives a request to load or find a certain class,
     * the request is delegated to the given source class loader instead if the request passes the filters.
     * If the source class loader returns {@code null}, the request will be handled by the original class loader as usual.
     * <p>
     * The loader filter checks if a certain instance of the target class loader should delegate the loading request.
     * <p>
     * The request filter checks if a certain request should be delegated:
     * It is called with the class which is requesting the load and the fully qualified name of the class that is to be loaded.
     *
     * @param source        the class loader that a request will be delegated to
     * @param target        the class loader class that will be hooked
     * @param filterLoader  the loader filter (see above)
     * @param filterRequest the request filter (see above)
     * @return the execution result
     */
    public static Expect<Void, Exception> injectLoaderDelegation(ClassLoader source, Class<? extends ClassLoader> target,
                                                                 Predicate<ClassLoader> filterLoader,
                                                                 BiPredicate<Class<?>, String> filterRequest) {
        return DelegationInjector.delegateTo(source, target, filterLoader, filterRequest).inject();
    }

}
