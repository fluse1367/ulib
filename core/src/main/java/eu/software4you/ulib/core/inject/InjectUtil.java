package eu.software4you.ulib.core.inject;

import eu.software4you.ulib.core.impl.inject.DelegationInjector;
import eu.software4you.ulib.core.util.Expect;

import java.util.function.BiPredicate;

// TODO: javadocs
public class InjectUtil {

    public static Expect<Void, Exception> injectLoaderDelegation(ClassLoader source, ClassLoader target) {
        return injectLoaderDelegation(source, target, (clazz, name) -> true);
    }

    public static Expect<Void, Exception> injectLoaderDelegation(ClassLoader source, ClassLoader target,
                                                                 BiPredicate<Class<?>, String> filter) {
        return DelegationInjector.delegateTo(source, target, filter).inject();
    }

}
