package com.example.youxian.juweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.youxian.juweather.weather.CurrentWeather;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.toString();
    private static final String TAG_FRAGMENT = "tag_fragment";

    public static final String LOCAL_CURRENT_WEATHER = "local_current_weather";

    private WeatherFragment mWeatherFragment;
    private SearchView mSearchView;

    private IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(LOCAL_CURRENT_WEATHER);

        startWeatherService();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mWeatherReceiver, mIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mWeatherReceiver);
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
                //searchWeather(query);
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

    private void startWeatherService() {
        Intent getWeatherIntent = new Intent(MainActivity.this, GetWeatherService.class);
        getWeatherIntent.setAction(GetWeatherService.GET_LOCAL_CURRENT_WEATHER);
        startService(getWeatherIntent);
    }

    private void initView() {
        replaceFragment(getWeatherFragment(), false);
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
            //Bundle bundle = new Bundle();
            //bundle.putString(LOCATION_STRING, mCityString);
            //mWeatherFragment.setArguments(bundle);
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
                    Log.d(TAG, currentWeather.getName());
                    Log.d(TAG, currentWeather.getWeather(0).getMain());
                    if (currentWeather != null)
                        mWeatherFragment.setCurrentWeather(currentWeather);
                }
            }
        }
    };
}
