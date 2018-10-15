package com.charis.choulis.athLife.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.charis.choulis.athLife.data.deserializers.PlaceLocationDeserializer;
import com.charis.choulis.athLife.data.deserializers.PlacePhotoRefDeserializer;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

public class Place implements Parcelable {

    @SerializedName("geometry")
    @JsonAdapter(PlaceLocationDeserializer.class)
    private LocationPoint location;
    @SerializedName("place_id")
    private String id;
    private String name;
    private String imgUrl;
    @SerializedName("photos")
    @JsonAdapter(PlacePhotoRefDeserializer.class)
    private String photoRef;
    private String url;
    private String address;

    public LocationPoint getLocation() {
        return location;
    }

    public void setLocation(LocationPoint location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getPhotoRef() {
        return photoRef;
    }

    public void setPhotoRef(String photoRef) {
        this.photoRef = photoRef;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    protected Place(Parcel in) {
        location = in.readParcelable(LocationPoint.class.getClassLoader());
        id = in.readString();
        name = in.readString();
        imgUrl = in.readString();
        photoRef = in.readString();
        url = in.readString();
        address = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location, flags);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(imgUrl);
        dest.writeString(photoRef);
        dest.writeString(url);
        dest.writeString(address);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
}
