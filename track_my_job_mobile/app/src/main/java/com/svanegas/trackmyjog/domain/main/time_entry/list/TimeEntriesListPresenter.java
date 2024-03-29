package com.svanegas.trackmyjog.domain.main.time_entry.list;

import android.text.Spannable;

import com.svanegas.trackmyjog.repository.model.TimeEntry;
import com.svanegas.trackmyjog.repository.model.User;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

public interface TimeEntriesListPresenter {

    void determineActivityTitle();

    void processSelectedFilterFrom(Calendar date);

    void processSelectedFilterTo(Calendar date);

    void timeEntryClicked(TimeEntry timeEntry);

    void fetchTimeEntries(boolean pulledToRefresh);

    void fetchTimeEntriesByCurrentUser(boolean pulledToRefresh);

    void sortTimeEntries(int sortOption, List<TimeEntry> timeEntries);

    String setupFormattedDate(String date) throws ParseException;

    Spannable setupDistanceText(long distance);

    Spannable setupDurationText(long duration);

    Spannable setupSpeedText(TimeEntry timeEntry);

    boolean isUserVisible();

    String setupUserText(User user);

    void unsubscribe();
}
