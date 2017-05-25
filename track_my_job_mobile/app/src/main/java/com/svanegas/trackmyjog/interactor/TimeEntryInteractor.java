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

    public Single<TimeEntry> createTimeEntry(String date, String distance, int duration) {
        return mRemoteRepository.createTimeEntry(date, distance, duration);
    }
}
