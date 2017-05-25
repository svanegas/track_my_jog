package com.svanegas.trackmyjog.domain.main.time_entry.create;

import java.util.Calendar;

interface CreateTimeEntryView {

    Calendar date();

    String distance();

    String hours();

    String minutes();

    void showLoadingAndDisableFields();

    void hideLoadingAndEnableFields();

    void onCreationSuccess();

    void onUpdateSuccess();

    void populateDate(Calendar calendar);

    void populateDistance(long distance);

    void populateDuration(long duration);

    void showEmptyDurationError();

    void showNegativeDurationError();

    void showInvalidDurationError();

    void showEmptyDistanceError();

    void showNegativeDistanceError();

    void showInvalidDistanceError();

    void showTimeoutError();

    void showNoConnectionError();

    void showDisplayableError(String errorMessage);

    void showUnableToParseDateError();

    void showUnknownError();
}
