package com.svanegas.trackmyjog.domain.main.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class DatePickerDialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public static final String CALLBACK_KEY = "callback";
    public static final String CALENDAR_KEY = "calendar";

    private Callback mCallback;
    private Calendar mCalendar;

    public static DatePickerDialogFragment newInstance(Callback callback, Calendar calendar) {
        DatePickerDialogFragment fragment = new DatePickerDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(CALLBACK_KEY, callback);
        args.putSerializable(CALENDAR_KEY, calendar);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mCallback = (Callback) args.getSerializable(CALLBACK_KEY);
        mCalendar = (Calendar) args.getSerializable(CALENDAR_KEY);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        dialog.getDatePicker().setMaxDate(new Date().getTime());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            dialog.getDatePicker().setFirstDayOfWeek(Calendar.MONDAY);
        return dialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mCalendar.set(year, month, dayOfMonth);
        if (mCallback != null) mCallback.onDateSet(mCalendar);
    }

    public interface Callback extends Serializable {

        void onDateSet(Calendar date);
    }
}
