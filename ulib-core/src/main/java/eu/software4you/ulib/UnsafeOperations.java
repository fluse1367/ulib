package eu.software4you.ulib;

import java.util.function.Supplier;

public final class UnsafeOperations {
    public static boolean allowed() {
        return Properties.getInstance().UNSAFE_OPERATIONS;
    }

    public static boolean comply(boolean bool, String module, String exception, String warning) {
        return comply(bool, module, () -> exception, () -> warning);
    }

    public static boolean comply(boolean bool, String module, Supplier<String> exception, Supplier<String> warning) {
        if (bool) {
            if (allowed())
                ULib.logger().warning(() -> String.format("(%s) %s (unsafe operations are allowed)", module, warning.get()));
            else
                throw new UnsafeOperationException(String.format("(%s) Cannot comply: %s (allow unsafe operations to bypass this)", module, exception.get()));
        }
        return bool;
    }
}
