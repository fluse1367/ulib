package eu.software4you.ulib.core.inject;

import eu.software4you.ulib.core.function.BiParamTask;
import eu.software4you.ulib.core.impl.inject.InjectionConfiguration;
import eu.software4you.ulib.core.impl.inject.InjectionManager;
import eu.software4you.ulib.core.reflect.ReflectUtil;
import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static eu.software4you.ulib.core.impl.inject.InjectionSupport.*;

/**
 * A hook injection builder.
 * <p>
 * Example:
 * <pre>{@code
 *  HookInjection injection = new HookInjection(targetClazz)
 *      .<ReturnType>addHook("someMethod", HookPoint.HEAD, (params, cb) -> {
 *          // someMethod(...) was called!
 *      })
 *      .addHook(hookClazz.getMethod("hook_someOtherMethod"), null);
 *  boolean success = !injection.inject().hasCaught();
 * }</pre>
 */
public class HookInjection {

    private final Class<?> target;
    private final Map<Class<?>, InjectionConfiguration> instructions = new HashMap<>();
    private boolean lock;

    /**
     * Constructs a new builder.
     *
     * @param target the global target
     */
    public HookInjection(@Nullable Class<?> target) {
        this.target = target;
    }

    /**
     * Constructs a new builder without a global target.
     */
    public HookInjection() {
        this(null);
    }

    /**
     * Adds the specified hook to the builder. The presence of a global target is assumed.
     *
     * @param methodDescriptor the target method JNI descriptor
     * @param at               where to apply the hook
     * @param call             the callable object
     * @param <R>              the return type of the target method
     * @return this
     * @throws NullPointerException if no global target is present
     * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html" target="_blank">JNI Types</a>
     */
    @NotNull
    @Contract("_, _, _ -> this")
    public <R> HookInjection addHook(String methodDescriptor, HookPoint at, BiParamTask<? super Object[], ? super Callback<R>, ?> call) {
        check();
        var cl = Objects.requireNonNull(target, "No global target declared");
        return addHook(cl, methodDescriptor, at, call);
    }

    /**
     * Adds the specified hook to the builder.
     *
     * @param target           the target class
     * @param methodDescriptor the target method JNI descriptor
     * @param at               where to apply the hook
     * @param call             the callable object
     * @param <R>              the return type of the target method
     * @return this
     * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html" target="_blank">JNI Types</a>
     */
    @NotNull
    @Contract("_, _, _, _ -> this")
    public <R> HookInjection addHook(@NotNull Class<?> target, @NotNull String methodDescriptor, @NotNull HookPoint at, @NotNull BiParamTask<? super Object[], ? super Callback<R>, ?> call) {
        check();
        if (!instructions.containsKey(target))
            instructions.put(target, new InjectionConfiguration(target));

        instructions.get(target).with(methodDescriptor, at, call);
        return this;
    }

    /**
     * Adds a hook method (as specified by {@link Hook}) to the builder.
     *
     * @param hook       the hook method
     * @param hookInvoke the method invoke instance ({@code null} for static methods)
     * @return this
     * @throws IllegalArgumentException if the hook method is not valid
     */
    @NotNull
    @Contract("_, _ -> this")
    public HookInjection addHook(@NotNull Method hook, @Nullable Object hookInvoke) {
        check();
        if (!hook.isAnnotationPresent(Hook.class))
            throw new IllegalArgumentException("Hook annotation not found on " + hook);
        checkInvoke(hook, hookInvoke);

        Hook anno = hook.getAnnotation(Hook.class);

        var target = findTargetClass(anno, hook.getDeclaringClass(), ReflectUtil.getCallerClass().getClassLoader());
        var descriptor = resolveDescriptor(anno, target);
        var call = buildCall(hook, hookInvoke);

        return addHook(target, descriptor, anno.at(), call);
    }


    /**
     * Adds a direct (manual) hook to the builder.
     *
     * @param hook       the hook method
     * @param hookInvoke the method invoke instance ({@code null} for static methods)
     * @param target     the target method
     * @param at         the hook point
     * @return this
     */
    @NotNull
    @Contract("_, _, _, _ -> this")
    public HookInjection addHook(@NotNull Method hook, @Nullable Object hookInvoke, @NotNull Method target, @NotNull HookPoint at) {
        check();
        checkInvoke(hook, hookInvoke);

        var targetClazz = target.getDeclaringClass();
        var descriptor = getDescriptor(target);
        var call = buildCall(hook, hookInvoke);

        return addHook(targetClazz, target.getName() + descriptor, at, call);
    }

    /**
     * Searches the given class for hookable methods and adds the hooks to the builder.
     *
     * @param hook   the class to search for hooks in
     * @param invoke the invoking instance
     * @return this
     */
    @NotNull
    @Contract("_, _ -> this")
    public HookInjection addHook(Class<?> hook, Object invoke) {
        Arrays.stream(hook.getMethods())
                .filter(method -> method.isAnnotationPresent(Hook.class))
                .filter(method -> !Modifier.isStatic(method.getModifiers()))
                .forEach(method -> addHook(method, invoke));

        return this;
    }

    /**
     * Performs the final code injection. May only be called once.
     *
     * @return an expect object wrapping the execution result
     */
    @NotNull
    public Expect<Void, Exception> inject() {
        check();
        lock = true;
        return Expect.compute(() -> InjectionManager.getInstance().injectionsJoin(instructions));
    }

    private void check() {
        if (lock)
            throw new IllegalStateException("Object is locked");
    }

}
