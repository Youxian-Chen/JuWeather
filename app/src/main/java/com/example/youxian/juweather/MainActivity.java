package com.example.youxian.juweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.youxian.juweather.weather.CurrentWeather;
import com.example.youxian.juweather.weather.ForecastWeather;

import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.toString();
    private static final String TAG_FRAGMENT = "tag_fragment";

    public static final String LOCAL_CURRENT_WEATHER = "local_current_weather";
    public static final String LOCAL_FORECAST_WEATHER = "local_forecast_weather";

    private WeatherFragment mWeatherFragment;
    private SearchView mSearchView;
    private CoordinatorLayout mCoordinatorLayout;

    private IntentFilter mWeatherIntentFilter;
    private IntentFilter mLocationIntentFilter;

    private CurrentWeather mCurrentWeather;
    private ForecastWeather mForecastWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mWeatherIntentFilter = new IntentFilter();
        mWeatherIntentFilter.addAction(LOCAL_CURRENT_WEATHER);
        mWeatherIntentFilter.addAction(LOCAL_FORECAST_WEATHER);

        mLocationIntentFilter = new IntentFilter();
        mLocationIntentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            startWeatherService();
        }

        initView();
    }

    private void getLocalWeather() {
        LocationService locationService = new LocationService(this);
        locationService.getLocation()
                .flatMap(new Func1<String, Observable<Void>>() {

                    WeatherService weatherService = new WeatherService();
                    @Override
                    public Observable<Void> call(String s) {
                        return Observable.zip(
                                weatherService.getCurrentWeather(s),
                                weatherService.getForecastWeather(s),

                                new Func2<CurrentWeather, ForecastWeather, Void>() {
                                    @Override
                                    public Void call(CurrentWeather currentWeather, ForecastWeather forecastWeather) {
                                        mCurrentWeather = currentWeather;
                                        mForecastWeather = forecastWeather;
                                        return null;
                                    }
                                }
                        );
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Void aVoid) {
                        if (mCurrentWeather != null && mForecastWeather != null) {
                            Log.d(TAG, mCurrentWeather.getName());
                            Log.d(TAG, mForecastWeather.getCity().getCountry());
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mWeatherReceiver, mWeatherIntentFilter);
        registerReceiver(mLocationReceiver, mLocationIntentFilter);
        getLocalWeather();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mWeatherReceiver);
        unregisterReceiver(mLocationReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setQueryHint(getString(R.string.search_city));
        // Configure the search info and add any event listeners...
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "query submit");
                mSearchView.clearFocus();
                searchWeather(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "query change");
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Log.d(TAG, "search selected");
                return true;

            case R.id.action_settings:

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void searchWeather(String city) {
        Intent getSearchWeatherIntent = new Intent(MainActivity.this, GetWeatherService.class);
        getSearchWeatherIntent.setAction(GetWeatherService.GET_SEARCH_WEATHER);
        getSearchWeatherIntent.putExtra(GetWeatherService.GET_SEARCH_WEATHER, city);
        startService(getSearchWeatherIntent);
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void initView() {
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout_main);
        replaceFragment(getWeatherFragment(), false);
    }

    public void startWeatherService() {
        Intent getWeatherIntent = new Intent(MainActivity.this, GetWeatherService.class);
        getWeatherIntent.setAction(GetWeatherService.GET_LOCAL_WEATHER);
        startService(getWeatherIntent);
    }

    public void showUpdateInfo() {
        String updateString = "Update Successfully !!";
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, updateString, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void replaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_weather, fragment, TAG_FRAGMENT);
        if (addToBackStack)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    private WeatherFragment getWeatherFragment() {
        if (mWeatherFragment == null) {
            mWeatherFragment = new WeatherFragment();
        }
        return mWeatherFragment;
    }

    private BroadcastReceiver mWeatherReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, action);
            if (LOCAL_CURRENT_WEATHER.equals(action)) {
                if (mWeatherFragment != null) {
                    CurrentWeather currentWeather = (CurrentWeather) intent
                            .getSerializableExtra(LOCAL_CURRENT_WEATHER);
                    if (currentWeather != null)
                        mWeatherFragment.setCurrentWeather(currentWeather);
                    else Log.d(TAG, "current weather null");
                }
            } else if (LOCAL_FORECAST_WEATHER.equals(action)) {
                if (mWeatherFragment != null) {
                    ForecastWeather forecastWeather = (ForecastWeather) intent
                            .getSerializableExtra(LOCAL_FORECAST_WEATHER);
                    if (forecastWeather != null) {
                        mWeatherFragment.setForecastWeather(forecastWeather);
                    } else {
                        Log.d(TAG, "forecast weather null");
                    }
                }
            }
        }
    };

    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, action);
            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(action)) {
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.d(TAG, "GPS enabled");
                    startWeatherService();
                }
            }
        }
    };
}
