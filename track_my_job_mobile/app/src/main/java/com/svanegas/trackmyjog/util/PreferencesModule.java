package com.svanegas.trackmyjog.util;

import android.app.Application;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

@Module
public class PreferencesModule {

    @Provides
    @Singleton
    public PreferencesManager providePreferencesManager(SharedPreferences sharedPreferences) {
        return new PreferencesManager(sharedPreferences);
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences(Application application) {
        return getDefaultSharedPreferences(application);
    }
}
