package com.svanegas.trackmyjog.repository.main;

import com.svanegas.trackmyjog.repository.model.TimeEntry;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class RemoteTimeEntryRepository implements TimeEntryRepository {

    private TimeEntryService mService;

    @Inject
    public RemoteTimeEntryRepository(TimeEntryService timeEntryService) {
        mService = timeEntryService;
    }

    @Override
    public Single<List<TimeEntry>> fetchTimeEntries() {
        return mService.fetchTimeEntries();
    }

    @Override
    public Single<TimeEntry> fetchTimeEntry(long timeEntryId) {
        return mService.fetchTimeEntry(timeEntryId);
    }

    @Override
    public Single<TimeEntry> createTimeEntry(String date, String distance, long duration) {
        return mService.createTimeEntry(date, distance, duration);
    }

    @Override
    public Single<TimeEntry> updateTimeEntry(long timeEntryId,
                                             String date,
                                             String distance,
                                             long duration) {
        return mService.updateTimeEntry(timeEntryId, date, distance, duration);
    }

    @Override
    public Single<Object> deleteTimeEntry(long timeEntryId) {
        return mService.deleteTimeEntry(timeEntryId);
    }
}
