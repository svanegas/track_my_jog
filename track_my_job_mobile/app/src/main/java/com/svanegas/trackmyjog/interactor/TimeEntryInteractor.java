package com.svanegas.trackmyjog.interactor;

import com.svanegas.trackmyjog.repository.main.RemoteTimeEntryRepository;
import com.svanegas.trackmyjog.repository.main.TimeEntryRepository;
import com.svanegas.trackmyjog.repository.model.TimeEntry;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class TimeEntryInteractor {

    private TimeEntryRepository mRemoteRepository;

    @Inject
    public TimeEntryInteractor(RemoteTimeEntryRepository remoteRepository) {
        mRemoteRepository = remoteRepository;
    }


    public Single<List<TimeEntry>> fetchTimeEntries() {
        return mRemoteRepository.fetchTimeEntries();
    }

    public Single<List<TimeEntry>> fetchTimeEntries(long userId) {
        return mRemoteRepository.fetchTimeEntries(userId);
    }

    public Single<TimeEntry> fetchTimeEntry(long timeEntryId) {
        return mRemoteRepository.fetchTimeEntry(timeEntryId);
    }

    public Single<TimeEntry> createTimeEntry(String date, String distance, long duration) {
        return mRemoteRepository.createTimeEntry(date, distance, duration);
    }

    public Single<TimeEntry> updateTimeEntry(long timeEntryId,
                                             String date,
                                             String distance,
                                             long duration) {
        return mRemoteRepository.updateTimeEntry(timeEntryId, date, distance, duration);
    }

    public Single<Object> deleteTimeEntry(long timeEntryId) {
        return mRemoteRepository.deleteTimeEntry(timeEntryId);
    }
}
