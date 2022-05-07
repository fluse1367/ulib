package eu.software4you.ulib.core.impl;

import eu.software4you.ulib.core.function.Func;
import eu.software4you.ulib.core.function.Task;
import eu.software4you.ulib.core.util.ArrayUtil;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public final class Tasks {
    private static final boolean SYNC = Internal.isForceSync();

    public static final ExecutorService RUNNER = new ThreadPoolExecutor(1, SYNC ? 1 : Integer.MAX_VALUE,
            10L, TimeUnit.SECONDS, SYNC ? new LinkedBlockingQueue<>() : new SynchronousQueue<>(), r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setName("runner-%d".formatted(t.getId()));
        return t;
    });

    @SafeVarargs
    public static <T> List<T> await(Func<T, ?> task, Func<T, ?>... tasks) {
        return Arrays.stream(ArrayUtil.concat(task, tasks))
                .map(Tasks::run)
                .map(Tasks::get)
                .toList();
    }

    // does this method even make sense?
    public static <T> T await(Func<T, ?> task) {
        return get(run(task));
    }

    @SneakyThrows
    private static <T> T get(Future<T> fut) {
        return fut.get();
    }

    public static <T> Future<T> run(Func<T, ?> task) {
        return RUNNER.submit(() -> catching(task));
    }

    public static <T> Future<T> run(Task<?> task, T result) {
        return RUNNER.submit(() -> catching(task), result);
    }

    public static Future<?> run(Task<?> task) {
        return RUNNER.submit(() -> catching(task));
    }

    private static void catching(Task<?> r) {
        try {
            r.execute();
        } catch (Throwable thr) {
            System.err.println("An error occurred while executing a task.");
            thr.printStackTrace();
        }
    }

    @SneakyThrows
    private static <R> R catching(Callable<R> c) {
        try {
            return c.call();
        } catch (Throwable thr) {
            System.err.println("An error occurred while executing a task.");
            thr.printStackTrace();
            throw thr;
        }
    }
}
