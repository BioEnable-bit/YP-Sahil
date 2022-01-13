package com.yespustak.yespustakapp.api.custom;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class BooleanDeserializer implements JsonDeserializer<Boolean> {

    public Boolean deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

//        return jsonElement.getAsInt() == 0 ? false : true;
        return jsonElement.getAsString().equals("1");
    }
}
