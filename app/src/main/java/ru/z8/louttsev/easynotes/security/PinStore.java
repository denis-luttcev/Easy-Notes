package ru.z8.louttsev.easynotes.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

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

import ru.z8.louttsev.easynotes.R;

public class PinStore implements Protector {
    private final String PROTECTION_CODE = "protection";
    private final String SALT_FILE_NAME = "data.txt";

    private final int KEY_LENGTH = 128;
    private final SecureRandom RANDOMIZER = new SecureRandom();
    private final int PIN_LENGTH = 4;

    private SharedPreferences preferences;
    private Context context;

    private EditText mPinCode;

    public PinStore(@NonNull Context context) {
        String SECURITY_PREFERENCES = "security";
        preferences = context.getSharedPreferences(SECURITY_PREFERENCES, Context.MODE_PRIVATE);
        this.context = context;
    }

    @Override
    public boolean isProtectionEnabled() throws Exception {
        String protectionCode = preferences.getString(PROTECTION_CODE, null);
        if (protectionCode != null) {
            return !protectionCode.isEmpty();
        } else {
            throw new Exception();
        }
    }

    @Override
    public void disableProtection() {
        preferences.edit().putString(PROTECTION_CODE, "").apply();
        deleteSalt();
    }

    @Override
    public boolean checkProtection(@NonNull char[] protectionCode) {
        return false;
    }

    @Override
    public void enableProtection(@NonNull FrameLayout protectionLayout) {
        LayoutInflater.from(context).inflate(R.layout.pin_protection_layout, protectionLayout, true);

        mPinCode = protectionLayout.findViewById(R.id.pin_code);
        mPinCode.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final ImageButton visibilityBtn = protectionLayout.findViewById(R.id.visibility_btn);
        visibilityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a = mPinCode.getInputType();
                if (mPinCode.getInputType() != InputType.TYPE_CLASS_TEXT) {
                    mPinCode.setInputType(InputType.TYPE_CLASS_TEXT);
                    visibilityBtn.setBackgroundResource(R.drawable.ic_visibility_off_black_24dp);
                } else {
                    mPinCode.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    visibilityBtn.setBackgroundResource(R.drawable.ic_visibility_black_24dp);
                }
            }
        });

        initPinPadButtons(protectionLayout);
    }

    private void initPinPadButtons(@NonNull FrameLayout protectionLayout) {
        View.OnClickListener numericListener = new NumericButtonOnClickListener();
        protectionLayout.findViewById(R.id.btn0).setOnClickListener(numericListener);
        protectionLayout.findViewById(R.id.btn1).setOnClickListener(numericListener);
        protectionLayout.findViewById(R.id.btn2).setOnClickListener(numericListener);
        protectionLayout.findViewById(R.id.btn3).setOnClickListener(numericListener);
        protectionLayout.findViewById(R.id.btn4).setOnClickListener(numericListener);
        protectionLayout.findViewById(R.id.btn5).setOnClickListener(numericListener);
        protectionLayout.findViewById(R.id.btn6).setOnClickListener(numericListener);
        protectionLayout.findViewById(R.id.btn7).setOnClickListener(numericListener);
        protectionLayout.findViewById(R.id.btn8).setOnClickListener(numericListener);
        protectionLayout.findViewById(R.id.btn9).setOnClickListener(numericListener);

        protectionLayout.findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPinCode.setText("");
            }
        });

        protectionLayout.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pinCodeString = mPinCode.getText().toString();
                pinCodeString = pinCodeString.substring(0, pinCodeString.length() - 1);
                mPinCode.setText(pinCodeString);
            }
        });
    }

    private class NumericButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String pinCodeString = mPinCode.getText().toString() + ((Button) view).getText();
            mPinCode.setText(pinCodeString);
            if (pinCodeString.length() == PIN_LENGTH) {
                if (!savePin(pinCodeString.toCharArray())) {
                    mPinCode.setText("");
                    disableProtection();
                };
            }
        }
    }

    private boolean checkPin(@NonNull char[] protectionCode) {
        try {
            byte[] hash = hashPinCode(protectionCode, readSalt());
            byte[] savedHash = readHash();
            return Arrays.equals(hash, savedHash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            return false;
        }
    }

    private boolean savePin(@NonNull char[] pinCode) {
        byte[] salt = getNextSalt();
        try {
            byte[] hash = hashPinCode(pinCode, salt);
            saveHash(hash);
            saveSalt(salt);
            return true;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            return false;
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

    private void deleteSalt() {
        context.deleteFile(SALT_FILE_NAME);
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
