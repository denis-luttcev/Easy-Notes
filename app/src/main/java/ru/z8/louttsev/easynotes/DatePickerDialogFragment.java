package ru.z8.louttsev.easynotes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
//import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class DatePickerDialogFragment extends DialogFragment {
    private static final String ARG_DATE = "date";
    static final String RESULT_DATE = "result_date";

    private Context mContext;

    @NonNull
    static DatePickerDialogFragment newInstance() {
        return new DatePickerDialogFragment();
    }

    @NonNull
    static DatePickerDialogFragment getInstance(@NonNull Calendar deadline) {
        Bundle args = new Bundle();
        DatePickerDialogFragment fragment = new DatePickerDialogFragment();

        args.putSerializable(ARG_DATE, deadline);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View mDatePickerView = LayoutInflater.from(getActivity())
                .inflate(R.layout.date_picker_dialog, null);

        final DatePicker datePicker = mDatePickerView.findViewById(R.id.dialog_date_picker);

        final Calendar result = Calendar.getInstance();
        // set default deadline time as next hour from current time
        result.set(Calendar.HOUR_OF_DAY, result.get(Calendar.HOUR_OF_DAY) + 1);
        result.set(Calendar.MINUTE, 0);

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(ARG_DATE)) {
                Calendar deadline = (Calendar) args.getSerializable(ARG_DATE);

                if (deadline != null) {
                    datePicker.init(deadline.get(Calendar.YEAR),
                            deadline.get(Calendar.MONTH),
                            deadline.get(Calendar.DATE),
                            null);
                    result.set(Calendar.YEAR, deadline.get(Calendar.YEAR));
                    result.set(Calendar.MONTH, deadline.get(Calendar.MONTH));
                    result.set(Calendar.DATE, deadline.get(Calendar.DATE));
                    result.set(Calendar.HOUR_OF_DAY, deadline.get(Calendar.HOUR_OF_DAY));
                    result.set(Calendar.MINUTE, deadline.get(Calendar.MINUTE));
                }
            }
        }

        return new AlertDialog.Builder(mContext)
                .setView(mDatePickerView)
                .setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendResult(Activity.RESULT_CANCELED, null);
                    }
                })
                .setNeutralButton(getString(R.string.clear_button_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendResult(Activity.RESULT_OK, null);
                    }
                })
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        result.set(Calendar.YEAR, datePicker.getYear());
                        result.set(Calendar.MONTH, datePicker.getMonth());
                        result.set(Calendar.DATE, datePicker.getDayOfMonth());
                        sendResult(Activity.RESULT_OK, result);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, Calendar result) {
        if (getTargetFragment() != null) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(RESULT_DATE, result);

            getTargetFragment()
                    .onActivityResult(getTargetRequestCode(), resultCode, resultIntent);
        }
    }
}
