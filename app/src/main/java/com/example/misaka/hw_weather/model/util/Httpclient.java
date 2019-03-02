package com.example.misaka.hw_weather.model.util;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author misaka
 * @date 2018/12/8
 */

public enum Httpclient {
    //单例
    instance;
    private int DEFAULT_TIME_OUT = 2;
    private OkHttpClient client;
    private Retrofit retrofit;

    private Httpclient() {
        client = new OkHttpClient.
                Builder()
                .connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://free-api.heweather.com/s6/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public OkHttpClient getClient() {
        return client;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }

    public <T> void getweather(Observable<T> o, DisposableObserver<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(2)//请求失败重连次数
                .subscribe(s);
    }
}
