package eu.software4you.litetransform.injection;

import eu.software4you.litetransform.Callback;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method to be injected into another method.<br>
 * The method must have the same parameters as the target method, plus a {@link Callback} object at the end.<br>
 * Note: the {@link Callback} object must be of the same type as the return type of the target method.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
    /**
     * The method to be injected to.
     */
    String method();

    /**
     * The method signature.
     */
    String signature();

    /**
     * The class to be injected to.
     */
    String clazz();

    /**
     * Specifies where in the method to inject.
     */
    InjectionPoint at() default InjectionPoint.RETURN;

    /**
     * When multiple injection points are acceptable, the ordinal is used to specify at which point to inject.<br>
     * {@code -1} means injection to all acceptable points.<br>
     * Any other number means to inject at the nth object (where n begins with {@code 0}).
     */
    int ordinal() default -1;
}
