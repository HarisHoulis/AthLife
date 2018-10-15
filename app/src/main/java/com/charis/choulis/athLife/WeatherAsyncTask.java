package com.charis.choulis.athLife;

import android.net.Uri;
import android.os.AsyncTask;

import com.charis.choulis.athLife.data.models.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherAsyncTask extends AsyncTask<Void, Void, Weather> {

    private OnWeatherDataFetchedListener listener;

    public void setWeatherDataFetchedListener(OnWeatherDataFetchedListener listener) {
        this.listener = listener;
    }

    @Override
    protected Weather doInBackground(Void... voids) {
        HttpHandler httpHandler = new HttpHandler();

        String json = httpHandler.makeServiceCall(getOpenWeatherMapUrl());
        if (json != null) {
            try {
                JSONObject rootObject = new JSONObject(json);
                JSONArray weatherArray = rootObject.getJSONArray("weather");
                JSONObject weatherObject = weatherArray.getJSONObject(0);
                String description = weatherObject.getString("main");
                // Get time of day based on the last char of the iconRef ('d' or 'n')
                String iconRef = weatherObject.getString("icon");
                String timeOfDay = iconRef.substring(iconRef.length() - 1);
                return new Weather(description, timeOfDay);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Weather weather) {
        //super.onPostExecute(weather);
        if (listener != null) {
            listener.onWeatherDataFetched(weather);
        }
    }

    private String getOpenWeatherMapUrl() {
        return Uri.parse(C.OPENWEATHER_BASE_URL)
                .buildUpon()
                .appendEncodedPath("weather")
                .appendQueryParameter("id", C.CITY_ID)
                .appendQueryParameter("appid", C.OPENWEATHERMAP_API_KEY)
                .build()
                .toString();

    }

    public interface OnWeatherDataFetchedListener {
        void onWeatherDataFetched(Weather weather);
    }
}
