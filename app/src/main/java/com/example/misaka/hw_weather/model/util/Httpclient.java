package com.example.misaka.hw_weather.model.util;

import okhttp3.OkHttpClient;

public enum Httpclient {
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
