package com.mvi;

import android.support.annotation.Nullable;
import android.util.Log;

import com.mvi.network.model.MeasurementResponse;

public class SearchResult {

    public enum State { IN_PROGRESS, SUCCESS, ERROR }

    final MeasurementResponse data;
    final Throwable error;
    final State state;

    public static SearchResult inProgress() {
        return new SearchResult(State.IN_PROGRESS, null, null);
    }

    public static SearchResult success(MeasurementResponse response) {
        return new SearchResult(State.SUCCESS, response, null);
    }

    public static SearchResult failure(Throwable error) {
        return new SearchResult(State.ERROR, null, error);
    }

    private SearchResult(State state, @Nullable MeasurementResponse data, @Nullable Throwable error) {
        Log.d(Constants.TAG_THREAD_DEBUGGING, "SearchResult instance constructed on: " + Thread.currentThread().toString());
        this.state = state;
        this.error = error;
        this.data = data;
    }

    public MeasurementResponse getData() {
        return data;
    }

    public Throwable getError() {
        return error;
    }
}