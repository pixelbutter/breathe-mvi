package com.mvi;

import android.util.Log;

// Generic base viewstate best suited for network requests
public abstract class BaseUiModel<T> {
    final boolean inProgress;
    final boolean success;
    final Throwable error;
    final T data;

    protected BaseUiModel(boolean inProgress, boolean success, Throwable error, T data) {
        Log.d(Constants.TAG_THREAD_DEBUGGING, "BaseUiModel instance constructed on: " + Thread.currentThread().toString());
        this.inProgress = inProgress;
        this.success = success;
        this.error = error;
        this.data = data;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public Throwable getError() {
        return error;
    }
}