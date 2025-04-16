package org.rxjava;

public class MyObserver implements Observer<Integer> {
    @Override
    public void onNext(Integer item) {
        System.out.println("Received: " + item);
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("Error occurred: " + t.getMessage());
    }

    @Override
    public void onComplete() {
        System.out.println("Stream completed!");
    }
}