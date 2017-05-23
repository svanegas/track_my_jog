package com.svanegas.trackmyjog.domain.main.time_entry;

import com.svanegas.trackmyjog.repository.model.TimeEntry;

import java.util.List;

interface TimeEntriesListView {

    void showLoading();

    void hideLoading();

    void populateTimeEntries(List<TimeEntry> timeEntries);

    void showTimeoutError();

    void showNoConnectionError();

    void goToWelcomeDueUnauthorized();

    void showDisplayableError(String errorMessage);

    void showUnknownError();
}
