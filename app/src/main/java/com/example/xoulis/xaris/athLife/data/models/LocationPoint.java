package com.example.xoulis.xaris.athLife.data.models;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationPoint implements Parcelable {

    private double lat;
    private double lng;

    public LocationPoint() {
    }

    public LocationPoint(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    protected LocationPoint(Parcel in) {
        lat = in.readDouble();
        lng = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LocationPoint> CREATOR = new Creator<LocationPoint>() {
        @Override
        public LocationPoint createFromParcel(Parcel in) {
            return new LocationPoint(in);
        }

        @Override
        public LocationPoint[] newArray(int size) {
            return new LocationPoint[size];
        }
    };
}
