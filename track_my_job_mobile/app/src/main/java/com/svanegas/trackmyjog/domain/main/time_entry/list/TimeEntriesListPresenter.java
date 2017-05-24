package com.svanegas.trackmyjog.domain.main.time_entry.list;

import android.text.Spannable;

import java.text.ParseException;

public interface TimeEntriesListPresenter {

    void fetchTimeEntries(boolean pulledToRefresh);

    String setupFormattedDate(String date) throws ParseException;

    Spannable setupDistanceText(long distance);

    String setupDurationText(long duration);
}
