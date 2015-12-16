package com.example.youxian.juweather;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.youxian.juweather.weather.CurrentWeather;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Youxian on 12/15/15.
 */
public class WeatherFragment extends Fragment {
    private static final String TAG = WeatherFragment.class.toString();

    private TextView mUpdateText;
    private TextView mCityText;
    private ImageView mWeatherIcon;
    private TextView mDescriptionText;
    private TextView mHumidityText;
    private TextView mPressureText;
    private TextView mTemperatureText;

    private CurrentWeather mCurrentWeather;

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
        mUpdateText = (TextView) view.findViewById(R.id.update_text_weather);
        mCityText = (TextView) view.findViewById(R.id.city_text_weather);
        mWeatherIcon = (ImageView) view.findViewById(R.id.icon_image_weather);
        mDescriptionText = (TextView) view.findViewById(R.id.description_text_weather);
        mHumidityText = (TextView) view.findViewById(R.id.humidity_text_weather);
        mPressureText = (TextView) view.findViewById(R.id.pressure_text_weather);
        mTemperatureText = (TextView) view.findViewById(R.id.temperature_text_weather);

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
        String description = mCurrentWeather.getWeather(0).getDescription();
        if (description.contains("clear")) {
            mWeatherIcon.setImageResource(R.drawable.clear);
        } else if (description.contains("rain")) {
            mWeatherIcon.setImageResource(R.drawable.rain);
        } else if (description.contains("cloud")) {
            mWeatherIcon.setImageResource(R.drawable.cloud);
        } else if (description.contains("snow")) {
            mWeatherIcon.setImageResource(R.drawable.snow);
        } else if (description.contains("mist")) {
            mWeatherIcon.setImageResource(R.drawable.mist);
        }

        mCityText.setText(mCurrentWeather.getName() + ", " + mCurrentWeather.getSys().getCountry());
        mDescriptionText.setText(mCurrentWeather.getWeather(0).getDescription());
        mHumidityText.setText("Humidity: " + mCurrentWeather.getMain().getHumidity() + " %");
        mPressureText.setText("Pressure: " + mCurrentWeather.getMain().getPressure() + " hPa");

        double temp = Double.parseDouble(mCurrentWeather.getMain().getTemp()) - 273.15;
        DecimalFormat tempFormat = new DecimalFormat("#.00");
        mTemperatureText.setText(tempFormat.format(temp) + " ℃");
    }
}
