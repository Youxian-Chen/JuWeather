package com.example.youxian.juweather.widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.youxian.juweather.MainActivity;
import com.example.youxian.juweather.R;
import com.example.youxian.juweather.Utils;
import com.example.youxian.juweather.model.CityWeather;



import rx.Subscriber;
import rx.schedulers.Schedulers;


/**
 * Created by Youxian on 1/3/16.
 */
public class WeatherWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("TAG", "onUpdate");
        ComponentName thisWidget = new ComponentName(context,
                WeatherWidgetProvider.class);
        final int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        WidgetManager widgetManager = new WidgetManager(context);
        widgetManager.fetchWeatherDataForWidget()
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<CityWeather>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CityWeather cityWeather) {
                        updateWeatherData(context, appWidgetManager, allWidgetIds, cityWeather);
                    }
                });

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void updateWeatherData(Context context, AppWidgetManager appWidgetManager,
                                   int[] allWidgetIds, CityWeather cityWeather) {

        for (int id: allWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_weather);

            remoteViews.setTextViewText(R.id.location_text_widget, cityWeather.name);

            String description = cityWeather.weathers[0].description;
            if (description.contains("clear")) {
                remoteViews.setImageViewResource(R.id.icon_image_widget, R.drawable.clear);
            } else if (description.contains("rain")) {
                remoteViews.setImageViewResource(R.id.icon_image_widget, R.drawable.rain);
            } else if (description.contains("cloud")) {
                remoteViews.setImageViewResource(R.id.icon_image_widget, R.drawable.cloud);
            } else if (description.contains("snow")) {
                remoteViews.setImageViewResource(R.id.icon_image_widget, R.drawable.snow);
            } else if (description.contains("mist") || description.contains("haze")) {
                remoteViews.setImageViewResource(R.id.icon_image_widget, R.drawable.mist);
            }

            remoteViews.setTextViewText(R.id.temperature_text_widget, Utils.temperatureFormat(cityWeather.main.temp));
            remoteViews.setTextViewText(R.id.description_text_widget, description);


            // Register an onClickListener
            /*
            Intent clickIntent = new Intent(context, WeatherWidgetProvider.class);

            clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    allWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.layout_widget, pendingIntent);
            */
            Intent configIntent = new Intent(context, MainActivity.class);
            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.layout_widget, configPendingIntent);

            appWidgetManager.updateAppWidget(id, remoteViews);

        }
    }

}
