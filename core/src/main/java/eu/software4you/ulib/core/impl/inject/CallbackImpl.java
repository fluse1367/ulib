package eu.software4you.ulib.core.impl.inject;

import eu.software4you.ulib.core.inject.Callback;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

final class CallbackImpl<T> implements Callback<T> {

    private final boolean isProxy;
    private final AtomicReference<T> initialReturnValue;

    private final Class<T> returnType;
    private final Object self, proxyInst;
    private final Class<?> callerClass;
    private AtomicReference<T> returnValue;
    @Getter
    private boolean canceled;

    // for hooks
    public CallbackImpl(Class<T> returnType, T returnValue, boolean hasReturnValue, Object self, Class<?> callerClass) {
        this(false, returnType, returnValue, hasReturnValue, self, null, callerClass);
    }

    // for proxies
    public CallbackImpl(Class<T> returnType, T initialValue, boolean hasInitialValue, Object self, Object proxyInst, Class<?> callerClass) {
        this(true, returnType, initialValue, hasInitialValue, self, proxyInst, callerClass);
    }

    private CallbackImpl(boolean isProxy, Class<T> returnType, T returnValue, boolean hasReturnValue, Object self, Object proxyInst, Class<?> callerClass) {
        this.isProxy = isProxy;

        this.returnType = returnType;
        this.returnValue = hasReturnValue ? new AtomicReference<>(returnValue) : null;
        this.initialReturnValue = this.returnValue;

        this.self = self;
        this.proxyInst = proxyInst;
        this.callerClass = callerClass;
    }

    @Override
    public @NotNull Optional<Object> proxyInst() {
        return Optional.ofNullable(proxyInst);
    }

    @Override
    @NotNull
    public Optional<Object> self() {
        return Optional.ofNullable(self);
    }

    @Override
    public @NotNull Class<?> callerClass() {
        return this.callerClass;
    }

    @Override
    public boolean hasReturnValue() {
        return returnValue != null;
    }

    @Override
    public @Nullable T getReturnValue() {
        if (returnValue == null)
            throw new IllegalStateException("No return value present");
        return this.returnValue.get();
    }

    @Override
    public T setReturnValue(final @Nullable T value) {
        if (returnType == void.class)
            throw new IllegalArgumentException("Return type void cannot have a return value");

        if (this.returnValue == null) {
            this.returnValue = new AtomicReference<>(value);
        } else {
            this.returnValue.set(value);
        }

        return value;
    }

    @Override
    public void clearReturnValue() {
        this.returnValue = null;
    }

    @Override
    public void cancel() {
        if (!this.isProxy
            && this.returnType != void.class
            && this.returnValue == null)
            throw new IllegalStateException("Cannot cancel with no return value");
        this.canceled = true;
    }

    @Override
    public void throwNow(Throwable t) {
        throw new HookException(t);
    }

    boolean isReturning() {
        if (canceled)
            return true;

        if (isProxy && returnValue == initialReturnValue) {
            // do not cancel control flow if initial return value is unmodified
            return false;
        }

        return hasReturnValue();
    }
}
