package com.svanegas.trackmyjog.domain.main.time_entry.list.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.svanegas.trackmyjog.R;
import com.svanegas.trackmyjog.domain.main.time_entry.list.TimeEntriesListPresenter;
import com.svanegas.trackmyjog.repository.model.TimeEntry;

import java.text.ParseException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimeEntriesAdapter extends RecyclerView.Adapter<TimeEntriesAdapter.ViewHolder> {

    private static final String TAG = TimeEntriesAdapter.class.getSimpleName();

    private TimeEntriesListPresenter mPresenter;
    private List<TimeEntry> mTimeEntries;

    public TimeEntriesAdapter(TimeEntriesListPresenter presenter,
                              @NonNull List<TimeEntry> timeEntries) {
        mPresenter = presenter;
        mTimeEntries = timeEntries;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemRow = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.time_entries_list_item, parent, false);
        return new ViewHolder(itemRow);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TimeEntry timeEntry = mTimeEntries.get(position);

        try {
            holder.dateValue.setText(mPresenter.setupFormattedDate(timeEntry.getDate()));
        } catch (ParseException e) {
            Log.e(TAG, "There was an error parsing the date of time entry " + position, e);
            holder.dateValue.setText(R.string.time_entries_list_no_date_available);
        }

        holder.distanceValue.setText(mPresenter.setupDistanceText(timeEntry.getDistance()));
        holder.durationValue.setText(mPresenter.setupDurationText(timeEntry.getDuration()));
        holder.speedValue.setText(mPresenter.setupSpeedText(timeEntry));
        holder.rootView.setOnClickListener(view -> mPresenter.timeEntryClicked(timeEntry));
    }

    @Override
    public int getItemCount() {
        return mTimeEntries != null ? mTimeEntries.size() : 0;
    }

    public List<TimeEntry> getItems() {
        return mTimeEntries;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.root_view)
        ViewGroup rootView;

        @BindView(R.id.date_value)
        AppCompatTextView dateValue;

        @BindView(R.id.distance_value)
        AppCompatTextView distanceValue;

        @BindView(R.id.duration_value)
        AppCompatTextView durationValue;

        @BindView(R.id.speed_value)
        AppCompatTextView speedValue;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
