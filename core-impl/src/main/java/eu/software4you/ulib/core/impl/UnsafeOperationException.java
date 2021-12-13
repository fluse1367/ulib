package eu.software4you.ulib.core.impl;

public class UnsafeOperationException extends IllegalStateException {
    public UnsafeOperationException() {
    }

    public UnsafeOperationException(String s) {
        super(s);
    }

    public UnsafeOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsafeOperationException(Throwable cause) {
        super(cause);
    }
}
