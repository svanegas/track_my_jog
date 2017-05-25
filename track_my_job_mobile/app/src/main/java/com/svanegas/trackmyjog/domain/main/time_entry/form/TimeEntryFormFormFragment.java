package com.svanegas.trackmyjog.domain.main.time_entry.form;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;

import com.svanegas.trackmyjog.R;
import com.svanegas.trackmyjog.domain.main.time_entry.dialog.DeleteConfirmDialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.support.design.widget.Snackbar.LENGTH_LONG;

public class TimeEntryFormFormFragment extends Fragment implements TimeEntryFormView,
        DeleteConfirmDialogFragment.Callback {

    private static final String TIME_ENTRY_ID_KEY = "time_entry_id";

    private OnTimeEntryFormListener mListener;
    private TimeEntryFormPresenter mPresenter;
    private ViewHolder mViewHolder;
    private long mTimeEntryId;
    private boolean mIsUpdate;

    public static TimeEntryFormFormFragment newInstance() {
        return new TimeEntryFormFormFragment();
    }

    public static TimeEntryFormFormFragment newInstance(long timeEntryId) {
        TimeEntryFormFormFragment fragment = new TimeEntryFormFormFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(TIME_ENTRY_ID_KEY, timeEntryId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new TimeEntryFormPresenterImpl(this);
        Bundle args = getArguments();
        if (args != null && args.containsKey(TIME_ENTRY_ID_KEY)) {
            mTimeEntryId = args.getLong(TIME_ENTRY_ID_KEY);
            mIsUpdate = true;
            setHasOptionsMenu(true);
        } else {
            mTimeEntryId = -1;
            mIsUpdate = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mIsUpdate) mListener.onActivityTitleRequested(R.string.time_entry_form_update_title);
        else mListener.onActivityTitleRequested(R.string.time_entry_form_create_title);
        View rootView = inflater.inflate(R.layout.time_entry_form_fragment, container, false);
        mViewHolder = new ViewHolder(rootView);
        ButterKnife.bind(this, rootView);

        setupDatePicker();
        setupSubmitButton();
        if (mIsUpdate) mPresenter.fetchTimeEntry(mTimeEntryId);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTimeEntryFormListener) {
            mListener = (OnTimeEntryFormListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnTimeEntryFormListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_time_entry_update, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showDeleteConfirmationDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.submit_button)
    public void onSubmitClicked() {
        mPresenter.submitTimeEntry(mIsUpdate ? mTimeEntryId : -1);
    }

    private void setupDatePicker() {
        mViewHolder.datePicker.setMaxDate(new Date().getTime());
    }

    private void setupSubmitButton() {
        if (mIsUpdate) {
            mViewHolder.submitButton.setText(R.string.time_entry_form_update_submit_text);
        } else {
            mViewHolder.submitButton.setText(R.string.time_entry_form_create_submit_text);
        }
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
        Snackbar.make(mViewHolder.rootView,
                R.string.time_entry_form_create_success, LENGTH_LONG).show();
        mListener.goToTimeEntriesList();
    }

    @Override
    public void onUpdateSuccess() {
        Snackbar.make(mViewHolder.rootView,
                R.string.time_entry_form_update_success, LENGTH_LONG).show();
        mListener.goToTimeEntriesList();
    }

    @Override
    public void onDeletionSuccess() {
        Snackbar.make(mViewHolder.rootView,
                R.string.time_entry_form_delete_success, LENGTH_LONG).show();
        mListener.goToTimeEntriesList();
    }

    @Override
    public void populateDate(Calendar calendar) {
        mViewHolder.datePicker.updateDate(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void populateDistance(long distance) {
        mViewHolder.distanceField.setText(String.valueOf(distance));
    }

    @Override
    public void populateDuration(long duration) {
        mViewHolder.hoursField.setText(String.format(Locale.US, "%02d", duration / 60));
        mViewHolder.minutesField.setText(String.format(Locale.US, "%02d", duration % 60));
    }

    @Override
    public void showEmptyDurationError() {
        mViewHolder.minutesField.setError(getString(R.string.error_field_required));
        mViewHolder.minutesField.requestFocus();
    }

    @Override
    public void showNegativeDurationError() {
        mViewHolder.minutesField.setError(getString(R.string.time_entry_form_error_negative));
        mViewHolder.minutesField.requestFocus();
    }

    @Override
    public void showInvalidDurationError() {
        mViewHolder.minutesField.setError(getString(R.string.time_entry_form_error_invalid));
        mViewHolder.minutesField.requestFocus();
    }

    @Override
    public void showEmptyDistanceError() {
        mViewHolder.distanceField.setError(getString(R.string.error_field_required));
        mViewHolder.distanceField.requestFocus();
    }

    @Override
    public void showNegativeDistanceError() {
        mViewHolder.distanceField.setError(getString(R.string.time_entry_form_error_negative));
        mViewHolder.distanceField.requestFocus();
    }

    @Override
    public void showInvalidDistanceError() {
        mViewHolder.distanceField.setError(getString(R.string.time_entry_form_error_invalid));
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
    public void showUnableToParseDateError() {
        mViewHolder.errorMessage.setVisibility(View.VISIBLE);
        mViewHolder.errorMessage.setText(R.string.time_entry_form_error_parsing_date);
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

    private void showDeleteConfirmationDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        DeleteConfirmDialogFragment alert = DeleteConfirmDialogFragment.newInstance(this);
        alert.show(fm, DeleteConfirmDialogFragment.class.getSimpleName());
    }

    @Override
    public void onDeleteConfirmed() {
        mPresenter.deleteTimeEntry(mTimeEntryId);
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

    public interface OnTimeEntryFormListener {

        void onActivityTitleRequested(int titleResId);

        void goToTimeEntriesList();
    }
}
