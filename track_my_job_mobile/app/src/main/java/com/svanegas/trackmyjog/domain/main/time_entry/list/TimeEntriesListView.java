package com.svanegas.trackmyjog.domain.main.time_entry.list;

import com.svanegas.trackmyjog.repository.model.TimeEntry;

import java.util.Calendar;
import java.util.List;

interface TimeEntriesListView {

    Calendar dateFrom();

    Calendar dateTo();

    void setupSpinnerAsTitle();

    void setupRegularTitle();

    void onTimeEntryClicked(TimeEntry timeEntry);

    void showLoading(boolean pulledToRefresh);

    void hideLoading(boolean pulledToRefresh);

    boolean shouldDisplayUserInList();

    void populateFilterDateFrom(String date);

    void populateFilterDateTo(String date);

    void populateTimeEntries(List<TimeEntry> timeEntries);

    void populateEmpty();

    void showTimeoutError();

    void showNoConnectionError();

    void goToWelcomeDueUnauthorized();

    void showDisplayableError(String errorMessage);

    void showUnknownError();
}
