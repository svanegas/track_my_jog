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
import com.svanegas.trackmyjog.domain.main.dialog.SortByDialogFragment;
import com.svanegas.trackmyjog.domain.main.time_entry.list.adapter.TimeEntriesAdapter;
import com.svanegas.trackmyjog.repository.model.TimeEntry;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.svanegas.trackmyjog.domain.main.time_entry.list.TimeEntriesListPresenterImpl.DATE_SORT_INDEX;

public class TimeEntriesListFragment extends Fragment implements TimeEntriesListView,
        SwipeRefreshLayout.OnRefreshListener, SortByDialogFragment.Callback {

    // Indexes of toolbar spinner
    private static final int ALL_RECORDS_INDEX = 0;

    private OnTimeEntriesListInteractionListener mListener;
    private TimeEntriesListPresenter mPresenter;
    private ViewHolder mViewHolder;
    private TimeEntriesAdapter mAdapter;
    private int mSelectedRecordsSpinnerIndex;
    private int mCurrentSortOption;

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
        if (mSelectedRecordsSpinnerIndex == ALL_RECORDS_INDEX) mPresenter.fetchTimeEntries(true);
        else mPresenter.fetchTimeEntriesByCurrentUser(true);
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
    }

    @Override
    public void hideLoading(boolean pulledToRefresh) {
        if (pulledToRefresh) mViewHolder.swipeRefreshLayout.setRefreshing(false);
        else mViewHolder.progress.setVisibility(View.GONE);
    }

    @Override
    public void populateTimeEntries(List<TimeEntry> timeEntries) {
        mPresenter.sortTimeEntries(mCurrentSortOption, timeEntries);
        mAdapter = new TimeEntriesAdapter(mPresenter, timeEntries);
        mViewHolder.timeEntriesList.setLayoutManager(new LinearLayoutManager(getContext()));
        mViewHolder.timeEntriesList.setAdapter(mAdapter);
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
        Toast.makeText(getContext(), "Welcome due unauthorized", Toast.LENGTH_SHORT).show();
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

    public void toolbarSpinnerItemSelected(int position) {
        if (position != mSelectedRecordsSpinnerIndex) {
            mSelectedRecordsSpinnerIndex = position;
            if (mSelectedRecordsSpinnerIndex == ALL_RECORDS_INDEX)
                mPresenter.fetchTimeEntries(false);
            else mPresenter.fetchTimeEntriesByCurrentUser(false);
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

        @BindView(R.id.time_entries_list)
        RecyclerView timeEntriesList;

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
    }
}
