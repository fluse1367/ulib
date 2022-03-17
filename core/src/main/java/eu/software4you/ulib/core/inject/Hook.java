package eu.software4you.ulib.core.inject;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
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
     * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html" target="_blank">https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html</a>
     */
    @NotNull
    String value();

    /**
     * The fully qualified class name of the class that is to be hooked. Optional if {@link Hooks} annotation is present.
     */
    @NotNull
    String clazz() default "";

    /**
     * Specifies where in the method to hook.
     */
    @NotNull
    HookPoint at() default HookPoint.HEAD;
}
