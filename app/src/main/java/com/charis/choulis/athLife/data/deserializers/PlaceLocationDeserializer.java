package com.charis.choulis.athLife.data.deserializers;

import com.charis.choulis.athLife.data.models.LocationPoint;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class PlaceLocationDeserializer implements JsonDeserializer<LocationPoint> {

    private Gson gson;

    @Override
    public LocationPoint deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (gson == null) {
            gson = new Gson();
        }
        JsonElement location = json.getAsJsonObject().get("location");
        return gson.fromJson(location, LocationPoint.class);
    }
}
