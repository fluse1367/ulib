package eu.software4you.ulib.supermodule.hooking;

import java.util.function.Function;
import java.util.function.Supplier;

public class CallbackReference<T> {
    private static Function<Object[], ?> runner;
    private static Function<Object, Boolean> funcIsReturning;
    private static Function<Object, ?> funcGetReturnValue;
    private static Supplier<Class<?>> funcDetermineCaller;

    private final Object callback;

    public CallbackReference(Class<T> returnType, T returnValue, boolean hasReturnValue, Object self, String hookId, int at, Object[] params) {
        Class<?> caller = funcDetermineCaller.get();
        this.callback = runner.apply(new Object[]{returnType, returnValue, hasReturnValue, self, caller, hookId, at, params});
    }

    @SuppressWarnings("unchecked")
    public static void put(Function<Object[], ?> runner,
                           Function<?, Boolean> isReturning,
                           Function<?, ?> getReturnValue,
                           Supplier<Class<?>> determineCaller) {
        if (CallbackReference.runner != null || CallbackReference.funcIsReturning != null || CallbackReference.funcGetReturnValue != null)
            throw new IllegalStateException();
        CallbackReference.runner = runner;
        CallbackReference.funcIsReturning = (Function<Object, Boolean>) isReturning;
        CallbackReference.funcGetReturnValue = (Function<Object, ?>) getReturnValue;
        CallbackReference.funcDetermineCaller = determineCaller;
    }

    public boolean isReturning() {
        return funcIsReturning.apply(callback);
    }

    @SuppressWarnings("unchecked")
    public T getReturnValue() {
        return (T) funcGetReturnValue.apply(callback);
    }
}
