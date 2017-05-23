package com.svanegas.trackmyjog.domain.main.time_entry;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.svanegas.trackmyjog.R;
import com.svanegas.trackmyjog.repository.model.TimeEntry;

import java.util.List;

import butterknife.ButterKnife;

public class TimeEntriesListFragment extends Fragment implements TimeEntriesListView {

    private OnMyRecordsInteractionListener mListener;
    private TimeEntriesListPresenter mPresenter;

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
        ButterKnife.bind(this, rootView);
        mPresenter.fetchTimeEntries();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMyRecordsInteractionListener) {
            mListener = (OnMyRecordsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnMyRecordsInteractionListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void showLoading() {
        Toast.makeText(getContext(), "Loading", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void populateTimeEntries(List<TimeEntry> timeEntries) {
        Toast.makeText(getContext(), "Populating " + timeEntries.size() + " entries",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showTimeoutError() {
        Toast.makeText(getContext(), "Timeout error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoConnectionError() {
        Toast.makeText(getContext(), "No connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void goToWelcomeDueUnauthorized() {
        Toast.makeText(getContext(), "Welcome due unauthorized", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDisplayableError(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showUnknownError() {
        Toast.makeText(getContext(), "Unknown error", Toast.LENGTH_SHORT).show();
    }

    public interface OnMyRecordsInteractionListener {

        void onActivityTitleRequested(int titleResId);
    }
}
