package me.axieum.mcmod.authme.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigObject;
import com.teamresourceful.resourcefulconfig.api.types.entries.SerializableObject;

import java.util.HashMap;
import java.util.Map;

@ConfigObject
public class SerializableMapWrapper<K, V> implements SerializableObject {
    private Map<K, V> map;

    public SerializableMapWrapper(Map<K, V> map) {
        this.map = map;
    }
    @Override
    public JsonElement save() {
        Gson gson = new Gson();
        return gson.toJsonTree(this);
    }

    @Override
    public void load(JsonElement json) {
        Gson gson = new Gson();
        map = gson.fromJson(json, Map.class);
    }
}