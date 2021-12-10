package eu.software4you.ulib;

import eu.software4you.ulib.core.api.utils.ArrayUtils;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Tasks {
    private static final boolean SYNC = Properties.getInstance().FORCE_SYNC;

    public static final ExecutorService RUNNER = new ThreadPoolExecutor(1, SYNC ? 1 : Integer.MAX_VALUE,
            10L, TimeUnit.SECONDS, SYNC ? new LinkedBlockingQueue<>() : new SynchronousQueue<>(), r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setName("runner-%d".formatted(t.getId()));
        return t;
    });

    @SafeVarargs
    public static <T> List<T> await(Callable<T> task, Callable<T>... tasks) {
        List<Future<T>> futs = new ArrayList<>();
        for (Callable<T> t : ArrayUtils.concat(task, tasks)) {
            futs.add(run(t));
        }
        return futs.stream().map(Tasks::get).collect(Collectors.toList());
    }

    // does this method even make sense?
    public static <T> T await(Callable<T> task) {
        return get(run(task));
    }

    @SneakyThrows
    private static <T> T get(Future<T> fut) {
        return fut.get();
    }

    public static <T> Future<T> run(Callable<T> task) {
        return RUNNER.submit(() -> catching(task));
    }

    public static <T> Future<T> run(Runnable task, T result) {
        return RUNNER.submit(() -> catching(task), result);
    }

    public static Future<?> run(Runnable task) {
        return RUNNER.submit(() -> catching(task));
    }

    private static void catching(Runnable r) {
        try {
            r.run();
        } catch (Throwable thr) {
            ULib.logger().log(Level.SEVERE, thr, () -> "An error occurred while executing a task.");
        }
    }

    @SneakyThrows
    private static <R> R catching(Callable<R> c) {
        try {
            return c.call();
        } catch (Throwable thr) {
            ULib.logger().log(Level.SEVERE, thr, () -> "An error occurred while executing a task.");
            throw thr;
        }
    }
}
