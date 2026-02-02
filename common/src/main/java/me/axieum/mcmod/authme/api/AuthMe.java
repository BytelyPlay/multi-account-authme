package me.axieum.mcmod.authme.api;

import com.teamresourceful.resourcefulconfig.api.loader.Configurator;
import me.axieum.mcmod.authme.api.gui.screen.UserSelectionScreen;
import me.axieum.mcmod.authme.config.Config;
import me.axieum.mcmod.authme.config.SecretsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The multi-platform common mod.
 */
public final class AuthMe
{
    private AuthMe() {}

    /**
     * The mod identifier.
     */
    public static final String MOD_ID = "multiaccauthme";

    /**
     * The mod display name.
     */
    public static final String MOD_NAME = "Multi-Account Auth Me";

    /**
     * The mod logger.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    /**
     * The mod configuration.
     */
    public static final Configurator CONFIG = new Configurator(MOD_ID);

    /**
     * The legacy Mojang account migration FAQ link.
     */
    public static final String MOJANG_ACCOUNT_MIGRATION_FAQ_URL = "https://aka.ms/MinecraftPostMigrationFAQ";

    /**
     * Initialises the multi-platform mod.
     */
    public static void init()
    {
        // Register the configuration
        CONFIG.register(Config.class);

        if (!Config.LoginMethods.Microsoft.encryptRefreshTokens) {
            if (!SecretsStorage.load()) {
                LOGGER.warn("Couldn't load secrets.");
            }
        }

        if (!Config.LoginMethods.Microsoft.encryptRefreshTokens) Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!SecretsStorage.save()) {
                LOGGER.warn("Couldn't save secrets.");
            }
        }));
    }
}
