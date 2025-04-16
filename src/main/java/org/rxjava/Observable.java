package org.rxjava;

import java.util.function.Function;
import java.util.function.Predicate;

public class Observable<T> {

    public interface OnSubscribe<T> {
        void subscribe(Emitter<T> emitter);
    }

    private final OnSubscribe<T> onSubscribe;

    private Scheduler subscribeScheduler = null;
    private Scheduler observeScheduler = null;

    private Observable(OnSubscribe<T> onSubscribe) {
        this.onSubscribe = onSubscribe;
    }

    public static <T> Observable<T> create(OnSubscribe<T> onSubscribe) {
        return new Observable<>(onSubscribe);
    }

    @SafeVarargs
    public static <T> Observable<T> just(T... items) {
        return create(emitter -> {
            for (T item : items) {
                emitter.onNext(item);
            }
            emitter.onComplete();
        });
    }

    public Observable<T> subscribeOn(Scheduler scheduler) {
        this.subscribeScheduler = scheduler;
        return this;
    }

    public Observable<T> observeOn(Scheduler scheduler) {
        this.observeScheduler = scheduler;
        return this;
    }

    public <R> Observable<R> map(Function<? super T, ? extends R> mapper) {
        return create(emitter ->
                this.subscribe(new Observer<>() {
                    public void onNext(T item) {
                        R result = mapper.apply(item);
                        emitter.onNext(result);
                    }

                    public void onError(Throwable t) {
                        emitter.onError(t);
                    }

                    public void onComplete() {
                        emitter.onComplete();
                    }
                }));
    }

    public Observable<T> filter(Predicate<? super T> predicate) {
        return create(emitter ->
                this.subscribe(new Observer<>() {
                    public void onNext(T item) {
                        if (predicate.test(item)) {
                            emitter.onNext(item);
                        }
                    }

                    public void onError(Throwable t) {
                        emitter.onError(t);
                    }

                    public void onComplete() {
                        emitter.onComplete();
                    }
                }));
    }

    public <R> Observable<R> flatMap(Function<? super T, Observable<R>> mapper) {
        return create(emitter ->
                this.subscribe(new Observer<>() {
                    public void onNext(T item) {
                        try {
                            Observable<R> newObservable = mapper.apply(item);
                            newObservable.subscribe(new Observer<>() {
                                public void onNext(R r) {
                                    emitter.onNext(r);
                                }

                                public void onError(Throwable t) {
                                    emitter.onError(t);
                                }

                                public void onComplete() {
                                    // No-op
                                }
                            });
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                    }

                    public void onError(Throwable t) {
                        emitter.onError(t);
                    }

                    public void onComplete() {
                        emitter.onComplete();
                    }
                }));
    }

    public Disposable subscribe(Observer<T> observer) {
        Disposable disposable = new Disposable() {
            private volatile boolean disposed = false;

            @Override
            public void dispose() {
                disposed = true;
            }

            @Override
            public boolean isDisposed() {
                return disposed;
            }
        };

        Runnable task = () -> onSubscribe.subscribe(new Emitter<>() {
            public void onNext(T value) {
                if (!disposable.isDisposed()) {
                    if (observeScheduler != null) {
                        observeScheduler.execute(() -> observer.onNext(value));
                    } else {
                        observer.onNext(value);
                    }
                }
            }

            public void onError(Throwable error) {
                if (!disposable.isDisposed()) {
                    if (observeScheduler != null) {
                        observeScheduler.execute(() -> observer.onError(error));
                    } else {
                        observer.onError(error);
                    }
                }
            }

            public void onComplete() {
                if (!disposable.isDisposed()) {
                    if (observeScheduler != null) {
                        observeScheduler.execute(observer::onComplete);
                    } else {
                        observer.onComplete();
                    }
                }
            }
        });

        if (subscribeScheduler != null) {
            subscribeScheduler.execute(task);
        } else {
            task.run();
        }

        return disposable;
    }
}