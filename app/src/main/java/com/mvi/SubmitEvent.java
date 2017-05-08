package com.mvi;

import android.util.Log;

public class SubmitEvent {
    private String cityName;

    public SubmitEvent(String cityName) {
        Log.d(Constants.TAG_THREAD_DEBUGGING, "SubmitEvent instance constructed on: " + Thread.currentThread().toString());
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }
}