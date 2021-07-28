package eu.software4you.ulib.inject;

import eu.software4you.ulib.ImplFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated constructor will get injected as {@link ImplFactory} into the target.
 * Target class needs a static {@link ImplFactory} field
 * that is annotated with {@link eu.software4you.ulib.Await}.
 */
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
public @interface Factory {
}
