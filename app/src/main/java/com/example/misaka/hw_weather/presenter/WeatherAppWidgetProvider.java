package com.example.misaka.hw_weather.presenter;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

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
 * @date 2018/12/10
 */
public class WeatherAppWidgetProvider extends AppWidgetProvider {
    private String action = "btn.text.com";
    private String ok = "ok";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.i("TAG", "onupdate");
        // 对每一个小部件进行更新
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget_noweather);
        remoteViews.setOnClickPendingIntent(R.id.update, getClickPendingIntent.INSTANCE.getClickPendingIntent(context, R.id.update, action, getClass()));
        getClickPendingIntent.INSTANCE.show(remoteViews, context, this.getClass());
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.i("TAG", "onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.i("TAG", "onDisabled");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (action.equals(intent.getAction())) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget_noweather);
            remoteViews.setOnClickPendingIntent(R.id.update, getClickPendingIntent.INSTANCE.getClickPendingIntent(context, R.id.update, action, getClass()));
            getClickPendingIntent.INSTANCE.show(remoteViews, context, this.getClass());
            Toast.makeText(context, "更新好了 喵", Toast.LENGTH_SHORT).show();
        }
        update(context, context.getPackageName());
        Log.e("ddd", "onReceive: ????");
    }


    private void update(final Context context, final String name) {
        if (Utility.isNetworkAvailable(context)) {
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
                    RemoteViews views = new RemoteViews(name, R.layout.app_widget_noweather);
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
                    AppWidgetManager manager = AppWidgetManager.getInstance(context);
                    ComponentName name = new ComponentName(
                            context,
                            WeatherAppWidgetProvider.class);
                    manager.updateAppWidget(name, views);
                }
            });
        }
    }
}
