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
    private static final int maxResults = 1;
    private LocationManager mLocationManager;
    private Context mContext;
    public LocationService(Context context) {
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public Observable<String> getLocationCity() {
        return Observable.create(new Observable.OnSubscribe<Location>() {

            @Override
            public void call(Subscriber<? super Location> subscriber) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                String provider = mLocationManager.getBestProvider(criteria, false);
                Log.i(TAG, "provider: " + provider);
                Location location = mLocationManager.getLastKnownLocation(provider);
                subscriber.onNext(location);
            }
        }).map(new Func1<Location, String>() {
            @Override
            public String call(Location location) {
                Log.d(TAG, "lat: " + location.getLatitude());
                Log.d(TAG, "lon: " + location.getLongitude());
                Geocoder geocoder = new Geocoder(mContext, Locale.ENGLISH);
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), maxResults);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String separatedString[] = addresses.get(0).getAdminArea().split(" ");
                Log.d(TAG, "city at: " + separatedString[0]);
                return separatedString[0];
            }
        });
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
                Location location = mLocationManager.getLastKnownLocation(provider);
                subscriber.onNext(location);
            }
        });
    }

}
