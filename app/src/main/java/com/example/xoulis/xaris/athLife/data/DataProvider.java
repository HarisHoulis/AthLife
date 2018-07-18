package com.example.xoulis.xaris.athLife.data;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.xoulis.xaris.athLife.C;
import com.example.xoulis.xaris.athLife.data.api.ApiClient;
import com.example.xoulis.xaris.athLife.data.api.EventfulService;
import com.example.xoulis.xaris.athLife.data.api.PlaceDetailsService;
import com.example.xoulis.xaris.athLife.data.api.PlaceSearchService;
import com.example.xoulis.xaris.athLife.data.models.Event;
import com.example.xoulis.xaris.athLife.data.models.Place;
import com.example.xoulis.xaris.athLife.data.models.EventsResponse;
import com.example.xoulis.xaris.athLife.data.models.PlaceDetailsResponse;
import com.example.xoulis.xaris.athLife.data.models.PlaceSearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataProvider {

    private OnDataFetchedListener listener;

    private volatile static DataProvider instance = null;

    private List<Event> events;
    private List<Place> places;
    private String eventsCategory = C.EVENT_DEFAULT_CATEGORY;
    private String placesCategory = C.PLACE_DEFAULT_CATEGORY;

    private DataProvider() {
    }

    public static DataProvider getInstance() {
        if (instance == null) {
            synchronized (DataProvider.class) {
                if (instance == null) {
                    instance = new DataProvider();
                }
            }
        }
        return instance;
    }

    public List<Event> getEvents() {
        return events;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public String getEventsCategory() {
        return eventsCategory;
    }

    public String getPlacesCategory() {
        return placesCategory;
    }

    public void setDataFetchedListener(OnDataFetchedListener listener) {
        this.listener = listener;
    }

    public void fetchEvents(boolean asynchronously, String category) {
        if (events == null || events.isEmpty() || !eventsCategory.equals(category)) {
            eventsCategory = category;
            if (asynchronously) {
                fetchEventsAsync(category);
            } else {
                fetchEventsSync(category);
            }
        } else {
            if (listener != null) {
                listener.onEventsFetched((ArrayList<Event>) events);
            }
        }
    }

    private void fetchEventsAsync(String category) {
        EventfulService eventfulService = ApiClient.getInstance().getEventfulService();
        Call<EventsResponse> eventsCall = eventfulService.getEvents(category);
        eventsCall.enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventsResponse> call, @NonNull Response<EventsResponse> response) {
                extractEventsFromResponse(response);
            }


            @Override
            public void onFailure(@NonNull Call<EventsResponse> call, @NonNull Throwable t) {
                Log.e("Failed to fetch events", t.getLocalizedMessage());
            }
        });
    }

    private void fetchEventsSync(String category) {
        EventfulService eventfulService = ApiClient.getInstance().getEventfulService();
        Call<EventsResponse> eventsCall = eventfulService.getEvents(category);
        try {
            Response<EventsResponse> response = eventsCall.execute();
            extractEventsFromResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extractEventsFromResponse(Response<EventsResponse> response) {
        if (response.isSuccessful() && response.body() != null && response.body().getEvents() != null) {
            events = response.body().getEvents();
        }
        if (listener != null) {
            listener.onEventsFetched((ArrayList<Event>) events);
        }
    }

    public void fetchPlaces(boolean asynchronously, String category) {
        if (places == null || places.isEmpty() || !placesCategory.equals(category)) {
            placesCategory = category;
            if (asynchronously) {
                fetchPlacesAsync(category);
            } else {
                fetchPlacesSync(category);
            }
        } else {
            if (listener != null) {
                listener.onPlacesFetched((ArrayList<Place>) places);
            }
        }
    }

    private void fetchPlacesAsync(String category) {
        PlaceSearchService placeSearchService = ApiClient.getInstance().getPlaceService();
        Call<PlaceSearchResponse> placeSearchCall = placeSearchService.getPlaces(category);
        placeSearchCall.enqueue(new Callback<PlaceSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlaceSearchResponse> call, @NonNull Response<PlaceSearchResponse> response) {
                extractPlacesFromResponse(true, response);
            }

            @Override
            public void onFailure(@NonNull Call<PlaceSearchResponse> call, @NonNull Throwable t) {
                Log.e("Error fetching places", t.getLocalizedMessage());
            }
        });
    }

    private void fetchPlacesSync(String category) {
        PlaceSearchService placeSearchService = ApiClient.getInstance().getPlaceService();
        Call<PlaceSearchResponse> placeSearchCall = placeSearchService.getPlaces(category);
        try {
            Response<PlaceSearchResponse> firstResponse = placeSearchCall.execute();
            extractPlacesFromResponse(false, firstResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extractPlacesFromResponse(boolean asynchronously, Response<PlaceSearchResponse> response) {
        if (response.isSuccessful() && response.body() != null && response.body().getPlaces() != null && !response.body().getPlaces().isEmpty()) {
            places = response.body().getPlaces();
            for (int i = 0; i < places.size(); i++) {
                final Place place = places.get(i);
                PlaceDetailsService placeDetailsService = ApiClient.getInstance().getPlaceDetailsService();
                Call<PlaceDetailsResponse> placeDetailsCall = placeDetailsService.getPlaceDetails(place.getId());
                final int finalI = i;
                if (asynchronously) {
                    placeDetailsCall.enqueue(new Callback<PlaceDetailsResponse>() {
                        @Override
                        public void onResponse(Call<PlaceDetailsResponse> call, Response<PlaceDetailsResponse> response) {
                            extractPlacesDetailsFromResponse(response, finalI, place);
                        }

                        @Override
                        public void onFailure(Call<PlaceDetailsResponse> call, Throwable t) {
                            Log.e("Error with placeDetails", t.getLocalizedMessage());
                        }
                    });
                } else {
                    try {
                        Response<PlaceDetailsResponse> syncResponse = placeDetailsCall.execute();
                        extractPlacesDetailsFromResponse(syncResponse, finalI, place);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void extractPlacesDetailsFromResponse(Response<PlaceDetailsResponse> response, int i, Place place) {
        if (response.isSuccessful() && response.body() != null) {
            PlaceDetailsResponse placeDetails = response.body();
            place.setAddress(placeDetails.getPlaceAddress());
            place.setUrl(placeDetails.getUrl());
            place.setImgUrl(buildPlaceImgUrl(place.getPhotoRef()));
            places.set(i, place);

            if (i + 1 == places.size()) {
                if (listener != null) {
                    listener.onPlacesFetched((ArrayList<Place>) places);
                }
            }
        }
    }

    private String buildPlaceImgUrl(String photoRef) {
        String imgUrl = null;
        if (!TextUtils.isEmpty(photoRef)) {
            imgUrl = Uri.parse(C.PLACE_PHOTOS_BASE_URL)
                    .buildUpon()
                    .appendEncodedPath("photo")
                    .appendQueryParameter("maxwidth", "400")
                    .appendQueryParameter("photoreference", photoRef)
                    .appendQueryParameter("key", C.GOOGLE_PLACE_API_KEY)
                    .build()
                    .toString();
        }
        return imgUrl;
    }

    public interface OnDataFetchedListener {
        void onEventsFetched(ArrayList<Event> events);

        void onPlacesFetched(ArrayList<Place> places);
    }
}
