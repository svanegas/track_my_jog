package com.svanegas.trackmyjog.repository.model;

public class TimeEntry {

    private long id;
    private String date;
    private long distance;
    private long duration;
    private User user;

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public long getDistance() {
        return distance;
    }

    public long getDuration() {
        return duration;
    }

    public User getUser() {
        return user;
    }
}
