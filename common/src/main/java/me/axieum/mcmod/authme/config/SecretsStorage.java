package me.axieum.mcmod.authme.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.axieum.mcmod.authme.api.AuthMe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: Passphrase Encryption
public class SecretsStorage {
                                // USERNAME, UUID  REFRESH TOKEN
    public static List<PlayerIdentifier> playerRefreshTokenPairs = new ArrayList<>();

    private final static Path FILE_TO_SAVE_TO = Path.of("./config/" + AuthMe.MOD_ID + "_secrets.json");
    private static final Logger log = LoggerFactory.getLogger(SecretsStorage.class);

    private static final Gson GSON = new Gson();
    private static final String REFRESH_TOKEN_KEY = "refresh_tokens";

    public static void save() {
        try {
            JsonObject root = new JsonObject();
            root.add(REFRESH_TOKEN_KEY, GSON.toJsonTree(playerRefreshTokenPairs));

            Files.writeString(FILE_TO_SAVE_TO, root.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Couldn't serialize secrets", e);
        }
    }

    public static void load() {
        try {
            if (Files.exists(FILE_TO_SAVE_TO)) {
                JsonObject root = GSON.fromJson(Files.readString(FILE_TO_SAVE_TO), JsonObject.class);

                JsonElement element = root.get(REFRESH_TOKEN_KEY);
                playerRefreshTokenPairs = GSON.fromJson(element, new TypeToken<
                        List<PlayerIdentifier>
                        >()
                {}.getType());
            }
        } catch (IOException e) {
            log.error("Couldn't load secrets", e);
        }
    }
}
