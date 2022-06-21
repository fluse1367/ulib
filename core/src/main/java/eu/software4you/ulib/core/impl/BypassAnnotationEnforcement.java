package eu.software4you.ulib.core.impl;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface BypassAnnotationEnforcement {
}
