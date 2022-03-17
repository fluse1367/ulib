package eu.software4you.ulib.core.inject;

import java.lang.annotation.*;

/**
 * Indicates that a hook method accepts multiple parameter specs in form of an object array.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FluentHookParams {
}
