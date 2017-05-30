package com.svanegas.trackmyjog.repository.model;

import com.google.gson.annotations.SerializedName;

public class Report {

    private int count;
    private String from;
    private String to;
    @SerializedName("distance_sum")
    private long distanceSum;
    @SerializedName("duration_sum")
    private long durationSum;

    public int getCount() {
        return count;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public long getDistanceSum() {
        return distanceSum;
    }

    public long getDurationSum() {
        return durationSum;
    }
}
