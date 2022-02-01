package eu.software4you.ulib.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Short for implementation constructor.
 * Will be injected as {@link eu.software4you.function.ConstructingFunction} into the target.
 * Target class needs a static {@link eu.software4you.function.ConstructingFunction} field
 * that is annotated with {@link eu.software4you.ulib.Await}.
 */
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImplConst {
}
