package com.example.xoulis.xaris.athLife.data.api;

import com.example.xoulis.xaris.athLife.C;
import com.example.xoulis.xaris.athLife.data.models.EventsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EventfulService {

    @GET(C.EVENTFUL_DFAULT_QUERY)
    Call<EventsResponse> getEvents(@Query("category") String category);
}
