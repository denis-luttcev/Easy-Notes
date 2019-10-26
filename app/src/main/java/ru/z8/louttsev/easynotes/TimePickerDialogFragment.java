package ru.z8.louttsev.easynotes;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Objects;

public class TimePickerDialogFragment extends DialogFragment {
    private static String ARG_TIME = "time";
    static final String RESULT_TIME = "result_time";

    @NonNull
    static TimePickerDialogFragment getInstance(@NonNull Calendar deadline) {
        Bundle args = new Bundle();
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();

        args.putSerializable(ARG_TIME, deadline);
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View mTimePickerView = LayoutInflater.from(getActivity())
                .inflate(R.layout.time_picker_dialog, null);

        final TimePicker timePicker = mTimePickerView.findViewById(R.id.dialog_time_picker);
        timePicker.setIs24HourView(true);

        final Calendar result = Calendar.getInstance();

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(ARG_TIME)) {
                Calendar deadline = (Calendar) args.getSerializable(ARG_TIME);
                if (deadline != null) {
                    // used deprecated methods since minSdkVersion is API level 19
                    //TODO: fix it after release
                    timePicker.setCurrentHour(deadline.get(Calendar.HOUR_OF_DAY));
                    timePicker.setCurrentMinute(deadline.get(Calendar.MINUTE));
                    result.set(Calendar.YEAR, deadline.get(Calendar.YEAR));
                    result.set(Calendar.MONTH, deadline.get(Calendar.MONTH));
                    result.set(Calendar.DATE, deadline.get(Calendar.DATE));
                    result.set(Calendar.HOUR_OF_DAY, deadline.get(Calendar.HOUR_OF_DAY));
                    result.set(Calendar.MINUTE, deadline.get(Calendar.MINUTE));
                }
            }
        }

        return new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(mTimePickerView)
                .setNegativeButton(getString(android.R.string.cancel), null)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // used deprecated methods since minSdkVersion is API level 19
                        //TODO: fix it after release
                        result.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                        result.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                        sendResult(Activity.RESULT_OK, result);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, Calendar result) {
        if (getTargetFragment() != null) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(RESULT_TIME, result);

            getTargetFragment()
                    .onActivityResult(getTargetRequestCode(), resultCode, resultIntent);
        }
    }
}
