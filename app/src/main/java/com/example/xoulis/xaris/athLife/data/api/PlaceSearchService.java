package com.example.xoulis.xaris.athLife.data.api;

import com.example.xoulis.xaris.athLife.C;
import com.example.xoulis.xaris.athLife.data.models.PlaceSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlaceSearchService {

    @GET(C.PLACE_SEARCH_DEFAULT_QUERY)
    Call<PlaceSearchResponse> getPlaces(@Query("type") String category);
}
