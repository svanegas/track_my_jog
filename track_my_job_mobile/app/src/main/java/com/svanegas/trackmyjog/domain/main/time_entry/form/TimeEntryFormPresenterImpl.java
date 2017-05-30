package com.svanegas.trackmyjog.domain.main.time_entry.form;

import android.text.TextUtils;
import android.util.Log;

import com.svanegas.trackmyjog.TrackMyJogApplication;
import com.svanegas.trackmyjog.interactor.TimeEntryInteractor;
import com.svanegas.trackmyjog.repository.model.APIError;
import com.svanegas.trackmyjog.repository.model.TimeEntry;
import com.svanegas.trackmyjog.util.PreferencesManager;

import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

import static com.svanegas.trackmyjog.domain.main.time_entry.list.TimeEntriesListPresenterImpl.INPUT_DATE_FORMAT;
import static com.svanegas.trackmyjog.domain.main.time_entry.list.TimeEntriesListPresenterImpl.KM_CONVERSION_FACTOR;
import static com.svanegas.trackmyjog.domain.main.time_entry.list.TimeEntriesListPresenterImpl.MI_CONVERSION_FACTOR;
import static com.svanegas.trackmyjog.domain.main.time_entry.list.TimeEntriesListPresenterImpl.computeDistance;
import static com.svanegas.trackmyjog.network.ConnectionInterceptor.isInternetConnectionError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.isHttpError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.parseHttpError;
import static com.svanegas.trackmyjog.util.PreferencesManager.KM_UNIT;
import static com.svanegas.trackmyjog.util.PreferencesManager.MILE_UNIT;

public class TimeEntryFormPresenterImpl implements TimeEntryFormPresenter {

    private static final String TAG = TimeEntryFormPresenterImpl.class.getSimpleName();

    private TimeEntryFormView mView;

    @Inject
    TimeEntryInteractor mInteractor;

    @Inject
    PreferencesManager mPreferencesManager;

    TimeEntryFormPresenterImpl(TimeEntryFormView timeEntryFormView) {
        mView = timeEntryFormView;
        TrackMyJogApplication.getInstance().getApplicationComponent().inject(this);
    }

    @Override
    public void setupDistanceUnits() {
        mView.populateDistanceUnits(mPreferencesManager.getDistanceUnits());
    }

    /**
     * Validates entered information related to the time entry and performs the create or update
     * request.
     *
     * @param timeEntryId -1 if is a create request, id of the time entry to be updated otherwise.
     */
    public void submitTimeEntry(long timeEntryId) {
        mView.showLoadingAndDisableFields();
        Calendar date = mView.date();
        String distance = mView.distance();
        String hours = mView.hours();
        String minutes = mView.minutes();

        long duration = validateDuration(hours, minutes);
        double distanceValue = validateDistance(distance);
        long distanceLong = computeMeters(distanceValue, mPreferencesManager.getDistanceUnits());

        if (distanceLong > 0 && duration > 0)
            processTimeEntry(timeEntryId, date, distanceLong, duration);
        else mView.hideLoadingAndEnableFields();
    }

    @Override
    public void fetchTimeEntry(long timeEntryId) {
        mView.showLoadingAndDisableFields();
        mInteractor.fetchTimeEntry(timeEntryId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timeEntry -> {
                    mView.hideLoadingAndEnableFields();
                    try {
                        mView.populateDate(parseDate(timeEntry.getDate()));
                    } catch (ParseException e) {
                        mView.showUnableToParseDateError();
                    }
                    mView.populateDistance(computeDistance(timeEntry.getDistance(),
                            mPreferencesManager.getDistanceUnits()));
                    mView.populateDuration(timeEntry.getDuration());
                }, throwable -> {
                    mView.hideLoadingAndEnableFields();
                    if (throwable instanceof SocketTimeoutException) {
                        mView.showTimeoutError();
                    } else if (isInternetConnectionError(throwable)) {
                        mView.showNoConnectionError();
                    } else if (isHttpError(throwable)) {
                        APIError error = parseHttpError((HttpException) throwable);
                        if (error != null && error.getErrorMessage() != null) {
                            mView.showDisplayableError(error.errorMessage);
                        } else mView.showUnknownError();
                    } else {
                        Log.e(TAG, "Could not fetch Time Entry due unknown error: ",
                                throwable);
                        mView.showUnknownError();
                    }
                });
    }

    @Override
    public void deleteTimeEntry(long timeEntryId) {
        mView.showLoadingAndDisableFields();
        mInteractor.deleteTimeEntry(timeEntryId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trash -> mView.onDeletionSuccess(), throwable -> {
                    mView.hideLoadingAndEnableFields();
                    if (throwable instanceof SocketTimeoutException) {
                        mView.showTimeoutError();
                    } else if (isInternetConnectionError(throwable)) {
                        mView.showNoConnectionError();
                    } else if (isHttpError(throwable)) {
                        APIError error = parseHttpError((HttpException) throwable);
                        if (error != null && error.getErrorMessage() != null) {
                            mView.showDisplayableError(error.errorMessage);
                        } else mView.showUnknownError();
                    } else {
                        Log.e(TAG, "Could not delete Time Entry due unknown error: ",
                                throwable);
                        mView.showUnknownError();
                    }
                });
    }

    private void processTimeEntry(long timeEntryId, Calendar date, long distance, long duration) {
        String dateString = String.format(Locale.US, "%d-%d-%d",
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH) + 1,
                date.get(Calendar.DAY_OF_MONTH));
        boolean isUpdate;
        Single<TimeEntry> single;
        if (timeEntryId != -1) {
            isUpdate = true;
            single = mInteractor.updateTimeEntry(timeEntryId, dateString, distance, duration);
        } else {
            isUpdate = false;
            single = mInteractor.createTimeEntry(dateString, distance, duration);
        }
        single.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timeEntry -> {
                    if (isUpdate) mView.onUpdateSuccess();
                    else mView.onCreationSuccess();
                }, throwable -> {
                    mView.hideLoadingAndEnableFields();
                    if (throwable instanceof SocketTimeoutException) {
                        mView.showTimeoutError();
                    } else if (isInternetConnectionError(throwable)) {
                        mView.showNoConnectionError();
                    } else if (isHttpError(throwable)) {
                        APIError error = parseHttpError((HttpException) throwable);
                        if (error != null && error.getErrorMessage() != null) {
                            mView.showDisplayableError(error.errorMessage);
                        } else mView.showUnknownError();
                    } else {
                        Log.e(TAG, "Could not " + (isUpdate ? "update" : "create") +
                                        " Time Entry due unknown error: ",
                                throwable);
                        mView.showUnknownError();
                    }
                });
    }

    private Calendar parseDate(String date) throws ParseException {
        DateFormat inputFormat = new SimpleDateFormat(INPUT_DATE_FORMAT, Locale.US);
        Calendar entryDate = Calendar.getInstance();
        entryDate.setTime(inputFormat.parse(date));
        return entryDate;
    }

    /**
     * Validates if the entered duration values are valid
     *
     * @param hours   string representing hours
     * @param minutes string representing minutes
     * @return 0 if duration is invalid, a positive integer otherwise. The positive integer
     * corresponds to the entered duration in minutes.
     */
    private long validateDuration(String hours, String minutes) {
        long total;
        if (TextUtils.isEmpty(hours) || TextUtils.isEmpty(minutes)) {
            mView.showEmptyDurationError();
            return 0L;
        } else {
            try {
                long hoursInt = Long.valueOf(hours);
                long minutesInt = Long.valueOf(minutes);
                if (minutesInt >= 60) {
                    mView.showNotInRangeMinutesError();
                    return 0L;
                }
                total = hoursInt * 60L + minutesInt;
                if (total <= 0L) {
                    mView.showNegativeDurationError();
                    return 0L;
                }
            } catch (NumberFormatException e) {
                mView.showInvalidDurationError();
                return 0L;
            }
        }
        return total;
    }

    /**
     * Validates if the entered distance value is valid
     *
     * @param distance string representation of the distance
     * @return 0.0 if the given distance is invalid, a positive floating point number otherwise.
     * The positive floating point number corresponds to the entered distance.
     */
    private double validateDistance(String distance) {
        double distanceDouble;
        if (TextUtils.isEmpty(distance)) {
            mView.showEmptyDistanceError();
            return 0.0;
        } else {
            try {
                distanceDouble = Double.valueOf(distance);
                if (distanceDouble <= 0.0) {
                    mView.showNegativeDistanceError();
                    return 0.0;
                }
            } catch (NumberFormatException e) {
                mView.showInvalidDistanceError();
                return 0.0;
            }
        }
        return distanceDouble;
    }

    /**
     * Converts the given distance from the specified units to meters.
     *
     * @param distance value to be converted to meters.
     * @param units    units of the given distance, possible units:
     *                 {@link PreferencesManager#KM_UNIT}, {@link PreferencesManager#MILE_UNIT}.
     * @return meters representation of the given value.
     */
    private long computeMeters(double distance, String units) {
        switch (units) {
            case KM_UNIT:
                return (long) (distance * KM_CONVERSION_FACTOR);
            case MILE_UNIT:
                return (long) (distance * MI_CONVERSION_FACTOR);
            default:
                throw new IllegalArgumentException(units + " is not a valid unit");
        }
    }
}
