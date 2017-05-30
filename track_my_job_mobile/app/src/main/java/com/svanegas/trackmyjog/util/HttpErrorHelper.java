package com.svanegas.trackmyjog.util;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.svanegas.trackmyjog.repository.model.APIError;

import java.net.HttpURLConnection;
import java.net.ProtocolException;

import retrofit2.HttpException;

public class HttpErrorHelper {

    public static boolean isUnauthorizedError(Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            return httpException.code() == HttpURLConnection.HTTP_UNAUTHORIZED;
        }
        return throwable instanceof ProtocolException;
    }

    public static boolean isHttpError(Throwable throwable) {
        return throwable instanceof HttpException;
    }

    @Nullable
    public static APIError parseHttpError(HttpException httpException) {
        try {
            return new Gson().fromJson(httpException
                    .response().errorBody().string(), APIError.class);
        } catch (Exception e) {
            return null;
        }
    }
}
