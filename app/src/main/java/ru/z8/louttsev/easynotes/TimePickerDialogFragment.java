package ru.z8.louttsev.easynotes;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class TimePickerDialogFragment extends DialogFragment {
    @NonNull
    static TimePickerDialogFragment newInstance() {
        return new TimePickerDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View mTimePickerView = LayoutInflater.from(getActivity())
                .inflate(R.layout.time_picker_dialog, null);

        return new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(mTimePickerView)
                .setPositiveButton(getString(android.R.string.ok), null)
                .create();
    }
}
