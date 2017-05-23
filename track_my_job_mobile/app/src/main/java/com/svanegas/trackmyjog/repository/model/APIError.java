package com.svanegas.trackmyjog.repository.model;

import android.support.annotation.Nullable;

import java.util.List;

public class APIError {

    private List<String> errors;

    /**
     * Ensure to call {@link #getErrorMessage()} before use this property.
     */
    public String errorMessage;

    @Nullable
    public String getErrorMessage() {
        if (errors != null && !errors.isEmpty()) {
            this.errorMessage = errors.get(0);
            return errorMessage;
        }
        return null;
    }
}
