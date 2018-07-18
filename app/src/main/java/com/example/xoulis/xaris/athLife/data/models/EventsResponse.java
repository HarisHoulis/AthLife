package com.example.xoulis.xaris.athLife.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EventsResponse {

    @SerializedName("event")
    private List<Event> events;

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
