package com.svanegas.trackmyjog.domain.main.time_entry.create;

interface CreateTimeEntryPresenter {

    void submitTimeEntry(long timeEntryId);

    void fetchTimeEntry(long mTimeEntryId);
}
