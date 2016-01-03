package com.example.youxian.juweather.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.youxian.juweather.LocationService;
import com.example.youxian.juweather.R;
import com.example.youxian.juweather.WeatherService;
import com.example.youxian.juweather.model.CityWeather;
import com.example.youxian.juweather.model.Current;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by Youxian on 1/3/16.
 */
public class UpdateWidgetService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private static final String TAG = UpdateWidgetService.class.toString();
    private static final int maxResults = 1;

    private LocationService mLocationService;
    private WeatherService mWeatherService;
    private Location mLocation;
    private CityWeather mCityWeather;
    private int[] allWidgetIds;

    public UpdateWidgetService() {
        super("UpdateWidgetService");
    }

    public UpdateWidgetService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        getLocalWeatherData();
    }

    private void getLocalWeatherData() {
        mLocationService = new LocationService(this);
        mWeatherService = new WeatherService();
        mLocationService.getLocation()
                .flatMap(new Func1<Location, Observable<Current>>() {
                    @Override
                    public Observable<Current> call(Location location) {
                        mLocation = location;
                        return mWeatherService.getCurrentWeather(String.valueOf(location.getLatitude()),
                                String.valueOf(location.getLongitude()));
                    }
                })
                .map(new Func1<Current, CityWeather>() {
                    @Override
                    public CityWeather call(Current current) {
                        String cityName = getCityFromLocation(mLocation);
                        for (CityWeather cityWeather: current.list) {
                            if (cityName.contains(cityWeather.name)) {
                                Log.d(TAG, "find match: " + cityWeather.name);
                                //displayCurrent(cityWeather);
                                mCityWeather = cityWeather;
                                return cityWeather;
                            }
                        }
                        //not find match, display one in the list
                        mCityWeather = current.list[1];
                        return mCityWeather;
                    }
                })
                .subscribe(new Subscriber<CityWeather>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CityWeather cityWeather) {
                        updateWeatherData(cityWeather);
                    }
                });
    }

    private String getCityFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
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

    private void updateWeatherData(CityWeather cityWeather) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        for (int id: allWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(),
                    R.layout.widget_weather);

            remoteViews.setTextViewText(R.id.location_text_widget, cityWeather.name);

            String description = cityWeather.weathers[0].description;
            if (description.contains("clear")) {
                remoteViews.setImageViewResource(R.id.icon_image_widget, R.drawable.clear);
            } else if (description.contains("rain")) {
                remoteViews.setImageViewResource(R.id.icon_image_widget, R.drawable.rain);
            } else if (description.contains("cloud")) {
                remoteViews.setImageViewResource(R.id.icon_image_widget, R.drawable.cloud);
            } else if (description.contains("snow")) {
                remoteViews.setImageViewResource(R.id.icon_image_widget, R.drawable.snow);
            } else if (description.contains("mist") || description.contains("haze")) {
                remoteViews.setImageViewResource(R.id.icon_image_widget, R.drawable.mist);
            }

            double temp = Double.parseDouble(cityWeather.main.temp) - 273.15;
            DecimalFormat tempFormat = new DecimalFormat("#.0");
            remoteViews.setTextViewText(R.id.temperature_text_widget, tempFormat.format(temp) + " ℃");

            remoteViews.setTextViewText(R.id.description_text_widget, description);

            // Register an onClickListener
            Intent clickIntent = new Intent(this.getApplicationContext(),
                    UpdateWidgetService.class);

            clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    allWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.layout_widget, pendingIntent);
            appWidgetManager.updateAppWidget(id, remoteViews);
        }
        stopSelf();
    }
}
