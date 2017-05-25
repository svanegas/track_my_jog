package com.svanegas.trackmyjog.domain.main.time_entry.list;

import com.svanegas.trackmyjog.repository.model.TimeEntry;

import java.util.List;

interface TimeEntriesListView {

    void onTimeEntryClicked(TimeEntry timeEntry);

    void showLoading(boolean pulledToRefresh);

    void hideLoading(boolean pulledToRefresh);

    void populateTimeEntries(List<TimeEntry> timeEntries);

    void showTimeoutError();

    void showNoConnectionError();

    void goToWelcomeDueUnauthorized();

    void showDisplayableError(String errorMessage);

    void showUnknownError();
}
