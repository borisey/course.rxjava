package org.rxjava;

public class Main {
    public static void main(String[] args) {
        Observable.just(1, 2, 3, 4, 5)
                .map(x -> x * 2)
                .filter(x -> x > 5)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(new Observer<>() {
                    public void onNext(Integer item) {
                        System.out.println("Received: " + item + " | Thread: " + Thread.currentThread().getName());
                    }

                    public void onError(Throwable t) {
                        System.err.println("Error: " + t.getMessage());
                    }

                    public void onComplete() {
                        System.out.println("Completed.");
                    }
                });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}
    }
}
