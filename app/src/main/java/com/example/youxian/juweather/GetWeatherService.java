package com.example.youxian.juweather;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.youxian.juweather.weather.CurrentWeather;
import com.example.youxian.juweather.weather.WeatherService;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Youxian on 12/15/15.
 */
public class GetWeatherService extends IntentService implements Callback<CurrentWeather> {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    private static final String TAG = GetWeatherService.class.toString();
    public static final String GET_LOCAL_CURRENT_WEATHER = "get_current_weather";
    private static final String WEB_SERVICE_BASE_URL = "http://api.openweathermap.org";

    private String mCityName;

    private Call<CurrentWeather> mCall;
    private CurrentWeather mCurrentWeather;
    public GetWeatherService(String name) {
        super(name);
    }

    public GetWeatherService() {
        super("GetWeatherService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, action);
        if (GET_LOCAL_CURRENT_WEATHER.equals(action)) {
            getLocation();
            getCurrentWeather();
        }

    }

    @Override
    public void onResponse(Response<CurrentWeather> response, Retrofit retrofit) {
        Log.d(TAG, "onResponse");
        if (response != null) {
            mCurrentWeather = response.body();
            Intent weatherDataIntent = new Intent();
            weatherDataIntent.setAction(MainActivity.LOCAL_CURRENT_WEATHER);
            weatherDataIntent.putExtra(MainActivity.LOCAL_CURRENT_WEATHER, mCurrentWeather);
            LocalBroadcastManager.getInstance(this).sendBroadcast(weatherDataIntent);
            stopSelf();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        Log.d(TAG, "onFailure");
        Log.d(TAG, t.toString());
    }

    private void getCurrentWeather() {
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(WEB_SERVICE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService weatherService = mRetrofit.create(WeatherService.class);
        mCall = weatherService.fetchCurrentWeather(mCityName);
        mCall.enqueue(GetWeatherService.this);
    }

    private void getLocation() {
        Location location = getLastKnownLocation();
        if (location != null) {
            Log.d(TAG, "location not null");
            Geocoder gcd = new Geocoder(this, Locale.ENGLISH);
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String separatedString[];
            if (addresses.size() > 0){
                Log.d(TAG, addresses.get(0).getAdminArea());
                separatedString = addresses.get(0).getAdminArea().split(" ");
                mCityName = separatedString[0];
                Log.d(TAG, mCityName);
            }
        }
    }

    private Location getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location == null) {
                continue;
            }
            if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = location;
            }
        }
        return bestLocation;
    }
}
