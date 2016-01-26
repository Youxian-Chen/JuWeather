package com.example.youxian.juweather.weather;

import com.example.youxian.juweather.model.CurrentByCity;
import com.example.youxian.juweather.model.Current;
import com.example.youxian.juweather.model.Forecast;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;

/**
 * Created by Youxian on 1/1/16.
 */
public class WeatherService {
    private static final String TAG = WeatherService.class.toString();

    private static final String WEB_SERVICE_BASE_URL = "http://api.openweathermap.org";
    private WeatherApi mWeatherApi;
    public WeatherService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WEB_SERVICE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        mWeatherApi = retrofit.create(WeatherApi.class);

    }

    public Observable<Current> getCurrentWeather(String lat, String lon) {
        return mWeatherApi.getCurrentWeather(lat, lon);
    }

    public Observable<Forecast> getForecastWeather(String cityName) {
        return mWeatherApi.getForecastWeather(cityName);
    }

    public Observable<CurrentByCity> getCurrentWeatherByCity(String cityName) {
        return mWeatherApi.getCurrentWeatherByCity(cityName);
    }
}
