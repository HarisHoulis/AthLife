package com.charis.choulis.athLife.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.charis.choulis.athLife.data.deserializers.EventImageDeserializer;
import com.charis.choulis.athLife.data.deserializers.EventPerformersDeserializer;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormatSymbols;

public class Event implements Parcelable {

    @SerializedName("image")
    @JsonAdapter(EventImageDeserializer.class)
    private String imageUrl;
    @SerializedName("title")
    private String title;
    @SerializedName("start_time")
    private String date;
    @SerializedName("venue_address")
    private String address;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("venue_name")
    private String venueName;
    @SerializedName("description")
    private String description;
    @SerializedName("performers")
    @JsonAdapter(EventPerformersDeserializer.class)
    private String performers;
    @SerializedName("venue_url")
    private String venueUrl;
    @SerializedName("url")
    private String eventUrl;

    private LocationPoint locationPoint;

    private String calendarDate;
    private String month;
    private String day;
    private String time;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPerformers() {
        return performers;
    }

    public void setPerformers(String performers) {
        this.performers = performers;
    }

    public String getVenueUrl() {
        return venueUrl;
    }

    public void setVenueUrl(String venueUrl) {
        this.venueUrl = venueUrl;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    public String getMonth() {
        if (month == null) {
            extractMonthDayAndTime();
        }
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        if (day == null) {
            extractMonthDayAndTime();
        }
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        if (time == null) {
            extractMonthDayAndTime();
        }
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public LocationPoint getLocationPoint() {
        if (locationPoint == null) {
            locationPoint = new LocationPoint(Double.parseDouble(latitude), Double.parseDouble(longitude));
        }
        return locationPoint;
    }

    public String getCalendarDate() {
        // Return only the calendar date, NOT the time too
        if (calendarDate == null) {
            calendarDate = date.substring(0, 10);
        }
        return calendarDate;
    }

    private void extractMonthDayAndTime() {
        int month = Integer.parseInt(date.substring(5, 7));
        this.month = new DateFormatSymbols().getShortMonths()[month - 1];
        this.time = date.substring(11, 16);
        this.day = date.substring(8, 10);
    }

    protected Event(Parcel in) {
        imageUrl = in.readString();
        title = in.readString();
        date = in.readString();
        address = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        venueName = in.readString();
        description = in.readString();
        performers = in.readString();
        venueUrl = in.readString();
        eventUrl = in.readString();
        month = in.readString();
        day = in.readString();
        time = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageUrl);
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(address);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(venueName);
        dest.writeString(description);
        dest.writeString(performers);
        dest.writeString(venueUrl);
        dest.writeString(eventUrl);
        dest.writeString(month);
        dest.writeString(day);
        dest.writeString(time);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

}
