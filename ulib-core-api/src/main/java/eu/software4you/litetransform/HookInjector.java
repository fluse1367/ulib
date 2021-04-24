package eu.software4you.litetransform;

import eu.software4you.reflect.ReflectUtil;
import eu.software4you.ulib.Await;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * Injects methods marked with {@link Hook}.
 */
public abstract class HookInjector {
    @Await
    private static HookInjector impl;

    /**
     * Searches a class for any static hooks and tries to inject them.
     */
    public static void hookStatic(@NotNull Class<?> clazz) {
        impl.hookStatic0(clazz);
    }

    /**
     * Searches the calling class for any static hooks and tries to inject them.
     */
    public static void hookStatic() {
        hookStatic(ReflectUtil.getCallerClass());
    }

    /**
     * Searches an object for any hooks and tries to inject them.
     *
     * @param inst       the object (class) to search in
     * @param hookStatic if static hooks should also be injected
     */
    public static void hook(@NotNull Object inst, boolean hookStatic) {
        impl.hook0(inst, hookStatic);
    }

    /**
     * Searches an object for any non-static hooks and tries to inject them.
     * Equivalent to {@code hook(inst, false)}.
     *
     * @param inst the object (class) to search in
     */
    public static void hook(Object inst) {
        hook(inst, false);
    }

    /**
     * Hooks a method directly into a specific class. Useful for dynamic injection.
     *
     * @param source           the method to hook
     * @param obj              the instance object, ignored on static methods
     * @param methodName       the name of the target method
     * @param methodDescriptor the signature of the target method
     * @param className        the fully qualified class name of the declaring class
     * @param at               the hook point
     */
    public static void directHook(@NotNull Method source, @Nullable Object obj, @NotNull String methodName, @NotNull String methodDescriptor, @NotNull String className, @NotNull HookPoint at) {
        impl.directHook0(source, obj, methodName, methodDescriptor, className, at);
    }

    /**
     * Hooks a source method into another. Useful for dynamic injection.
     *
     * @param source the method to hook
     * @param obj    the instance object, ignored on static methods
     * @param into   the method to be injected in
     * @param at     the point where to inject
     */
    public static void directHook(@NotNull Method source, @Nullable Object obj, @NotNull Method into, @NotNull HookPoint at) {
        impl.directHook0(source, obj, into, at);
    }

    protected abstract void hookStatic0(Class<?> clazz);

    protected abstract void hook0(Object inst, boolean hookStatic);

    protected abstract void directHook0(Method source, Object obj, String methodName, String methodDescriptor, String className, HookPoint at);

    protected abstract void directHook0(Method source, Object obj, Method into, HookPoint at);
}
