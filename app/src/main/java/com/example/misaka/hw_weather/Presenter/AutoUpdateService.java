package com.example.misaka.hw_weather.Presenter;


import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;

import android.support.annotation.Nullable;

import android.util.Log;
import android.widget.RemoteViews;


import com.example.misaka.hw_weather.R;
import com.example.misaka.hw_weather.model.GSON.HeWeather6;
import com.example.misaka.hw_weather.model.util.Httpclient;
import com.example.misaka.hw_weather.model.util.Utility;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.support.constraint.Constraints.TAG;


public class AutoUpdateService extends Service {

    private Timer timer = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("TAG", "定时器服务启动");
        timer = new Timer();//更新时间
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "run: ？" );
                final RemoteViews views = new RemoteViews(getPackageName(), R.layout.app_widget_noweather);
                if (Utility.isNetworkAvailable(getApplicationContext())) {
                    System.out.println("更新");
                    OkHttpClient okHttpClient = Httpclient.instance.getClient();
                    Request request = new Request.Builder()
                            .url("https://free-api.heweather.com/s6/weather?location=auto_ip&key=78027c3cef2d4c4398f53c0cbefe57dc")
                            .build();
                    Response response;
                    try {
                        response = okHttpClient.newCall(request).execute();
                        if (response.isSuccessful()) {
                            String responsetext = response.body().string();
                            try {
                                JSONObject jsonObject = new JSONObject(responsetext);
                                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
                                HeWeather6 heWeather6 = new Gson().fromJson(jsonArray.getJSONObject(0).toString(), HeWeather6.class);
                                if (!heWeather6.status.equals("ok")){
                                    Log.e(TAG, "用完次数 " );
                                }else {
                                    views.setTextViewText(R.id.widget_loaction, heWeather6.basic.location);
                                    views.setTextViewText(R.id.widget_cond, heWeather6.now.cond_txt);
                                    views.setTextViewText(R.id.widget_temps, String.valueOf(heWeather6.now.tmp));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //获得组件的服务类
                    AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());

                    ComponentName name = new ComponentName(
                            getApplicationContext(),
                            WeatherAppWidgetProvider.class);
                    manager.updateAppWidget(name, views);
                }
            }
        }, 0, 360000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: service" );
        timer.cancel();
        timer = null;
        super.onDestroy();
    }

}