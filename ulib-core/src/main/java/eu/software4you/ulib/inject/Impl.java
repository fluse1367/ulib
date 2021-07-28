package eu.software4you.ulib.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Short for implementation. A new instance of the class will be injected into the target class.
 * Target class needs a static field that is annotated with {@link eu.software4you.ulib.Await}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Impl {
    Class<?>[] value();

    // The higher, so sooner the implementation will be loaded
    int priority() default 0;

    // maven dependency coordinates for maven central
    String[] dependencies() default {};
}
