package com.example.misaka.hw_weather.model.db;

import org.litepal.crud.LitePalSupport;

public class County extends LitePalSupport {
    private int id;
    private String CountyName;
    private int Cityid;
    private String Weatherid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return CountyName;
    }

    public void setCountyName(String countyName) {
        CountyName = countyName;
    }

    public int getCityid() {
        return Cityid;
    }

    public void setCityid(int cityid) {
        Cityid = cityid;
    }

    public String getWeatherid() {
        return Weatherid;
    }

    public void setWeatherid(String weatherid) {
        Weatherid = weatherid;
    }
}