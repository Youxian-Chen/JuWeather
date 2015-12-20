package com.example.youxian.juweather;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.youxian.juweather.weather.CurrentWeather;
import com.example.youxian.juweather.weather.ForecastWeather;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by Youxian on 12/15/15.
 */
public class WeatherFragment extends Fragment {
    private static final String TAG = WeatherFragment.class.toString();

    //current weather
    private TextView mUpdateText;
    private TextView mCityText;
    private ImageView mWeatherIcon;
    private TextView mDescriptionText;
    private TextView mHumidityText;
    private TextView mPressureText;
    private TextView mTemperatureText;

    //forecast weather
    private ListView mListView;
    private List<ForecastWeather.List> mForecastList;
    private ForecastWeatherAdapter mAdapter;

    private CurrentWeather mCurrentWeather;
    private ForecastWeather mForecastWeather;

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
        //current
        mUpdateText = (TextView) view.findViewById(R.id.update_text_weather);
        mCityText = (TextView) view.findViewById(R.id.city_text_weather);
        mWeatherIcon = (ImageView) view.findViewById(R.id.icon_image_weather);
        mDescriptionText = (TextView) view.findViewById(R.id.description_text_weather);
        mHumidityText = (TextView) view.findViewById(R.id.humidity_text_weather);
        mPressureText = (TextView) view.findViewById(R.id.pressure_text_weather);
        mTemperatureText = (TextView) view.findViewById(R.id.temperature_text_weather);

        //forecast
        mListView = (ListView) view.findViewById(R.id.list_weather);

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

        double temp = Double.parseDouble(mCurrentWeather.getMain().getTemp()) - 273.15;
        DecimalFormat tempFormat = new DecimalFormat("#.00");
        mTemperatureText.setText(tempFormat.format(temp) + " ℃");
    }

    public void setForecastWeather(ForecastWeather forecastWeather) {
        mForecastWeather = forecastWeather;
        updateForecastList();
    }

    private void updateForecastList() {
        mForecastList = new ArrayList<>();
        Collections.addAll(mForecastList, mForecastWeather.getList());
        mAdapter = new ForecastWeatherAdapter();
        mListView.setAdapter(mAdapter);
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
                tag.icon = (ImageView) convertView.findViewById(R.id.icon_list_item);
                tag.description = (TextView) convertView.findViewById(R.id.description_list_item);
                tag.maxTemp = (TextView) convertView.findViewById(R.id.max_temp_list_item);
                tag.minTemp = (TextView) convertView.findViewById(R.id.min_temp_list_item);
                convertView.setTag(tag);
            }

            ViewHolder tag = (ViewHolder) convertView.getTag();

            if (forecastList != null) {
                ForecastWeather.Weather[] weathers = forecastList.getWeather();
                setWeatherIcon(tag.icon, weathers[0].getMain());
                tag.description.setText(weathers[0].getDescription());
                tag.maxTemp.setText(forecastList.getMain().getTemp_max());
                tag.minTemp.setText(forecastList.getMain().getTemp_min());
            }
            return convertView;
        }
    }

    private static class ViewHolder {
        ImageView icon;
        TextView description;
        TextView maxTemp;
        TextView minTemp;
    }
}
