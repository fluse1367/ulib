package eu.software4you.ulib.core.impl.value;

import eu.software4you.ulib.core.function.Func;
import eu.software4you.ulib.core.function.ParamTask;
import eu.software4you.ulib.core.reflect.Param;
import eu.software4you.ulib.core.reflect.ReflectUtil;
import eu.software4you.ulib.core.util.Expect;
import eu.software4you.ulib.core.util.LazyValue;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class LazyValueImpl<T> implements LazyValue<T> {

    private static final Func<?, ?> NO_FETCH = () -> null;
    private static final ParamTask<?, ?> NO_PUSH = o -> {};

    // tasks
    private final Func<T, ?> fetch;
    private final ParamTask<T, ?> push;

    // actual value
    private final AtomicReference<T> val = new AtomicReference<>();

    // internal indicators
    @Getter
    private volatile boolean running;
    @Getter
    private volatile boolean present;

    private volatile Thread executingThread;

    // locks
    private final Object $waiter = new Object[0];
    private final Object $running = new Object[0];

    @SuppressWarnings("unchecked")
    public LazyValueImpl(@Nullable T val, @Nullable Func<T, ?> fetch, @Nullable ParamTask<T, ?> push) {
        this.fetch = Objects.requireNonNullElse(fetch, (Func<T, ?>) NO_FETCH);
        this.push = Objects.requireNonNullElse(push, (ParamTask<T, ?>) NO_PUSH);

        if (val == null)
            return;

        this.val.set(val);
        this.present = true;
    }

    @Override
    public boolean isAvailable() {
        return present || fetch != NO_FETCH;
    }


    public void clear() {
        cancel();

        this.present = false;
        this.val.set(null);
    }

    private void cancel() {
        if (!running)
            return;

        // forcibly cancel execution
        ReflectUtil.doPrivileged(() -> ReflectUtil.icall(this.executingThread, "stop0()",
                Param.single(Object.class, new LazyValueCancel())).rethrowRE());
    }

    @SneakyThrows
    public T get() {
        if (present) // return value if it is already present
            return val.get();

        if (fetch == NO_FETCH)
            throw new NoSuchElementException();

        if (running) {
            // wait for running task
            synchronized ($waiter) {
                $waiter.wait();
            }
            // enter get() again to either retrieve the value or start another fetch task
            return get();
        }

        synchronized ($running) {
            try {
                this.executingThread = Thread.currentThread();
                this.running = true;

                // clear previous value
                this.val.lazySet(null);
                // no need to set `present` to false bc it already is

                // execute fetch task
                var res = Expect.compute(fetch);
                res.rethrowRE();

                // set ref
                var val = res.getValue();
                this.val.lazySet(val);
                present = true;

                return val;
            } catch (LazyValueCancel lvc) {
                // thread has been cancelled
                // catch to prevent death of thread
                return null;
            } finally {
                this.running = false;
                this.executingThread = null;

                // finally, notify all waiters
                synchronized ($waiter) {
                    $waiter.notifyAll();
                }
            }
        }
    }

    public T set(final T val) {
        cancel();

        synchronized ($running) {
            this.val.set(val);
            this.present = true;
            Expect.compute(() -> push.execute(val)).rethrowRE();
        }

        return val;
    }
}
