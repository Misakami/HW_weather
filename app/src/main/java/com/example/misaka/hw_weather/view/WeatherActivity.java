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

import com.example.misaka.hw_weather.R;
import com.example.misaka.hw_weather.model.GSON.Bean;
import com.example.misaka.hw_weather.model.util.ButtomDilog;
import com.example.misaka.hw_weather.model.util.Utility;
import com.example.misaka.hw_weather.model.util.WeatherLodaer;
import com.example.misaka.hw_weather.presenter.WeatherFragment;
import com.example.misaka.hw_weather.presenter.WeatherFragmentAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * @author misaka
 * @date 2018/12/11
 */
public class WeatherActivity extends AppCompatActivity {
    private ArrayList<String> pageList;
    private ArrayList<String> pageList2;
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
        pageList2 = new ArrayList<>();
        weatherFragments = new ArrayList<>();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String json = pref.getString("Fragmentlist", "");
        if (!"".equals(json)) {
            pageList = new Gson().fromJson(json, new TypeToken<ArrayList<String>>() {
            }.getType());
        }
        if (Utility.isNetworkAvailable(this)) {
            retorfitrx();
        } else {
            for (String id : pageList) {
                WeatherFragment weatherFragment = WeatherFragment.getInstance(id);
                weatherFragments.add(weatherFragment);
            }
            viewPager = (ViewPager) findViewById(R.id.weather_page);
            viewPager.setOffscreenPageLimit(3);
            weatherFragmentAdapter = new WeatherFragmentAdapter(getSupportFragmentManager(), weatherFragments);
            viewPager.setAdapter(weatherFragmentAdapter);
        }
    }

    private void retorfitrx(){
        WeatherLodaer lodaer = new WeatherLodaer();
        lodaer.getweather("auto_ip")
                .subscribe(new Consumer<List<Bean.HeWeather6Bean>>() {
                    @Override
                    public void accept(List<Bean.HeWeather6Bean> heWeather6Beans) throws Exception {
                        pageList.remove(heWeather6Beans.get(0).getBasic().getCid());
                        pageList.add(0,heWeather6Beans.get(0).getBasic().getCid());
                        editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("Fragmentlist", new Gson().toJson(pageList));
                        editor.apply();
                        for (String id : pageList) {
                            WeatherFragment weatherFragment = WeatherFragment.getInstance(id);
                            weatherFragments.add(weatherFragment);
                        }
                        viewPager = (ViewPager) findViewById(R.id.weather_page);
                        viewPager.setOffscreenPageLimit(3);
                        weatherFragmentAdapter = new WeatherFragmentAdapter(getSupportFragmentManager(), weatherFragments);
                        viewPager.setAdapter(weatherFragmentAdapter);
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
