package mmhelloworld.idrisjvmruntime;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;

import static java.lang.Runtime.getRuntime;
import static java.util.concurrent.ForkJoinTask.inForkJoinPool;

public class Concurrent {
    private static final ForkJoinPool fjpool = new ForkJoinPool(2 * getRuntime().availableProcessors());
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static Object par(Thunk thunk) {
        ForkJoinTask<?> task = inForkJoinPool() ? ForkJoinTask.adapt(thunk).fork() : fjpool.submit(thunk);
        return task.join();
    }

    public static Object fork(Thunk thunk) {
        return executor.submit(thunk);
    }

    public static void shutdownExecutor() {
        executor.shutdown();
    }

    public static void executorAwaitTermination(long timeout, TimeUnit timeUnit) {
        try {
            executor.awaitTermination(timeout, timeUnit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
