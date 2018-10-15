package com.charis.choulis.athLife.data.api;

import com.charis.choulis.athLife.data.deserializers.EventsResponseDeserialiser;
import com.charis.choulis.athLife.data.deserializers.PlaceDetailsDeserializer;
import com.charis.choulis.athLife.data.models.EventsResponse;
import com.charis.choulis.athLife.data.models.PlaceDetailsResponse;
import com.charis.choulis.athLife.C;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static ApiClient instance = null;
    private static EventfulService eventfulService;
    private static PlaceSearchService placeService;
    private static PlaceDetailsService placeDetailsService;

    private ApiClient() {

    }

    public static ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(EventsResponse.class, new EventsResponseDeserialiser())
                    .registerTypeAdapter(PlaceDetailsResponse.class, new PlaceDetailsDeserializer())
                    .create();

            Retrofit.Builder builder = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson));
            eventfulService = builder.baseUrl(C.EVENTFUL_BASE_URL).build().create(EventfulService.class);
            placeService = builder.baseUrl(C.PLACE_SEARCH_BASE_URL).build().create(PlaceSearchService.class);
            placeDetailsService = builder.baseUrl(C.PLACE_DETAILS_BASE_URL).build().create(PlaceDetailsService.class);
        }
        return instance;
    }

    public EventfulService getEventfulService() {
        return eventfulService;
    }

    public PlaceSearchService getPlaceService() {
        return placeService;
    }

    public PlaceDetailsService getPlaceDetailsService() {
        return placeDetailsService;
    }
}
