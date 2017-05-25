package com.svanegas.trackmyjog.domain.main.time_entry.list;

import android.text.Spannable;

import com.svanegas.trackmyjog.repository.model.TimeEntry;

import java.text.ParseException;

public interface TimeEntriesListPresenter {

    void determineActivityTitle();

    void timeEntryClicked(TimeEntry timeEntry);

    void fetchTimeEntries(boolean pulledToRefresh);

    void fetchTimeEntriesByCurrentUser(boolean pulledToRefresh);

    String setupFormattedDate(String date) throws ParseException;

    Spannable setupDistanceText(long distance);

    String setupDurationText(long duration);
}
