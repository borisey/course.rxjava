package org.rxjava;

public interface Observer<T> {
    void onNext(T item);
    default void onError(Throwable t) {
        // Обрабатываем ошибки
        System.err.println("Error: " + t.getMessage());
    }

    default void onComplete() {
        // Завершение потока
        System.out.println("Completed");
    }
}