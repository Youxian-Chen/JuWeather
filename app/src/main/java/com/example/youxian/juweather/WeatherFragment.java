package com.example.youxian.juweather;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.youxian.juweather.model.CityWeather;
import com.example.youxian.juweather.model.CurrentByCity;
import com.example.youxian.juweather.model.Forecast;
import com.example.youxian.juweather.model.ForecastList;
import com.example.youxian.juweather.model.Weather;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.List;
import java.util.Locale;


/**
 * Created by Youxian on 12/15/15.
 */
public class WeatherFragment extends Fragment {
    private static final String TAG = WeatherFragment.class.toString();

    private ScrollView mScrollView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //current weather
    private TextView mUpdateText;
    private TextView mCityText;
    private ImageView mWeatherIcon;
    private TextView mDescriptionText;
    private TextView mHumidityText;
    private TextView mPressureText;
    private TextView mTemperatureText;
    private TextView mWeekdayText;
    private TextView mTemperatureMaxText;
    private TextView mTemperatureMinText;

    //forecast weather
    private ListView mListView;
    private Forecast mForecast;
    private List<ForecastList> mForecastList;
    private ForecastWeatherAdapter mAdapter;

    private WeatherManager mWeatherManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeatherManager = new WeatherManager((MainActivity)getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScrollView = (ScrollView) view.findViewById(R.id.scrollview_weather);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_weather);
        mSwipeRefreshLayout.setColorSchemeColors(R.color.blue50, R.color.orange50, R.color.redA2);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWeatherManager.fetchLocalWeatherData();
            }
        });


        //current
        mUpdateText = (TextView) view.findViewById(R.id.update_text_weather);
        mCityText = (TextView) view.findViewById(R.id.city_text_weather);
        mWeatherIcon = (ImageView) view.findViewById(R.id.icon_image_weather);
        mDescriptionText = (TextView) view.findViewById(R.id.description_text_weather);
        mHumidityText = (TextView) view.findViewById(R.id.humidity_text_weather);
        mPressureText = (TextView) view.findViewById(R.id.pressure_text_weather);
        mTemperatureText = (TextView) view.findViewById(R.id.temperature_text_weather);
        mWeekdayText = (TextView) view.findViewById(R.id.weekday_text_weather);
        mTemperatureMaxText = (TextView) view.findViewById(R.id.maxTemp_text_weather);
        mTemperatureMinText = (TextView) view.findViewById(R.id.minTemp_text_weather);

        //forecast
        mListView = (ListView) view.findViewById(R.id.list_weather);
        mListView.setDivider(null);

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

    private void updateWeather(CityWeather cityWeather) {
        Log.d(TAG, "updateWeather");
        Log.d(TAG, "updateWeather: " + cityWeather.main.temp);
        Log.d(TAG, "description: " + cityWeather.weathers[0].description);
        String dateFormat = "dd/MM/yyyy hh:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        mUpdateText.setText("Update At: " + formatter.format(calendar.getTime()));
        //update image
        setWeatherIcon(mWeatherIcon, cityWeather.weathers[0].description);

        mCityText.setText(cityWeather.name + ", " + mForecast.city.country);
        mDescriptionText.setText(cityWeather.weathers[0].description);
        mHumidityText.setText("Humidity: " + cityWeather.main.humidity + " %");
        mPressureText.setText("Pressure: " + cityWeather.main.pressure + " hPa");

        String weekdayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);
        mWeekdayText.setText(weekdayName);

        double temp = Double.parseDouble(cityWeather.main.temp) - 273.15;
        DecimalFormat tempFormat = new DecimalFormat("#.0");
        mTemperatureText.setText(tempFormat.format(temp) + " ℃");

        temp = Double.parseDouble(cityWeather.main.temp_max) - 273.15;
        mTemperatureMaxText.setText(tempFormat.format(temp) + " ℃");

        temp = Double.parseDouble(cityWeather.main.temp_min) - 273.15;
        mTemperatureMinText.setText(tempFormat.format(temp) + " ℃");
        scrollViewToTop();
    }

    private void updateForecastList() {
        mAdapter = new ForecastWeatherAdapter();
        mListView.setAdapter(mAdapter);
        scrollViewToTop();
    }

    private void updateWeatherByCity(CurrentByCity currentByCity) {
        Log.d(TAG, "updateWeatherByCity");
        Log.d(TAG, "updateWeatherByCity: " + currentByCity.main.temp);
        Log.d(TAG, "description: " + currentByCity.weathers[0].description);
        String dateFormat = "dd/MM/yyyy hh:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        mUpdateText.setText("Update At: " + formatter.format(calendar.getTime()));
        //update image
        setWeatherIcon(mWeatherIcon, currentByCity.weathers[0].description);

        mCityText.setText(currentByCity.name + ", " + currentByCity.system.country);
        mDescriptionText.setText(currentByCity.weathers[0].description);
        mHumidityText.setText("Humidity: " + currentByCity.main.humidity + " %");
        mPressureText.setText("Pressure: " + currentByCity.main.pressure + " hPa");

        String weekdayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);
        mWeekdayText.setText(weekdayName);

        double temp = Double.parseDouble(currentByCity.main.temp) - 273.15;
        DecimalFormat tempFormat = new DecimalFormat("#.0");
        mTemperatureText.setText(tempFormat.format(temp) + " ℃");

        temp = Double.parseDouble(currentByCity.main.temp_max) - 273.15;
        mTemperatureMaxText.setText(tempFormat.format(temp) + " ℃");

        temp = Double.parseDouble(currentByCity.main.temp_min) - 273.15;
        mTemperatureMinText.setText(tempFormat.format(temp) + " ℃");
        scrollViewToTop();
    }

    public void setCityWeather(CityWeather cityWeather) {
        Log.d(TAG, "setCityWeather");
        updateWeather(cityWeather);
    }

    public void setForecast(Forecast forecast) {
        Log.d(TAG, "setForecast");
        mForecast = forecast;
        mForecastList = new ArrayList<>();
        mForecastList.addAll(forecast.list);
        mForecastList.remove(0);
        updateForecastList();
    }

    public void setCurrentByCity(CurrentByCity currentByCity) {
        Log.d(TAG, "setCurrentByCity");
        updateWeatherByCity(currentByCity);
    }

    public void setRefreshing(boolean refreshing) {
        mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    private void scrollViewToTop() {
        mScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        }, 20);
    }

    private String getWeekdayFromString(String date) {
        long time = Long.parseLong(date) * 1000;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);
    }

    private class ForecastWeatherAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mForecastList.size();
        }

        @Override
        public Object getItem(int position) {
            return mForecastList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ForecastList forecastList = mForecastList.get(position);
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.weather_list_item, null);
                ViewHolder tag = new ViewHolder();
                tag.weekday = (TextView) convertView.findViewById(R.id.weekday_list_item);
                tag.icon = (ImageView) convertView.findViewById(R.id.icon_list_item);
                tag.description = (TextView) convertView.findViewById(R.id.description_list_item);
                tag.maxTemp = (TextView) convertView.findViewById(R.id.max_temp_list_item);
                tag.minTemp = (TextView) convertView.findViewById(R.id.min_temp_list_item);
                convertView.setTag(tag);
            }

            ViewHolder tag = (ViewHolder) convertView.getTag();
            Weather[] weathers = forecastList.weathers;

            if (forecastList != null) {
                tag.weekday.setText(getWeekdayFromString(forecastList.timestamp));
                setWeatherIcon(tag.icon, weathers[0].description);
                tag.description.setText(weathers[0].description);
                double temp = Double.parseDouble(forecastList.temp.max) - 273.15;
                DecimalFormat tempFormat = new DecimalFormat("#.0");
                tag.maxTemp.setText(tempFormat.format(temp) + " ℃");
                temp = Double.parseDouble(forecastList.temp.min) - 273.15;
                tag.minTemp.setText(tempFormat.format(temp) + " ℃");
            }
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView weekday;
        ImageView icon;
        TextView description;
        TextView maxTemp;
        TextView minTemp;
    }
}
