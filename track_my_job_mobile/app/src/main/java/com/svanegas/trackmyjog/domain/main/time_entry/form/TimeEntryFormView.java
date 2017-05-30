package com.svanegas.trackmyjog.domain.main.time_entry.form;

import java.util.Calendar;

interface TimeEntryFormView {

    Calendar date();

    String distance();

    String hours();

    String minutes();

    void showLoadingAndDisableFields();

    void hideLoadingAndEnableFields();

    void onCreationSuccess();

    void onUpdateSuccess();

    void onDeletionSuccess();

    void populateDate(Calendar calendar);

    void populateDistanceUnits(String distanceUnits);

    void populateDistance(double distance);

    void populateDuration(long duration);

    void showEmptyDurationError();

    void showNegativeDurationError();

    void showNotInRangeMinutesError();

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
