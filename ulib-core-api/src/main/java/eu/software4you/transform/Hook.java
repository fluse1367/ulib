package eu.software4you.transform;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method to be hooked into another method.<br>
 * The method must have the same parameters as the target method, plus a {@link Callback} object at the end.<br>
 * Note: the {@link Callback} object must be of the same type as the return type of the target method.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Hook {
    /**
     * The method to be hooked into. A descriptor (as specified in the JNI documentation) can be specified.
     *
     * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html">https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html</a>
     */
    @NotNull
    String method();

    /**
     * The class to be injected to.
     */
    @NotNull
    String clazz();

    /**
     * Specifies where in the method to inject.
     */
    @NotNull
    HookPoint at() default HookPoint.HEAD;
}
