package eu.software4you.ulib.core.impl.value;

import eu.software4you.ulib.core.function.Func;
import eu.software4you.ulib.core.function.ParamTask;
import org.jetbrains.annotations.Nullable;

public class NoSetLazyValue<T> extends LazyValueImpl<T> {
    public NoSetLazyValue(@Nullable T val, @Nullable Func<T, ?> fetch, @Nullable ParamTask<T, ?> push) {
        super(val, fetch, push);
    }

    @Override
    public T set(T val) {
        throw new UnsupportedOperationException();
    }
}
