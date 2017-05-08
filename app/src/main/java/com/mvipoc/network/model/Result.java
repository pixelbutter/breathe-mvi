package com.mvipoc.network.model;

import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("location")
    private String location;

    @SerializedName("date")
    private Date date;

    @SerializedName("value")
    private Integer value;

    @SerializedName("unit")
    private String unit;

    @SerializedName("country")
    private String country;

    @SerializedName("city")
    private String city;

    public String getLocation() {
        return location;
    }

    public Date getDate() {
        return date;
    }

    public Integer getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }
}