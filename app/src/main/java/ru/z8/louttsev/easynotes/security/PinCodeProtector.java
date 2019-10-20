package ru.z8.louttsev.easynotes.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Objects;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PinCodeProtector implements Protector {
    // separate storage of hash (in shared preferences) and salt (in file) is used
    private final String PROTECTION_CODE = "protection";
    private final String SALT_FILE_NAME = "data.txt";
    private final String DIALOG_PIN_CODE_INPUT = "pincode";

    /**
     * Main hash encrypt settings set in constants
     * @see PinCodeProtector#getNextSalt() about the salt generate method
     * @see PinCodeProtector#hashPinCode(String, byte[]) about used hash algorithms
     */
    private final int KEY_LENGTH = 128;
    private final SecureRandom RANDOMIZER = new SecureRandom();
    @SuppressWarnings("FieldCanBeLocal")
    private final int ITERATIONS = 16384;

    private final SharedPreferences preferences;
    private final Context context;

    public PinCodeProtector(@NonNull Context context) {
        this.context = context;
        final String SECURITY_PREFERENCES = "security";
        preferences = context.getSharedPreferences(SECURITY_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public boolean isProtectionConfigured() {
        // missing of saved hash considered as unconfigured protection (possible at the first run)
        return preferences.getString(PROTECTION_CODE, null) == null;
    }

    @Override
    public boolean isProtectionEnabled() {
        // the call is supposed strictly after checking the protection code exist
        return !Objects.requireNonNull(preferences.getString(PROTECTION_CODE, null)).isEmpty();
    }

    /**
     * Tryes to enable protection (possible technical problems or user refusal)
     */
    @Override
    public void enableProtection(@NonNull FragmentManager fragmentManager,
                                 @NonNull final OnProtectionResultListener resultListener) {
        PinCodeInputFragment pinCodeInput = new PinCodeInputFragment();
        pinCodeInput
                .setPinCodeInputDialogListener(new PinCodeInputFragment.PinCodeInputResultListener() {
            @Override
            public void onDismiss(String inputedPinCode) {
                if (savePinCode(inputedPinCode)) { // technical problems will cause false
                    resultListener.onProtectionResultSuccess();
                } else resultListener.onProtectionResultFailure();
            }

            @Override
            public void onCancel() {
                resultListener.onProtectionResultFailure();
            }
        });
        pinCodeInput.show(fragmentManager, DIALOG_PIN_CODE_INPUT);
    }

    /**
     * @return true if save was successful or false in case of technical problems
     */
    private boolean savePinCode(@NonNull String pinCode) {
        byte[] salt = getNextSalt();
        try {
            saveHash(hashPinCode(pinCode, salt));
            saveSalt(salt);
            return true;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            return false;
        }
    }

    @NonNull
    private byte[] getNextSalt() {
        byte[] salt = new byte[KEY_LENGTH];
        RANDOMIZER.nextBytes(salt);
        return salt;
    }

    @NonNull
    private byte[] hashPinCode(@NonNull String pinCode, @NonNull byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(pinCode.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return factory.generateSecret(spec).getEncoded();
    }

    private void saveHash(@NonNull byte[] hash) {
        preferences
                .edit()
                .putString(PROTECTION_CODE, Base64.encodeToString(hash, Base64.DEFAULT))
                .apply();
    }

    private void saveSalt(@NonNull byte[] salt) throws IOException {
        FileOutputStream saltFile = context.openFileOutput(SALT_FILE_NAME, Context.MODE_PRIVATE);
        saltFile.write(salt, 0, KEY_LENGTH);
        saltFile.close();
    }

    @Override
    public void disableProtection() {
        preferences
                .edit()
                .putString(PROTECTION_CODE, "")
                .apply();
        deleteSalt();
    }

    private void deleteSalt() {
        context.deleteFile(SALT_FILE_NAME);
    }

    /**
     * Technical problems and user refusal are need considered as access denied
     */
    @Override
    public void checkAuthorization(@NonNull FragmentManager fragmentManager,
                                   @NonNull final OnProtectionResultListener resultListener) {
        PinCodeInputFragment pinCodeInput = new PinCodeInputFragment();
        pinCodeInput
                .setPinCodeInputDialogListener(new PinCodeInputFragment.PinCodeInputResultListener() {
            @Override
            public void onDismiss(String inputedPinCode) {
                if (checkPinCode(inputedPinCode)) { // technical problems will cause false
                    resultListener.onProtectionResultSuccess();
                } else resultListener.onProtectionResultFailure();
            }

            @Override
            public void onCancel() {
                resultListener.onProtectionResultFailure();
            }
        });
        pinCodeInput.show(fragmentManager, DIALOG_PIN_CODE_INPUT);
    }

    /**
     * @return false if invalid pin code or in case of technical problems
     */
    private boolean checkPinCode(@NonNull String pinCode) {
        try {
            return Arrays.equals(hashPinCode(pinCode, readSalt()), readHash());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            return false;
        }
    }

    @NonNull
    private byte[] readSalt() throws IOException {
        byte[] salt = new byte[KEY_LENGTH];
        FileInputStream saltFile = context.openFileInput(SALT_FILE_NAME);
        //noinspection ResultOfMethodCallIgnored
        saltFile.read(salt, 0, KEY_LENGTH); // read in byte array, result ignored
        saltFile.close();
        return salt;
    }

    @NonNull
    private byte[] readHash() {
        // the call is supposed strictly after checking the protection code exist
        return Base64.decode(preferences.getString(PROTECTION_CODE, null), Base64.DEFAULT);
    }
}
