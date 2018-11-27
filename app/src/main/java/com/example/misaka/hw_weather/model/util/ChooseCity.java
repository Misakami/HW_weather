/*
package com.example.misaka.hw_weather.model.util;

import android.util.Log;
import android.widget.Toast;

import com.example.misaka.hw_weather.view.MainActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChooseCity {
    private OkHttpClient okHttpClient = Httpclient.instance.getClient();

    public void query(String address, final int Type) {
        final Request request = new Request.Builder()
                .url(address)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (Type == ){
                    Utility.handleCityResponse(response.body().string(),);
                }
            }
        });
    }
}
*/
