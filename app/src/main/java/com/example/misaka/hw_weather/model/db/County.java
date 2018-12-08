package com.example.misaka.hw_weather.model.db;

import org.litepal.crud.LitePalSupport;
/**
 * @author misaka
 * @date 2018/12/8
 */
public class County extends LitePalSupport {
    private int id;
    private String countyName;
    private int cityid;
    private String weatherid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getCityid() {
        return cityid;
    }

    public void setCityid(int cityid) {
        this.cityid = cityid;
    }

    public String getWeatherid() {
        return weatherid;
    }

    public void setWeatherid(String weatherid) {
        this.weatherid = weatherid;
    }
}
