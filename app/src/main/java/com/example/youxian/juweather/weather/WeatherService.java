package com.example.youxian.juweather.weather;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Youxian on 12/15/15.
 */
public interface WeatherService {

    @GET("/data/2.5/weather?appid=2de143494c0b295cca9337e1e96b00e0")
    Call<CurrentWeather> fetchCurrentWeather(@Query("q") String city);

    @GET("/data/2.5/forecast/daily?appid=2de143494c0b295cca9337e1e96b00e0&mode=json&cnt=8")
    Call<ForecastWeather> fetchForecastWeather(@Query("q") String city);
}
