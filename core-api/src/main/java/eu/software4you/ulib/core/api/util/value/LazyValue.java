package eu.software4you.ulib.core.api.util.value;

import lombok.SneakyThrows;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;

/**
 * Represents a value that will be lazy-loaded.
 * Lazy loading means the actual value will be loaded later when the value is queried.
 * The lazy value object will hold the loaded value until it is gc'd or {@link #reset() reset}.
 * <p>
 * This class utilizes java's {@link FutureTask future tasks}.
 *
 * @param <V> the value type
 */
public class LazyValue<V> {
    private final Callable<V> call;
    private boolean running;
    private FutureTask<V> fut;

    /**
     * @param loadTask the loading function
     */
    public LazyValue(final Supplier<V> loadTask) {
        this.call = loadTask::get;
        set();
    }

    private void set() {
        fut = new FutureTask<>(call);
    }

    /**
     * @return if the loading function is currently running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Resets the lazy value.<p>
     * The loading task will be called again when the value is queried.
     */
    public synchronized void reset() {
        if (running && !fut.isDone())
            fut.cancel(true);
        running = false;
        set();
    }

    /**
     * Queries the value supplied by this object.<p>
     * If the loading task did not yet get called (or if this object has been reset) the task will be run.
     *
     * @return the value as specified by this object's {@link #LazyValue(Supplier) loading task}.
     */
    @SneakyThrows
    public V get() {
        if (!fut.isDone() && !running) {
            synchronized (this) {
                running = true;
                fut.run();
                running = false;
            }
        }
        return fut.get();
    }

    /**
     * Queries the value supplied by this object only if the loading task is already done.<p>
     * If the loading task did not yet supply the value, it <b>won't</b> be run.
     *
     * @return this's object's value, or {@code null} if the value is not loaded yet
     */
    public V getIfDone() {
        return fut.isDone() ? get() : null;
    }
}
