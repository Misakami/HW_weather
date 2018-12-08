package com.example.misaka.hw_weather.model.GSON;
/**
 * @author misaka
 * @date 2018/12/8
 */
public class Forecast {
    /**
     * 早间天气
     */
    public String cond_txt_d;
    /**
    * 晚间天气
    */
    public String cond_txt_n;
    /**
     * 最高温度
     */
    public int tmp_max;
    /**
     * 最低温度
     */
    public int tmp_min;
    /**
     * 时间
     */
    public String date;
    /**
     * 天气图片码
     */
    public int cond_code_d;
}
