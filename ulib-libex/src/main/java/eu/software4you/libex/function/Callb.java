package eu.software4you.libex.function;

import java.util.function.Supplier;

/**
 * Callback container
 */
@SuppressWarnings("unchecked")
public class Callb<T> {
    private static FuncArr8<?, Class<?>, Object, Boolean, Object, Class<?>, String, Integer, Object[]> runner;
    private static BoolFunc<Object> isReturning;
    private static Func<Object, ?> getReturnValue;
    private static Supplier<Class<?>> determineCaller;

    private final Object callback;

    public Callb(Class<T> returnType, T returnValue, boolean hasReturnValue, Object self, String hookId, int at, Object[] params) {
        Class<?> caller = determineCaller.get();
        this.callback = runner.run(returnType, returnValue, hasReturnValue, self, caller, hookId, at, params);
    }

    public static void put(FuncArr8<?, Class<?>, Object, Boolean, Object, Class<?>, String, Integer, Object[]> runner,
                           BoolFunc<?> isReturning,
                           Func<?, ?> getReturnValue,
                           Supplier<Class<?>> determineCaller) {
        if (Callb.runner != null || Callb.isReturning != null || Callb.getReturnValue != null)
            throw new IllegalStateException();
        Callb.runner = runner;
        Callb.isReturning = (BoolFunc<Object>) isReturning;
        Callb.getReturnValue = (Func<Object, ?>) getReturnValue;
        Callb.determineCaller = determineCaller;
    }

    public boolean isReturning() {
        return isReturning.run(callback);
    }

    public T getReturnValue() {
        return (T) getReturnValue.run(callback);
    }

}
