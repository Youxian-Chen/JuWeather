package com.example.youxian.juweather;

import com.example.youxian.juweather.weather.CurrentWeather;
import com.example.youxian.juweather.weather.ForecastWeather;
import com.example.youxian.juweather.weather.WeatherApi;

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

    public Observable<CurrentWeather> getCurrentWeather(String cityName) {
        return mWeatherApi.fetchRCurrentWeather(cityName);
    }

    public Observable<ForecastWeather> getForecastWeather(String cityName) {
        return mWeatherApi.fetchRForecastWeather(cityName);
    }
}
