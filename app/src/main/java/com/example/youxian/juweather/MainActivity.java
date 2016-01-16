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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.youxian.juweather.model.CityWeather;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.toString();
    private static final String TAG_FRAGMENT = "tag_fragment";

    private WeatherFragment mWeatherFragment;
    private SearchView mSearchView;
    private CoordinatorLayout mCoordinatorLayout;
    private ProgressBar mProgressBar;
    private RelativeLayout mRelativeLayout;
    private RecyclerView mRecyclerView;
    private IntentFilter mLocationIntentFilter;

    private WeatherManager mWeatherManager;
    private CityAdapter mCityAdapter;
    private List<CityWeather> mCityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mLocationIntentFilter = new IntentFilter();
        mLocationIntentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        mWeatherManager = new WeatherManager(this);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        mWeatherManager.checkLocationState();
        registerReceiver(mLocationReceiver, mLocationIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        mWeatherManager.fetchSearchWeatherData(Observable.just(city));
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Need GPS enabled to get your location, please open it!!")
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
        mProgressBar = (ProgressBar) findViewById(R.id.load_bar_main);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.container_weather);
        mRecyclerView = (RecyclerView) findViewById(R.id.city_list_main);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mCityList = new ArrayList<>();
        mCityAdapter = new CityAdapter();
        mRecyclerView.setAdapter(mCityAdapter);
        replaceFragment(getWeatherFragment(), false);
        mRelativeLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
    }

    public void showUpdateInfo(String info) {
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, info, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void replaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_weather, fragment, TAG_FRAGMENT);
        if (addToBackStack)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    public WeatherFragment getWeatherFragment() {
        if (mWeatherFragment == null) {
            mWeatherFragment = new WeatherFragment();
        }
        return mWeatherFragment;
    }

    public void showCityList(CityWeather[] cityWeathers) {
        mRecyclerView.setVisibility(View.VISIBLE);
        mCityList.clear();
        Collections.addAll(mCityList, cityWeathers);
        mCityAdapter.notifyDataSetChanged();
        String info = "Select the city you live in, Please !!";
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, info, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    public void hideCityList() {
        mRecyclerView.setVisibility(View.GONE);
    }

    public void showWeatherFragment() {
        mRelativeLayout.setVisibility(View.VISIBLE);
    }

    public void hideWeatherFragment() {
        mRelativeLayout.setVisibility(View.GONE);
    }

    public void hideLoadingBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
        boolean isGpsOn = false;
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, action);
            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(action)) {
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !isGpsOn) {
                    Log.d(TAG, "GPS enabled");
                    mWeatherManager.fetchLocalWeatherData();
                    isGpsOn = true;
                }
            }
        }
    };

    private class CityAdapter extends RecyclerView.Adapter<CityViewHolder> {

        @Override
        public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_list_item, parent, false);
            return new CityViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CityViewHolder holder, int position) {
            CityWeather cityWeather = mCityList.get(position);
            holder.name.setText(cityWeather.name);
            double temp = Double.parseDouble(cityWeather.main.temp) - 273.15;
            DecimalFormat tempFormat = new DecimalFormat("#.0");
            String stateString = tempFormat.format(temp) + " ℃";
            holder.temp.setText(stateString);
            holder.description.setText(cityWeather.weathers[0].description);
            setWeatherIcon(holder.icon, cityWeather.weathers[0].description);
        }

        @Override
        public int getItemCount() {
            return mCityList.size();
        }

        private void setWeatherIcon(ImageView icon, String description) {
            if (description.contains("clear")) {
                icon.setImageResource(R.drawable.clear);
            } else if (description.contains("rain")) {
                icon.setImageResource(R.drawable.rain);
            } else if (description.contains("cloud")) {
                icon.setImageResource(R.drawable.cloud);
            } else if (description.contains("snow")) {
                icon.setImageResource(R.drawable.snow);
            } else if (description.contains("mist") || description.contains("haze")) {
                icon.setImageResource(R.drawable.mist);
            }
        }
    }

    private class CityViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView name;
        TextView temp;
        TextView description;

        public CityViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.city_item_icon);
            name = (TextView) itemView.findViewById(R.id.city_item_name);
            temp = (TextView) itemView.findViewById(R.id.city_item_temp);
            description = (TextView) itemView.findViewById(R.id.city_item_description);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeatherManager.storeCityName(mCityList.get(getAdapterPosition()).name);
                }
            });
        }
    }
}
