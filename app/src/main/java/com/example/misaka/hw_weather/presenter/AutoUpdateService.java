package com.example.misaka.hw_weather.presenter;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.misaka.hw_weather.R;
import com.example.misaka.hw_weather.model.GSON.HeWeather6;
import com.example.misaka.hw_weather.model.util.Utility;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.support.constraint.Constraints.TAG;

/**
 * @author misaka
 * @date 2018/12/8
 */
public class AutoUpdateService extends Service {

    private String ok = "ok";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Utility.isNetworkAvailable(getApplicationContext())) {
            System.out.println("更新");
            OkHttpClient okHttpClient = new OkHttpClient.
                    Builder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://free-api.heweather.com/s6/weather?location=auto_ip&key=78027c3cef2d4c4398f53c0cbefe57dc")
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responsetext = response.body().string();
                    RemoteViews views = new RemoteViews(getPackageName(), R.layout.app_widget_noweather);
                    try {
                        JSONObject jsonObject = new JSONObject(responsetext);
                        JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
                        HeWeather6 heWeather6 = new Gson().fromJson(jsonArray.getJSONObject(0).toString(), HeWeather6.class);
                        if (!ok.equals(heWeather6.status)) {
                            Log.e(TAG, "用完次数 ");
                        } else {
                            views.setTextViewText(R.id.widget_loaction, heWeather6.basic.location);
                            views.setTextViewText(R.id.widget_cond, heWeather6.now.cond_txt);
                            views.setTextViewText(R.id.widget_temps, String.valueOf(heWeather6.now.tmp) + "°C");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //获得组件的服务类
                    AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
                    ComponentName name = new ComponentName(
                            getApplicationContext(),
                            WeatherAppWidgetProvider.class);
                    manager.updateAppWidget(name, views);
                }
            });
            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
            int hour = 60 * 60 * 1000;
            long time = SystemClock.elapsedRealtime() + hour;
            Intent i = new Intent(this, AutoUpdateService.class);
            PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
            manager.cancel(pi);
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, pi);
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("TAG", "定时器服务启动");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: service");
        super.onDestroy();
    }
}