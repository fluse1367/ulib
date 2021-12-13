package eu.software4you.ulib.core.impl;

import lombok.SneakyThrows;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;

public class Loader<V> {
    private final Callable<V> call;
    private boolean running;
    private FutureTask<V> fut;

    public Loader(final Supplier<V> loadTask) {
        this.call = loadTask::get;
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
        if (!fut.isDone() && !running) {
            synchronized (this) {
                running = true;
                fut.run();
                running = false;
            }
        }
        return fut.get();
    }

    public V getIfDone() {
        return fut.isDone() ? get() : null;
    }
}
