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

import com.example.youxian.juweather.weather.CurrentWeather;
import com.example.youxian.juweather.weather.ForecastWeather;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
    private List<ForecastWeather.List> mForecastList;
    private ForecastWeatherAdapter mAdapter;

    private CurrentWeather mCurrentWeather;

    private boolean isCurrentWeatherUpdated = false;
    private boolean isForecastWeatherUpdated = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                refreshWeatherData();
            }
        });
        mSwipeRefreshLayout.setEnabled(false);

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



    }

    private void refreshWeatherData() {
        if (!isCurrentWeatherUpdated && !isForecastWeatherUpdated) {
            Log.d(TAG, "go refresh");
            ((MainActivity) getActivity()).startWeatherService();
        } else if (isCurrentWeatherUpdated && isForecastWeatherUpdated) {
            Log.d(TAG, "stop refresh");
            mSwipeRefreshLayout.setRefreshing(false);
            isCurrentWeatherUpdated = false;
            isForecastWeatherUpdated = false;
            ((MainActivity) getActivity()).showUpdateInfo();
        } else {
            Log.d(TAG, "Do nothing");
        }
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
        } else if (description.contains("mist")) {
            icon.setImageResource(R.drawable.mist);
        }
    }

    public void setCurrentWeather(CurrentWeather currentWeather) {
        mCurrentWeather = currentWeather;
        updateWeather();
    }

    private void updateWeather() {
        String dateFormat = "dd/MM/yyyy hh:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        mUpdateText.setText(formatter.format(calendar.getTime()));
        //update image
        setWeatherIcon(mWeatherIcon, mCurrentWeather.getWeather(0).getDescription());

        mCityText.setText(mCurrentWeather.getName() + ", " + mCurrentWeather.getSys().getCountry());
        mDescriptionText.setText(mCurrentWeather.getWeather(0).getDescription());
        mHumidityText.setText("Humidity: " + mCurrentWeather.getMain().getHumidity() + " %");
        mPressureText.setText("Pressure: " + mCurrentWeather.getMain().getPressure() + " hPa");

        String weekdayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);
        mWeekdayText.setText(weekdayName);

        double temp = Double.parseDouble(mCurrentWeather.getMain().getTemp()) - 273.15;
        DecimalFormat tempFormat = new DecimalFormat("#.0");
        mTemperatureText.setText(tempFormat.format(temp) + " ℃");

        temp = Double.parseDouble(mCurrentWeather.getMain().getTemp_max()) - 273.15;
        mTemperatureMaxText.setText(tempFormat.format(temp) + " ℃");

        temp = Double.parseDouble(mCurrentWeather.getMain().getTemp_min()) - 273.15;
        mTemperatureMinText.setText(tempFormat.format(temp) + " ℃");
        scrollViewToTop();
        isCurrentWeatherUpdated = true;
        refreshWeatherData();
    }

    public void setForecastWeather(ForecastWeather forecastWeather) {
        //mForecastWeather = forecastWeather;
        mForecastList = new ArrayList<>();
        /*
        ForecastWeather.List[] lists = mForecastWeather.getList();
        for (ForecastWeather.List list: lists) {
            if (list.getDt_txt().contains("09:00:00")) {
                Log.d(TAG, "get date");
                mForecastList.add(list);
            }
        }
        */
        Collections.addAll(mForecastList, forecastWeather.getList());
        mForecastList.remove(0);
        //mForecastList.remove(0);
        //mForecastList.remove(2);
        updateForecastList();
        refreshWeatherData();
    }

    private void updateForecastList() {
        mAdapter = new ForecastWeatherAdapter();
        mListView.setAdapter(mAdapter);
        scrollViewToTop();
        isForecastWeatherUpdated = true;
        mSwipeRefreshLayout.setEnabled(true);
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
        /*
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date mDate = null;
        try {
            mDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(mDate);
        return c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);
        */
        //Log.d(TAG, date);
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
            ForecastWeather.List forecastList = mForecastList.get(position);
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
            ForecastWeather.Weather[] weathers = forecastList.getWeather();

            if (forecastList != null) {
                tag.weekday.setText(getWeekdayFromString(forecastList.getTimeStamp()));
                setWeatherIcon(tag.icon, weathers[0].getDescription());
                tag.description.setText(weathers[0].getDescription());
                double temp = Double.parseDouble(forecastList.getTemp().getMax()) - 273.15;
                DecimalFormat tempFormat = new DecimalFormat("#.0");
                tag.maxTemp.setText(tempFormat.format(temp) + " ℃");
                temp = Double.parseDouble(forecastList.getTemp().getMin()) - 273.15;
                tag.minTemp.setText(tempFormat.format(temp) + " ℃");
            }
            //ImageView icon = (ImageView) convertView.findViewById(R.id.icon_list_item);

            //Log.d(TAG, weathers[0].getDescription());
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
