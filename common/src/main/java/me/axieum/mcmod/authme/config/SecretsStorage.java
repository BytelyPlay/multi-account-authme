package me.axieum.mcmod.authme.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.axieum.mcmod.authme.api.AuthMe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO: Passphrase Encryption
public class SecretsStorage {
    public static List<PlayerIdentifier> playerRefreshTokenPairs = new ArrayList<>();

    // This isn't really "good" practice, but for now it's fine.
    private static String passPhrase = "";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final Path FILE_TO_SAVE_TO = Path.of("./config/" + AuthMe.MOD_ID + "_secrets.json");
    private static final Logger log = LoggerFactory.getLogger(SecretsStorage.class);

    private static final Gson GSON = new Gson();
    private static final String REFRESH_TOKEN_KEY = "refresh_tokens";

    private static final int ITERATIONS = 650_535;
    private static final int KEY_LENGTH = 256;
    private static final String ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";
    private static final String KEY_DERIVING_ALGORITHM = "PBKDF2WithHmacSHA256";

    private static final int SALT_LENGTH = 32;
    private static final int IV_LENGTH = 12;

    public static void setPassPhrase(String newPassPhrase) {
        passPhrase = newPassPhrase;
    }

    public static void save() {
        try {
            if (passPhrase.isEmpty()) {
                throw new RuntimeException("No Passphrase");
            }

            JsonObject root = new JsonObject();
            root.add(REFRESH_TOKEN_KEY, GSON.toJsonTree(playerRefreshTokenPairs));

            String data = root.toString();

            byte[] stringBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = encrypt(passPhrase, stringBytes);

            Files.write(FILE_TO_SAVE_TO, encrypted);
        } catch (IOException e) {
            log.error("Couldn't serialize secrets", e);
        }
    }

    public static void load() {
        try {
            if (passPhrase.isEmpty()) {
                throw new RuntimeException("No Passphrase");
            }

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

    private static byte[] encrypt(String passPhrase, byte[] decrypted) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);

            char[] passPhraseChars = new char[passPhrase.length()];
            passPhrase.getChars(0, passPhraseChars.length, passPhraseChars, 0);

            byte[] salt = new byte[SALT_LENGTH];
            SECURE_RANDOM.nextBytes(salt);

            byte[] iv = new byte[IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);

            byte[] ivAndSalt = new byte[salt.length + iv.length];
            System.arraycopy(salt, 0, ivAndSalt, 0, salt.length);
            System.arraycopy(iv, 0, ivAndSalt, salt.length - 1, iv.length);

            cipher.updateAAD(ivAndSalt);
            cipher.update(decrypted);

            cipher.init(Cipher.ENCRYPT_MODE, deriveKey(passPhraseChars, salt));

            byte[] encrypted = cipher.doFinal();
            byte[] result = new byte[encrypted.length + ivAndSalt.length];

            System.arraycopy(ivAndSalt, 0, result, encrypted.length - 1, ivAndSalt.length);
            System.arraycopy(encrypted, 0, result, 0, encrypted.length);

            return result;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
    private static byte[] decrypt(String passPhrase, byte[] encrypted) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);

            char[] passPhraseChars = new char[passPhrase.length()];
            passPhrase.getChars(0, passPhraseChars.length, passPhraseChars, 0);

            byte[] salt = Arrays.copyOfRange(encrypted, 0, SALT_LENGTH - 1);
            byte[] iv = Arrays.copyOfRange(encrypted, SALT_LENGTH - 1, IV_LENGTH - 1);

            byte[] ivAndSalt = new byte[salt.length + iv.length];
            System.arraycopy(salt, 0, ivAndSalt, 0, salt.length);
            System.arraycopy(iv, 0, ivAndSalt, salt.length - 1, iv.length);

            byte[] encryptedData =
                    Arrays.copyOfRange(encrypted,
                            ivAndSalt.length - 1,
                            encrypted.length - 1);

            SecretKey key = deriveKey(passPhraseChars, salt);

            cipher.init(Cipher.DECRYPT_MODE, key);

            cipher.updateAAD(ivAndSalt);
            cipher.update(encryptedData);

            return cipher.doFinal();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
    private static SecretKey deriveKey(char[] password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_DERIVING_ALGORITHM);

            return factory.generateSecret(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
