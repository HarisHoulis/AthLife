package com.example.xoulis.xaris.athLife.data.api;



import com.example.xoulis.xaris.athLife.C;
import com.example.xoulis.xaris.athLife.data.models.PlaceDetailsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlaceDetailsService {

    @GET(C.PLACE_DETAILS_DEFAULT_QUERY)
    Call<PlaceDetailsResponse> getPlaceDetails(@Query("placeid") String id);
}
