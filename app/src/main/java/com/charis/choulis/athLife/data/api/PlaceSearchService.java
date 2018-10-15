package com.charis.choulis.athLife.data.api;

import com.charis.choulis.athLife.data.models.PlaceSearchResponse;
import com.charis.choulis.athLife.C;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlaceSearchService {

    @GET(C.PLACE_SEARCH_DEFAULT_QUERY)
    Call<PlaceSearchResponse> getPlaces(@Query("type") String category);
}
