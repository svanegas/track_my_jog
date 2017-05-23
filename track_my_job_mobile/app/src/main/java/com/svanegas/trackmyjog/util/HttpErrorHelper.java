package com.svanegas.trackmyjog.util;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.svanegas.trackmyjog.repository.model.APIError;

import retrofit2.HttpException;

public class HttpErrorHelper {

    public static boolean isUnauthorizedError(Throwable throwable) {
        // TODO: Implement and use this method
        return false;
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
