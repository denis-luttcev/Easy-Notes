package ru.z8.louttsev.easynotes.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.widget.Toast;

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

import ru.z8.louttsev.easynotes.R;

public class PinCodeProtector implements Protector {
    // separate storage of hash (in shared preferences) and salt (in file) is used
    private final String PROTECTION_CODE = "protection";
    private final String SALT_FILE_NAME = "data.txt";

    // Main hash encrypt settings set in constants
    // see PinCodeProtector#hashPinCode(String, byte[]) about used hash algorithms
    private final int KEY_LENGTH = 128;
    private final SecureRandom RANDOMIZER = new SecureRandom();
    @SuppressWarnings("FieldCanBeLocal")
    private final int ITERATIONS = 16384;

    private final SharedPreferences preferences;
    private final Context context;
    private InputDialogFragment pinCodeInput;

    private final String DIALOG_CHECK_PIN_CODE_INPUT = "check_pin_code";
    private final String DIALOG_NEW_PIN_CODE_INPUT = "new_pin_code";

    private boolean isLogged = false;

    final private int LIMIT_LOGIN_ATTEMPTS = 3;
    private int loginAttempt = 1;

    public PinCodeProtector(@NonNull Context context) {
        this.context = context;
        final String SECURITY_PREFERENCES = "security";
        preferences = context.getSharedPreferences(SECURITY_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public boolean isProtectionNotConfigured() {
        // missing of saved hash considered as unconfigured protection (possible at the first run)
        return preferences.getString(PROTECTION_CODE, null) == null;
    }

    @Override
    public boolean isProtectionEnabled() {
        // call strictly after checking the protection code exist
        return !Objects.requireNonNull(preferences.getString(PROTECTION_CODE, null)).isEmpty();
    }

    @Override
    public void enableProtection(@NonNull FragmentManager fragmentManager,
                                 @NonNull final ResultListener resultListener) {

        pinCodeInput = getInputDialogFragment(fragmentManager, DIALOG_NEW_PIN_CODE_INPUT);
        pinCodeInput.setResultListener(new InputDialogFragment.ResultListener() {
            @Override
            public void onDismiss(String enteredPinCode) {
                if (savePinCode(enteredPinCode)) { // technical problems will cause false
                    isLogged = true;
                    pinCodeInput.dismiss();
                    resultListener.onProtectionResultSuccess();
                } else {
                    pinCodeInput.dismiss();
                    resultListener.onProtectionResultFailure();
                }
            }

            @Override
            public void onCancel() {
                resultListener.onProtectionResultFailure();
            }
        });
    }

    @NonNull
    private InputDialogFragment getInputDialogFragment(@NonNull FragmentManager fragmentManager,
                                                       @NonNull String dialogTag) {

        InputDialogFragment pinCodeInput =
                (InputDialogFragment) fragmentManager.findFragmentByTag(dialogTag);

        if (pinCodeInput == null) {

            String dialogTitle = "";
            switch (dialogTag) {
                case DIALOG_CHECK_PIN_CODE_INPUT:
                    dialogTitle = context.getString(R.string.pin_code_input_check_dialog_title);
                    break;
                case DIALOG_NEW_PIN_CODE_INPUT:
                    dialogTitle = context.getString(R.string.pin_code_input_new_dialog_title);
                    break;
                default: // impossible
            }

            pinCodeInput = InputDialogFragment.newInstance(dialogTitle);
            pinCodeInput.show(fragmentManager, dialogTag);
        }

        return pinCodeInput;
    }

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

        isLogged = false;
    }

    private void deleteSalt() {
        context.deleteFile(SALT_FILE_NAME);
    }

    @Override
    public void checkAuthorization(@NonNull FragmentManager fragmentManager,
                                   @NonNull final ResultListener resultListener,
                                   boolean forcibly) {

        if (!forcibly && isLogged) {
            resultListener.onProtectionResultSuccess();
        } else {
            pinCodeInput = getInputDialogFragment(fragmentManager, DIALOG_CHECK_PIN_CODE_INPUT);
            pinCodeInput.setResultListener(new InputDialogFragment.ResultListener() {
                @Override
                public void onDismiss(String enteredPinCode) {
                    if (checkPinCode(enteredPinCode)) { // technical problems will cause false
                        isLogged = true;
                        loginAttempt = 1;
                        pinCodeInput.dismiss();
                        resultListener.onProtectionResultSuccess();
                    } else {
                        if (loginAttempt < LIMIT_LOGIN_ATTEMPTS) {
                            Toast.makeText(context,
                                    context.getString(R.string.incorrect_protection_key_toast_message),
                                    Toast.LENGTH_SHORT)
                                    .show();
                            pinCodeInput.clearPinCode();
                            loginAttempt++;
                        } else {
                            isLogged = false;
                            loginAttempt = 1;
                            pinCodeInput.dismiss();
                            resultListener.onProtectionResultFailure();
                        }
                    }
                }

                @Override
                public void onCancel() {
                    resultListener.onProtectionResultFailure();
                }
            });
        }
    }

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
        return Base64.decode(preferences.getString(PROTECTION_CODE, null), Base64.DEFAULT);
    }
}
