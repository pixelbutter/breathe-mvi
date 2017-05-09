package com.mvi.main;

import android.util.Log;

import com.mvi.Constants;

final class SubmitEvent {
    private String cityName;

    SubmitEvent(String cityName) {
        Log.d(Constants.TAG_THREAD_DEBUGGING, "SubmitEvent instance constructed on: " + Thread.currentThread().toString());
        this.cityName = cityName;
    }

    String getCityName() {
        return cityName;
    }
}