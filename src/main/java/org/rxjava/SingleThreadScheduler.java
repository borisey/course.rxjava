package org.rxjava;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadScheduler implements Scheduler {

    private final ExecutorService executorService;

    public SingleThreadScheduler() {
        // Используем один поток для выполнения задач
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void execute(Runnable task) {
        executorService.submit(task); // Задача будет выполнена в одном потоке
    }

    public void shutdown() {
        executorService.shutdown();
    }
}