package com.svanegas.trackmyjog.domain.main.time_entry.list;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.svanegas.trackmyjog.R;
import com.svanegas.trackmyjog.domain.main.dialog.DatePickerDialogFragment;
import com.svanegas.trackmyjog.domain.main.dialog.SortByDialogFragment;
import com.svanegas.trackmyjog.domain.main.time_entry.list.adapter.TimeEntriesAdapter;
import com.svanegas.trackmyjog.repository.model.TimeEntry;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.svanegas.trackmyjog.domain.main.time_entry.list.TimeEntriesListPresenterImpl.DATE_SORT_INDEX;

public class TimeEntriesListFragment extends Fragment implements TimeEntriesListView,
        SwipeRefreshLayout.OnRefreshListener, SortByDialogFragment.Callback, DatePickerDialogFragment.Callback {

    // Indexes of toolbar spinner
    private static final int ALL_RECORDS_INDEX = 0;

    // Identifiers for date picker for filters
    private static final int DATE_FROM_IDENTIFIER = 0;
    private static final int DATE_TO_IDENTIFIER = 1;

    private OnTimeEntriesListInteractionListener mListener;
    private TimeEntriesListPresenter mPresenter;
    private ViewHolder mViewHolder;
    private TimeEntriesAdapter mAdapter;
    private int mSelectedRecordsSpinnerIndex;
    private int mCurrentSortOption;
    private Calendar mDateFilterFrom;
    private Calendar mDateFilterTo;

    public static TimeEntriesListFragment newInstance() {
        return new TimeEntriesListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new TimeEntriesListPresenterImpl(this);
        mCurrentSortOption = DATE_SORT_INDEX;
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.time_entries_list_fragment, container, false);
        mViewHolder = new ViewHolder(rootView);
        ButterKnife.bind(this, rootView);
        mViewHolder.swipeRefreshLayout.setOnRefreshListener(this);
        mSelectedRecordsSpinnerIndex = -1;
        mPresenter.determineActivityTitle();

        setupDateFiltersTexts();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTimeEntriesListInteractionListener) {
            mListener = (OnTimeEntriesListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnTimeEntriesListInteractionListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_time_entries_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by:
                showSortByDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        fetchTimeEntries(true);
    }

    @Override
    public void onSortOptionSelected(int position) {
        if (mCurrentSortOption != position) {
            mCurrentSortOption = position;
            if (mAdapter != null) populateTimeEntries(mAdapter.getItems());
        }
    }

    @OnClick(R.id.add_button)
    public void onAddClicked() {
        mListener.onAddTimeEntryRequested();
    }

    @OnClick(R.id.date_filter_from)
    public void onDateFilterFromClicked() {
        Calendar initialDate;
        if (mDateFilterFrom != null) initialDate = mDateFilterFrom;
        else initialDate = Calendar.getInstance();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        DatePickerDialogFragment alert = DatePickerDialogFragment
                .newInstance(this, initialDate, null, mDateFilterTo,
                        DATE_FROM_IDENTIFIER, true);
        alert.show(fm, DatePickerDialogFragment.class.getSimpleName());
    }

    @OnClick(R.id.date_filter_to)
    public void onDateFilterToClicked() {
        Calendar initialDate;
        if (mDateFilterTo != null) initialDate = mDateFilterTo;
        else initialDate = Calendar.getInstance();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        DatePickerDialogFragment alert = DatePickerDialogFragment
                .newInstance(this, initialDate, mDateFilterFrom, null, DATE_TO_IDENTIFIER, true);
        alert.show(fm, DatePickerDialogFragment.class.getSimpleName());
    }

    @Override
    public void onDateSet(Calendar date, int identifier) {
        switch (identifier) {
            case DATE_FROM_IDENTIFIER:
                mDateFilterFrom = date;
                mPresenter.processSelectedFilterFrom(mDateFilterFrom);
                break;
            case DATE_TO_IDENTIFIER:
                mDateFilterTo = date;
                mPresenter.processSelectedFilterTo(mDateFilterTo);
                break;
        }
        fetchTimeEntries(false);
    }

    @Override
    public void onDateCleared(int identifier) {
        switch (identifier) {
            case DATE_FROM_IDENTIFIER:
                mDateFilterFrom = null;
                mViewHolder.dateFilterFrom.setText(R.string.time_entries_list_filter_from);
                break;
            case DATE_TO_IDENTIFIER:
                mDateFilterTo = null;
                mViewHolder.dateFilterTo.setText(R.string.time_entries_list_filter_to);
                break;
        }
        fetchTimeEntries(false);
    }

    @Override
    public Calendar dateFrom() {
        return mDateFilterFrom;
    }

    @Override
    public Calendar dateTo() {
        return mDateFilterTo;
    }

    @Override
    public void setupSpinnerAsTitle() {
        mListener.onActivityTitleSpinnerRequested();
    }

    @Override
    public void setupRegularTitle() {
        mListener.onActivityTitleRequested(R.string.time_entries_list_my_records);
    }

    @Override
    public void onTimeEntryClicked(TimeEntry timeEntry) {
        mListener.onUpdateTimeEntryRequested(timeEntry.getId());
    }

    @Override
    public void showLoading(boolean pulledToRefresh) {
        if (!pulledToRefresh) mViewHolder.progress.setVisibility(View.VISIBLE);
        mViewHolder.errorMessage.setVisibility(View.GONE);
        mViewHolder.dateFilterFrom.setEnabled(false);
        mViewHolder.dateFilterTo.setEnabled(false);
    }

    @Override
    public void hideLoading(boolean pulledToRefresh) {
        if (pulledToRefresh) mViewHolder.swipeRefreshLayout.setRefreshing(false);
        else mViewHolder.progress.setVisibility(View.GONE);
        mViewHolder.dateFilterFrom.setEnabled(true);
        mViewHolder.dateFilterTo.setEnabled(true);
    }

    @Override
    public void populateFilterDateFrom(String date) {
        mViewHolder.dateFilterFrom.setText(date);
    }

    @Override
    public void populateFilterDateTo(String date) {
        mViewHolder.dateFilterTo.setText(date);
    }

    @Override
    public void populateTimeEntries(List<TimeEntry> timeEntries) {
        mViewHolder.timeEntriesList.setVisibility(View.VISIBLE);
        mViewHolder.emptyScreen.setVisibility(View.GONE);
        mPresenter.sortTimeEntries(mCurrentSortOption, timeEntries);
        mAdapter = new TimeEntriesAdapter(mPresenter, timeEntries);
        mViewHolder.timeEntriesList.setLayoutManager(new LinearLayoutManager(getContext()));
        mViewHolder.timeEntriesList.setAdapter(mAdapter);
    }

    @Override
    public void populateEmpty() {
        mViewHolder.timeEntriesList.setVisibility(View.GONE);
        mViewHolder.emptyScreen.setVisibility(View.VISIBLE);
        if (mSelectedRecordsSpinnerIndex == ALL_RECORDS_INDEX)
            mViewHolder.emptyMessage.setText(R.string.time_entries_list_empty_all);
        else mViewHolder.emptyMessage.setText(R.string.time_entries_list_empty);

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
    public void goToWelcomeDueUnauthorized() {
        mListener.onUnauthorizedUser();
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

    private void fetchTimeEntries(boolean pulledToRefresh) {
        if (mSelectedRecordsSpinnerIndex == ALL_RECORDS_INDEX)
            mPresenter.fetchTimeEntries(pulledToRefresh);
        else mPresenter.fetchTimeEntriesByCurrentUser(pulledToRefresh);
    }

    private void setupDateFiltersTexts() {
        if (mDateFilterFrom != null) mPresenter.processSelectedFilterFrom(mDateFilterFrom);
        if (mDateFilterTo != null) mPresenter.processSelectedFilterTo(mDateFilterTo);
    }

    public void toolbarSpinnerItemSelected(int position) {
        if (position != mSelectedRecordsSpinnerIndex) {
            mSelectedRecordsSpinnerIndex = position;
            fetchTimeEntries(false);
        }
    }

    private void showSortByDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        SortByDialogFragment dialog = SortByDialogFragment.newInstance(this,
                R.array.time_entries_list_sort_options);
        dialog.show(fm, SortByDialogFragment.class.getSimpleName());
    }

    static class ViewHolder {

        @BindView(R.id.swipe_refresh_layout)
        SwipeRefreshLayout swipeRefreshLayout;

        @BindView(R.id.error_message)
        AppCompatTextView errorMessage;

        @BindView(R.id.date_filter_from)
        AppCompatTextView dateFilterFrom;

        @BindView(R.id.date_filter_to)
        AppCompatTextView dateFilterTo;

        @BindView(R.id.time_entries_list)
        RecyclerView timeEntriesList;

        @BindView(R.id.screen_message_root)
        ViewGroup emptyScreen;

        @BindView(R.id.screen_message_text)
        AppCompatTextView emptyMessage;

        @BindView(R.id.progress)
        ProgressBar progress;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);

            swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                    R.color.colorAccentDark);
        }
    }

    public interface OnTimeEntriesListInteractionListener {

        void onActivityTitleRequested(int titleResId);

        void onActivityTitleSpinnerRequested();

        void onAddTimeEntryRequested();

        void onUpdateTimeEntryRequested(long timeEntryId);

        void onUnauthorizedUser();
    }
}
