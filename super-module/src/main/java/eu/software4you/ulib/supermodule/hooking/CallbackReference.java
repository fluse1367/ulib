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
        if (runner == null || funcIsReturning == null || funcGetReturnValue == null || funcDetermineCaller == null)
            putSelf();
        Class<?> caller = funcDetermineCaller.get();
        this.callback = runner.apply(new Object[]{returnType, returnValue, hasReturnValue, self, caller, hookId, at, params});
    }

    @SuppressWarnings("unchecked")
    private static void putSelf() {
        Object[] arr = (Object[]) System.getProperties().remove("ulib.hooking");
        runner = (Function<Object[], ?>) arr[0];
        funcIsReturning = (Function<Object, Boolean>) arr[1];
        funcGetReturnValue = (Function<Object, ?>) arr[2];
        funcDetermineCaller = (Supplier<Class<?>>) arr[3];
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
