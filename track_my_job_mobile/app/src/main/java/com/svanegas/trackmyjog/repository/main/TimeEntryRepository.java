package com.svanegas.trackmyjog.repository.main;

import com.svanegas.trackmyjog.repository.model.Report;
import com.svanegas.trackmyjog.repository.model.TimeEntry;

import java.util.List;

import io.reactivex.Single;

public interface TimeEntryRepository {

    Single<List<TimeEntry>> fetchTimeEntries(String dateFrom, String dateTo);

    Single<List<TimeEntry>> fetchTimeEntries(long userId, String dateFrom, String dateTo);

    Single<TimeEntry> fetchTimeEntry(long timeEntryId);

    Single<TimeEntry> createTimeEntry(String date, long distance, long duration);

    Single<TimeEntry> updateTimeEntry(long timeEntryId,
                                      String date,
                                      long distance,
                                      long duration);

    Single<Object> deleteTimeEntry(long timeEntryId);

    Single<Report> fetchReport(String date);
}
