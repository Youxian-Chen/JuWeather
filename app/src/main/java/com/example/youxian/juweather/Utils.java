package com.example.youxian.juweather;

import android.location.Location;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Youxian on 1/26/16.
 */
public class Utils {


    public static String temperatureFormat(String temp) {
        double tempFormatted = Double.parseDouble(temp) - 273.15;
        DecimalFormat tempFormat = new DecimalFormat("#.0");
        return tempFormat.format(tempFormatted) + " ℃";
    }

    public static String currentFormattedTime() {
        String dateFormat = "dd/MM/yyyy hh:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        return "Update At: " + formatter.format(calendar.getTime());
    }

    public static String weekDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);
    }

    public static boolean isLocationChanged(Location newlocation, String oldLat, String oldLon) {
        double lat = Double.parseDouble(oldLat);
        double lon = Double.parseDouble(oldLon);
        return Math.abs((newlocation.getLatitude() - lat)) > 1 && Math.abs((newlocation.getLongitude() - lon)) > 1;
    }
}
