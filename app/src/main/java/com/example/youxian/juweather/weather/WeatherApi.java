package com.example.youxian.juweather.weather;

import com.example.youxian.juweather.weather.model.Current;
import com.example.youxian.juweather.weather.model.Forecast;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Youxian on 1/1/16.
 */
public interface WeatherApi {
    @GET("/data/2.5/weather?appid=2de143494c0b295cca9337e1e96b00e0")
    Call<CurrentWeather> fetchCurrentWeather(@Query("q") String city);

    @GET("/data/2.5/forecast/daily?appid=2de143494c0b295cca9337e1e96b00e0&mode=json&cnt=8")
    Call<ForecastWeather> fetchForecastWeather(@Query("q") String city);

    @GET("/data/2.5/find?appid=2de143494c0b295cca9337e1e96b00e0&cnt=8")
    Observable<Current> getCurrentWeather(@Query("lat") String lat, @Query("lon") String lon);

    @GET("/data/2.5/forecast/daily?appid=2de143494c0b295cca9337e1e96b00e0&mode=json&cnt=8")
    Observable<Forecast> getForecastWeather(@Query("q") String city);
}
