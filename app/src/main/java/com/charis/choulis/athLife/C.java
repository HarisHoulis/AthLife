package com.charis.choulis.athLife;

import com.charis.choulis.athLife.BuildConfig;

public class C {

    private static final String EVENTFUL_API_KEY = BuildConfig.EVENTFUL_API_KEY;
    public static final String EVENTFUL_BASE_URL = "http://api.eventful.com/json/events/";
    public static final String EVENTFUL_DFAULT_QUERY = "search?sort_order=popularity&location=Athens&image_sizes=blackborder500&app_key=" + EVENTFUL_API_KEY;
    public static final String EVENT_DEFAULT_CATEGORY = "music";

    public static final String GOOGLE_PLACE_API_KEY = BuildConfig.GOOGLE_PLACE_API_KEY;
    public static final String PLACE_SEARCH_BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/";
    public static final String PLACE_SEARCH_DEFAULT_QUERY = "json?location=37.983810,23.727539&rankby=distance&key=" + GOOGLE_PLACE_API_KEY;
    public static final String PLACE_DEFAULT_CATEGORY = "restaurant";

    public static final String PLACE_DETAILS_DEFAULT_QUERY = "json?key=" + GOOGLE_PLACE_API_KEY;
    public static final String PLACE_DETAILS_BASE_URL = "https://maps.googleapis.com/maps/api/place/details/";

    public static final String PLACE_PHOTOS_BASE_URL = "https://maps.googleapis.com/maps/api/place/";
    public static final String PLACE_PHOTOS_DEFAULT_QUERY = "photo?maxwidth=400&key=" + GOOGLE_PLACE_API_KEY;

    public static final String OPENWEATHERMAP_API_KEY = BuildConfig.OPENWEATHERMAP_API_KEY;
    public static final String OPENWEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static final String CITY_ID = "8133876"; // Athens, Greece

    public static final String GOOGLE_STATIC_MAPS_API_KEY = BuildConfig.GOOGLE_STATIC_MAPS_API_KEY;
    public static final String GOOGLE_STATIC_MAPS_BASE_URL = "https://maps.googleapis.com/maps/api/staticmap";

    public static final String EXTRA_APPWIDGET_ID = "extra_appwidget_id";
    public static final String EXTRA_SHOWEVENTS = "extra_showEvents";
    public static final String EXTRA_CATEGORY = "extra_source_category";
    public static final String ACTION_UPDATE_WIDGET = "com.xaris.xoulis.athlife";
}
