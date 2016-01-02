package com.example.youxian.juweather;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
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
        Location location = mLocationManager.getLastKnownLocation(provider);
        if (location == null) {
            return  getLastLocation(provider);
        } else {
            return location;
        }
    }

}
