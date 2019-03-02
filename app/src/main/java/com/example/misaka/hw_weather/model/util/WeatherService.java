package com.example.misaka.hw_weather.model.util;

import com.example.misaka.hw_weather.model.GSON.Bean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface WeatherService {
    @GET
    Observable<Bean> getHeather(@Url String url, @Query("location") String l, @Query("key")String k);
    @GET
    Observable<ResponseBody> getHeather2(@Url String url, @Query("location") String l, @Query("key")String k);
}
