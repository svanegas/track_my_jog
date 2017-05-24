package com.svanegas.trackmyjog.domain.main.time_entry;

import android.text.Spannable;

import java.text.ParseException;

public interface TimeEntriesListPresenter {

    void fetchTimeEntries();

    String setupFormattedDate(String date) throws ParseException;

    Spannable setupDistanceText(long distance);

    String setupDurationText(long duration);
}
