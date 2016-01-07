package com.example.youxian.juweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by Youxian on 1/1/16.
 */
public class LocationService {
    private static final String TAG = LocationService.class.toString();
    private static final String LAST_LOCATION = "last_location";
    private static final String LAST_LOCATION_LAT = "last_location_lat";
    private static final String LAST_LOCATION_LON = "last_location_lon";
    private LocationManager mLocationManager;
    private Context mContext;
    public LocationService(Context context) {
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public Observable<Location> getLocation() {
        return Observable.create(new Observable.OnSubscribe<Location>() {
            @Override
            public void call(Subscriber<? super Location> subscriber) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                String provider = mLocationManager.getBestProvider(criteria, false);
                Log.i(TAG, "provider: " + provider);
                Location location = getLastLocation(provider);
                //Location location = mLocationManager.getLastKnownLocation(provider);
                Log.d(TAG, "location: " + location.getLatitude());
                subscriber.onNext(location);
            }
        });
    }

    private Location getLastLocation(String provider) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(LAST_LOCATION, Context.MODE_PRIVATE);
        Location location = mLocationManager.getLastKnownLocation(provider);
        if (location == null) {
            Log.d(TAG, "location null");
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.d(TAG, "gps on");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return  getLastLocation(provider);
            } else {
                String lat = sharedPreferences.getString(LAST_LOCATION_LAT, null);
                String lon = sharedPreferences.getString(LAST_LOCATION_LON, null);
                Log.d(TAG, "lat: " + lat + " @ lon: " + lon);
                if (lat != null && lon != null) {
                    Location lastLocation = new Location(LocationManager.GPS_PROVIDER);
                    lastLocation.setLatitude(Double.parseDouble(lat));
                    lastLocation.setLongitude(Double.parseDouble(lon));
                    return lastLocation;
                }
            }
        } else {
            Log.d(TAG, "lat: " + location.getLatitude() + " @ lon: " + location.getLongitude());
            sharedPreferences.edit().putString(LAST_LOCATION_LAT, String.valueOf(location.getLatitude())).apply();
            sharedPreferences.edit().putString(LAST_LOCATION_LON, String.valueOf(location.getLongitude())).apply();
            return location;
        }
        return null;
    }

}
