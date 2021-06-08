package eu.software4you.ulib;

import eu.software4you.utils.ArrayUtils;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Tasks {
    public static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(0);
    public static final ExecutorService RUNNER = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            0, TimeUnit.NANOSECONDS, new SynchronousQueue<>());

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
        return RUNNER.submit(task);
    }

    public static <T> Future<T> run(Runnable task, T result) {
        return RUNNER.submit(task, result);
    }

    public static Future<?> run(Runnable task) {
        return RUNNER.submit(task);
    }
}
