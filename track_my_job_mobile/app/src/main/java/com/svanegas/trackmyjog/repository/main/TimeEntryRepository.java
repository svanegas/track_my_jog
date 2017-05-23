package com.svanegas.trackmyjog.repository.main;

import com.svanegas.trackmyjog.repository.model.TimeEntry;

import java.util.List;

import io.reactivex.Single;

public interface TimeEntryRepository {

    Single<List<TimeEntry>> fetchTimeEntries();
}
