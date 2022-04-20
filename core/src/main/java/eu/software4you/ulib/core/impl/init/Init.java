package eu.software4you.ulib.core.impl.init;

import eu.software4you.ulib.core.impl.Internal;
import lombok.Synchronized;

import java.lang.instrument.Instrumentation;

public class Init {
    @Synchronized
    public static void init(Object inst) {
        Internal.agentInit((Instrumentation) inst);
    }
}
