package com.example.youxian.juweather;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.example.youxian.juweather.model.CityWeather;
import com.example.youxian.juweather.model.Current;
import com.example.youxian.juweather.model.CurrentByCity;
import com.example.youxian.juweather.model.Forecast;
import com.example.youxian.juweather.model.WeatherBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by Youxian on 1/2/16.
 */
public class WeatherManager {
    private static final String TAG = WeatherManager.class.toString();
    private static final int maxResults = 1;
    private static final String UPDATED_SUCCESS = "Update Weather Data Successfully !!";
    private static final String UPDATED_FAILED = "Can't fetch Weather Data, Check your GPS or network !!";
    private MainActivity mMainView;
    private LocationService mLocationService;
    private WeatherService mWeatherService;

    private CityWeather mCityWeather;
    private Location mLocation;

    private List<WeatherBase> mSearchWeatherData;

    public WeatherManager(MainActivity mainActivity) {
        mMainView = mainActivity;
        mLocationService = new LocationService(mMainView);
        mWeatherService = new WeatherService();
    }

    public void fetchSearchWeatherData(Observable<String> city) {
        city.flatMap(new Func1<String, Observable<List<WeatherBase>>>() {
            @Override
            public Observable<List<WeatherBase>> call(String s) {
                return Observable.zip(mWeatherService.getCurrentWeatherByCity(s),
                        mWeatherService.getForecastWeather(s),
                        new Func2<CurrentByCity, Forecast, List<WeatherBase>>() {
                            @Override
                            public List<WeatherBase> call(CurrentByCity currentByCity, Forecast forecast) {
                                mSearchWeatherData = new ArrayList<WeatherBase>();
                                mSearchWeatherData.add(currentByCity);
                                mSearchWeatherData.add(forecast);
                                return mSearchWeatherData;
                            }
                        });
            }
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<List<WeatherBase>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<WeatherBase> weatherBases) {
                displayCurrentByCity((CurrentByCity) weatherBases.get(0));
                displayForecast((Forecast) weatherBases.get(1));
                mMainView.getWeatherFragment().setRefreshing(false);
                mMainView.showUpdateInfo(UPDATED_SUCCESS);
            }
        });
    }

    public void fetchLocalWeatherData() {
        mLocationService.getLocation()
                .flatMap(new Func1<Location, Observable<Current>>() {
                    @Override
                    public Observable<Current> call(Location location) {
                        mLocation = location;
                        return mWeatherService.getCurrentWeather(String.valueOf(location.getLatitude()),
                                String.valueOf(location.getLongitude()));
                    }
                })
                .map(new Func1<Current, String>() {
                    @Override
                    public String call(Current current) {
                        String cityName = getCityFromLocation(mLocation);
                        for (CityWeather cityWeather: current.list) {
                            if (cityName.contains(cityWeather.name)) {
                                Log.d(TAG, "find match: " + cityWeather.name);
                                //displayCurrent(cityWeather);
                                mCityWeather = cityWeather;
                                return cityWeather.name;
                            }
                        }
                        //not find match, display one in the list
                        mCityWeather = current.list[1];
                        return cityName;
                    }
                })
                .flatMap(new Func1<String, Observable<Forecast>>() {
                    @Override
                    public Observable<Forecast> call(String s) {
                        Log.d(TAG, "s: " + s);
                        return mWeatherService.getForecastWeather(s);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Forecast>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mMainView.getWeatherFragment().setRefreshing(false);
                        mMainView.showUpdateInfo(UPDATED_FAILED);
                    }

                    @Override
                    public void onNext(Forecast forecast) {
                        displayForecast(forecast);
                        displayCurrent(mCityWeather);
                        mMainView.getWeatherFragment().setRefreshing(false);
                        mMainView.showUpdateInfo(UPDATED_SUCCESS);
                    }
                });

    }

    private String getCityFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(mMainView, Locale.ENGLISH);
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

    public void displayCurrent(CityWeather cityWeather) {
        Log.d(TAG, "displayCurrent");
        mMainView.getWeatherFragment().setCityWeather(cityWeather);
    }

    public void displayForecast(Forecast forecast) {
        Log.d(TAG, "displayForecast");
        mMainView.getWeatherFragment().setForecast(forecast);
    }

    public void displayCurrentByCity(CurrentByCity currentByCity) {
        mMainView.getWeatherFragment().setCurrentByCity(currentByCity);
    }


}
