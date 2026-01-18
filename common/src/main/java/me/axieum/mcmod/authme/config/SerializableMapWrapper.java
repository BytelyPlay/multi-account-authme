package me.axieum.mcmod.authme.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigObject;
import com.teamresourceful.resourcefulconfig.api.types.entries.SerializableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@ConfigObject
public class SerializableMapWrapper<K, V> implements SerializableObject {
    private final static Gson GSON = new Gson();

    private Map<K, V> map;

    public SerializableMapWrapper(Map<K, V> map) {
        this.map = map;
    }
    public SerializableMapWrapper() {
        this.map = new HashMap<>();
    }

    public Map<K, V> getMap() {
        return map;
    }

    @Override
    public JsonElement save() {
        return GSON.toJsonTree(map);
    }

    @Override
    public void load(JsonElement json) {
        map = GSON.fromJson(json, Map.class);
    }
}