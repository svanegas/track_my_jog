package com.svanegas.trackmyjog.repository.main;

import com.svanegas.trackmyjog.repository.model.TimeEntry;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface TimeEntryService {

    @GET("time_entries")
    Single<List<TimeEntry>> fetchTimeEntries();

    @POST("time_entries")
    @FormUrlEncoded
    Single<TimeEntry> createTimeEntry(@Field("date") String date,
                                      @Field("distance") String distance,
                                      @Field("duration") int duration);
}
