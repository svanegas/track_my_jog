package com.svanegas.trackmyjog.interactor;

import com.svanegas.trackmyjog.domain.main.report.ReportFragment;
import com.svanegas.trackmyjog.repository.main.RemoteTimeEntryRepository;
import com.svanegas.trackmyjog.repository.main.TimeEntryRepository;
import com.svanegas.trackmyjog.repository.model.Report;
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


    public Single<List<TimeEntry>> fetchTimeEntries(String dateFrom, String dateTo) {
        return mRemoteRepository.fetchTimeEntries(dateFrom, dateTo);
    }

    public Single<List<TimeEntry>> fetchTimeEntries(long userId, String dateFrom, String dateTo) {
        return mRemoteRepository.fetchTimeEntries(userId, dateFrom, dateTo);
    }

    public Single<TimeEntry> fetchTimeEntry(long timeEntryId) {
        return mRemoteRepository.fetchTimeEntry(timeEntryId);
    }

    public Single<TimeEntry> createTimeEntry(String date, long distance, long duration) {
        return mRemoteRepository.createTimeEntry(date, distance, duration);
    }

    public Single<TimeEntry> updateTimeEntry(long timeEntryId,
                                             String date,
                                             long distance,
                                             long duration) {
        return mRemoteRepository.updateTimeEntry(timeEntryId, date, distance, duration);
    }

    public Single<Object> deleteTimeEntry(long timeEntryId) {
        return mRemoteRepository.deleteTimeEntry(timeEntryId);
    }

    public Single<Report> fetchReport(String date) {
        return mRemoteRepository.fetchReport(date);
    }
}
