package com.svanegas.trackmyjog;

import android.app.Application;

public class TrackMyJogApplication extends Application {

    private static TrackMyJogApplication mInstance;
    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }

    public static TrackMyJogApplication getInstance() {
        return mInstance;
    }
}
