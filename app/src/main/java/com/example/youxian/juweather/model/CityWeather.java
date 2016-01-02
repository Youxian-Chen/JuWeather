package com.example.youxian.juweather.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Youxian on 1/1/16.
 */
public class CityWeather {
    public int id;
    public String name;
    public Main main;
    @SerializedName("dt")
    public String timestamp;
    @SerializedName("sys")
    public System system;
    @SerializedName("weather")
    public Weather[] weathers;
}
