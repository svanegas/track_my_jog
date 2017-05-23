package com.svanegas.trackmyjog.repository.main;

import com.svanegas.trackmyjog.repository.model.TimeEntry;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface TimeEntryService {

    @GET("time_entries")
    Single<List<TimeEntry>> fetchTimeEntries();
}
