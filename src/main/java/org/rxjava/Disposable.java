package org.rxjava;

public interface Disposable {
    void dispose();
    boolean isDisposed();
}