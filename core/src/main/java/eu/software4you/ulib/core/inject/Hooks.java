package eu.software4you.ulib.core.inject;

import java.lang.annotation.*;

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
