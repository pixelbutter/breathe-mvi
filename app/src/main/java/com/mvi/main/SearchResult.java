package com.mvi.main;

import android.support.annotation.Nullable;
import android.util.Log;

import com.mvi.Constants;
import com.mvi.network.model.MeasurementResponse;

final class SearchResult {

    enum State { IN_PROGRESS, SUCCESS, ERROR }

    final MeasurementResponse data;
    final Throwable error;
    final State state;

    static SearchResult inProgress() {
        return new SearchResult(State.IN_PROGRESS, null, null);
    }

    static SearchResult success(MeasurementResponse response) {
        return new SearchResult(State.SUCCESS, response, null);
    }

    static SearchResult failure(Throwable error) {
        return new SearchResult(State.ERROR, null, error);
    }

    private SearchResult(State state, @Nullable MeasurementResponse data, @Nullable Throwable error) {
        Log.d(Constants.TAG_THREAD_DEBUGGING, "SearchResult instance constructed on: " + Thread.currentThread().toString());
        this.state = state;
        this.error = error;
        this.data = data;
    }

    MeasurementResponse getData() {
        return data;
    }

    Throwable getError() {
        return error;
    }
}