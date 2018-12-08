package com.example.misaka.hw_weather.model.util;

import okhttp3.OkHttpClient;

/**
 * @author misaka
 * @date 2018/12/8
 */

public enum Httpclient {
    //单例
    instance;
    private OkHttpClient client;

    private Httpclient(){
        client = new OkHttpClient.
                Builder()
                .build();
    }

    public OkHttpClient getClient(){
        return client;
    }
}
