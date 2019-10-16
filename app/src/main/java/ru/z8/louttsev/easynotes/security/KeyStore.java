package ru.z8.louttsev.easynotes.security;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Objects;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class KeyStore implements KeyKeeper {
    private final String PROTECTION_CODE = "protection";
    private final String SALT_FILE_NAME = "data.txt";

    private final int KEY_LENGTH = 128;
    private final SecureRandom RANDOMIZER = new SecureRandom();

    private SharedPreferences preferences;
    private Context context;

    public KeyStore(@NonNull Context context) {
        String SECURITY_PREFERENCES = "security";
        preferences = context.getSharedPreferences(SECURITY_PREFERENCES, Context.MODE_PRIVATE);
        this.context = context;
    }

    @Override
    public boolean isProtectionDisabled() throws Exception {
        String protectionCode = preferences.getString(PROTECTION_CODE, null);
        if (protectionCode != null) {
            return protectionCode.isEmpty();
        } else {
            throw new Exception();
        }
    }

    @Override
    public void disableProtection() {
        preferences.edit().putString(PROTECTION_CODE, "").apply();
    }

    @Override
    public boolean checkProtection(@NonNull char[] protectionCode) throws Exception {
        try {
            byte[] hash = hashPinCode(protectionCode, readSalt());
            byte[] savedHash = readHash();
            return Arrays.equals(hash, savedHash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            throw new Exception();
        }
    }

    @Override
    public void enableProtection(@NonNull char[] protectionCode) throws Exception {
        byte[] salt = getNextSalt();
        try {
            byte[] hash = hashPinCode(protectionCode, salt);
            saveHash(hash);
            saveSalt(salt);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            throw new Exception();
        }
    }

    @NonNull
    private byte[] hashPinCode(@NonNull char[] pinCode, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int ITERATIONS = 65536;
        KeySpec spec = new PBEKeySpec(pinCode, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return factory.generateSecret(spec).getEncoded();
    }

    @NonNull
    private byte[] getNextSalt() {
        byte[] salt = new byte[KEY_LENGTH];
        RANDOMIZER.nextBytes(salt);
        return salt;
    }

    private void saveHash(@NonNull byte[] hash) {
        preferences.edit().putString(PROTECTION_CODE, new String(hash, StandardCharsets.UTF_8)).apply();
    }

    private void saveSalt(@NonNull byte[] salt) throws IOException {
        FileOutputStream saltFile = context.openFileOutput(SALT_FILE_NAME, Context.MODE_PRIVATE);
        saltFile.write(salt, 0, KEY_LENGTH);
        saltFile.close();
    }

    @NonNull
    private byte[] readHash() {
        return Objects.requireNonNull(preferences.getString(PROTECTION_CODE, null)).getBytes();
    }

    @NonNull
    private byte[] readSalt() throws IOException {
        byte[] salt = new byte[KEY_LENGTH];
        FileInputStream saltFile = context.openFileInput(SALT_FILE_NAME);
        //noinspection ResultOfMethodCallIgnored
        saltFile.read(salt, 0, KEY_LENGTH);
        saltFile.close();
        return salt;
    }
}
