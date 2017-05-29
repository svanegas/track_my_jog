package com.svanegas.trackmyjog.domain.main.time_entry.list;

import com.svanegas.trackmyjog.repository.model.TimeEntry;

import java.util.List;

interface TimeEntriesListView {

    void setupSpinnerAsTitle();

    void setupRegularTitle();

    void onTimeEntryClicked(TimeEntry timeEntry);

    void showLoading(boolean pulledToRefresh);

    void hideLoading(boolean pulledToRefresh);

    void populateTimeEntries(List<TimeEntry> timeEntries);

    void populateEmpty();

    void showTimeoutError();

    void showNoConnectionError();

    void goToWelcomeDueUnauthorized();

    void showDisplayableError(String errorMessage);

    void showUnknownError();
}
