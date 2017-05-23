package com.svanegas.trackmyjog.network;

import android.support.annotation.NonNull;

import com.svanegas.trackmyjog.util.PreferencesManager;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.svanegas.trackmyjog.util.PreferencesManager.ACCESS_TOKEN_KEY;
import static com.svanegas.trackmyjog.util.PreferencesManager.CLIENT_KEY;
import static com.svanegas.trackmyjog.util.PreferencesManager.UID_KEY;

@Singleton
public class AuthorizationInterceptor implements Interceptor {

    private PreferencesManager mPreferencesManager;

    @Inject
    AuthorizationInterceptor(PreferencesManager preferencesManager) {
        mPreferencesManager = preferencesManager;
    }

    @Override
    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
        Request originalRequest = chain.request();

        String accessToken = mPreferencesManager.getAuthToken();
        String client = mPreferencesManager.getClient();
        String uid = mPreferencesManager.getUid();

        if (accessToken == null || client == null || uid == null) {
            Response response = chain.proceed(originalRequest);
            storeHeadersFromResponse(response);
            return response;
        }

        // Inject the authorization token in headers.
        Request authorizedRequest = originalRequest.newBuilder()
                .header(ACCESS_TOKEN_KEY, accessToken)
                .header(CLIENT_KEY, client)
                .header(UID_KEY, uid)
                .build();

        Response response = chain.proceed(authorizedRequest);
        storeHeadersFromResponse(response);
        return response;
    }

    private void storeHeadersFromResponse(Response response) {
        String accessToken = response.header(ACCESS_TOKEN_KEY);
        String client = response.header(CLIENT_KEY);
        String uid = response.header(UID_KEY);
        if (accessToken != null && client != null && uid != null) {
            mPreferencesManager.saveAuthHeaders(accessToken, client, uid);
        }
    }
}
