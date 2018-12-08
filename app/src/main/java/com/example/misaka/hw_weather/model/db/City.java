package com.example.misaka.hw_weather.model.db;

import org.litepal.crud.LitePalSupport;
/**
 * @author misaka
 * @date 2018/12/8
 */
public class City extends LitePalSupport {
    private int id;
    private int cityCode;
    private String cityName;
    private int provinceID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(int provinceID) {
        this.provinceID = provinceID;
    }
}
