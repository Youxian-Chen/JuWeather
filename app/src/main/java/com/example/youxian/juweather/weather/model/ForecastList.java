package com.example.youxian.juweather.weather.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Youxian on 1/1/16.
 */
public class ForecastList {
    @SerializedName("dt")
    public String timestamp;
    public Temp temp;
    @SerializedName("weather")
    public Weather[] weathers;
}
