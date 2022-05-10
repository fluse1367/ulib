package eu.software4you.ulib.core.inject;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * There are two fundamental types of hooks: intercepting and proxying.
 * A hook can alter the control flow of a method using a {@link Callback} object.
 * <p>
 *
 * <h2>Intercepting Hooks</h2>
 * An intercepting hook directly intercepts the control flow of a method (hence the name).
 * It has the ability to {@link Callback#cancel() immediately abort the control flow} (returning out of the method).
 * If the hooked method returns an object, the control flow may only be aborted if {@link Callback#setReturnValue(Object) a return value is provided}.
 * An intercepting hook also receives the method parameters.
 * <p>
 *
 * <h2>Proxying Hooks</h2>
 * A proxying hook redirects either a field access or a method call.
 * <p>
 *
 * <h3>Method Call Proxy</h3>
 * To abort the usual control flow you have to provide an alternative method result with {@link Callback#setReturnValue(Object)}.
 * If you use {@link Callback#cancel()}, {@code null} will be returned. In either case, the original method will <b>not</b> get called.
 *
 * <p>
 *
 * <h3>Reading Field Access Proxy</h3>
 * To abort the usual control flow you have to provide an alternative value with {@link Callback#setReturnValue(Object)}.
 * <p>
 *
 * <h3>Writing Field Access Proxy</h3>
 * The Callback object will hold the value that is attempted to be written, you can receive it with {@link Callback#getReturnValue()}.
 * To abort the usual control flow you can just use {@link Callback#cancel()}.
 * <p>
 *
 * <hr>
 * Marks a method to be hooked into another method.<br>
 * The method must have the same parameters as the target method, plus an additional {@link Callback} object at the end.<br>
 * Note: the {@link Callback} object must be of the same type as the return type of the target method.
 * <p>
 * Example:
 * <pre>{@code
 *      // the target method
 *      private boolean someMethod(String param1, int param2) {
 *          //...
 *      }
 *
 *      // the hook
 *      @Hook("someMethod")
 *      public void hook_someMethod(String param1, int param2, Callback<Boolean> callback) {
 *          // ...
 *      }
 * }</pre>
 * <p>
 * Another example using fluent parameters:
 * <pre>{@code
 *      // the target method
 *      private int someOtherMethod(Object param1, [possible more parameters]) {
 *          //...
 *      }
 *
 *      // the hook
 *      @Hook("someOtherMethod")
 *      @FluentHookParams
 *      public void hook_someOtherMethod(Object[] params, Callback<Integer> callback) {
 *          // ...
 *      }
 *
 * }</pre>
 *
 * @see Hooks
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Hook {
    /**
     * The method to be hooked into. A descriptor (as specified in the JNI documentation) can be specified.
     *
     * @see Spec#target()
     */
    @NotNull
    String value();

    /**
     * The fully qualified class name of the class that is to be hooked. Optional if {@link Hooks} annotation is present.
     */
    @NotNull
    String clazz() default "";

    /**
     * Specifies where and what in the method to hook.
     */
    @NotNull
    Spec spec() default @Spec();
}
