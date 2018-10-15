package com.charis.choulis.athLife.data.api;



import com.charis.choulis.athLife.data.models.PlaceDetailsResponse;
import com.charis.choulis.athLife.C;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlaceDetailsService {

    @GET(C.PLACE_DETAILS_DEFAULT_QUERY)
    Call<PlaceDetailsResponse> getPlaceDetails(@Query("placeid") String id);
}
