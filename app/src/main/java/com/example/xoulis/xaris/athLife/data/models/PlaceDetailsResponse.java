package com.example.xoulis.xaris.athLife.data.models;

import com.google.gson.annotations.SerializedName;

//@JsonAdapter(PlaceDetailsDeserializer.class)
public class PlaceDetailsResponse {

    @SerializedName("formatted_address")
    private String address;
    private String url;

    public String getPlaceAddress() {
        return address;
    }

    public void setPlaceAddress(String placeAddress) {
        this.address = placeAddress;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
