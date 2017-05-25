package com.svanegas.trackmyjog.domain.main.time_entry.form;

interface TimeEntryFormPresenter {

    void submitTimeEntry(long timeEntryId);

    void fetchTimeEntry(long mTimeEntryId);
}
