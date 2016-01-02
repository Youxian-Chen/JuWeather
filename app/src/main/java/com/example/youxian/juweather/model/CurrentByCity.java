package com.example.youxian.juweather.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Youxian on 1/2/16.
 */
public class CurrentByCity extends WeatherBase {
    @SerializedName("weather")
    public Weather[] weathers;
    public Main main;
    @SerializedName("dt")
    public String timestamp;
    @SerializedName("sys")
    public System system;
    public String id;
    public String name;
}
