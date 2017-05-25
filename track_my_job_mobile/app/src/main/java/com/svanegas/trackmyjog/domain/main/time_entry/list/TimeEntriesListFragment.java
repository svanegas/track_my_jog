package com.svanegas.trackmyjog.domain.main.time_entry.list;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.svanegas.trackmyjog.R;
import com.svanegas.trackmyjog.domain.main.time_entry.list.adapter.TimeEntriesAdapter;
import com.svanegas.trackmyjog.repository.model.TimeEntry;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TimeEntriesListFragment extends Fragment implements TimeEntriesListView,
        SwipeRefreshLayout.OnRefreshListener {

    private OnMyTimeEntriesListInteractionListener mListener;
    private TimeEntriesListPresenter mPresenter;
    private ViewHolder mViewHolder;
    private TimeEntriesAdapter mAdapter;

    public static TimeEntriesListFragment newInstance() {
        return new TimeEntriesListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new TimeEntriesListPresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mListener.onActivityTitleRequested(R.string.my_time_entries_title);
        View rootView = inflater.inflate(R.layout.my_time_entries_fragment, container, false);
        mViewHolder = new ViewHolder(rootView);
        ButterKnife.bind(this, rootView);
        mViewHolder.swipeRefreshLayout.setOnRefreshListener(this);
        mPresenter.fetchTimeEntries(false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMyTimeEntriesListInteractionListener) {
            mListener = (OnMyTimeEntriesListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnMyTimeEntriesListInteractionListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        mPresenter.fetchTimeEntries(true);
    }

    @OnClick(R.id.add_button)
    public void onAddClicked() {
        mListener.onAddTimeEntryRequested();
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
        if (mAdapter == null) {
            mAdapter = new TimeEntriesAdapter(mPresenter, timeEntries);
        } else mAdapter.updateData(timeEntries);
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

    public interface OnMyTimeEntriesListInteractionListener {

        void onActivityTitleRequested(int titleResId);

        void onAddTimeEntryRequested();

        void onUpdateTimeEntryRequested(long timeEntryId);
    }
}
