package com.mvi.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MeasurementResponse {

    @SerializedName("results")
    private List<Result> results = null;

    public List<Result> getResults() {
        return results;
    }
}