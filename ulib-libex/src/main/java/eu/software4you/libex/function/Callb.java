package eu.software4you.libex.function;

/**
 * Callback container
 */
@SuppressWarnings("unchecked")
public class Callb<T> {
    private static FuncArr7<?, Class<?>, Object, Boolean, Object, String, Integer, Object[]> runner;
    private static BoolFunc<Object> isReturning;
    private static Func<Object, ?> getReturnValue;

    private final Object callback;

    public Callb(Class<T> a, T b, boolean c, Object d, String e, int f, Object[] g) {
        this.callback = runner.run(a, b, c, d, e, f, g);
    }

    public static void put(FuncArr7<?, Class<?>, Object, Boolean, Object, String, Integer, Object[]> runner, BoolFunc<?> isReturning, Func<?, ?> getReturnValue) {
        if (Callb.runner != null || Callb.isReturning != null || Callb.getReturnValue != null)
            throw new IllegalStateException();
        Callb.runner = runner;
        Callb.isReturning = (BoolFunc<Object>) isReturning;
        Callb.getReturnValue = (Func<Object, ?>) getReturnValue;
    }

    public boolean isReturning() {
        return isReturning.run(callback);
    }

    public T getReturnValue() {
        return (T) getReturnValue.run(callback);
    }

}
