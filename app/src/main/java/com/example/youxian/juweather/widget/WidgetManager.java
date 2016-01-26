package com.example.youxian.juweather.widget;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.example.youxian.juweather.LocationService;
import com.example.youxian.juweather.weather.WeatherService;
import com.example.youxian.juweather.model.CityWeather;
import com.example.youxian.juweather.model.Current;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Youxian on 1/3/16.
 */
public class WidgetManager {

    private static final String TAG = WidgetManager.class.toString();
    private static final int maxResults = 1;
    private Context mContext;
    private LocationService mLocationService;
    private WeatherService mWeatherService;
    private CityWeather mCityWeather;
    private Location mLocation;

    public WidgetManager(Context context) {
        mContext = context;
        mLocationService = new LocationService(mContext);
        mWeatherService = new WeatherService();
    }

    public Observable<CityWeather> fetchWeatherDataForWidget() {
        mLocationService = new LocationService(mContext);
        mWeatherService = new WeatherService();
        return mLocationService.getLocation()
                .flatMap(new Func1<Location, Observable<Current>>() {
                    @Override
                    public Observable<Current> call(Location location) {
                        mLocation = location;
                        return mWeatherService.getCurrentWeather(String.valueOf(location.getLatitude()),
                                String.valueOf(location.getLongitude()));
                    }
                })
                .flatMap(new Func1<Current, Observable<CityWeather>>() {
                    @Override
                    public Observable<CityWeather> call(Current current) {
                        String cityName = getCityFromLocationForWidget(mLocation);
                        for (CityWeather cityWeather : current.list) {
                            if (cityName.contains(cityWeather.name)) {
                                Log.d(TAG, "find match: " + cityWeather.name);
                                //displayCurrent(cityWeather);
                                mCityWeather = cityWeather;
                                return Observable.just(cityWeather);
                            }
                        }
                        //not find match, display one in the list
                        mCityWeather = current.list[1];
                        return Observable.just(mCityWeather);
                    }
                });

    }


    private String getCityFromLocationForWidget(Location location) {
        Geocoder geocoder = new Geocoder(mContext, Locale.ENGLISH);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), maxResults);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null) {
            if (addresses.get(0) != null) {
                return addresses.get(0).getAdminArea();
            }
        }
        return null;
    }
}
