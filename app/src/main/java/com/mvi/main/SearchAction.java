package com.mvi.main;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.mvi.network.AqService;
import com.mvi.network.model.MeasurementResponse;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

final class SearchAction {

    private String location;

    SearchAction(String location) {
        this.location = location;
    }

    Observable<MeasurementResponse> getLocationObservable() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.openaq.org/v1/")
                .client(client)
                .build();

        AqService service = retrofit.create(AqService.class);
        return service.getLocation(location, 1);
    }
}