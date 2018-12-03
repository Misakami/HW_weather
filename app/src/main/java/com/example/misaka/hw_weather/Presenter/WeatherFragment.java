package com.example.misaka.hw_weather.Presenter;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.misaka.hw_weather.R;
import com.example.misaka.hw_weather.model.GSON.Forecast;
import com.example.misaka.hw_weather.model.GSON.HeWeather6;
import com.example.misaka.hw_weather.model.util.Httpclient;
import com.google.gson.Gson;

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

import static android.support.constraint.Constraints.TAG;


public class WeatherFragment extends Fragment {

    private String id;
    private SharedPreferences prefer;
    private SharedPreferences.Editor editor;
    private String responseText;
    private TextView tem;
    private DailyWeatherAdapter adapter;
    private List<Dailyweather> dailyweathers;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static WeatherFragment getInstance(String url) {
        WeatherFragment weatherFragment = new WeatherFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        weatherFragment.setArguments(bundle);
        return weatherFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.weather_demo, container, false);
        Bundle arguments = this.getArguments();
        id = arguments.getString("url");
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        prefer = PreferenceManager.getDefaultSharedPreferences(getContext());
        responseText = prefer.getString(id, "");
        if (!responseText.equals("")) {
            showinfo(responseText, view);
        } else
            queryWeather(view, id);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryWeather(view, id);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
    }

    private void queryWeather(final View view, final String id) {
        OkHttpClient okHttpClient = Httpclient.instance.getClient();

        Request request = new Request.Builder()
                .url("https://free-api.heweather.com/s6/weather?location=" + id + "&key=78027c3cef2d4c4398f53c0cbefe57dc")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), "失败", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseText = response.body().string();
                editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putString(id, responseText);
                editor.apply();
                showinfo(responseText, view);
            }
        });
        swipeRefreshLayout = view.findViewById(R.id.swipe);
    }

    private void showinfo(String responseText, final View view) {
        try {
            JSONObject jsonObject = new JSONObject(responseText);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            final HeWeather6 heWeather6 = new Gson().fromJson(jsonArray.getJSONObject(0).toString(), HeWeather6.class);
            if (!heWeather6.status.equals("ok")) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "暂时找不到这个城市", Toast.LENGTH_SHORT).show();
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
                        Log.e(TAG, "run: " + id);
                        recyclerView = view.findViewById(R.id.recyclerview);
                        gridLayoutManager = new GridLayoutManager(getContext(), 1);
                        recyclerView.setLayoutManager(gridLayoutManager);
                        adapter = new DailyWeatherAdapter(dailyweathers);
                        recyclerView.setAdapter(adapter);
                        tem = view.findViewById(R.id.temp);
                        tem.setText(spannableString);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
