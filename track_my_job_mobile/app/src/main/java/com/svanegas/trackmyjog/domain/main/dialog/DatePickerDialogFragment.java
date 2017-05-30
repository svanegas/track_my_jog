package com.svanegas.trackmyjog.domain.main.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.svanegas.trackmyjog.R;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class DatePickerDialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public static final String CALLBACK_KEY = "callback";
    public static final String CALENDAR_KEY = "calendar";
    public static final String MIN_DATE_KEY = "min_date";
    public static final String MAX_DATE_KEY = "max_date";
    public static final String IDENTIFIER_KEY = "identifier";
    public static final String CLEAR_BUTTON_KEY = "clear_button";

    private Callback mCallback;
    private Calendar mCalendar;
    private Calendar mMinDate;
    private Calendar mMaxDate;
    private int mIdentifier;
    private boolean mIncludeClearButton;

    public static DatePickerDialogFragment newInstance(Callback callback, Calendar calendar,
                                                       Calendar minDate, Calendar maxDate,
                                                       int identifier, boolean clearButton) {
        DatePickerDialogFragment fragment = new DatePickerDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(CALLBACK_KEY, callback);
        args.putSerializable(CALENDAR_KEY, calendar);
        args.putSerializable(MIN_DATE_KEY, minDate);
        args.putSerializable(MAX_DATE_KEY, maxDate);
        args.putInt(IDENTIFIER_KEY, identifier);
        args.putBoolean(CLEAR_BUTTON_KEY, clearButton);
        fragment.setArguments(args);
        return fragment;
    }

    public static DatePickerDialogFragment newInstance(Callback callback, Calendar calendar) {
        return newInstance(callback, calendar, null, null, -1, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mCallback = (Callback) args.getSerializable(CALLBACK_KEY);
        mCalendar = (Calendar) args.getSerializable(CALENDAR_KEY);
        mMinDate = (Calendar) args.getSerializable(MIN_DATE_KEY);
        mMaxDate = (Calendar) args.getSerializable(MAX_DATE_KEY);
        mIdentifier = args.getInt(IDENTIFIER_KEY);
        mIncludeClearButton = args.getBoolean(CLEAR_BUTTON_KEY);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        if (mMinDate != null) dialog.getDatePicker().setMinDate(mMinDate.getTimeInMillis());

        if (mMaxDate != null) dialog.getDatePicker().setMaxDate(mMaxDate.getTimeInMillis());
        else dialog.getDatePicker().setMaxDate(new Date().getTime());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            dialog.getDatePicker().setFirstDayOfWeek(Calendar.MONDAY);
        if (mIncludeClearButton) {
            String clearMessage = getString(R.string.time_entries_list_date_picker_clear);
            dialog.setButton(DialogInterface.BUTTON_NEUTRAL, clearMessage,
                    (dialog1, which) -> mCallback.onDateCleared(mIdentifier));
        }
        return dialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mCalendar.set(year, month, dayOfMonth);
        if (mCallback != null) mCallback.onDateSet(mCalendar, mIdentifier);
    }

    public interface Callback extends Serializable {

        void onDateSet(Calendar date, int identifier);

        void onDateCleared(int identifier);
    }
}
