package com.example.misaka.hw_weather.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;

import com.example.misaka.hw_weather.Presenter.DailyWeatherAdapter;
import com.example.misaka.hw_weather.Presenter.Dailyweather;
import com.example.misaka.hw_weather.R;
import com.example.misaka.hw_weather.model.GSON.HeWeather6;
import com.example.misaka.hw_weather.model.util.SimpleItemTouchHelperCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManagementActiviy extends AppCompatActivity implements View.OnClickListener {

    private List<Dailyweather> dailyweathers;
    private Boolean chosseboolean = false;
    private RecyclerView recyclerView;
    private List<Dailyweather> dailyweathersbackups;
    private ItemTouchHelper touchHelper;
    private Button back;
    private Button change;
    private List<String> pageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_activiy);
        initView();
    }

    private void initView() {
        back = (Button) findViewById(R.id.back);
        change = (Button) findViewById(R.id.change);
        Toolbar managerbar = (Toolbar) findViewById(R.id.Managerbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        managerbar.setTitle("");
        setSupportActionBar(managerbar);

        initrecycler();
        recyclerView = findViewById(R.id.Manager_recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        DailyWeatherAdapter dailyWeatherAdapter = new DailyWeatherAdapter(dailyweathers);
        recyclerView.setAdapter(dailyWeatherAdapter);

        back.setOnClickListener(this);
        change.setOnClickListener(this);
        fab.setOnClickListener(this);

        //实现侧滑删除动画
        //先实例化Callback
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(dailyWeatherAdapter);
        //用Callback构造ItemtouchHelper
        touchHelper = new ItemTouchHelper(callback);
        //调用ItemTouchHelper的attachToRecyclerView方法建立联系
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                if (!chosseboolean) {
                    finish();
                } else {
                    chosseboolean = false;
                    change.setText("");
                    back.setText("");
                    dailyweathers = dailyweathersbackups;
                    touchHelper.attachToRecyclerView(null);
                }
                break;
            case R.id.change:
                if (!chosseboolean) {
                    chosseboolean = true;
                    change.setText("完成");
                    back.setText("取消");
                    dailyweathersbackups = dailyweathers;
                    touchHelper.attachToRecyclerView(recyclerView);
                } else {
                    chosseboolean = false;
                    change.setText("");
                    back.setText("");
                    List<String> page = new ArrayList<>();
                    for (Dailyweather dailyweather : dailyweathers) {
                        page.add(dailyweather.getCid());
                    }
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
                    Intent intent = new Intent("com.example.misaka.hw_weather.view.LOCAL_BROADCAST");
                    localBroadcastManager.sendBroadcast(intent);
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                    editor.putString("Fragmentlist", new Gson().toJson(page));
                    editor.apply();
                    touchHelper.attachToRecyclerView(null);
                }
                break;
            case R.id.fab:

                break;
        }
    }

    private void initrecycler() {
        dailyweathers = new ArrayList<>();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String json = pref.getString("Fragmentlist", "");
        if (!json.equals("")) {
            pageList = new Gson().fromJson(json, new TypeToken<ArrayList<String>>() {
            }.getType());
            for (int i = 0; i < pageList.size(); i++) {
                String id = pageList.get(i);
                String response = pref.getString(id, "");
                if (!response.isEmpty()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
                        final HeWeather6 heWeather6 = new Gson().fromJson(jsonArray.getJSONObject(0).toString(), HeWeather6.class);
                        String location = heWeather6.basic.location;
                        int image = heWeather6.forecastList.get(0).cond_code_d;
                        String url = "https://cdn.heweather.com/cond_icon/" + image + ".png";
                        String temp = heWeather6.forecastList.get(0).tmp_max + "/" + heWeather6.forecastList.get(0).tmp_min + "°C";
                        Dailyweather dailyweather = new Dailyweather(location, url, temp);
                        dailyweather.setCid(id);
                        dailyweathers.add(dailyweather);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
