package com.example.youxian.juweather;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Youxian on 12/15/15.
 */
public class GetWeatherService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    private static final String TAG = GetWeatherService.class.toString();
    private static final String GET_CURRENT_WEATHER = "get_current_weather";
    public GetWeatherService(String name) {
        super(name);
    }

    public GetWeatherService() {
        super("GetWeatherService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, action);
        if (GET_CURRENT_WEATHER.equals(action)) {

        }

    }
}
