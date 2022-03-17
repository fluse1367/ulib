package eu.software4you.ulib.core.impl.inject;

import eu.software4you.ulib.core.inject.Callback;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class CallbackImpl<T> implements Callback<T> {

    private final Class<T> returnType;
    private final Object self;
    private final Class<?> callerClass;
    private T returnValue;
    private boolean hasReturnValue;
    @Getter
    private boolean canceled;

    public CallbackImpl(Class<T> returnType, T returnValue, boolean hasReturnValue, Object self, Class<?> callerClass) {
        this.returnType = returnType;
        this.returnValue = returnValue;
        this.hasReturnValue = hasReturnValue;
        this.self = self;
        this.callerClass = callerClass;
    }

    @Override
    public Object self() {
        return self;
    }

    @Override
    public @NotNull Class<?> callerClass() {
        return this.callerClass;
    }

    @Override
    public boolean hasReturnValue() {
        return this.hasReturnValue;
    }

    @Override
    public @Nullable T getReturnValue() {
        if (!hasReturnValue)
            throw new IllegalStateException("No return value provided");
        return this.returnValue;
    }

    @Override
    public T setReturnValue(@Nullable T value) {
        if (returnType == void.class)
            throw new IllegalArgumentException("Return type void cannot have a return value");
        hasReturnValue = true;
        return this.returnValue = value;
    }

    @Override
    public void clearReturnValue() {
        hasReturnValue = false;
        this.returnValue = null;
    }

    @Override
    public void cancel() {
        if (!hasReturnValue && returnType != void.class)
            throw new IllegalStateException("Cannot cancel with no return value");
        canceled = true;
    }

    @Override
    public void throwNow(Throwable t) {
        throw new HookException(t);
    }

    boolean isReturning() {
        return canceled || hasReturnValue;
    }
}
