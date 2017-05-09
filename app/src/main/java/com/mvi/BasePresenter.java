package com.mvi;

public interface BasePresenter<V> {
    void onViewAttached(V view);
    void onViewDetached();
    void onDestroyed();
}
