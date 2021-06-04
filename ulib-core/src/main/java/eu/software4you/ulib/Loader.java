package eu.software4you.ulib;

import lombok.SneakyThrows;
import lombok.val;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;

public class Loader<V> {
    private final Callable<V> call;
    private boolean running;
    private Future<V> fut;

    public Loader(final Supplier<V> loadTask) {
        this.call = () -> {
            running = true;
            val v = loadTask.get();
            running = false;
            return v;
        };
        set();
    }

    private void set() {
        fut = new FutureTask<>(call);
    }

    public boolean isRunning() {
        return running;
    }

    public synchronized void reset() {
        if (running && !fut.isDone())
            fut.cancel(true);
        running = false;
        set();
    }

    @SneakyThrows
    public V get() {
        return fut.get();
    }

    public V getIfDone() {
        return fut.isDone() ? get() : null;
    }
}
