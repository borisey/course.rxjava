package org.rxjava;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Schedulers {

    private static final Scheduler IO = new Scheduler() {
        private final ExecutorService executor = Executors.newCachedThreadPool();

        @Override
        public void execute(Runnable task) {
            executor.submit(task);
        }
    };

    private static final Scheduler COMPUTATION = new Scheduler() {
        private final ExecutorService executor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()
        );

        @Override
        public void execute(Runnable task) {
            executor.submit(task);
        }
    };

    private static final Scheduler SINGLE = new Scheduler() {
        private final ExecutorService executor = Executors.newSingleThreadExecutor();

        @Override
        public void execute(Runnable task) {
            executor.submit(task);
        }
    };

    public static Scheduler io() {
        return IO;
    }

    public static Scheduler computation() {
        return COMPUTATION;
    }

    public static Scheduler single() {
        return SINGLE;
    }
}