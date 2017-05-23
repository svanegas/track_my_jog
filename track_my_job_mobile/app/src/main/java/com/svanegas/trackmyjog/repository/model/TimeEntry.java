package com.svanegas.trackmyjog.repository.model;

import com.google.gson.annotations.SerializedName;

public class TimeEntry {

    private long id;
    private String date;
    private long distance;
    private long duration;
    @SerializedName("user_id")
    private long userId;
}
