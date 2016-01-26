package com.example.youxian.juweather.weather;

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

    @GET("/data/2.5/find?appid=727aa6438ef7127bfc8650be4d1ecb2d&cnt=8")
    Observable<Current> getCurrentWeather(@Query("lat") String lat, @Query("lon") String lon);

    @GET("/data/2.5/forecast/daily?appid=727aa6438ef7127bfc8650be4d1ecb2d&mode=json&cnt=8")
    Observable<Forecast> getForecastWeather(@Query("q") String city);

    @GET("/data/2.5/weather?appid=727aa6438ef7127bfc8650be4d1ecb2d")
    Observable<CurrentByCity> getCurrentWeatherByCity(@Query("q") String city);
}
