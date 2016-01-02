package com.example.youxian.juweather;

import com.example.youxian.juweather.model.Current;
import com.example.youxian.juweather.model.CurrentByCity;
import com.example.youxian.juweather.model.Forecast;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Youxian on 1/1/16.
 */
public interface WeatherApi {

    @GET("/data/2.5/find?appid=2de143494c0b295cca9337e1e96b00e0&cnt=8")
    Observable<Current> getCurrentWeather(@Query("lat") String lat, @Query("lon") String lon);

    @GET("/data/2.5/forecast/daily?appid=2de143494c0b295cca9337e1e96b00e0&mode=json&cnt=8")
    Observable<Forecast> getForecastWeather(@Query("q") String city);

    @GET("/data/2.5/weather?appid=2de143494c0b295cca9337e1e96b00e0")
    Observable<CurrentByCity> getCurrentWeatherByCity(@Query("q") String city);
}
