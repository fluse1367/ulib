package eu.software4you.ulib.core.api.transform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a collection of hooks for a specific other class.
 *
 * @see Hook
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Hooks {
    /**
     * The fully qualified class name of the class that is to be hooked.
     */
    String value();
}
