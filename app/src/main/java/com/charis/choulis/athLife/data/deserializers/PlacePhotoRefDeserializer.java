package com.charis.choulis.athLife.data.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class PlacePhotoRefDeserializer implements JsonDeserializer<String> {
    @Override
    public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonElement photoElement = json.getAsJsonArray().get(0);
        return photoElement.getAsJsonObject().get("photo_reference").getAsString();
    }
}
