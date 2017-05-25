package com.svanegas.trackmyjog.domain.main.time_entry.form;

import android.text.TextUtils;
import android.util.Log;

import com.svanegas.trackmyjog.TrackMyJogApplication;
import com.svanegas.trackmyjog.interactor.TimeEntryInteractor;
import com.svanegas.trackmyjog.repository.model.APIError;
import com.svanegas.trackmyjog.repository.model.TimeEntry;

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
import static com.svanegas.trackmyjog.network.ConnectionInterceptor.isInternetConnectionError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.isHttpError;
import static com.svanegas.trackmyjog.util.HttpErrorHelper.parseHttpError;

public class TimeEntryFormPresenterImpl implements TimeEntryFormPresenter {

    private static final String TAG = TimeEntryFormPresenterImpl.class.getSimpleName();

    private TimeEntryFormView mView;

    @Inject
    TimeEntryInteractor mInteractor;

    TimeEntryFormPresenterImpl(TimeEntryFormView timeEntryFormView) {
        mView = timeEntryFormView;
        TrackMyJogApplication.getInstance().getApplicationComponent().inject(this);
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
        float distanceValue = validateDistance(distance);

        if (distanceValue > 0f && duration > 0)
            processTimeEntry(timeEntryId, date, distance, duration);
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
                    mView.populateDistance(timeEntry.getDistance());
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

    private void processTimeEntry(long timeEntryId, Calendar date, String distance, long duration) {
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
     * @return 0f if the given distance is invalid, a positive floating point number otherwise.
     * The positive floating point number corresponds to the entered distance.
     */
    private float validateDistance(String distance) {
        float distanceFloat;
        if (TextUtils.isEmpty(distance)) {
            mView.showEmptyDistanceError();
            return 0f;
        } else {
            try {
                distanceFloat = Float.valueOf(distance);
                if (distanceFloat <= 0f) {
                    mView.showNegativeDistanceError();
                    return 0f;
                }
            } catch (NumberFormatException e) {
                mView.showInvalidDistanceError();
                return 0f;
            }
        }
        return distanceFloat;
    }
}
