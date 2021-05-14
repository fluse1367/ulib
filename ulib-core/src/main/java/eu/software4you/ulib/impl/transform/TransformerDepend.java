package eu.software4you.ulib.impl.transform;

import eu.software4you.aether.Dependencies;

final class TransformerDepend {
    static {
        Dependencies.depend("{{maven.javassist}}", "javassist.CtMethod");
    }

    static void $() {
        // only used for easy class loading
    }
}
