package com.example.misaka.hw_weather.presenter;


/**
 * @author misaka
 * @date 2018/12/8
 */
public class Dailyweather {
    private String cond;
    private String imageid;
    private String temp;
    private String cid;

    public Dailyweather(String cond, String imageid, String temp) {
        this.cond = cond;
        this.imageid = imageid;
        this.temp = temp;
    }

    public String getCond() {
        return cond;
    }

    public String getImageid() {
        return imageid;
    }

    public String getTemp() {
        return temp;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
