package com.svanegas.trackmyjog.repository.main;

import com.svanegas.trackmyjog.repository.model.TimeEntry;

import java.util.List;

import io.reactivex.Single;

public interface TimeEntryRepository {

    Single<List<TimeEntry>> fetchTimeEntries();

    Single<TimeEntry> fetchTimeEntry(long timeEntryId);

    Single<TimeEntry> createTimeEntry(String date, String distance, long duration);

    Single<TimeEntry> updateTimeEntry(long timeEntryId,
                                      String date,
                                      String distance,
                                      long duration);

    Single<Object> deleteTimeEntry(long timeEntryId);
}
