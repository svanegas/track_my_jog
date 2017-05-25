package com.svanegas.trackmyjog.repository.main;

import com.svanegas.trackmyjog.repository.model.TimeEntry;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TimeEntryService {

    @GET("time_entries")
    Single<List<TimeEntry>> fetchTimeEntries();

    @GET("time_entries")
    Single<List<TimeEntry>> fetchTimeEntries(@Query("user_id") long userId);

    @POST("time_entries")
    @FormUrlEncoded
    Single<TimeEntry> createTimeEntry(@Field("date") String date,
                                      @Field("distance") String distance,
                                      @Field("duration") long duration);

    @GET("time_entries/{timeEntryId}")
    Single<TimeEntry> fetchTimeEntry(@Path("timeEntryId") long timeEntryId);

    @PATCH("time_entries/{timeEntryId}")
    @FormUrlEncoded
    Single<TimeEntry> updateTimeEntry(@Path("timeEntryId") long timeEntryId,
                                      @Field("date") String date,
                                      @Field("distance") String distance,
                                      @Field("duration") long duration);

    @DELETE("time_entries/{timeEntryId}")
    Single<Object> deleteTimeEntry(@Path("timeEntryId") long timeEntryId);
}
