package com.mvi.network;

import io.reactivex.Observable;
import com.mvi.network.model.MeasurementResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AqService {
    @GET("measurements")
    Observable<MeasurementResponse> getLocation(@Query("city") String location, @Query("limit") int limit);
}