package com.svanegas.trackmyjog.domain.main.time_entry.create;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;

import com.svanegas.trackmyjog.R;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.support.design.widget.Snackbar.LENGTH_LONG;

public class CreateTimeEntryFragment extends Fragment implements CreateTimeEntryView {

    private OnCreateTimeEntryListener mListener;
    private CreateTimeEntryPresenter mPresenter;
    private ViewHolder mViewHolder;

    public static CreateTimeEntryFragment newInstance() {
        return new CreateTimeEntryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new CreateTimeEntryPresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mListener.onActivityTitleRequested(R.string.create_time_entry_title);
        View rootView = inflater.inflate(R.layout.create_time_entry_fragment, container, false);
        mViewHolder = new ViewHolder(rootView);
        ButterKnife.bind(this, rootView);

        setupDatePicker();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCreateTimeEntryListener) {
            mListener = (OnCreateTimeEntryListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnCreateTimeEntryListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick(R.id.submit_button)
    public void onSubmitClicked() {
        mPresenter.validateTimeEntryCreation();
    }

    private void setupDatePicker() {
        mViewHolder.datePicker.setMaxDate(new Date().getTime());
    }

    @Override
    public Calendar date() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(mViewHolder.datePicker.getYear(),
                mViewHolder.datePicker.getMonth(),
                mViewHolder.datePicker.getDayOfMonth());
        return calendar;
    }

    @Override
    public String distance() {
        return mViewHolder.distanceField.getText().toString().trim();
    }

    @Override
    public String hours() {
        return mViewHolder.hoursField.getText().toString().trim();
    }

    @Override
    public String minutes() {
        return mViewHolder.minutesField.getText().toString().trim();
    }

    @Override
    public void showLoadingAndDisableFields() {
        mViewHolder.errorMessage.setVisibility(View.GONE);
        setLoadingAndFieldsEnabled(false);
    }

    @Override
    public void hideLoadingAndEnableFields() {
        setLoadingAndFieldsEnabled(true);
    }

    @Override
    public void onCreationSuccess() {
        Snackbar.make(mViewHolder.rootView, R.string.create_time_entry_success, LENGTH_LONG).show();
        mListener.goToTimeEntriesList();
    }

    @Override
    public void showEmptyDurationError() {
        mViewHolder.minutesField.setError(getString(R.string.error_field_required));
        mViewHolder.minutesField.requestFocus();
    }

    @Override
    public void showNegativeDurationError() {
        mViewHolder.minutesField.setError(getString(R.string.create_time_entry_error_negative));
        mViewHolder.minutesField.requestFocus();
    }

    @Override
    public void showInvalidDurationError() {
        mViewHolder.minutesField.setError(getString(R.string.create_time_entry_error_invalid));
        mViewHolder.minutesField.requestFocus();
    }

    @Override
    public void showEmptyDistanceError() {
        mViewHolder.distanceField.setError(getString(R.string.error_field_required));
        mViewHolder.distanceField.requestFocus();
    }

    @Override
    public void showNegativeDistanceError() {
        mViewHolder.distanceField.setError(getString(R.string.create_time_entry_error_negative));
        mViewHolder.distanceField.requestFocus();
    }

    @Override
    public void showInvalidDistanceError() {
        mViewHolder.distanceField.setError(getString(R.string.create_time_entry_error_invalid));
        mViewHolder.distanceField.requestFocus();
    }

    @Override
    public void showTimeoutError() {
        mViewHolder.errorMessage.setVisibility(View.VISIBLE);
        mViewHolder.errorMessage.setText(R.string.error_timeout);
    }

    @Override
    public void showNoConnectionError() {
        mViewHolder.errorMessage.setVisibility(View.VISIBLE);
        mViewHolder.errorMessage.setText(R.string.error_no_internet);
    }

    @Override
    public void showDisplayableError(String errorMessage) {
        mViewHolder.errorMessage.setVisibility(View.VISIBLE);
        mViewHolder.errorMessage.setText(errorMessage);
    }

    @Override
    public void showUnknownError() {
        mViewHolder.errorMessage.setVisibility(View.VISIBLE);
        mViewHolder.errorMessage.setText(R.string.error_unknown);
    }

    private void setLoadingAndFieldsEnabled(boolean enabled) {
        mViewHolder.progressBar.setVisibility(enabled ? View.GONE : View.VISIBLE);
        mViewHolder.datePicker.setEnabled(enabled);
        mViewHolder.distanceField.setEnabled(enabled);
        mViewHolder.hoursField.setEnabled(enabled);
        mViewHolder.minutesField.setEnabled(enabled);
        mViewHolder.submitButton.setEnabled(enabled);
    }


    static class ViewHolder {

        @BindView(R.id.root_view)
        ViewGroup rootView;

        @BindView(R.id.error_message)
        AppCompatTextView errorMessage;

        @BindView(R.id.date_picker)
        DatePicker datePicker;

        @BindView(R.id.distance_field)
        AppCompatEditText distanceField;

        @BindView(R.id.hours_field)
        AppCompatEditText hoursField;

        @BindView(R.id.minutes_field)
        AppCompatEditText minutesField;

        @BindView(R.id.submit_button)
        AppCompatButton submitButton;

        @BindView(R.id.progress_bar)
        ProgressBar progressBar;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface OnCreateTimeEntryListener {

        void onActivityTitleRequested(int titleResId);

        void goToTimeEntriesList();
    }
}
