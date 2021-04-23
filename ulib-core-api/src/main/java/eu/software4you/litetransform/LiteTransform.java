package eu.software4you.litetransform;

import eu.software4you.litetransform.injection.Injector;
import eu.software4you.ulib.Await;
import lombok.Getter;

/**
 * Access point for injection.
 */
public class LiteTransform {
    @Getter
    @Await
    private static Injector injector;
}
