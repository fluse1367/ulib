package eu.software4you.ulib.core.impl;

import java.util.function.Supplier;

public final class UnsafeOperations {
    public static boolean allowed() {
        return Internal.isUnsafeOperations();
    }

    public static boolean comply(boolean bool, String module, String exception, String warning) {
        return comply(bool, module, () -> exception, () -> warning);
    }

    public static boolean comply(boolean bool, String module, Supplier<String> exception, Supplier<String> warning) {
        if (bool) {
            if (allowed())
                System.err.printf("(%s) %s (unsafe operations are allowed)%n", module, warning.get());
            else
                throw new UnsafeOperationException(String.format("(%s) Cannot comply: %s (allow unsafe operations to bypass this)", module, exception.get()));
        }
        return bool;
    }
}
