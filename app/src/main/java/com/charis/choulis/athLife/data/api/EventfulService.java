package com.charis.choulis.athLife.data.api;

import com.charis.choulis.athLife.data.models.EventsResponse;
import com.charis.choulis.athLife.C;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EventfulService {

    @GET(C.EVENTFUL_DFAULT_QUERY)
    Call<EventsResponse> getEvents(@Query("category") String category);
}
