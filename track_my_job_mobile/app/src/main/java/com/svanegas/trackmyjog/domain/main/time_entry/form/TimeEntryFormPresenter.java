package com.svanegas.trackmyjog.domain.main.time_entry.form;

interface TimeEntryFormPresenter {

    void setupDistanceUnits();

    void submitTimeEntry(long timeEntryId);

    void fetchTimeEntry(long timeEntryId);

    void deleteTimeEntry(long timeEntryId);
}
