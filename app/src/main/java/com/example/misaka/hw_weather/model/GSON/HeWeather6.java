package com.example.misaka.hw_weather.model.GSON;

import com.google.gson.annotations.SerializedName;

import java.util.List;
/**
 * @author misaka
 * @date 2018/12/8
 */
public class HeWeather6 {
    public String status;
    public Update update;
    public Now now;
    public Basic basic;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
    public class Update{
        @SerializedName("loc")
        public String updatetime;
    }
}
