package com.svanegas.trackmyjog.network;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@Singleton
public class ConnectionInterceptor implements Interceptor {

    private static final String NO_INTERNET_CONNECTION = "no_internet_connection";

    private Application mApplication;

    @Inject
    ConnectionInterceptor(Application application) {
        mApplication = application;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        if (isInternetAvailable(mApplication)) return chain.proceed(originalRequest);
        else throw new IOException(NO_INTERNET_CONNECTION);
    }

    private static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isInternetConnectionError(Throwable throwable) {
        return throwable instanceof IOException &&
                NO_INTERNET_CONNECTION.equals(throwable.getMessage());
    }
}
