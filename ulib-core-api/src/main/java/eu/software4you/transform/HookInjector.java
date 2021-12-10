package eu.software4you.ulib.core.api.transform;

import eu.software4you.ulib.core.api.reflect.ReflectUtil;
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
     * Searches a class for any active static hooks and removes them.
     */
    public static void unhookStatic(@NotNull Class<?> clazz) {
        impl.unhookStatic0(clazz);
    }

    /**
     * Searches the calling class for any active static hooks and removes them.
     */
    public static void unhookStatic() {
        unhookStatic(ReflectUtil.getCallerClass());
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
    public static void hook(@NotNull Object inst) {
        hook(inst, false);
    }

    /**
     * Searches an object for any hooks and removes them.
     *
     * @param inst         the object (class) to search in
     * @param unhookStatic if static hooks should also be removed
     */
    public static void unhook(@NotNull Object inst, boolean unhookStatic) {
        impl.unhook0(inst, unhookStatic);
    }

    /**
     * Searches an object for any non-static hooks and removes them.
     * Equivalent to {@code hook(inst, false)}.
     *
     * @param inst the object (class) to search in
     */
    public static void unhook(@NotNull Object inst) {
        unhook(inst, false);
    }

    /**
     * Hooks a method directly into another method. Useful for dynamic injection.
     *
     * @param source     the hook method
     * @param sourceInst the instance object, ignored on static methods
     * @param into       the method to hook
     * @param at         the point where to hook
     */
    public static void directHook(@NotNull Method source, @Nullable Object sourceInst, @NotNull Method into, @NotNull HookPoint at) {
        impl.directHook0(source, sourceInst, into, at);
    }

    /**
     * Hooks a method directly into another method. Useful for dynamic injection.
     *
     * @param source           the hook method
     * @param sourceInst       the instance object, ignored on static methods
     * @param className        the fully qualified class name of the class to hook
     * @param methodName       the name of the method to hook
     * @param methodDescriptor the JNI method descriptor of the method to hook
     * @param at               the point where to hook
     */
    public static void directHook(@NotNull Method source, @Nullable Object sourceInst, @NotNull String className, @NotNull String methodName, @NotNull String methodDescriptor, @NotNull HookPoint at) {
        impl.directHook0(source, sourceInst, className, methodName, methodDescriptor, at, ReflectUtil.getCallerClass().getClassLoader());
    }

    /**
     * Removes a hook.
     *
     * @param source     the hook method
     * @param sourceInst the instance object, ignored on static methods
     * @param into       the hooked method
     * @param at         the point from where to remove the hook
     */
    public static void directUnHook(@NotNull Method source, @Nullable Object sourceInst, @NotNull Method into, @NotNull HookPoint at) {
        impl.directUnhook0(source, sourceInst, into, at);
    }

    /**
     * Removes a hook.
     *
     * @param source           the hook method
     * @param sourceInst       the instance object, ignored on static methods
     * @param className        the fully qualified class name of the hooked class
     * @param methodName       the name of the hooked method
     * @param methodDescriptor the JNI method descriptor of the hooked method
     * @param at               the point from where to remove the hook
     */
    public static void directUnhook(@NotNull Method source, @Nullable Object sourceInst, @NotNull String className, @NotNull String methodName, @NotNull String methodDescriptor, @NotNull HookPoint at) {
        impl.directUnhook0(source, sourceInst, className, methodName, methodDescriptor, at, ReflectUtil.getCallerClass().getClassLoader());
    }

    protected abstract void hookStatic0(Class<?> clazz);

    protected abstract void unhookStatic0(Class<?> clazz);

    protected abstract void hook0(Object inst, boolean hookStatic);

    protected abstract void unhook0(Object inst, boolean unhookStatic);

    protected abstract void directHook0(Method source, Object sourceInst, Method into, HookPoint at);

    protected abstract void directHook0(Method source, Object sourceInst, String className, String methodName, String methodDescriptor, HookPoint at, ClassLoader cl);

    protected abstract void directUnhook0(Method source, Object sourceInst, Method into, HookPoint at);

    protected abstract void directUnhook0(Method source, Object sourceInst, String className, String methodName, String methodDescriptor, HookPoint at, ClassLoader cl);
}
