package eu.software4you.ulib.core.inject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.lang.annotation.*;

/**
 * Specification where and what to hook in a method.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Spec {

    /**
     * What to hook.
     */
    @NotNull
    HookPoint point() default HookPoint.HEAD;

    /**
     * Which occurrences to hook. A {@code 0} is interpreted as every occurrence.
     * <p>
     * Not applicable for {@link HookPoint#HEAD HEAD} and {@link HookPoint#RETURN RETURN} points.
     */
    @Range(from = 0, to = Integer.MAX_VALUE)
    int[] n() default 0;

    /**
     * Full JNI signature of the target. The full signature consists out of the class signature and the method/field signature.
     * <br>
     * For example, the full signature of <pre>{@code
     *  String.valueOf(int)
     * }</pre> would be <pre>{@code
     * Ljava/lang/String;valueOf(I)L/java/lang/String;
     * }</pre>
     * <p>
     * Not applicable for {@link HookPoint#HEAD HEAD} and {@link HookPoint#RETURN RETURN} points.
     *
     * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html" target="_blank">JNI Types</a>
     * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html#type_signatures" target="_blank">JNI Type Signatures</a>
     */
    @NotNull
    String target() default "";
}
