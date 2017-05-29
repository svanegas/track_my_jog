package com.svanegas.trackmyjog.domain.main.report;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.svanegas.trackmyjog.R;
import com.svanegas.trackmyjog.domain.main.dialog.DatePickerDialogFragment;
import com.svanegas.trackmyjog.repository.model.Report;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReportFragment extends Fragment implements ReportView,
        DatePickerDialogFragment.Callback, SwipeRefreshLayout.OnRefreshListener {

    private OnReportListener mListener;
    private ReportPresenter mPresenter;
    private ViewHolder mViewHolder;
    private Calendar mCalendar;

    public static ReportFragment newInstance() {
        return new ReportFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ReportPresenterImpl(this);
        mCalendar = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mListener.onActivityTitleRequested(R.string.report_title);
        View rootView = inflater.inflate(R.layout.report_fragment, container, false);
        mViewHolder = new ViewHolder(rootView);
        ButterKnife.bind(this, rootView);
        mViewHolder.swipeRefreshLayout.setOnRefreshListener(this);

        mPresenter.fetchReport(mCalendar, false);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnReportListener) {
            mListener = (OnReportListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnReportListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        mPresenter.fetchReport(mCalendar, true);
    }

    @OnClick(R.id.range_button)
    public void onPickDateClicked() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        DatePickerDialogFragment alert = DatePickerDialogFragment.newInstance(this, mCalendar);
        alert.show(fm, DatePickerDialogFragment.class.getSimpleName());
    }

    @Override
    public void onDateSet(Calendar date) {
        mCalendar = date;
        mPresenter.fetchReport(mCalendar, false);
    }

    @Override
    public void populateDateRange(String dateFrom, String dateTo) {
        mViewHolder.rangeButton.setText(getString(R.string.report_date_range_format,
                dateFrom, dateTo));
    }

    @Override
    public void populateEmpty() {
        mViewHolder.reportContent.setVisibility(View.GONE);
        mViewHolder.emptyScreen.setVisibility(View.VISIBLE);
        mViewHolder.emptyMessage.setText(R.string.report_empty_range);
    }

    @Override
    public void populateReport(Report report) {
        mViewHolder.reportContent.setVisibility(View.VISIBLE);
        mViewHolder.emptyScreen.setVisibility(View.GONE);
        mViewHolder.entriesCount.setText(String.valueOf(report.getCount()));
        mViewHolder.distanceSum.setText(String.valueOf(report.getDistanceSum()));
        mViewHolder.durationSum.setText(String.valueOf(report.getDurationSum()));
    }

    @Override
    public void showLoadingAndDisableFields(boolean pulledToRefresh) {
        mViewHolder.errorMessage.setVisibility(View.GONE);
        setLoadingAndFieldsEnabled(false, pulledToRefresh);
    }

    @Override
    public void hideLoadingAndEnableFields(boolean pulledToRefresh) {
        setLoadingAndFieldsEnabled(true, pulledToRefresh);
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

    private void setLoadingAndFieldsEnabled(boolean enabled, boolean pulledToRefresh) {
        if (pulledToRefresh) mViewHolder.swipeRefreshLayout.setRefreshing(!enabled);
        else mViewHolder.progressBar.setVisibility(enabled ? View.GONE : View.VISIBLE);
        mViewHolder.rangeButton.setEnabled(enabled);
    }

    static class ViewHolder {

        @BindView(R.id.swipe_refresh_layout)
        SwipeRefreshLayout swipeRefreshLayout;

        @BindView(R.id.error_message)
        AppCompatTextView errorMessage;

        @BindView(R.id.range_button)
        AppCompatButton rangeButton;

        @BindView(R.id.report_content)
        ViewGroup reportContent;

        @BindView(R.id.entries_count_value)
        AppCompatTextView entriesCount;

        @BindView(R.id.distance_sum_value)
        AppCompatTextView distanceSum;

        @BindView(R.id.duration_sum_value)
        AppCompatTextView durationSum;

        @BindView(R.id.screen_message_root)
        ViewGroup emptyScreen;

        @BindView(R.id.screen_message_text)
        AppCompatTextView emptyMessage;

        @BindView(R.id.progress_bar)
        ProgressBar progressBar;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);

            swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                    R.color.colorAccentDark);
        }
    }

    public interface OnReportListener {

        void onActivityTitleRequested(int titleResId);
    }
}
