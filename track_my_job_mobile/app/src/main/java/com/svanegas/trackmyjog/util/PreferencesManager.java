package com.svanegas.trackmyjog.util;

import android.content.SharedPreferences;

import com.svanegas.trackmyjog.repository.model.User;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PreferencesManager {

    public static final String ACCESS_TOKEN_KEY = "access-token";
    public static final String CLIENT_KEY = "client";
    public static final String UID_KEY = "uid";
    private static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String EMAIL_KEY = "email";

    public static final String KM_UNIT = "km";
    public static final String MILE_UNIT = "mi.";
    private static final String DISTANCE_UNIT_KEY = "distance_unit";

    private static final String ROLE_KEY = "role";
    public static final String REGULAR_ROLE = "regular";

    public static final String MANAGER_ROLE = "manager";
    public static final String ADMIN_ROLE = "admin";

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

    public long getId() {
        return mPreferences.getLong(ID_KEY, 0L);
    }

    public String getName() {
        return mPreferences.getString(NAME_KEY, null);
    }

    public String getEmail() {
        return mPreferences.getString(EMAIL_KEY, null);
    }

    public boolean isAdmin() {
        return ADMIN_ROLE.equals(mPreferences.getString(ROLE_KEY, null));
    }

    public boolean isManager() {
        return MANAGER_ROLE.equals(mPreferences.getString(ROLE_KEY, null));
    }

    public boolean isLoggedIn() {
        return getAuthToken() != null && getClient() != null && getUid() != null;
    }

    public String getDistanceUnits() {
        return mPreferences.getString(DISTANCE_UNIT_KEY, KM_UNIT);
    }

    public void saveAuthHeaders(String accessToken, String client, String uid) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(ACCESS_TOKEN_KEY, accessToken);
        editor.putString(CLIENT_KEY, client);
        editor.putString(UID_KEY, uid);
        editor.apply();
    }

    public void saveUserInfo(User user) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(ID_KEY, user.getId());
        editor.putString(NAME_KEY, user.getName());
        editor.putString(EMAIL_KEY, user.getEmail());
        editor.putString(ROLE_KEY, user.getRole());
        editor.apply();
    }

    public void removeAuthHeaders() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(ACCESS_TOKEN_KEY);
        editor.remove(CLIENT_KEY);
        editor.remove(UID_KEY);
        editor.apply();
    }

    public void removeUserInfo() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(ID_KEY);
        editor.remove(NAME_KEY);
        editor.remove(EMAIL_KEY);
        editor.remove(ROLE_KEY);
        editor.apply();
    }
}
