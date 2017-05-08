package com.mvipoc.network.model;

import com.google.gson.annotations.SerializedName;

public class Date {

    @SerializedName("utc")
    private String utc;

    @SerializedName("local")
    private String local;
}