package com.svanegas.trackmyjog.util;

import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PreferencesManager {

    public static final String ACCESS_TOKEN_KEY = "access-token";
    public static final String CLIENT_KEY = "client";
    public static final String UID_KEY = "uid";

    private SharedPreferences mPreferences;

    @Inject
    PreferencesManager(SharedPreferences sharedPreferences) {
        mPreferences = sharedPreferences;
    }

    public String getAuthToken() {
        return mPreferences.getString(ACCESS_TOKEN_KEY, null);
    }

    public String getClient() {
        return mPreferences.getString(CLIENT_KEY, null);
    }

    public String getUid() {
        return mPreferences.getString(UID_KEY, null);
    }

    public boolean isLoggedIn() {
        return getAuthToken() != null && getClient() != null && getUid() != null;
    }

    public void saveAuthHeaders(String accessToken, String client, String uid) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(ACCESS_TOKEN_KEY, accessToken);
        editor.putString(CLIENT_KEY, client);
        editor.putString(UID_KEY, uid);
        editor.apply();
    }

    public void removeAuthHeaders() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(ACCESS_TOKEN_KEY);
        editor.remove(CLIENT_KEY);
        editor.remove(UID_KEY);
        editor.apply();
    }
}
