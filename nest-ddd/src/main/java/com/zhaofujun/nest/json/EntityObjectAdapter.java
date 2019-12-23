package com.zhaofujun.nest.json;

import com.google.gson.*;
import com.zhaofujun.nest.context.model.Entity;

import java.lang.reflect.Type;

public class EntityObjectAdapter implements JsonSerializer<Entity>, JsonDeserializer<Entity> {
    @Override
    public Entity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String type = jsonObject.get("__type__").getAsString();

        try {
            return context.deserialize(json, Class.forName(type));
        } catch (ClassNotFoundException ex) {
            throw new JsonParseException("Unknown element type:" + type, ex);
        }

    }

    @Override
    public JsonElement serialize(Entity src, Type typeOfSrc, JsonSerializationContext context) {

        JsonElement jsonElement = context.serialize(src, src.getClass());
        jsonElement.getAsJsonObject().addProperty("__type__", src.getClass().getName());

        return jsonElement;
    }
}
