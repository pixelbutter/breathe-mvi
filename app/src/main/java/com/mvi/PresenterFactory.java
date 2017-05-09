package com.mvi;

public interface PresenterFactory<T extends BasePresenter> {
    T create();
}