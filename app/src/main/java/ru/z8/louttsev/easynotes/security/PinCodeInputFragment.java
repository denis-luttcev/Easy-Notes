package ru.z8.louttsev.easynotes.security;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import ru.z8.louttsev.easynotes.R;

@SuppressWarnings("WeakerAccess")
class PinCodeInputFragment extends DialogFragment {

    interface PinCodeInputResultListener {
        void onDismiss(String inputedPinCode);
        void onCancel();
    }

    private PinCodeInputResultListener resultListener;

    void setPinCodeInputDialogListener(@NonNull PinCodeInputResultListener resultListener) {
        this.resultListener = resultListener;
    }

    private EditText mPinCodeField;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View pinCodeInputView = LayoutInflater.from(getActivity())
                .inflate(R.layout.pin_code_input, null);

        mPinCodeField = pinCodeInputView.findViewById(R.id.pin_code_field);
        mPinCodeField.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        if (savedInstanceState != null) {
            Log.e("TAG", "instance not null");
        } else Log.e("TAG", "instance null");

        final CheckBox visibilityCheckbox = pinCodeInputView.findViewById(R.id.visibility_checkbox);
        visibilityCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton visibilityCheckbox, boolean isChecked) {
                if (mPinCodeField.getInputType() != InputType.TYPE_CLASS_TEXT) {
                    mPinCodeField.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    mPinCodeField.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        View.OnClickListener numericButtonsOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View numericButton) {
                String currentPinCode = mPinCodeField.getText().toString()
                        + ((Button) numericButton).getText();
                mPinCodeField.setText(currentPinCode);
                final int PIN_LENGTH = 4;
                if (currentPinCode.length() == PIN_LENGTH) {
                    returnPinCode(currentPinCode);
                }
            }
        };

        pinCodeInputView.findViewById(R.id.btn0).setOnClickListener(numericButtonsOnClickListener);
        pinCodeInputView.findViewById(R.id.btn1).setOnClickListener(numericButtonsOnClickListener);
        pinCodeInputView.findViewById(R.id.btn2).setOnClickListener(numericButtonsOnClickListener);
        pinCodeInputView.findViewById(R.id.btn3).setOnClickListener(numericButtonsOnClickListener);
        pinCodeInputView.findViewById(R.id.btn4).setOnClickListener(numericButtonsOnClickListener);
        pinCodeInputView.findViewById(R.id.btn5).setOnClickListener(numericButtonsOnClickListener);
        pinCodeInputView.findViewById(R.id.btn6).setOnClickListener(numericButtonsOnClickListener);
        pinCodeInputView.findViewById(R.id.btn7).setOnClickListener(numericButtonsOnClickListener);
        pinCodeInputView.findViewById(R.id.btn8).setOnClickListener(numericButtonsOnClickListener);
        pinCodeInputView.findViewById(R.id.btn9).setOnClickListener(numericButtonsOnClickListener);

        pinCodeInputView.findViewById(R.id.clear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPinCodeField.setText("");
            }
        });

        pinCodeInputView.findViewById(R.id.backspace_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pinCode = mPinCodeField.getText().toString();
                pinCode = pinCode.substring(0, pinCode.length() - 1);
                mPinCodeField.setText(pinCode);
            }
        });

        return new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(pinCodeInputView)
                .setTitle(R.string.pin_code_input_title)
                .create();
    }

    private void returnPinCode(@NonNull String pinCode) {
        resultListener.onDismiss(pinCode);
        dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        resultListener.onCancel();
        super.onCancel(dialog);
    }
}
