package com.example.misaka.hw_weather.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.misaka.hw_weather.presenter.WeatherFragment;
import com.example.misaka.hw_weather.presenter.WeatherFragmentAdapter;
import com.example.misaka.hw_weather.R;
import com.example.misaka.hw_weather.model.GSON.HeWeather6;
import com.example.misaka.hw_weather.model.util.ButtomDilog;
import com.example.misaka.hw_weather.model.util.Httpclient;
import com.example.misaka.hw_weather.model.util.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ArrayList<String> pageList;
    private List<WeatherFragment> weatherFragments;
    public DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private WeatherFragmentAdapter weatherFragmentAdapter;
    private SharedPreferences.Editor editor;
    private ButtomDilog dilog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_activity);
        initview();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.misaka.hw_weather.view.LOCAL_BROADCAST");
        LocalReceiver localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);

        drawerLayout = findViewById(R.id.drwaerlayout);
        Button addbutton = findViewById(R.id.add);
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        Button setbutton = findViewById(R.id.set);
        setbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dilog = new ButtomDilog(WeatherActivity.this);
                dilog.setfirsr(ManagementCity());
                dilog.show();
            }
        });

    }


    private View.OnClickListener ManagementCity() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dilog.close();
                Intent intent = new Intent(WeatherActivity.this, ManagementActiviy.class);
                startActivity(intent);
            }
        };
    }

    public void initview() {
        pageList = new ArrayList<>();
        weatherFragments = new ArrayList<>();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String json = pref.getString("Fragmentlist", "");
        if (!"".equals(json)) {
            pageList = new Gson().fromJson(json, new TypeToken<ArrayList<String>>() {
            }.getType());
            for (String id : pageList) {
                WeatherFragment weatherFragment = WeatherFragment.getInstance(id);
                weatherFragments.add(weatherFragment);
            }
            viewPager = (ViewPager) findViewById(R.id.weather_page);
            viewPager.setOffscreenPageLimit(3);
            weatherFragmentAdapter = new WeatherFragmentAdapter(getSupportFragmentManager(), weatherFragments);
            viewPager.setAdapter(weatherFragmentAdapter);
        } else if (Utility.isNetworkAvailable(this)) {
            webget();
        }
    }

    private void webget() {
        OkHttpClient okHttpClient = Httpclient.instance.getClient();
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
                try {
                    JSONObject jsonObject = new JSONObject(responsetext);
                    JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
                    HeWeather6 heWeather6 = new Gson().fromJson(jsonArray.getJSONObject(0).toString(), HeWeather6.class);

                    pageList.add(heWeather6.basic.cid);

                    editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                    editor.putString("Fragmentlist", new Gson().toJson(pageList));
                    editor.apply();

                    final String location = heWeather6.basic.cid;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            WeatherFragment weatherFragment_location = WeatherFragment.getInstance(location);
                            weatherFragments.add(weatherFragment_location);

                            viewPager = (ViewPager) findViewById(R.id.weather_page);
                            viewPager.setOffscreenPageLimit(3);
                            weatherFragmentAdapter = new WeatherFragmentAdapter(getSupportFragmentManager(), weatherFragments);
                            viewPager.setAdapter(weatherFragmentAdapter);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addList(String cid) {
        int i = 0;
        for (String id : pageList) {
            if (id.equals(cid)) {
                viewPager.setCurrentItem(i);
                return;
            }
            i++;
        }

        WeatherFragment weatherFragment = WeatherFragment.getInstance(cid);
        pageList.add(cid);
        editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
        editor.putString("Fragmentlist", new Gson().toJson(pageList));
        editor.apply();
        weatherFragments.add(weatherFragment);
        weatherFragmentAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(i);
    }


    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            initview();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
