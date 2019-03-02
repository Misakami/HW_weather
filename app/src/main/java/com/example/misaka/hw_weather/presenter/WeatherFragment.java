package com.example.misaka.hw_weather.presenter;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.misaka.hw_weather.R;
import com.example.misaka.hw_weather.model.GSON.Forecast;
import com.example.misaka.hw_weather.model.GSON.HeWeather6;
import com.example.misaka.hw_weather.model.util.Httpclient;
import com.example.misaka.hw_weather.model.util.Utility;
import com.example.misaka.hw_weather.model.util.WeatherLodaer;
import com.example.misaka.hw_weather.view.WeatherActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author misaka
 * @date 2018/12/11
 */
public class WeatherFragment extends Fragment {

    private String id;
    private SharedPreferences.Editor editor;
    private String responseText;
    private TextView tem;
    private DailyWeatherAdapter adapter;
    private List<Dailyweather> dailyweathers;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private long lastupdatetime;
    private long nowtime;
    private WeatherActivity activity;
    private boolean isfirst = false;
    private boolean isVisible = false;

    public static WeatherFragment getInstance(String url) {
        WeatherFragment weatherFragment = new WeatherFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        weatherFragment.setArguments(bundle);
        return weatherFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = this.getArguments();
        id = arguments.getString("url");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.weather_demo, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipe);
        SharedPreferences prefer = PreferenceManager.getDefaultSharedPreferences(getContext());
        responseText = prefer.getString(id, "");
        lastupdatetime = prefer.getLong(id + "time", 0);
        nowtime = System.currentTimeMillis();

        if (!"".equals(responseText)) {
            showinfo(responseText, view);
        } else {
            isfirst = true;
        }
        //1小时为更新节点
        if (nowtime - lastupdatetime > 60 * 60 * 1000 && isVisible && Utility.isNetworkAvailable(getContext())) {
            rxjavaquery(view, id);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                rxjavaquery(view, id);
            }
        });
        return view;
    }


    @SuppressLint("CheckResult")
    private void rxjavaquery(final View view, final String id){
        swipeRefreshLayout.setRefreshing(true);
        WeatherLodaer lodaer =new WeatherLodaer();
        lodaer.getweatherbody(id)
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody)  {
                        try {
                            responseText = responseBody.string();
                            nowtime = System.currentTimeMillis();
                            lastupdatetime = nowtime;
                            editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                            editor.putLong(id + "time", nowtime);
                            editor.putString(id, responseText);
                            editor.apply();
                            showinfo(responseText,view);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable){
                        Toast.makeText(getActivity(), "失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void queryWeather(final View view, final String id) {
        swipeRefreshLayout.setRefreshing(true);
        OkHttpClient okHttpClient = Httpclient.instance.getClient();

        Request request = new Request.Builder()
                .url("https://free-api.heweather.com/s6/weather?location=" + id + "&key=78027c3cef2d4c4398f53c0cbefe57dc")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseText = response.body().string();
                nowtime = System.currentTimeMillis();
                lastupdatetime = nowtime;
                editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putLong(id + "time", nowtime);
                editor.putString(id, responseText);
                editor.apply();
                showinfo(responseText, view);
            }
        });
    }

    private void showinfo(String responseText, final View view) {
        try {
            JSONObject jsonObject = new JSONObject(responseText);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            final HeWeather6 heWeather6 = new Gson().fromJson(jsonArray.getJSONObject(0).toString(), HeWeather6.class);
            if (!"ok".equals(heWeather6.status)) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "暂时找不到这个城市", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            } else {
                //实时天气
                String nowtemp = String.valueOf(heWeather6.now.tmp);
                String location = heWeather6.basic.location;
                String cond = heWeather6.now.cond_txt;
                final SpannableString spannableString = new SpannableString(nowtemp + "°C\n" + location + "\n" + cond);
                SuperscriptSpan superscriptSpan = new SuperscriptSpan();
                RelativeSizeSpan sizeSpan = new RelativeSizeSpan(0.3f);
                RelativeSizeSpan sizeSpan1 = new RelativeSizeSpan(0.3f);
                spannableString.setSpan(superscriptSpan, nowtemp.length(), nowtemp.length() + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(sizeSpan, nowtemp.length(), nowtemp.length() + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(sizeSpan1, nowtemp.length() + 2, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                //三日天气
                dailyweathers = new ArrayList<>();
                for (Forecast forecast : heWeather6.forecastList) {
                    String url = "https://cdn.heweather.com/cond_icon/" + forecast.cond_code_d + ".png";

                    String forecond = forecast.cond_txt_d.equals(forecast.cond_txt_n) ? forecast.cond_txt_d : forecast.cond_txt_d + "转" + forecast.cond_txt_n;

                    String temp = forecast.tmp_max + "/" + forecast.tmp_min + "°C";

                    Dailyweather dailyweather = new Dailyweather(forecond, url, temp);
                    dailyweathers.add(dailyweather);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView = view.findViewById(R.id.recyclerview);
                        gridLayoutManager = new GridLayoutManager(getContext(), 1);
                        recyclerView.setLayoutManager(gridLayoutManager);
                        adapter = new DailyWeatherAdapter(dailyweathers);
                        recyclerView.setAdapter(adapter);
                        tem = view.findViewById(R.id.temp);
                        tem.setText(spannableString);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isVisible = true;
            nowtime = System.currentTimeMillis();
            if (nowtime - lastupdatetime > 60 * 60 * 1000 && getView() != null && Utility.isNetworkAvailable(Objects.requireNonNull(getContext()))) {
                if (!isfirst) {
                    Toast.makeText(getContext(), "数据过期,正在更新", Toast.LENGTH_SHORT).show();
                }
                rxjavaquery(getView(), id);
            }
        }
    }
}
