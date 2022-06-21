package eu.software4you.ulib.core.inject;

import eu.software4you.ulib.core.impl.inject.ClassLoaderDelegationHook;
import eu.software4you.ulib.core.reflect.ReflectUtil;
import eu.software4you.ulib.core.util.Expect;
import eu.software4you.ulib.core.util.LazyValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Hook injection utility.
 *
 * @see HookInjection
 */
public class InjectUtil {
    private static final LazyValue<Spec> DEFAULT_SPEC = LazyValue.immutable(() -> createHookingSpec(null, null, (Integer[]) null));

    @NotNull
    public static Spec defaultHookingSpec() {
        return DEFAULT_SPEC.getIfAvailable().orElseThrow();
    }

    @NotNull
    public static Spec createHookingSpec(@Nullable HookPoint point, @Nullable String target, @Nullable Integer... n) {
        Map<String, Object> vals = new HashMap<>(3, 1f);

        if (point != null)
            vals.put("point", point);

        if (target != null)
            vals.put("target", target);

        if (n != null && n.length > 0) {
            // convert into primitive array
            int[] array = new int[n.length];
            for (int i = 0; i < n.length; i++) {
                array[i] = n[i];
            }
            vals.put("n", array);
        }

        return ReflectUtil.instantiateAnnotation(Spec.class, vals);
    }

    @NotNull
    public static Spec createHookingSpec(@Nullable HookPoint point) {
        return createHookingSpec(point, null, (Integer[]) null);
    }

    /**
     * @see #injectLoaderDelegation(ClassLoaderDelegation, Predicate, BiPredicate, Class)
     */
    @NotNull
    public static Expect<Void, Exception> injectLoaderDelegation(@NotNull ClassLoaderDelegation delegation, @NotNull ClassLoader target) {
        return injectLoaderDelegation(delegation, (requester, request) -> true, target);
    }

    /**
     * @see #injectLoaderDelegation(ClassLoaderDelegation, Predicate, BiPredicate, Class)
     */
    @NotNull
    public static Expect<Void, Exception> injectLoaderDelegation(@NotNull ClassLoaderDelegation delegation,
                                                                 @NotNull BiPredicate<Class<?>, String> filter,
                                                                 @NotNull ClassLoader target) {
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
     * <p>
     * The underlying implementation has built-in protection against recursion. Without recursion detection & protection,
     * an infinite recursion (would eventually throw a {@link StackOverflowError}) would occur if the injection target
     * is one of the delegation's parents: A regular class loader will always first delegate a request to its parent.
     * If the parent happens to be the injection target, it will then (because of the injected hook) delegate the request
     * to the class loader delegation which will, again, first delegate the request to its parent. É voilà, recursion.
     *
     * @param delegation    the delegation that requests will be forwarded to
     * @param filterLoader  the predicate that determines if a particular class loader instance should forward a request
     * @param filterRequest the predicate that determines if a particular class loading request should be forwarded;
     *                      the first argument is the class that is initially requesting the class that is to be loaded,
     *                      the second argument is the fully qualified name of the class that is to be loaded
     * @param target        the target class that will be injected
     * @return the execution result
     * @see ClassLoaderDelegation#ClassLoaderDelegation(ClassLoader)
     */
    @NotNull
    public static Expect<Void, Exception> injectLoaderDelegation(@NotNull ClassLoaderDelegation delegation,
                                                                 @NotNull Predicate<ClassLoader> filterLoader,
                                                                 @NotNull BiPredicate<Class<?>, String> filterRequest,
                                                                 @NotNull Class<? extends ClassLoader> target) {
        return new ClassLoaderDelegationHook(target, delegation.additional.getAndSet(null),
                delegation, filterLoader, filterRequest).inject();
    }

}
