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

public class DatePickerDialogFragment extends DialogFragment {
    @NonNull
    static DatePickerDialogFragment newInstance() {
        return new DatePickerDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View mDatePickerView = LayoutInflater.from(getActivity())
                .inflate(R.layout.date_picker_dialog, null);

        return new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(mDatePickerView)
                .setPositiveButton(getString(android.R.string.ok), null)
                .create();
    }
}
