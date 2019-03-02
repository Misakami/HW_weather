package com.example.misaka.hw_weather.model.util;


import com.example.misaka.hw_weather.model.GSON.Bean;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;


public class WeatherLodaer {
    private WeatherService weatherService;

    public WeatherLodaer() {
        weatherService = Httpclient.instance.create(WeatherService.class);

    }

    public Observable<List<Bean.HeWeather6Bean>> getweather(String location) {
        return weatherService.getHeather("weather", location, "78027c3cef2d4c4398f53c0cbefe57dc")
                .map(new Function<Bean, List<Bean.HeWeather6Bean>>() {
                    @Override
                    public List<Bean.HeWeather6Bean> apply(Bean bean) throws Exception {
                        return bean.getHeWeather6();
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public Observable<ResponseBody> getweatherbody(String location){
        return weatherService.getHeather2("weather",location,"78027c3cef2d4c4398f53c0cbefe57dc")
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
