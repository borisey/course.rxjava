package org.rxjava;

public interface Scheduler {
    void execute(Runnable task);
}